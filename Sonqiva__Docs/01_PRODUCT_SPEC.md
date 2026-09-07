# SONQIVA --- Product Specification

## 1. Product vision

Sonqiva is a local music player for people who already have music files
on their Android phones and want a better playback and organization
experience.

It is not a streaming platform.

The product should make local music feel as polished as a premium
streaming application while remaining private, offline and lightweight.

## 2. Target users

Primary: - Android users with downloaded MP3/AAC/FLAC and other
supported audio files. - Users who organize music into folders. - Users
who want background playback without an internet connection. - Users
using older or lower-memory phones.

Secondary: - Students - Offline listeners - Users with large personal
music collections - Users who prefer local files over streaming

## 3. Core user journeys

### First launch

Launch Sonqiva → Explain local music access → Request appropriate media
permission → Discover available audio → Show Home immediately → Populate
library progressively → User can start playback as soon as a playable
item is available

Do not force the user to wait for a long full-library scan before seeing
the interface.

### Play a song

Home/Library/Folder/Search → Tap song → Immediate playback → Mini player
appears → User can expand full player

### Background playback

Start song → Press Home → Music continues → Android media notification
remains available → Lock screen controls work → Bluetooth/headset
controls work

### Folder playback

Folders → Open folder → Play All or Shuffle → Queue created from folder
contents → Playback continues in background

### Speed

Full Player → Playback Speed → Select 0.5x--3x → Player updates
immediately → Selected speed persists according to settings

## 4. MVP features

### Library

-   Songs
-   Albums
-   Artists
-   Folders
-   Search
-   Recently played

### Playback

-   Play/pause
-   Previous/next
-   Seek
-   Queue
-   Shuffle
-   Repeat
-   Background playback
-   Notification controls
-   Lock-screen controls
-   Bluetooth/headset controls
-   Playback speed 0.5x--3x

### Personalization

-   Favorites
-   Basic playlists
-   Theme
-   Playback settings

## 5. V1 features

-   Genres
-   Most played
-   Recently added
-   Folder exclusions
-   Sort/filter
-   Sleep timer
-   Resume playback
-   Metadata display
-   Add to queue / play next
-   Save queue as playlist
-   Compact mode
-   Low-memory mode

## 6. V1.5 features

-   Crossfade
-   Gapless playback
-   Audio normalization where technically appropriate
-   Lyrics from local metadata/files
-   Metadata editing
-   Share song/file
-   Android home-screen widget
-   Better empty states
-   Backup/restore app-specific library state

## 7. V2 / advanced features

Consider only after the core app is stable: - Equalizer integration -
Bass boost - Android Auto - Smart playlists - Folder watch/rescan
improvements - Duplicate music detection - Replay statistics - Custom
themes - Advanced queue management - Local lyrics synchronization -
Audio visualizer, only if it can be made optional and lightweight

## 8. Features deliberately excluded from the core

Do not add: - User accounts - Social feed - Music streaming - Cloud
music storage - Mandatory analytics - Server dependency - Ads in the
core architecture - AI recommendations requiring cloud processing

These would weaken the local-first identity.

## 9. Smart feature ideas

### Quick actions

-   Shuffle all
-   Resume last session
-   Recently played
-   Favorites
-   Downloads

### Long-press actions

Long press a song: - Play next - Add to queue - Add to playlist -
Favorite - View album - View artist - View folder - Share - Delete

### Context-aware Home

If no history exists: - Show Recently Added instead of Recently Played.

If there are no playlists: - Promote Create Playlist.

If the library is empty: - Show a clear import/access action.

### Resume experience

If the app is reopened after playback: - Show the previous song and
position. - Offer one-tap resume.

## 10. UX rules

Every primary action should have immediate visual feedback.

Playback actions should not depend on a slow database round trip.

Long operations should: - Run off the main thread. - Show progress only
when useful. - Allow the UI to remain usable.

Avoid unnecessary confirmation dialogs for reversible actions.

Destructive actions such as delete should require confirmation.

## 11. Accessibility

-   Minimum 48dp touch targets.
-   Readable typography.
-   High contrast.
-   Content descriptions for meaningful icons.
-   Selected states must be visually obvious.
-   Do not rely only on color.
-   Support long song and artist names.
