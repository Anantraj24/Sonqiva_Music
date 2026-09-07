# SONQIVA --- Antigravity Project Documentation

## Purpose

Sonqiva is a premium, offline-first Android local music player designed
to combine a beautiful modern interface with extremely fast and reliable
playback on low-end Android devices.

**Stitch frontend reference ID:** `2980046674713411607`

The Stitch designs are the visual source of truth for the frontend.
Antigravity should study the Stitch design and implement its UX
faithfully while improving it where necessary for Android usability and
performance.

## Core promise

> Your music. Your device. Your way.

Sonqiva should: - Discover music stored locally on the phone. - Play
music smoothly in the foreground and background. - Continue playback
when the app is minimized or the screen is locked. - Support folders and
nested folders. - Support playback speed from 0.5x to 3x. - Provide
playlists, favorites, history, search, queue and library organization. -
Feel premium without becoming heavy. - Work well on 2 GB, 3 GB and 4 GB
RAM Android phones.

## Primary target

Reference device: - Realme 6 - Android 11 - 4 GB RAM

Performance target: - Older/low-end Android devices - Android 7.0+ where
technically practical

## Technology direction

Use native Android: - Kotlin - Jetpack Compose - Material 3 - AndroidX
Media3 / ExoPlayer - MediaSessionService - MediaStore - Room -
DataStore - Hilt only where useful - Navigation Compose - Coil for
artwork

Avoid Flutter, WebView, Firebase and a backend for the core product.

## Non-negotiable principles

1.  Playback reliability before visual effects.
2.  Instant-feeling controls.
3.  Never block the main thread.
4.  Never scan the whole music library on the UI thread.
5.  Do not duplicate the playback state across screens.
6.  Do not load full-resolution artwork into scrolling lists.
7.  Keep dependencies limited.
8.  Build and test incrementally.
9.  Use release builds for real performance testing.
10. Do not generate the entire application in one giant step.

## Recommended implementation order

1.  Project foundation
2.  Design system and navigation shell
3.  MediaStore discovery
4.  Basic Media3 playback
5.  Background playback and notification
6.  Mini player
7.  Full player
8.  Queue
9.  Folders
10. Room-backed favorites/history/playlists
11. Search
12. Speed control
13. Settings
14. Animations and polish
15. Performance optimization
16. Testing and release

## How Antigravity should use these documents

Read all documents before making architectural decisions.

Do not blindly implement every possible feature. Separate: - MVP - V1 -
V1.5 - V2 - Experimental features

If a feature conflicts with low-memory performance, reliability wins.

If Stitch and an implementation detail conflict, preserve the visual
intent but adapt the implementation to native Android constraints.

## Product identity

Name: **Sonqiva**

Tone: - Premium - Minimal - Modern - Calm - Technical without looking
complicated

Avoid making Sonqiva look like a Spotify clone.
