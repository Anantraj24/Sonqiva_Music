# SONQIVA --- Stitch Frontend Implementation Guide

## Stitch reference

**Stitch project/design ID:** `2980046674713411607`

Use the Stitch design as the visual source of truth for the frontend.

## Visual direction

The design inspiration is a premium music application with: -
Blue/violet atmospheric gradients - Large album artwork - Rounded
cards - Dark/translucent surfaces - Elegant typography - Soft depth -
Compact navigation - Immersive player

Sonqiva must remain original and must not copy proprietary branding or
artwork.

## Core screens

### Home

-   Greeting
-   Search
-   Recently Played
-   New Albums
-   Quick Playlists
-   Recently Added
-   Most Played
-   Favorites
-   Mini player

### Library

-   Songs
-   Albums
-   Artists
-   Genres
-   Playlists
-   Folders
-   Recently Played
-   Favorite Albums

### Songs

-   Search
-   Sort
-   Filter
-   Shuffle All
-   Play All
-   Song list

### Albums

-   2-column grid on typical phones
-   Artwork
-   Album name
-   Artist
-   Song count

### Artists

-   Circular artwork
-   Artist name
-   Song count

### Folders

-   Internal Storage
-   Folder list
-   Song counts
-   Nested navigation
-   Play folder
-   Shuffle folder

### Folder detail

-   Folder name
-   Song count
-   Play All
-   Shuffle
-   Add to playlist
-   Song list

### Search

Search across: - Songs - Albums - Artists - Folders - Playlists

### Full player

Hero screen: - Artwork - Atmospheric background - Song title - Artist -
Seek bar - Previous - Play/Pause - Next - Shuffle - Repeat - Favorite -
Queue - Speed - More

### Queue

-   Currently Playing
-   Up Next
-   Reorder
-   Remove
-   Play next
-   Clear

### Playlists

-   Playlist list
-   Create
-   Playlist detail
-   Play all
-   Shuffle
-   Add songs

### Favorites

-   Favorite songs
-   Play all
-   Shuffle
-   Search

### Settings

Playback, library, appearance, performance and about.

## Mini player

Persistent above bottom navigation.

Show: - Artwork - Song title - Artist - Play/Pause

Tapping opens full player.

The transition should feel continuous and premium.

## Animation rules

Use: - Short transitions - Play/pause morph - Artwork scale/transition -
Bottom sheet motion - Favorite feedback - Button press feedback - Smooth
seek interaction

Avoid: - Continuous animation - Video backgrounds - Particle systems -
Excessive blur - Heavy Lottie animations - Large animated backgrounds

## Component design system

Create reusable: - App bars - SongRow - AlbumCard - ArtistRow -
FolderRow - PlaylistCard - MiniPlayer - PlayerControls - SeekBar -
SpeedSheet - QueueSheet - BottomNavigation - SearchBar - EmptyState -
LoadingState - ErrorState - ConfirmationDialog

Do not duplicate visually identical components across screens.

## Responsive behavior

Target: - 360dp - 393dp - 412dp

Long text: - Use ellipsis. - Keep duration visible. - Never allow title
overflow to break controls.

Player: - Keep major controls comfortably reachable. - Preserve safe
areas. - Avoid tiny controls.

## Performance-sensitive UI

Use lazy lists/grids.

Do not render hundreds of composables unnecessarily.

Artwork: - Use thumbnails. - Cache efficiently. - Avoid full-resolution
bitmap decoding for lists.

Avoid expensive derived calculations during recomposition.

## Design philosophy

The UI should communicate speed.

Good: - Immediate press feedback - Predictable navigation - Smooth list
scrolling - Fast search - Instant playback response

Bad: - Long splash screens - Blocking scans - Spinners after every tap -
Excessive modal dialogs - Animations delaying interaction
