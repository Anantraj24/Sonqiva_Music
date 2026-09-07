# AI_PROJECT_CONTEXT — Sonqiva

> Maintained file for AI-assisted development. Read FIRST before any task. Update when architecture/features/dependencies/commands change. Never include secrets.

## Purpose & Features
**Sonqiva** = native, offline-first Android music player for local files on low-end devices (targets 2–4 GB RAM, reference: Realme 6/Android 11). Premium "glassmorphic" near-black OLED UI. Zero network, zero trackers, zero ads, no login.

Major features:
- MediaStore library scan (progressive 50-song batch emission) with Songs / Albums / Artists / Folders / Search / Playlists / Favorites / Recently played (history)
- Media3/ExoPlayer background playback via `MediaSessionService` (notification, lock screen, Bluetooth/headset, audio focus, becoming-noisy pause)
- Playback: play/pause, next/prev, seek, queue (play next/add to queue), shuffle, repeat (OFF→ALL→ONE), speed 0.5x–3.0x, sleep timer (in-memory)
- Room-backed favorites, playlists, playback history; DataStore preferences
- System equalizer launcher (`AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL`), track-info inspector (MIME, size, bitrate-derived, path, date added)
- Coil artwork tuned for low RAM (15% memory cap, 64MB disk cache)
- `web-preview/` = standalone web simulator/showcase (vanilla JS + Web Audio synth), NOT part of Android app; served locally by `web-preview/serve.mjs` (port 3210)
- `tests-web/` = Playwright E2E + axe WCAG 2.2 AA suites (library/nav, playback, accessibility) run against `web-preview/` via `playwright.config.mjs` (desktop + mobile projects)

## Tech Stack & Versions (see `gradle/libs.versions.toml`)
- Kotlin 2.2.10, AGP 9.3.2, Gradle 9.5.0 (wrapper), KSP 2.2.10-2.0.2
- Jetpack Compose BOM 2024.12.01, Material3, material-icons-extended
- Media3 1.5.1 (exoplayer, session, ui, common), Room 2.6.1, DataStore 1.1.2, Coil 2.7.0, Navigation Compose 2.8.5, Lifecycle 2.8.7, Activity Compose 1.10.0
- JDK 17, compileSdk 36, minSdk 24, targetSdk 36, Java/Kotlin 17 target
- Java unit tests: JUnit 4.13.2; instrumentation: androidx.test 1.2.1, Espresso 3.6.1
- Single module `:app`; package `com.anant.sonqiva`; root node `package.json` devDeps: `@playwright/test`, `@axe-core/playwright`, `axe-core` (web-preview QA tooling)

## Folder Structure (key paths under `app/src/main/java/com/anant/sonqiva/`)
- `SonqivaApp.kt` — Application; Coil `ImageLoaderFactory` (low-RAM cache config)
- `MainActivity.kt` — single-activity Compose host; wires ViewModel → `SonqivaAppShell`
- `data/`
  - `local/database/SonqivaDatabase.kt` — Room DB (entities + DAOs inline)
  - `local/datastore/UserPreferencesRepository.kt` — DataStore `sonqiva_preferences`
  - `local/mediastore/MediaStoreAudioDataSource.kt` — MediaStore audio query engine
  - `model/` — Song, LibraryModels (Album/Artist/FolderItem/PlaybackState/RepeatMode), SortOrder enums, MockData (sample data)
  - `repository/AudioRepository.kt` — derives Albums/Artists/Folders from Song list
- `player/`
  - `controller/PlaybackController.kt` — MediaController lifecycle, queue mgmt, playback state flow, sleep timer
  - `service/SonqivaMediaSessionService.kt` — ExoPlayer + MediaSession + notification channel
- `ui/` — `theme/` (dark tokens), `components/` (Glass*, SongRow, sheets, PermissionHandler, mini/full player pieces), `navigation/` (Screen routes, BottomNavBar, AppNavigation shell), screens: `home/ library/ albums/ artists/ playlists/ folders/ search/ settings/ player/`, `viewmodel/MainViewModel.kt`
- `Sonqiva_Antigravity_Docs/` — product/architecture/roadmap/backlog/UI guides (source of truth for intent)
- `gradle/libs.versions.toml` — central version catalog (add deps here first)

