# SONQIVA --- ANTIGRAVITY START HERE

You are working on **Sonqiva**, a native Android offline music player.

Before changing code, read every file in this documentation folder.

**Stitch frontend reference ID:** `2980046674713411607`

The Stitch design is the visual source of truth. The product documents
define behavior and the architecture document defines the technical
direction.

## Non-negotiables

-   Kotlin
-   Jetpack Compose
-   Material 3
-   Media3 / ExoPlayer
-   MediaSessionService for background playback
-   MediaStore for local music discovery
-   Room for app-owned relational state
-   DataStore for preferences
-   Offline-first
-   No backend required for core functionality
-   No streaming service
-   No login
-   No network dependency for playback
-   Must remain responsive on 2--4 GB RAM Android devices
-   Reference device: Realme 6, Android 11

## Product priorities

1.  Reliable playback
2.  Background playback
3.  Instant-feeling controls
4.  Low memory usage
5.  Smooth scrolling
6.  Excellent UX
7.  Premium visual polish
8.  Additional features

## Important behavior

Music must continue when: - App is minimized - Screen is locked -
Another app is opened

Media controls must work through: - Notification - Lock screen -
Bluetooth/headset controls where supported

Playback speed: - 0.5x to 3.0x

Music organization: - Songs - Albums - Artists - Genres - Folders -
Nested folders - Playlists - Favorites - Recently played - Recently
added - Most played

## Performance rules

Never: - Scan a large library on the main thread. - Query Room directly
from Composables. - Create multiple ExoPlayer instances for different
screens. - Load full-resolution album art into long lists. - Re-render
the entire library after every small change. - Add heavy animations that
run continuously. - Add unnecessary dependencies. - Block startup while
waiting for the full library scan.

## Development process

Work in phases.

Do not build the entire application in one pass.

For every task: 1. Inspect the existing implementation. 2. Compare it
with the docs and Stitch design. 3. State the implementation plan
briefly. 4. Make the smallest correct change. 5. Build/test. 6. Fix
errors. 7. Check performance implications. 8. Report what changed.

If a requested feature conflicts with performance or playback
reliability, prioritize reliability and propose a lighter
implementation.

Do not invent backend APIs.

Do not replace working architecture merely to make the code shorter.

The goal is a production-quality Android app, not a generated demo.
