# SONQIVA --- Technical Architecture

## 1. Architecture goal

Build a native Android application with a clear separation between UI,
domain logic, data and playback.

Do not over-engineer.

Recommended high-level structure:

UI → ViewModel → Use Case → Repository → Data Source

Playback is a specialized subsystem and should have one source of truth.

## 2. High-level architecture

Compose UI → ViewModels → Domain/use cases → Repositories → MediaStore /
Room / DataStore

Playback: Compose UI → Playback Controller → Media3 ExoPlayer →
MediaSessionService → Android media controls

## 3. Suggested modules/packages

app/ - data/ - local/ - database/ - dao/ - media/ -
MediaStoreDataSource - repository/ - domain/ - model/ - repository/ -
usecase/ - player/ - PlayerManager - PlaybackController -
MediaSessionService - QueueManager - ui/ - home/ - library/ - songs/ -
albums/ - artists/ - folders/ - playlists/ - favorites/ - search/ -
player/ - settings/ - components/ - navigation/ - MainActivity

The exact package structure can be adjusted if the codebase remains
understandable.

## 4. MediaStore strategy

MediaStore should be the primary source for device-indexed audio.

Store useful media properties such as: - Media ID - Content URI -
Display name - Title - Artist - Album - Album ID - Duration - Date
added - Relative path where available - MIME type

Do not repeatedly rescan the entire storage for every screen.

Cache/transform data where useful.

## 5. Room strategy

Room stores app-owned state, not a needless duplicate of every
MediaStore field.

Good Room data: - Favorites - Play counts - Last played time - Playlist
membership - Custom ordering - App-specific metadata - User preferences
that need relational storage

When media disappears from the device, handle stale records gracefully.

## 6. DataStore

Use DataStore for lightweight preferences: - Theme - Default playback
speed - Repeat mode - Shuffle preference - Compact mode - Low-memory
mode - Animation preference - Resume playback preference

## 7. Playback subsystem

Use Media3 / ExoPlayer.

Use a MediaSessionService for background playback.

The player should own: - Current media item - Current position -
Duration - Is playing - Playback speed - Repeat mode - Shuffle - Queue

Expose a stable playback state to the UI.

Do not let each screen instantiate its own ExoPlayer.

## 8. Background playback

Required behavior: - App minimized: playback continues. - Screen locked:
playback continues. - Notification: controls remain available. -
Bluetooth: media controls work. - Wired headset: media controls work
where supported. - Audio focus interruption: handle appropriately. -
Phone call interruption: pause/duck/resume according to Android audio
behavior. - App process recreation: recover state where practical.

The service should be lightweight and dedicated to playback.

## 9. Queue architecture

Represent queue as an ordered list of media items.

Operations: - Play item - Add to queue - Play next - Remove -
Move/reorder - Clear - Shuffle - Repeat - Save queue as playlist

Avoid making queue changes trigger unnecessary full-library reloads.

## 10. Folder architecture

Prefer Android-supported media/storage APIs.

Folder UI should be derived from indexed media information where
possible.

A folder model can contain: - Display name - Identifier/path
representation - Parent - Child folders - Song count

Do not assume unrestricted filesystem access on modern Android.

## 11. UI state

Prefer immutable UI state objects.

Example conceptual state:

PlaybackUiState: - currentSong - isPlaying - position - duration -
speed - shuffle - repeat - queue

LibraryUiState: - songs - loading - error - sort - filter - search query

Do not put database access or MediaStore queries directly inside
Composables.

## 12. Threading

Main thread: - UI - lightweight state updates - user interactions

Background: - MediaStore queries - metadata processing - database
operations - artwork processing - library synchronization

Never perform a large scan or database migration on the main thread.

## 13. Error handling

Handle: - Permission denied - File deleted while queued - Unsupported
media - Corrupt media - Missing artwork - MediaStore changes - Storage
unavailable - Player errors - Bluetooth disconnect - Audio focus loss

Errors should be recoverable and user-readable.

Do not show stack traces to users.

## 14. Dependency discipline

Every dependency must justify itself.

Prefer AndroidX/platform functionality.

Do not add a library just to solve a small problem that can be handled
with Kotlin or AndroidX.

## 15. Security/privacy

Core playback should require no account.

Do not upload local music or metadata.

Avoid unnecessary permissions.

Do not collect unnecessary personal data.

## 16. Build configuration

Use Kotlin DSL.

Prepare: - Debug build - Release build - R8/minification - Baseline
Profile - Proper versioning

Real performance testing must use a release-like build.