## Architecture & Data Flow
```
Compose UI ←(StateFlows/collectAsState)→ MainViewModel → AudioRepository / MediaStoreAudioDataSource (Flow<List<Song>>, Dispatchers.IO)
                                          → Room DB (favorites, playlists, history)
                                          → PlaybackController (MediaController) → Media3 ExoPlayer ← MediaSessionService
                                          → UserPreferencesRepository (DataStore)
```
- Single `MainViewModel` (AndroidViewModel) creates Room DB (`fallbackToDestructiveMigration`), repository, prefs, `PlaybackController`
- `songs` StateFlow = combine(rawScan, favoriteIds, sortOrder) — sorted in memory; `_rawScannedSongs` populated by `loadAudioLibrary()`
- Library scan emits progressively (chunks of 50) so UI responds before scan completes
- Playback state is a single authoritative `PlaybackState` data class in `PlaybackController`; polled position updates every 400ms via coroutine while playing
- Song ↔ MediaItem mapping in `PlaybackController` via `mediaId` = song.id and metadata extras (albumId, folderPath, mimeType, etc.)
- Navigation: bottom bar (Home/Library/Folders/Search/Settings) + detail routes `album_detail/{albumId}`, `artist_detail/{artistId}`, `playlist_detail/{playlistId}`; MiniPlayer floats above bottom bar; FullPlayer = AnimatedVisibility overlay

## Database (Room, file `sonqiva_database`, version 2, exportSchema=false)
| Table | Columns | Purpose |
|---|---|---|
| `favorites` | **songId** PK, addedAt | favorite song ids (ids reference MediaStore) |
| `playlists` | **id** auto PK, name, createdAt | custom playlists |
| `playlist_songs` | **playlistId+songId** composite PK, addedAt | playlist membership |
| `history` | **songId** PK, lastPlayedTimestamp, playCount, lastPositionMs | playback history (last 50 queried) |

DAOs: `FavoriteDao` (REPLACE insert), `PlaylistDao` (insert/delete/addSong/removeSong/getSongIds), `HistoryDao` (upsert by REPLACE, get recent/last). No FK constraints; playlist songs referencing deleted MediaStore tracks simply won't match scanned songs.

## "API" Endpoints (all local — no network)
- **MediaStore**: `MediaStore.Audio.Media` external volume; projection incl. `DATA` path; selection `IS_MUSIC != 0 AND DURATION >= 10000`; album art uri `content://media/external/audio/albumart/{albumId}`
- **Room DAOs** above; **DataStore** keys: `low_memory_mode`, `playback_speed`, `auto_resume`, `last_played_song_id`, `last_played_position`, `song_sort_order`, `album_sort_order`
- **System intents**: system equalizer panel, audio share (`Intent.ACTION_SEND`)
- No REST/GraphQL/backend anywhere.

