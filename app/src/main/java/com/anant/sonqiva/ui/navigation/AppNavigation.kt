package com.anant.sonqiva.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anant.sonqiva.data.local.database.PlaylistEntity
import com.anant.sonqiva.data.model.Album
import com.anant.sonqiva.data.model.Artist
import com.anant.sonqiva.data.model.FolderItem
import com.anant.sonqiva.data.model.PlaybackState
import com.anant.sonqiva.data.model.Song
import com.anant.sonqiva.data.model.SongSortOrder
import com.anant.sonqiva.ui.albums.AlbumDetailScreen
import com.anant.sonqiva.ui.artists.ArtistDetailScreen
import com.anant.sonqiva.ui.components.MiniPlayer
import com.anant.sonqiva.ui.components.SongActionBottomSheet
import com.anant.sonqiva.ui.components.SongSortBottomSheet
import com.anant.sonqiva.ui.components.TrackInfoBottomSheet
import com.anant.sonqiva.ui.folders.FoldersScreen
import com.anant.sonqiva.ui.home.HomeScreen
import com.anant.sonqiva.ui.library.LibraryScreen
import com.anant.sonqiva.ui.player.FullPlayerScreen
import com.anant.sonqiva.ui.playlists.AddToPlaylistBottomSheet
import com.anant.sonqiva.ui.playlists.CreatePlaylistDialog
import com.anant.sonqiva.ui.playlists.PlaylistDetailScreen
import com.anant.sonqiva.ui.search.SearchScreen
import com.anant.sonqiva.ui.settings.FolderFilterScreen
import com.anant.sonqiva.ui.settings.SettingsScreen
import com.anant.sonqiva.ui.theme.BackgroundDark

