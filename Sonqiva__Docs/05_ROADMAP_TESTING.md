# SONQIVA --- Development Roadmap, Testing and Antigravity Workflow

## Phase 0 --- Understand before coding

Tasks: - Read all Sonqiva docs. - Inspect Stitch design ID
`2980046674713411607`. - Inspect current Android project. - Confirm
package name and build setup. - Identify existing generated code. -
Create a clean baseline commit.

Do not implement features during this phase.

## Phase 1 --- Foundation

Set up: - Kotlin - Compose - Material 3 - Navigation - Theme -
Dependency management - Basic package structure

Deliverable: A clean app shell with navigation.

## Phase 2 --- UI shell

Implement Stitch-inspired: - Home - Library - Songs - Folders - Search -
Mini player placeholder - Full player placeholder - Settings

Use fake data.

Deliverable: A complete clickable frontend with no real playback yet.

## Phase 3 --- Real media library

Implement: - Permissions - MediaStore - Song discovery - Album/artist
metadata - Folder representation - Progressive loading

Deliverable: Real local music appears.

## Phase 4 --- Playback engine

Implement: - Media3/ExoPlayer - Play/pause - Previous/next - Seek -
Queue - Shuffle - Repeat

Deliverable: Reliable local playback.

## Phase 5 --- Background playback

Implement: - MediaSessionService - Media notification - Lock screen -
Bluetooth/headset controls - Audio focus - Interruption handling

Deliverable: Music continues outside the app.

This phase is mandatory before visual polish.

## Phase 6 --- Player UI

Connect real player state to: - Mini player - Full player - Seek bar -
Queue - Speed sheet - Favorite action

Deliverable: Complete playback experience.

## Phase 7 --- Persistence

Add Room: - Favorites - Playlists - History - Play counts - Custom
ordering

Add DataStore: - Settings - Playback preferences

## Phase 8 --- Folder experience

Implement: - Nested folders - Folder playback - Folder shuffle - Add
folder contents to playlist - Folder exclusions

## Phase 9 --- Search

Implement local search.

Requirements: - Fast - Debounced appropriately - No network - Search
songs/artists/albums/folders/playlists

For very large libraries, consider indexed database queries rather than
filtering a huge list on every keystroke.

## Phase 10 --- UX polish

Add: - Transitions - Micro-interactions - Better loading states - Empty
states - Error states - Haptics only where useful - Accessibility

## Phase 11 --- Performance

Measure: - Cold start - Warm start - Memory - CPU - Frame rendering -
Scrolling - Library scan - Artwork loading - Playback start latency

Test on: - 2 GB RAM Android phone - 3 GB RAM Android phone - 4 GB RAM
Realme 6 / similar - Android 11 reference device

Test libraries with: - 100 songs - 500 songs - 1,000 songs - 5,000+
songs

## Performance requirements

Target: - Fast perceived launch - Immediate playback control response -
Smooth scrolling - No blocking scans - Stable background playback -
Controlled artwork memory usage

Do not promise exact milliseconds unless measured.

## Performance tactics

-   LazyColumn/LazyVerticalGrid
-   Stable keys
-   Stable state
-   Background work
-   Incremental library updates
-   Thumbnail artwork
-   Image caching
-   Avoid unnecessary recomposition
-   Avoid unnecessary allocations
-   Release build testing
-   R8
-   Baseline Profile

## Phase 12 --- Reliability testing

Test: - File deleted while playing - File moved - File renamed - Corrupt
file - Unsupported file - No artwork - Permission denied - Permission
revoked - Phone call - Bluetooth connect/disconnect - Headphones
connect/disconnect - Screen lock - App minimized - App reopened -
Process killed - Device reboot - Very long song - Very large queue

## Phase 13 --- Release preparation

Checklist: - App icon - Splash screen - Versioning - Release build -
R8 - Baseline Profile - Privacy documentation - Permission
explanations - Crash/error review - Accessibility review - Low-end
performance test - Battery behavior test - Play Store assets if
publishing

## Antigravity workflow

For every implementation task:

1.  Inspect existing code first.
2.  Explain what will change.
3.  Identify dependencies.
4.  Implement the smallest coherent change.
5.  Build.
6.  Run tests/checks.
7.  Fix errors.
8.  Review performance.
9.  Summarize changed files.
10. Create a focused Git commit.

Do not rewrite working modules unnecessarily.

## Commit strategy

Use meaningful commits such as:

-   `chore: initialize sonqiva architecture`
-   `feat: add media store music discovery`
-   `feat: add media3 playback`
-   `feat: add background playback service`
-   `feat: add mini player`
-   `feat: add full player`
-   `feat: add folder browser`
-   `feat: add playlists and favorites`
-   `perf: optimize artwork loading`
-   `perf: reduce compose recomposition`
-   `fix: handle audio focus interruption`

## Critical Antigravity rule

Never respond to a feature request by blindly changing unrelated files.

Before editing: - Inspect. - Understand. - Plan. - Implement. -
Validate.

The project should remain understandable to a human developer.

## Definition of done

A feature is not complete merely because it compiles.

It is complete when: - It works on a real device. - UI state is
correct. - Errors are handled. - Background behavior is correct where
relevant. - Performance is acceptable. - Accessibility is reasonable. -
No unrelated functionality is broken.