## Authentication / Authorization
- **None.** No accounts, no INTERNET permission, no login.
- Runtime permissions (Android 13+): `READ_MEDIA_AUDIO`, `POST_NOTIFICATIONS`; pre-13: `READ_EXTERNAL_STORAGE` (maxSdk 32). Handled by `AudioPermissionHandler` before content shows; `MainActivity` triggers library load on grant.
- Manifest declares `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `WAKE_LOCK`; foreground service type `mediaPlayback`, exported, with `MediaSessionService` + `MediaBrowserService` actions.

## Key User Flows
1. **First launch**: permission prompt → `loadAudioLibrary()` → Home appears immediately, library populates progressively
2. **Play a song**: tap song anywhere → `playSong(song, queue)` builds MediaItems, sets queue/position, prepare+play; records history; MiniPlayer appears; tap → FullPlayer overlay
3. **Background playback**: MediaSessionService keeps playing; notification (channel `sonqiva_playback_channel`, id 1001) with controls; audio-focus & becoming-noisy auto-pause; `START_STICKY`
4. **Shuffle All**: shuffles all songs, plays + toggles shuffle mode
5. **Folder playback**: Folders → open → Play Folder/Shuffle Folder (`folder.songs` becomes queue)
6. **Playlist**: create (dialog), add song (AddToPlaylistBottomSheet), play all/shuffle, delete
7. **Speed/sleep**: FullPlayer speed sheet (persisted) & sleep timer bottom sheet (15/30/45/60/90 min, live countdown, pauses at 0)

## Environment Variables
- **None.** No env vars, no secrets, no `BuildConfig` fields, no network config. `local.properties` only holds `sdk.dir` (machine-local, gitignored). Do not introduce secrets.

## Tests & Commands
- Unit tests (JVM): `app/src/test/…/ExampleUnitTest.kt` (1) and `SonqivaCoreUnitTest.kt` (9) — duration/progress/sleep-timer formatting, nav routes, repeat cycle, models, sorting, search filtering. **10 total, all pass**.
- Instrumented: `app/src/androidTest/…/ExampleInstrumentedTest.kt` (package-name check) — run on device/emulator
- Commands (Windows: `.\gradlew.bat`):
  - `.\gradlew.bat testDebugUnitTest` — JVM tests
  - `.\gradlew.bat lintDebug` / `lint` — Android lint (HTTP: 0 errors; remaining warnings = version-bump suggestions, unused design-token colors, UseKtx, OldTargetApi, intentional exported MediaSessionService)
  - `.\gradlew.bat assembleDebug` / `installDebug`
  - `.\gradlew.bat connectedAndroidTest` — instrumented (needs device)
- Web E2E (node): `npx playwright test` — runs `tests-web/` desktop + mobile projects against `web-preview/serve.mjs` (46 tests, all green). Port 3210; do NOT run a manual server the webServer will start its own.
- Configured Gradle flags: `org.gradle.configuration-cache=true`, jvmargs `-Xmx2048m`, `kotlin.code.style=official`

## Known Bugs / Limitations
- **Settings toggles are cosmetic**: Low-Memory Mode & Auto Resume switches in `SettingsScreen` use local `remember` state only — not wired to `UserPreferencesRepository` or behavior. `autoResume` prefs and `lastPlayedSongId`/`lastPlayedPosition` exist but auto-resume is NOT implemented.
- **Favorites/History surfacing**: favorites tab exists in Library; history is recorded to Room but Home's "Continue Listening" card uses `playbackState.currentSong ?: songs.firstOrNull()` and "Recently Added Songs" is just `songs.take(10)` — neither reads the history DB.
- **Folders not truly nested**: MediaStore source captures only the immediate parent (`File(data).parent`); `FolderItem.subFolders`/`subFolderCount` never populated; folder navigation is single-level. Genres not implemented.
- **Artist IDs unstable**: `AudioRepository.getArtists` assigns `id = index.toLong()` — not stable across scans; detail-screen navigation relies on it matching current in-memory list.
- **Detail screens fragile**: `AlbumDetailScreen`/`ArtistDetailScreen`/`PlaylistDetailScreen` render nothing if the entity isn't found in the passed list (no empty/error state).
- **Database migration**: `fallbackToDestructiveMigration()` wipes favorites/playlists/history on version bump.
- **Release build**: `isMinifyEnabled = false` — no R8; proguard-rules.pro present but unused until enabled.
- **Sleep timer**: coroutine in `PlaybackController` — lost on process death / service restart; not persisted.
- **Playback speed persistence**: `setPlaybackSpeed` writes speed; applied via `observeStoredPreferences`; OK, but no UI to set it in Settings (only in FullPlayer).
- **`LocalActivityManager`/launcher**: none used; single activity Compose.
- **Web preview persists state** in `localStorage` (song/playlist data only); independent of Android app.

## Important Dependencies / Config Notes
- Version catalog is single source: add libs to `gradle/libs.versions.toml`, reference via `libs.*` aliases in `app/build.gradle.kts`
- `testOptions.unitTests.isReturnDefaultValues = true` (default returns for Android stubs)
- Coil tuned in `SonqivaApp.newImageLoader()` (15% RAM, 64MB disk, crossfade, no cache-header respect)
- Docs in `Sonqiva_Antigravity_Docs/` define non-negotiables: Kotlin/Compose/Media3/Room/DataStore/offline-only, minimal deps, live scan off main thread, single `ExoPlayer` instance, build incrementally. Respect these when changing code.
- Git: branch `master`; conventional-commit style log (`feat:`/`fix:`/`chore:`/`perf:`/`test:`). `.agents/skills/` contains QA/Playwright/Selenium skills used for testing workflows.