# 🎵 SONQIVA MUSIC

> **Native, Offline-First Android Music Player for Local Files**  
> *Atmospheric Glassmorphic UI • Near-Black OLED Theme • Zero Cloud Dependencies • Zero Trackers • Zero Ads*

---

## 🌟 Overview

**Sonqiva** is a high-performance, native Android music player crafted specifically for listening to local audio files (MP3, AAC, FLAC, WAV, OPUS). Built with modern Android technologies (Jetpack Compose, AndroidX Media3, Room, DataStore, and Coil), Sonqiva provides an immersive, distraction-free listening experience that stays fast and responsive even on budget devices (2GB–4GB RAM).

---

## 🚀 Key Features

- **🎨 Atmospheric Design System**:
  - Near-black OLED dark mode (`#08080A`), frosted glass card containers (`GlassCard`, `GlassSurface`), and 1px ambient border highlights.
  - Hanken Grotesk typography scale with high contrast and readable hierarchy.
  - Floating, non-intrusive `MiniPlayer` with quick pause/resume and swipe-up to full-screen player.

- **🎧 Native AndroidX Media3 Audio Engine**:
  - `SonqivaMediaSessionService` foreground service supporting seamless background playback, lock screen controls, and Bluetooth remote controls.
  - Automatic audio focus management and "becoming noisy" detection (pauses smoothly when headphones are unplugged).
  - Dynamic playback speed control (0.5x to 3.0x).
  - Sleep timer with configurable durations (15m, 30m, 45m, 60m).

- **📂 Complete Library Organization**:
  - **Songs**: Progressive MediaStore loading with instant offline search and 7 sorting modes.
  - **Albums**: Grid cards with cached artwork and dedicated `AlbumDetailScreen`.
  - **Artists**: Discography breakdown with `ArtistDetailScreen`.
  - **Folders**: Hierarchical physical storage browser with subfolder navigation and "Play Folder" action.
  - **Playlists**: Full Room-backed custom playlist creation, addition, and management.

- **⚡ Low-RAM & Performance Optimized**:
  - Tuned specifically for 2GB–4GB RAM devices (e.g. Realme 6 reference device).
  - Custom Coil `ImageLoader` capped to 15% max RAM and 64MB disk caching.
  - Efficient Room queries with Kotlin Coroutine Flows and background MediaStore pagination.

- **🎛️ Audio Utilities**:
  - System Equalizer launcher (`AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL`).
  - Track Properties inspector (MIME type, exact file size in MB, bitrate, file path, date added).
  - Dynamic queue manipulation (`Play Next`, `Add to Queue`).
  - Native Android Audio Sharing (`Intent.ACTION_SEND`).

---

## 🏗️ Architecture & Tech Stack

```text
app/src/main/java/com/anant/sonqiva/
├── SonqivaApp.kt                         # Application entrypoint & Coil RAM cache config
├── MainActivity.kt                       # Single activity Compose host
├── data/
│   ├── local/
│   │   ├── database/SonqivaDatabase.kt   # Room DB (Favorites, Playlists, PlaybackHistory)
│   │   ├── datastore/                    # DataStore preferences (Speed, Sort, Low-RAM mode)
│   │   └── mediastore/                   # MediaStore audio query engine
│   ├── model/                            # Domain models (Song, Album, Artist, Folder, PlaybackState)
│   └── repository/AudioRepository.kt     # Repository abstraction for MediaStore queries
├── player/
│   ├── controller/PlaybackController.kt  # MediaController connection & command dispatcher
│   └── service/SonqivaMediaSessionService.kt # Media3 MediaSessionService
└── ui/
    ├── theme/                            # Color, Type, Theme tokens
    ├── components/                       # Reusable atmospheric glass components & bottom sheets
    ├── home/                             # Home dashboard screen
    ├── library/                          # Library screen (Songs, Albums, Artists, Playlists)
    ├── albums/                           # AlbumDetailScreen
    ├── artists/                          # ArtistDetailScreen
    ├── playlists/                        # PlaylistDetailScreen & dialogs
    ├── folders/                          # Folders hierarchy screen
    ├── search/                           # Offline instant search screen
    ├── settings/                         # Settings & equalizer launcher
    ├── player/                           # FullPlayerScreen & sheets
    ├── navigation/                       # Bottom navigation & route destinations
    └── viewmodel/MainViewModel.kt        # Reactive state coordination
```

---

## 🛠️ Building & Running

### Prerequisites
- Android Studio Ladybug / Meerkat or IntelliJ IDEA
- Android SDK 36 (target SDK 36, min SDK 24)
- JDK 17+
- Gradle 9+ (Gradle Wrapper included)

### Commands
```bash
# Run pure JVM Unit Tests
./gradlew testDebugUnitTest

# Assemble Debug APK
./gradlew assembleDebug

# Install on connected device or emulator
./gradlew installDebug
```

---

## 🔒 Privacy & Offline Guarantee

Sonqiva strictly adheres to an **offline-only** architecture:
- ❌ No internet permissions required (`android.permission.INTERNET` is not requested).
- ❌ Zero network telemetry, analytics, or third-party ad SDKs.
- ❌ Zero cloud sync or authentication.
- ✅ 100% of user data and playlists are stored locally on-device in encrypted SQLite/Room.

---

## 📄 License
MIT License. Free and open source for local music lovers.
