# QA Report

**Project:** Sonqiva — Atmospheric Offline Music Player (Android + web-preview simulator)
**Cycle:** Full automated QA sweep · **Date:** 2026-09-08
**Environment:** Windows PowerShell 5.1 · Node v26.4.0 · Playwright 1.63.0 · axe-core 4.13.0 · Gradle 9.5.0 · AGP 9.3.2 · JDK 17 · Kotlin 2.2.10

---

## 1. Objective

Run a complete automated QA cycle using the repo's `.agents/skills/` QA tooling across every layer of the project:
1. Android JVM unit tests
2. Android lint + debug build
3. Web-preview E2E flows (library/navigation, playback)
4. Web-preview accessibility (axe — WCAG 2.2 A/AA)
5. Security/dependency checks

Fix every critical/serious finding at root cause and regression-test the whole suite. Full resolving details in `BUG_REPORT.md`.

## 2. Scope & Coverage

| Layer | What was exercised | Status |
|-------|--------------------|--------|
| Android unit logic | Duration/progress/sleep-timer formatting, nav routes, repeat cycle, models, sorting, search filtering | 10/10 pass |
| Android static analysis | `gradlew lintDebug` (All issues incl. NewApi, DefaultLocale, UnusedResources, etc.) | 0 errors, 33 warnings |
| Android build | `gradlew assembleDebug` (with configuration cache) | BUILD SUCCESSFUL |
| Web E2E — desktop (Chrome) | Nav across 5 screens, library counters/tabs/favorites/sort, playlist dialog, search, playback (play, shuffle, pause, 1.5× speed, repeat, sleep timer) | 23/23 pass |
| Web E2E — mobile (Pixel 5) | Same full suite under mobile emulation | 23/23 pass |
| Web a11y — axe WCAG 2.2 A/AA | `home`, `library`, `folders`, `search`, `settings` screens + song-row action-button names | 10/10 pass |
| Web a11y — manual/keyboard assertions | Tab-list arrows + Enter, song-row keyboard play, sheet-option Enter, Escape + focus restore | covered by 4 added tests |
| Dependency security | `npm audit` (node/web tooling) | 0 vulnerabilities |
| Dependency security | Gradle (Android) | No OWASP plugin configured — manual dependency review: all deps are pinned in `gradle/libs.versions.toml`; no newer-severity advisories surfaced by `GradleDependency` lint reviews |

**Total automated tests:** 10 (JVM) + 46 (Playwright: 23×2 viewports) — **all passing at cycle end.**

## 3. Commands run (all root)

```powershell
.\gradlew.bat testDebugUnitTest lint assembleDebug   # Android: unit + lint + build
npx playwright test                                    # web E2E + a11y (desktop + mobile, 46 tests)
npm audit                                              # web dependency security
```

## 4. Results by area

### 4.1 Android — pass
- `testDebugUnitTest`: **10 passed** (`ExampleUnitTest` 1 + `SonqivaCoreUnitTest` 9).
- `lintDebug`: first run **1 error, 42 warnings** → after fixes **0 errors, 33 warnings** (remaining = version-bump suggestions, unused design-token colors, `UseKtx`, `OldTargetApi`, intentional exported MediaSessionService — see `BUG_REPORT.md` SQ-OPEN-*).
- `assembleDebug`: **BUILD SUCCESSFUL** (54 tasks, 27 executed / 27 up-to-date, config cache reused).

### 4.2 Web E2E — pass (46/46)
- Desktop project: **23/23**
- Mobile project (Pixel 5): **23/23**
- Earlier blockers: two test-side data/assertion bugs (SQ-TEST-001/002) fixed; the mobile viewport layout bug (SQ-BUG-008) fixed at source in CSS.

### 4.3 Accessibility (axe) — pass after fixes

Baseline violations captured with `@axe-core/playwright` (WCAG 2.2 A/AA tags) before fixes:

| Screen | Before (violations) | After |
|--------|--------------------|-------|
| home | `label` (crit) ×1 — `#seek-slider`; `scrollable-region-focusable` (serious) ×1 — `#home-albums-carousel` | 0 |
| library | `label` (crit) — `#seek-slider`; `scrollable-region-focusable` (serious) — `.tab-bar-container` | 0 |
| folders | `label` (crit) — `#seek-slider` | 0 |
| search | `label` (crit) — `#seek-slider` | 0 |
| settings | `label` (crit) ×3 — `#seek-slider`, `#low-memory-toggle`, `#auto-resume-toggle` | 0 |
| song rows | `.song-action-btn` accessible name = null on all rows | all named |

`nested-interactive` conversation during fixes: an initial attempt to make song rows `role="button"` was reverted (axe `nested-interactive`) in favor of keyboard-operable rows with `role="group"` + Enter activation + labeled inner action buttons.

### 4.4 Keyboard operability & focus management (new regression tests)
- Tab arrows/Home/End navigate tabs; Enter/Space activates; `aria-selected` + roving `tabindex` correct.
- Song rows playable from keyboard (Enter).
- Sheet options activate via Enter.
- Escape closes any open sheet/modal and restores focus to the trigger element.

## 5. Defects found & disposition

- **11 product bugs fixed** (web: 8; Android: 3) — see `BUG_REPORT.md`.
- **2 test-side defects fixed** (SQ-TEST-001/002, test correctness only).
- **5 intentional/non-blocking findings documented** (SQ-OPEN-001…005).

## 6. Evidence artifacts

| Artifact | Path |
|----------|------|
| JUnit (Playwright XML) | `web-preview/.playwright-results/junit.xml` |
| HTML report (Playwright) | `web-preview/.playwright-report/index.html` |
| Android lint text report | `app/build/intermediates/lint_intermediate_text_report/debug/lintReportDebug/lint-results-debug.txt` |
| Android lint HTML report | `app/build/reports/lint-results-debug.html` |
| Crashing-test screenshots/videos | `test-results/` (cleaned on passing runs) |

## 7. Remaining known issues (accepted)

- Pre-existing product gaps listed in `AI_PROJECT_CONTEXT.md → Known Bugs / Limitations` (settings toggles cosmetic, favorites/history surfacing, non-nested folders, fragile detail screens, destructive DB migration, non-persistent sleep timer). **Out of scope for this QA cycle; no regression introduced.**
- No Android OWASP/dependency-scan plugin configured (recommend `org.owasp.dependencycheck` or a `dependencyUpdates` task as follow-up).