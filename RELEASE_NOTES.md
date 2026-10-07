# 🎵 Sonqiva — Version 2.0.0

Sonqiva v2.0.0 is a complete redesign and feature overhaul of the original stable release. It delivers a more immersive, polished listening experience with a new atmospheric design system, a more capable audio engine, and a fully expanded library — while staying fast, offline, and lightweight.

---

## ✨ What's New

- Atmospheric glassmorphic dark UI with near-black OLED theme (`#08080A`)
- Frosted glass card containers (`GlassCard`, `GlassSurface`) with 1px ambient border highlights
- Hanken Grotesk typography replacing the default font stack
- Floating `MiniPlayer` with swipe-up to full-screen player
- **Folder Visibility Filter** — hide unwanted audio folders (ringtones, notifications) from your library via Settings
- **Favorites tab** — persistent one-tap favorites with its own library section
- **Multi-category Search** — search across Songs, Albums, and Artists with category chips
- **Track Properties inspector** — view MIME type, file size, bitrate, file path, and date added
- **Sleep timer** with live countdown badge (15m / 30m / 45m / 60m)
- **Dynamic playback speed** (0.5× to 3.0×)
- **Play Next** and **Add to Queue** dynamic queue management
- Native **Audio Sharing** via system share sheet
- **System Equalizer** launcher
- Dedicated **Album Detail** and **Artist Detail** screens
- **Playlist management** fully backed by Room database

---

## 🎧 Features

- Play music stored locally on your device
- Smooth audio playback with AndroidX Media3 / ExoPlayer
- Background playback via foreground service
- Lock screen and notification media controls
- Bluetooth and headset media controls
- Automatic audio focus — pauses on headphone unplug
- Browse music by Songs, Albums, Artists, Folders, and Playlists
- Hierarchical folder browser with subfolder navigation and Play Folder action
- Fast local music search with offline instant results
- Favorites tab
- Custom playlists with Room persistence
- Queue management — Play Next and Add to Queue
- Playback speed from 0.5× to 3.0×
- Shuffle and repeat modes
- Sleep timer with configurable duration
- Recently played history
- Track Properties inspector
- System Equalizer launcher
- Dark premium OLED interface
- Smooth and subtle animations
- Optimized for low-end Android devices

---

## ⚡ Performance

Sonqiva v2.0 is designed with performance as a priority.

Target devices include:

- Android 7.0+
- 2 GB RAM devices
- 3 GB RAM devices
- 4 GB RAM devices
- Android 11 devices such as the Realme 6

The application uses a native Android architecture focused on fast interactions, efficient memory usage, and reliable background playback.

New in v2.0:
- Custom Coil `ImageLoader` capped at 15% max heap + 64 MB disk cache
- Background MediaStore pagination with Kotlin Coroutine Flows
- Progressive library loading — UI is responsive before the full scan completes

---

## 🔧 Technology

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Media3 / ExoPlayer
- MediaSessionService
- MediaStore
- Room
- DataStore
- Coil

---

## 🔒 Privacy

Sonqiva is designed as an offline-first application. Core music playback does not require an account, streaming service, or backend.

- No internet permission — the app cannot make network requests
- Zero telemetry, analytics, or ad SDKs
- All playlists and preferences are stored locally on-device

---

## 🚀 Release Status

This is the v2.0 stable release of Sonqiva — a complete overhaul from v1.0.0.

Further improvements including gapless playback and a home screen widget are planned for future versions.

---

Sonqiva — Your music. Your way.

**Full Changelog:** https://github.com/Anantraj24/Sonqiva/compare/v1.0.0...v2.0.0
