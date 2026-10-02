package com.anant.sonqiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.anant.sonqiva.ui.components.AudioPermissionHandler
import com.anant.sonqiva.ui.navigation.SonqivaAppShell
import com.anant.sonqiva.ui.theme.SonqivaTheme
import com.anant.sonqiva.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SonqivaTheme {
                AudioPermissionHandler(
                    onPermissionGranted = {
                        viewModel.loadAudioLibrary()
                    }
                ) {
                    val songs by viewModel.songs.collectAsState()
                    val albums by viewModel.albums.collectAsState()
                    val artists by viewModel.artists.collectAsState()
                    val folders by viewModel.folders.collectAsState()
                    val allUnfilteredFolders by viewModel.allUnfilteredFolders.collectAsState()
                    val excludedFolderPaths by viewModel.excludedFolderPaths.collectAsState()
                    val playlists by viewModel.playlists.collectAsState()
                    val currentFolder by viewModel.currentFolder.collectAsState()
                    val playbackState by viewModel.playbackState.collectAsState()
                    val songSortOrder by viewModel.songSortOrder.collectAsState()

                    SonqivaAppShell(
                        songs = songs,
                        albums = albums,
                        artists = artists,
                        folders = folders,
                        playlists = playlists,
                        currentFolder = currentFolder,
                        playbackState = playbackState,
                        songSortOrder = songSortOrder,
                        onSongClick = { song ->
                            viewModel.playSong(song, songs)
                        },
                        onAlbumClick = { album ->
                            if (album.songs.isNotEmpty()) {
                                viewModel.playSong(album.songs.first(), album.songs)
                            }
                        },
                        onArtistClick = { artist ->
                            if (artist.songs.isNotEmpty()) {
                                viewModel.playSong(artist.songs.first(), artist.songs)
                            }
                        },
                        onFolderClick = { folder ->
                            viewModel.selectFolder(folder)
                        },
                        onFolderBackClick = {
                            viewModel.clearSelectedFolder()
                        },
                        onPlayAllClick = {
                            songs.firstOrNull()?.let { firstSong ->
                                viewModel.playSong(firstSong, songs)
                            }
                        },
                        onShuffleAllClick = {
                            val shuffled = songs.shuffled()
                            shuffled.firstOrNull()?.let { firstSong ->
                                viewModel.playSong(firstSong, shuffled)
                                if (!playbackState.isShuffleEnabled) {
                                    viewModel.toggleShuffle()
                                }
                            }
                        },
                        onPlayFolderClick = { folder ->
                            folder.songs.firstOrNull()?.let { firstSong ->
                                viewModel.playSong(firstSong, folder.songs)
                            }
                        },
                        onShuffleFolderClick = { folder ->
                            val shuffled = folder.songs.shuffled()
                            shuffled.firstOrNull()?.let { firstSong ->
                                viewModel.playSong(firstSong, shuffled)
                                if (!playbackState.isShuffleEnabled) {
                                    viewModel.toggleShuffle()
                                }
                            }
                        },
                        onPlayPauseClick = {
                            viewModel.playPause()
                        },
                        onNextClick = {
                            viewModel.skipToNext()
                        },
                        onPreviousClick = {
                            viewModel.skipToPrevious()
                        },
                        onSeekClick = { newPosition ->
                            viewModel.seekTo(newPosition)
                        },
                        onShuffleToggle = {
                            viewModel.toggleShuffle()
                        },
                        onRepeatToggle = {
                            viewModel.toggleRepeat()
                        },
                        onFavoriteToggle = { song ->
                            viewModel.toggleFavorite(song)
                        },
                        onSpeedChange = { newSpeed ->
                            viewModel.setPlaybackSpeed(newSpeed)
                        },
                        onQueueItemClick = { index ->
                            viewModel.skipToQueueItem(index)
                        },
                        onRescanLibraryClick = {
                            viewModel.loadAudioLibrary()
                        },
                        onSortOrderSelected = { sortOrder ->
                            viewModel.setSongSortOrder(sortOrder)
                        },
                        allUnfilteredFolders = allUnfilteredFolders,
                        excludedFolderPaths = excludedFolderPaths,
                        onExcludedFolderPathsChanged = { paths ->
                            viewModel.setExcludedFolderPaths(paths)
                        },
                        onPlayNext = { song ->
                            viewModel.playNext(song)
                        },
                        onAddToQueue = { song ->
                            viewModel.addToQueue(song)
                        },
                        onCreatePlaylist = { name ->
                            viewModel.createPlaylist(name)
                        },
                        onDeletePlaylist = { playlistId ->
                            viewModel.deletePlaylist(playlistId)
                        },
                        onAddSongToPlaylist = { playlistId, songId ->
                            viewModel.addSongToPlaylist(playlistId, songId)
                        },
                        onSetSleepTimer = { minutes ->
                            viewModel.startSleepTimer(minutes)
                        },
                        onCancelSleepTimer = {
                            viewModel.cancelSleepTimer()
                        }
                    )
                }
            }
        }
    }
}