@Composable
fun SonqivaAppShell(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<FolderItem>,
    playlists: List<PlaylistEntity>,
    currentFolder: FolderItem?,
    playbackState: PlaybackState,
    songSortOrder: SongSortOrder,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onFolderClick: (FolderItem) -> Unit,
    onFolderBackClick: () -> Unit,
    onPlayAllClick: () -> Unit,
    onShuffleAllClick: () -> Unit,
    onPlayFolderClick: (FolderItem) -> Unit,
    onShuffleFolderClick: (FolderItem) -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeekClick: (Long) -> Unit,
    onShuffleToggle: () -> Unit,
    onRepeatToggle: () -> Unit,
    onFavoriteToggle: (Song) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onQueueItemClick: (Int) -> Unit,
    onRescanLibraryClick: () -> Unit,
    onSortOrderSelected: (SongSortOrder) -> Unit,
    allUnfilteredFolders: List<FolderItem> = emptyList(),
    excludedFolderPaths: Set<String> = emptySet(),
    onExcludedFolderPathsChanged: ((Set<String>) -> Unit)? = null,
    onPlayNext: ((Song) -> Unit)? = null,
    onAddToQueue: ((Song) -> Unit)? = null,
    onCreatePlaylist: ((String) -> Unit)? = null,
    onDeletePlaylist: ((Long) -> Unit)? = null,
    onAddSongToPlaylist: ((Long, Long) -> Unit)? = null,
    onSetSleepTimer: ((Int) -> Unit)? = null,
    onCancelSleepTimer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var isFullPlayerExpanded by remember { mutableStateOf(false) }
    var selectedSongForActions by remember { mutableStateOf<Song?>(null) }
    var songForTrackInfo by remember { mutableStateOf<Song?>(null) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var showSortBottomSheet by remember { mutableStateOf(false) }
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isBottomBarVisible = currentRoute in Screen.bottomNavItems.map { it.route }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (isBottomBarVisible) {
                    SonqivaBottomNavBar(navController = navController)
                }
            },
            containerColor = BackgroundDark
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            songs = songs,
                            albums = albums,
                            playbackState = playbackState,
                            onSongClick = onSongClick,
                            onAlbumClick = { album ->
                                navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                            },
                            onShuffleAllClick = onShuffleAllClick,
                            onFavoritesClick = {
                                navController.navigate(Screen.Library.route)
                            },
                            onSearchClick = {
                                navController.navigate(Screen.Search.route)
                            },
                            onFavoriteToggle = onFavoriteToggle
                        )
                    }

                    composable(Screen.Library.route) {
                        LibraryScreen(
                            songs = songs,
                            albums = albums,
                            artists = artists,
                            playlists = playlists,
                            playbackState = playbackState,
                            onSongClick = onSongClick,
                            onAlbumClick = { album ->
                                navController.navigate(Screen.AlbumDetail.createRoute(album.id))
                            },
                            onArtistClick = { artist ->
                                navController.navigate(Screen.ArtistDetail.createRoute(artist.id))
                            },
                            onPlaylistClick = { playlist ->
                                navController.navigate(Screen.PlaylistDetail.createRoute(playlist.id))
                            },
                            onCreatePlaylistClick = {
                                showCreatePlaylistDialog = true
                            },
                            onPlayAllClick = onPlayAllClick,
                            onShuffleAllClick = onShuffleAllClick,
                            onFavoriteToggle = onFavoriteToggle,
                            onSortClick = {
                                showSortBottomSheet = true
                            },
                            onSongMoreClick = { song ->
                                selectedSongForActions = song
                            }
                        )
                    }

                    composable(Screen.Folders.route) {
                        FoldersScreen(
                            folders = folders,
                            currentFolder = currentFolder,
                            playbackState = playbackState,
                            onFolderClick = onFolderClick,
                            onBackClick = onFolderBackClick,
                            onSongClick = onSongClick,
                            onPlayFolderClick = onPlayFolderClick,
                            onShuffleFolderClick = onShuffleFolderClick
                        )
                    }

                    composable(Screen.Search.route) {
                        SearchScreen(
                            allSongs = songs,
                            allAlbums = albums,
                            allArtists = artists,
                            playbackState = playbackState,
                            onSongClick = onSongClick,
                            onAlbumClick = onAlbumClick,
                            onArtistClick = onArtistClick
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onRescanLibraryClick = onRescanLibraryClick,
                            onManageFoldersClick = {
                                navController.navigate(Screen.FolderFilter.route)
                            }
                        )
                    }

                    composable(Screen.FolderFilter.route) {
                        FolderFilterScreen(
                            allFolders = allUnfilteredFolders,
                            excludedPaths = excludedFolderPaths,
                            onExcludedPathsChanged = { paths ->
                                onExcludedFolderPathsChanged?.invoke(paths)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Album Detail Screen
                    composable(
                        route = Screen.AlbumDetail.route,
                        arguments = listOf(navArgument("albumId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val albumId = backStackEntry.arguments?.getLong("albumId") ?: -1L
                        val album = albums.firstOrNull { it.id == albumId }

                        if (album != null) {
                            AlbumDetailScreen(
                                album = album,
                                playbackState = playbackState,
                                onBackClick = { navController.popBackStack() },
                                onSongClick = onSongClick,
                                onPlayAllClick = {
                                    if (album.songs.isNotEmpty()) {
                                        onSongClick(album.songs.first())
                                    }
                                },
                                onShuffleAllClick = {
                                    val shuffled = album.songs.shuffled()
                                    if (shuffled.isNotEmpty()) {
                                        onSongClick(shuffled.first())
                                    }
                                },
                                onFavoriteToggle = onFavoriteToggle
                            )
                        }
                    }

                    // Artist Detail Screen
                    composable(
                        route = Screen.ArtistDetail.route,
                        arguments = listOf(navArgument("artistId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val artistId = backStackEntry.arguments?.getLong("artistId") ?: -1L
                        val artist = artists.firstOrNull { it.id == artistId }

                        if (artist != null) {
                            ArtistDetailScreen(
                                artist = artist,
                                playbackState = playbackState,
                                onBackClick = { navController.popBackStack() },
                                onSongClick = onSongClick,
                                onPlayAllClick = {
                                    if (artist.songs.isNotEmpty()) {
                                        onSongClick(artist.songs.first())
                                    }
                                },
                                onShuffleAllClick = {
                                    val shuffled = artist.songs.shuffled()
                                    if (shuffled.isNotEmpty()) {
                                        onSongClick(shuffled.first())
                                    }
                                },
                                onFavoriteToggle = onFavoriteToggle
                            )
                        }
                    }

                    // Playlist Detail Screen
                    composable(
                        route = Screen.PlaylistDetail.route,
                        arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: -1L
                        val playlist = playlists.firstOrNull { it.id == playlistId }

                        if (playlist != null) {
                            PlaylistDetailScreen(
                                playlist = playlist,
                                songs = songs,
                                playbackState = playbackState,
                                onBackClick = { navController.popBackStack() },
                                onDeletePlaylistClick = {
                                    onDeletePlaylist?.invoke(playlist.id)
                                    navController.popBackStack()
                                },
                                onSongClick = onSongClick,
                                onPlayAllClick = onPlayAllClick,
                                onShuffleAllClick = onShuffleAllClick,
                                onFavoriteToggle = onFavoriteToggle
                            )
                        }
                    }
                }

                // Persistent Floating Mini-Player positioned right above the BottomNavBar
                if (playbackState.currentSong != null && !isFullPlayerExpanded) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        MiniPlayer(
                            playbackState = playbackState,
                            onPlayPauseClick = onPlayPauseClick,
                            onNextClick = onNextClick,
                            onExpandClick = { isFullPlayerExpanded = true }
                        )
                    }
                }
            }
        }

        // Full Player Modal Screen
        AnimatedVisibility(
            visible = isFullPlayerExpanded && playbackState.currentSong != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            FullPlayerScreen(
                playbackState = playbackState,
                onCollapse = { isFullPlayerExpanded = false },
                onPlayPause = onPlayPauseClick,
                onNext = onNextClick,
                onPrevious = onPreviousClick,
                onSeek = onSeekClick,
                onShuffleToggle = onShuffleToggle,
                onRepeatToggle = onRepeatToggle,
                onFavoriteToggle = {
                    playbackState.currentSong?.let { onFavoriteToggle(it) }
                },
                onSpeedChange = onSpeedChange,
                onQueueItemClick = onQueueItemClick,
                onSetSleepTimer = onSetSleepTimer,
                onCancelSleepTimer = onCancelSleepTimer
            )
        }

        // Song Action Bottom Sheet (Long Press / More Options)
        selectedSongForActions?.let { song ->
            SongActionBottomSheet(
                song = song,
                onDismiss = { selectedSongForActions = null },
                onPlayNext = { onPlayNext?.invoke(song) },
                onAddToQueue = { onAddToQueue?.invoke(song) },
                onAddToPlaylist = {
                    songToAddToPlaylist = song
                },
                onToggleFavorite = { onFavoriteToggle(song) },
                onGoToAlbum = {
                    navController.navigate(Screen.AlbumDetail.createRoute(song.albumId))
                },
                onGoToArtist = {
                    val artist = artists.firstOrNull { it.name.equals(song.artist, ignoreCase = true) }
                    if (artist != null) {
                        navController.navigate(Screen.ArtistDetail.createRoute(artist.id))
                    }
                },
                onShowTrackInfo = {
                    songForTrackInfo = song
                }
            )
        }

        // Add to Playlist Bottom Sheet
        songToAddToPlaylist?.let { song ->
            AddToPlaylistBottomSheet(
                song = song,
                playlists = playlists,
                onDismiss = { songToAddToPlaylist = null },
                onPlaylistSelected = { playlistId ->
                    onAddSongToPlaylist?.invoke(playlistId, song.id)
                },
                onCreateNewPlaylistClick = {
                    showCreatePlaylistDialog = true
                }
            )
        }

        // Create Playlist Dialog
        if (showCreatePlaylistDialog) {
            CreatePlaylistDialog(
                onDismiss = { showCreatePlaylistDialog = false },
                onCreate = { name ->
                    onCreatePlaylist?.invoke(name)
                    showCreatePlaylistDialog = false
                }
            )
        }

        // Sort Songs Bottom Sheet
        if (showSortBottomSheet) {
            SongSortBottomSheet(
                currentSortOrder = songSortOrder,
                onSortOrderSelected = onSortOrderSelected,
                onDismiss = { showSortBottomSheet = false }
            )
        }

        // Track Information Bottom Sheet
        songForTrackInfo?.let { song ->
            TrackInfoBottomSheet(
                song = song,
                onDismiss = { songForTrackInfo = null }
            )
        }
    }
}
