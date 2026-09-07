# SONQIVA --- Refined Feature Backlog

## Priority system

P0 = required for the first usable build P1 = important for V1 P2 =
polish/advanced P3 = experimental

## P0 --- Core

### Music discovery

-   [ ] Permission flow
-   [ ] MediaStore audio query
-   [ ] Song model
-   [ ] Progressive library loading
-   [ ] Handle library refresh

### Playback

-   [ ] Media3 ExoPlayer
-   [ ] Play
-   [ ] Pause
-   [ ] Next
-   [ ] Previous
-   [ ] Seek
-   [ ] Queue
-   [ ] Shuffle
-   [ ] Repeat
-   [ ] Speed 0.5x--3x

### Background

-   [ ] MediaSessionService
-   [ ] Notification
-   [ ] Lock-screen controls
-   [ ] Audio focus
-   [ ] Bluetooth/media buttons
-   [ ] Resume after interruption where appropriate

### UI

-   [ ] Home
-   [ ] Library
-   [ ] Songs
-   [ ] Folders
-   [ ] Search
-   [ ] Mini player
-   [ ] Full player

## P1 --- Personal library

-   [ ] Albums
-   [ ] Artists
-   [ ] Genres
-   [ ] Favorites
-   [ ] Recently played
-   [ ] Recently added
-   [ ] Most played
-   [ ] Playlists
-   [ ] Folder playback
-   [ ] Folder shuffle
-   [ ] Sort/filter
-   [ ] Play next
-   [ ] Add to queue
-   [ ] Sleep timer
-   [ ] Resume last position

## P2 --- Premium experience

-   [ ] Crossfade
-   [ ] Gapless playback
-   [ ] Local lyrics
-   [ ] Metadata editing
-   [ ] Home widget
-   [ ] Android Auto
-   [ ] Backup/restore
-   [ ] Smart playlists
-   [ ] Advanced statistics
-   [ ] Custom themes

## P3 --- Experimental

-   [ ] Lightweight visualizer
-   [ ] Duplicate detection
-   [ ] Smart recommendations based only on local history
-   [ ] Local AI features

Do not implement P3 until the app is already fast and stable.

## High-value feature ideas

### Sleep timer

Presets: - 15 min - 30 min - 45 min - 60 min - End of current song

### Quick resume

Home can show: "Continue listening"

with the last track and position.

### Folder exclusion

Allow the user to hide unwanted directories from the library.

### Compact mode

For users with very large libraries: - Smaller rows - More songs
visible - Less artwork

### Low-memory mode

When enabled: - Smaller artwork cache - Reduced animations - Less
aggressive image caching - More compact lists

### Smart sorting

Offer: - Recently added - Recently played - Most played - A--Z -
Artist - Duration

### Queue save

"Save Queue as Playlist"

### Long press

Make long press a powerful but discoverable shortcut.

### Swipe gestures

Use only where discoverable: - Swipe song for queue/favorite actions -
Swipe queue item to remove

Do not make essential actions gesture-only.

## Quality rule

A feature should only be added when: 1. It has a clear user benefit. 2.
It does not make playback less reliable. 3. It does not noticeably
damage low-end performance. 4. It does not introduce unnecessary
complexity.
