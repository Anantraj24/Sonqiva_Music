package com.anant.sonqiva.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.anant.sonqiva.data.local.database.FavoriteEntity
import com.anant.sonqiva.data.local.database.PlaybackHistoryEntity
import com.anant.sonqiva.data.local.database.PlaylistEntity
import com.anant.sonqiva.data.local.database.PlaylistSongEntity
import com.anant.sonqiva.data.local.database.SonqivaDatabase
import com.anant.sonqiva.data.local.datastore.UserPreferencesRepository
import com.anant.sonqiva.data.local.mediastore.MediaStoreAudioDataSource
import com.anant.sonqiva.data.model.Album
import com.anant.sonqiva.data.model.Artist
import com.anant.sonqiva.data.model.FolderItem
import com.anant.sonqiva.data.model.PlaybackState
import com.anant.sonqiva.data.model.Song
import com.anant.sonqiva.data.model.SongSortOrder
import com.anant.sonqiva.data.repository.AudioRepository
import com.anant.sonqiva.player.controller.PlaybackController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database: SonqivaDatabase = Room.databaseBuilder(
        application,
        SonqivaDatabase::class.java,
        "sonqiva_database"
    ).fallbackToDestructiveMigration().build()

    private val mediaStoreDataSource = MediaStoreAudioDataSource(application)
    private val audioRepository = AudioRepository(mediaStoreDataSource)
    private val preferencesRepository = UserPreferencesRepository(application)
    val playbackController = PlaybackController(application)

    private val _rawScannedSongs = MutableStateFlow<List<Song>>(emptyList())

    val songSortOrder: StateFlow<SongSortOrder> = preferencesRepository.songSortOrderFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, SongSortOrder.TITLE_ASC)

    val excludedFolderPaths: StateFlow<Set<String>> = preferencesRepository.excludedFolderPathsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val favoriteIds: StateFlow<List<Long>> = database.favoriteDao().getAllFavoriteSongIds()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val songs: StateFlow<List<Song>> = combine(
        _rawScannedSongs, favoriteIds, songSortOrder, excludedFolderPaths
    ) { rawList, favIds, sortOrder, excluded ->
        val favSet = favIds.toSet()
        val filtered = if (excluded.isEmpty()) rawList
                       else rawList.filter { it.folderPath.isEmpty() || it.folderPath !in excluded }
        val withFavs = filtered.map { it.copy(isFavorite = favSet.contains(it.id)) }
        when (sortOrder) {
            SongSortOrder.TITLE_ASC -> withFavs.sortedBy { it.title.lowercase() }
            SongSortOrder.TITLE_DESC -> withFavs.sortedByDescending { it.title.lowercase() }
            SongSortOrder.ARTIST_ASC -> withFavs.sortedWith(compareBy({ it.artist.lowercase() }, { it.title.lowercase() }))
            SongSortOrder.DATE_ADDED_DESC -> withFavs.sortedByDescending { it.dateAdded }
            SongSortOrder.DATE_ADDED_ASC -> withFavs.sortedBy { it.dateAdded }
            SongSortOrder.DURATION_DESC -> withFavs.sortedByDescending { it.durationMs }
            SongSortOrder.DURATION_ASC -> withFavs.sortedBy { it.durationMs }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _folders = MutableStateFlow<List<FolderItem>>(emptyList())
    val folders: StateFlow<List<FolderItem>> = _folders.asStateFlow()

    // All folders on device, ignoring the visibility filter — used by the Folder Visibility picker
    // so users can always see and re-enable previously hidden folders.
    private val _allUnfilteredFolders = MutableStateFlow<List<FolderItem>>(emptyList())
    val allUnfilteredFolders: StateFlow<List<FolderItem>> = _allUnfilteredFolders.asStateFlow()

    private val _currentFolder = MutableStateFlow<FolderItem?>(null)
    val currentFolder: StateFlow<FolderItem?> = _currentFolder.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = playbackController.playbackState

    val playlists: StateFlow<List<PlaylistEntity>> = database.playlistDao().getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val lowMemoryMode: StateFlow<Boolean> = preferencesRepository.lowMemoryModeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        observeStoredPreferences()
        observeSongsForPlayback()
    }

    private fun observeSongsForPlayback() {
        viewModelScope.launch {
            songs.collect { songList ->
                if (songList.isNotEmpty()) {
                    playbackController.onSongsUpdated(songList)
                }
            }
        }
    }

    fun loadAudioLibrary() {
        viewModelScope.launch {
            audioRepository.getSongs().collect { scannedSongs ->
                _rawScannedSongs.value = scannedSongs

                // Build unfiltered folder list for the Folder Visibility picker
                _allUnfilteredFolders.value = audioRepository.getFolderHierarchy(scannedSongs)

                // Build albums, artists, and folders from the currently filtered song set
                val excluded = excludedFolderPaths.value
                val filteredSongs = if (excluded.isEmpty()) scannedSongs
                                    else scannedSongs.filter { it.folderPath.isEmpty() || it.folderPath !in excluded }
                _albums.value = audioRepository.getAlbums(filteredSongs)
                _artists.value = audioRepository.getArtists(filteredSongs)
                _folders.value = audioRepository.getFolderHierarchy(filteredSongs)
            }
        }
    }

    private fun observeStoredPreferences() {
        viewModelScope.launch {
            preferencesRepository.playbackSpeedFlow.collect { savedSpeed ->
                playbackController.setPlaybackSpeed(savedSpeed)
            }
        }
    }

    fun setSongSortOrder(sortOrder: SongSortOrder) {
        viewModelScope.launch {
            preferencesRepository.setSongSortOrder(sortOrder)
        }
    }

    fun setExcludedFolderPaths(paths: Set<String>) {
        viewModelScope.launch {
            preferencesRepository.setExcludedFolderPaths(paths)
            // Rebuild albums/artists/folders immediately with the new filter
            val excluded = paths
            val raw = _rawScannedSongs.value
            val filtered = if (excluded.isEmpty()) raw
                           else raw.filter { it.folderPath.isEmpty() || it.folderPath !in excluded }
            _albums.value = audioRepository.getAlbums(filtered)
            _artists.value = audioRepository.getArtists(filtered)
            _folders.value = audioRepository.getFolderHierarchy(filtered)
        }
    }

    fun playSong(song: Song, queue: List<Song> = songs.value) {
        playbackController.playSong(song, queue)
        recordHistory(song)
    }

    fun playNext(song: Song) {
        playbackController.playNext(song)
    }

    fun addToQueue(song: Song) {
        playbackController.addToQueue(song)
    }

    fun playPause() {
        playbackController.playPause()
    }

    fun seekTo(positionMs: Long) {
        playbackController.seekTo(positionMs)
    }

    fun skipToNext() {
        playbackController.skipToNext()
        playbackState.value.currentSong?.let { recordHistory(it) }
    }

    fun skipToPrevious() {
        playbackController.skipToPrevious()
        playbackState.value.currentSong?.let { recordHistory(it) }
    }

    fun skipToQueueItem(index: Int) {
        playbackController.skipToQueueItem(index)
    }

    fun toggleShuffle() {
        playbackController.toggleShuffle()
    }

    fun toggleRepeat() {
        playbackController.toggleRepeat()
    }

    fun setPlaybackSpeed(speed: Float) {
        playbackController.setPlaybackSpeed(speed)
        viewModelScope.launch {
            preferencesRepository.setPlaybackSpeed(speed)
        }
    }

    fun startSleepTimer(minutes: Int) {
        playbackController.startSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        playbackController.cancelSleepTimer()
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (song.isFavorite) {
                    database.favoriteDao().deleteFavorite(song.id)
                } else {
                    database.favoriteDao().insertFavorite(FavoriteEntity(songId = song.id))
                }
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.playlistDao().insertPlaylist(PlaylistEntity(name = name))
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.playlistDao().deletePlaylist(playlistId)
            }
        }
    }

    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.playlistDao().addSongToPlaylist(
                    PlaylistSongEntity(playlistId = playlistId, songId = songId)
                )
            }
        }
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> {
        return database.playlistDao().getSongIdsForPlaylist(playlistId).map { songIds ->
            val idSet = songIds.toSet()
            songs.value.filter { idSet.contains(it.id) }
        }
    }

    fun setLowMemoryMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setLowMemoryMode(enabled)
        }
    }

    fun selectFolder(folder: FolderItem) {
        _currentFolder.value = folder
    }

    fun clearSelectedFolder() {
        _currentFolder.value = null
    }

    private fun recordHistory(song: Song) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.historyDao().insertOrUpdateHistory(
                    PlaybackHistoryEntity(
                        songId = song.id,
                        lastPlayedTimestamp = System.currentTimeMillis(),
                        lastPositionMs = 0L
                    )
                )
            }
            preferencesRepository.saveLastPlaybackState(song.id, 0L)
        }
    }

    override fun onCleared() {
        playbackController.release()
        super.onCleared()
    }
}
