# Sonqiva — Bug Report

Repository: `E:\Sonqiva` (branch `master`)

Severities:
- **CRITICAL** — blocks release/builds, or severe accessibility non-conformance (WCAG A)
- **SERIOUS** — significant functional/usability or WCAG AA conformance defect
- **MODERATE** — degraded UX, robustness, or maintainability
- **MINOR** — cosmetic / informational

---

## Web simulator (`web-preview/`)

| ID | Sev | File | Description | Root cause | Evidence | Status |
|----|-----|------|-------------|------------|----------|--------|
| SQ-BUG-001 | CRITICAL | `web-preview/app.js` (`createSongRow`) | Song-row overflow action buttons (`⋮`) exposed no accessible name to assistive tech | `aria-label` absent on `.song-action-btn` | axe: `aria-label` attribute = null on all action buttons | **Fixed** — `aria-label="More options for {title}"` added |
| SQ-BUG-002 | CRITICAL | `web-preview/index.html` | `#seek-slider` (range input) had no accessible name on every screen | Input rendered without `<label>`/`aria-label` | axe: `label` (critical), impact critical | **Fixed** — `aria-label="Seek position"` |
| SQ-BUG-003 | CRITICAL | `web-preview/index.html` (settings) | `#low-memory-toggle`, `#auto-resume-toggle` checkboxes had no accessible name | Text labels live in a sibling `<div>` outside the `<label class="switch">`, so they do not label the input | axe: `label` (critical), 2 nodes | **Fixed** — `aria-label` on both inputs |
| SQ-BUG-004 | SERIOUS | `web-preview/index.html` | `#home-albums-carousel` and library `.tab-bar-container` were keyboard-unreachable scrollable regions | `scrollable-region-focusable` rule: scrollable boxes without `tabindex` | axe: `scrollable-region-focusable` (serious), 2 nodes | **Fixed** — carousel: `role="region" tabindex="0" aria-label="Albums"`; tab bar: `role="tablist" tabindex="0"` |
| SQ-BUG-005 | SERIOUS | `web-preview/app.js` / `index.html` | `.tab-item`, `.filter-chip`, `.sheet-option` are clickable `<div>`s with no keyboard activation (Enter/Space) | Handlers bound to `click` only; no roles/tabindex | Manual keyboard audit; goes against WCAG 2.1.1 | **Fixed** — tabs: WAI-ARIA tablist (arrows/Home/End + Enter/Space, roving tabindex, `aria-selected`); chips: `role="button"` + `aria-pressed`; sheet options: `role="button"`, Enter/Space handled |
| SQ-BUG-006 | MODERATE | `web-preview/app.js` | Sheets/dialogs could not be dismissed with Escape and did not manage focus (no focus move-in, no restore on close) | No keydown handler; openers/`closeAllSheets` never set focus | Manual keyboard audit | **Fixed** — `openSheet()` focuses first focusable, `closeAllSheets()` restores focus, global Escape handler |
| SQ-BUG-007 | MODERATE | `web-preview/index.html` | `#search-input` and `#playlist-name-input` lacked programmatic labels (relied only on `placeholder`, which is not a name) | No `<label>`/`aria-label` | Ragged a11y; placeholder is not a substitute label | **Fixed** — `aria-label` added |
| SQ-BUG-008 | MODERATE | `web-preview/styles.css` | Preview mock (fixed 395×820 canvas) did not adapt to small viewports: nav bar / mini-player / content overlapped and intercepted clicks after page scroll, breaking mobile QA | Desktop-first fixed-frame layout, no responsive rules | Playwright mobile: `<div id="mini-player">`/`.song-row` intercepted nav clicks on every test | **Fixed** — `@media (max-width: 520px)` fill-mode: preview becomes a full-screen app (no chrome/scroll) |

### Test data/assertion issues (tests corrected — no product change)

| ID | Sev | File | Description | Fix |
|----|-----|------|-------------|-----|
| SQ-TEST-001 | MODERATE | `tests-web/playback.e2e.spec.ts` | Spec assumed `.song-row` `.first()` = "Starlight Echoes", but library sorts by title A–Z ("Cosmic Horizon" is first) → false failures | Click the row filtered by the track title (`hasText: "Starlight Echoes"`); assertions unchanged |
| SQ-TEST-002 | MODERATE | `tests-web/library.e2e.spec.ts` | "app shell" test asserted desktop header `<h1>Sonqiva</h1>`, which is intentionally hidden in mobile fill-mode | Assert `.bottom-nav` visibility (present in both layouts) |

---

## Android app (`app/`)

| ID | Sev | File | Description | Root cause | Evidence | Status |
|----|-----|------|-------------|------------|----------|--------|
| SQ-BUG-009 | CRITICAL | `app/src/main/res/values/themes.xml` | `android:windowLightNavigationBar` used while `minSdk` is 24 (attr requires API 27+) → `NewApi` lint error; would fail theme inflation / crash on API 24–26 | Attribute placed in base `values/` | `gradlew lintDebug`: `themes.xml:8: Error: requires API level 27 (current min is 24)` | **Fixed** — attribute moved to `values-v27/themes.xml` (base theme keeps only API-24-safe items) |
| SQ-BUG-010 | MINOR | `Song.kt:31,33`, `LibraryModels.kt:62,70,78`, `PlayerComponents.kt:201,203`, `SleepTimerBottomSheet.kt:82` | `String.format` without explicit `Locale` → time/duration strings depend on device locale (e.g. non-ASCII digits) | `DefaultLocale` code smell (8 hits) | `gradlew lintDebug` warnings | **Fixed** — `Locale.US` passed to all format calls + imports |
| SQ-BUG-011 | MINOR | `app/src/main/AndroidManifest.xml:31` | `android:label` on `MainActivity` duplicates application label (`RedundantLabel`) | Copy-paste label | `gradlew lintDebug` warning | **Fixed** — removed redundant activity label |

---

## Fixed under different scope (prior reporting) / not introduced this cycle

See `AI_PROJECT_CONTEXT.md → "Known Bugs / Limitations"` for pre-existing product gaps (cosmetic settings toggles, non-nested folders, fragile detail screens, destructive DB migration, non-persistent sleep timer, etc.). These are **known and accepted**, not regressions from this QA cycle.

## Open / intentionally not fixed (documented)

| ID | Sev | Area | Reason |
|----|-----|------|--------|
| SQ-OPEN-001 | NONE | `AndroidManifest.xml:41` `ExportedService` | Media3 `MediaSessionService` is intentionally exported so the system/media apps can control playback; adding a permission would break media-session integration. Standard Media3 pattern. |
| SQ-OPEN-002 | NONE | Version-bump warnings (AGP 9.4.0, compileSdk 37, Compose BOM 2026.08, Media3 1.11, etc.) | Informational; bumping major dependency versions is out of scope for a QA cycle and risks regressions. |
| SQ-OPEN-003 | NONE | `UnusedResources` (7 colors in `colors.xml`) | Deliberate design-token set (referenced from Compose theme in code); kept for maintainability. |
| SQ-OPEN-004 | NONE | `OldTargetApi` (targetSdk 36) | Intentional project decision. |
| SQ-OPEN-005 | NONE | `UseKtx` (5) | Cosmetic Kotlin-extension suggestions only. |