package com.anant.sonqiva.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object Library : Screen(
        route = "library",
        title = "Library",
        selectedIcon = Icons.Filled.LibraryMusic,
        unselectedIcon = Icons.Outlined.LibraryMusic
    )

    object Folders : Screen(
        route = "folders",
        title = "Folders",
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder
    )

    object Search : Screen(
        route = "search",
        title = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    // Sub-screens & Detail routes
    object AlbumDetail : Screen("album_detail/{albumId}", "Album Detail") {
        fun createRoute(albumId: Long) = "album_detail/$albumId"
    }

    object ArtistDetail : Screen("artist_detail/{artistId}", "Artist Detail") {
        fun createRoute(artistId: Long) = "artist_detail/$artistId"
    }

    object PlaylistDetail : Screen("playlist_detail/{playlistId}", "Playlist Detail") {
        fun createRoute(playlistId: Long) = "playlist_detail/$playlistId"
    }

    object Favorites : Screen("favorites", "Favorites")

    object FolderFilter : Screen("folder_filter", "Folder Visibility")

    companion object {
        val bottomNavItems = listOf(Home, Library, Folders, Search, Settings)
    }
}
