# BooksApp (Android, Kotlin, Compose)

Production-like Android app for browsing books from Open Library with offline-first caching, Firebase authentication, and realtime comments/favorites.

This repository is aligned with strict Endterm/Final requirements for:
- architecture and clean layering,
- networking + pagination + debounced search,
- local persistence + sync policy,
- Firebase realtime user data,
- release readiness and documentation quality.

## Table of Contents
1. [Project summary](#project-summary)
2. [Requirements coverage (Endterm + Final)](#requirements-coverage-endterm--final)
3. [Feature overview](#feature-overview)
4. [Architecture overview](#architecture-overview)
5. [Tech stack](#tech-stack)
6. [Domain model and business rules](#domain-model-and-business-rules)
7. [External API used](#external-api-used)
8. [Firebase model and assumptions](#firebase-model-and-assumptions)
9. [Project setup](#project-setup)
10. [Build, test, release](#build-test-release)
11. [Offline-first and sync strategy](#offline-first-and-sync-strategy)
12. [Error handling and stability](#error-handling-and-stability)
13. [Testing](#testing)
14. [Defense-ready documentation](#defense-ready-documentation)
15. [Known limitations](#known-limitations)
16. [Demo credentials](#demo-credentials)
17. [AI usage disclosure](#ai-usage-disclosure)

## Project summary
- Track: **Android (Kotlin)**.
- UI: **Jetpack Compose**.
- Pattern: **MVVM + Repository**.
- Persistence: **Room**.
- API networking: **Retrofit + OkHttp + Moshi**.
- Realtime and auth: **Firebase Realtime Database + Firebase Auth**.
- Images: **Coil**.
- Concurrency: **Coroutines + Flow/StateFlow**.
- Dependency Injection: **Hilt**.

## Requirements coverage (Endterm + Final)
The project covers mandatory requirements from both assignments:
- 5+ screens and deep navigation.
- Form validation and user-friendly error states.
- 3+ domain entities and non-trivial business logic.
- Sign in/out + session persistence + user-scoped data.
- Pagination + debounced search + retry flow.
- Offline-first Room cache with stale/refresh policy.
- Firebase realtime CRUD feature with live updates.
- Release debug/release split, structured logging, versioning.
- 10+ unit tests, release checklist (15+), QA fixes log (5+).
- Updated README, architecture doc, release notes, and supporting docs.

Detailed mapping is available in:
- `docs/REQUIREMENTS_COVERAGE.md`

## Feature overview
- Authentication:
  - sign up / sign in / sign out,
  - session persistence after restart.
- Main screens:
  - `Auth`,
  - `Feed`,
  - `Search`,
  - `Details`,
  - `Review Editor`,
  - `Profile`.
- Navigation:
  - deep flow `Feed -> Details -> Review Editor`,
  - bottom navigation for signed-in users.
- Search:
  - debounce (`450 ms`),
  - pagination (`Load more`),
  - retry from error state.
- Feed/search state handling:
  - loading,
  - empty,
  - error + retry,
  - cache fallback messaging.
- Offline-first:
  - Room cache for feed/search,
  - stale cache policy,
  - merge/dedup for paginated pages.
- Realtime Firebase:
  - favorites CRUD (user scoped),
  - comments CRUD (per book),
  - realtime UI updates via database listeners.
- Recommendation/business logic:
  - recommendation scoring,
  - query and review sanitization,
  - credentials/review validation.

## Architecture overview
- Clean separation by layers:
  - UI (`ui/*` Compose screens),
  - Presentation (`ViewModel`),
  - Domain (`domain/model`, `domain/repository`, `domain/usecase`),
  - Data (`data/remote`, `data/local`, `data/firebase`, `data/repository`),
  - DI/infra (`di/*`, `core/*`).
- UI does not call Retrofit/Firebase directly.
- Repositories encapsulate data source coordination.
- ViewModels expose state via `StateFlow`.
- Lifecycle-safe async operations with `viewModelScope`.

Data flow (simplified):

```text
Compose UI
  -> ViewModel
    -> Repository interface (domain)
      -> Repository implementation (data)
        -> Retrofit API / Room / Firebase
      <- domain models
    <- UiState (Flow/StateFlow)
<- rendered screen
```

See full details in:
- `docs/ARCHITECTURE.md`

## Tech stack
- Kotlin `2.0.0`
- AGP `9.0.0`
- Gradle Wrapper `9.1.0`
- Compose BOM `2024.09.00`
- Navigation Compose `2.8.9`
- Coroutines `1.8.1`
- Hilt `2.57.1`
- Room `2.7.0`
- Retrofit `2.11.0`
- OkHttp `4.12.0`
- Moshi `1.15.1`
- Coil `2.7.0`
- Firebase BoM `33.10.0`

## Domain model and business rules
Main entities include:
- `Book`
- `BookComment`
- `AppUser`
- (plus user-scoped `Favorite` records in Firebase path model)

Business logic/use-cases:
- `RecommendationScorer` (non-trivial scoring rule),
- `QuerySanitizer`,
- `ReviewValidator`,
- `CredentialsValidator`,
- `BookMergePolicy` (duplicate prevention/update policy).

## External API used
Open Library Search API:
- `GET https://openlibrary.org/search.json?q={query}&page={page}&limit={limit}`

Main usage patterns:
- feed bootstrap query,
- user search query with pagination.

## Firebase model and assumptions
Firebase Auth:
- Email/Password sign-in method.

Realtime Database paths:
- favorites: `/users/{uid}/favorites/{bookId}`
- comments: `/comments/{bookId}/{commentId}`

Security assumptions:
- authentication required for writes,
- favorites are user-scoped (`uid == auth.uid`),
- comment write/delete allowed for comment owner.

See:
- `docs/FIREBASE_RULES.md`

## Project setup
1. Create Firebase project:
   - enable **Authentication -> Email/Password**,
   - create **Realtime Database**.
2. Add local secrets to `local.properties` (excluded from git):

```properties
FIREBASE_API_KEY=...
FIREBASE_APP_ID=...
FIREBASE_PROJECT_ID=...
FIREBASE_DATABASE_URL=https://<project>.firebaseio.com
```

3. Open project in Android Studio.
4. Use JDK 17 (recommended for AGP 9 toolchain).
5. Sync Gradle and run app.

SDK/version info:
- `minSdk = 26`
- `targetSdk = 36`
- `compileSdk = 36`
- `versionCode = 2`
- `versionName = 1.1.0`

## Build, test, release
Windows:
```bash
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleRelease
```

macOS/Linux:
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

Build variants:
- `debug`: verbose logs enabled.
- `release`: verbose logs disabled (`BuildConfig.ENABLE_VERBOSE_LOGS=false`).

Release notes:
- `docs/RELEASE_NOTES.md`

Release checklist:
- `docs/RELEASE_CHECKLIST.md`

## Offline-first and sync strategy
- Remote API data is cached in Room by query tag.
- Query metadata stores page/fetch/order fields.
- `CachePolicy` decides stale/fresh behavior.
- On network failure:
  - show cached data when available,
  - surface error only if no cache exists.
- Pagination merge policy prevents duplicates and unnecessary writes.

Performance evidence and notes:
- `docs/PERFORMANCE_NOTE.md`

## Error handling and stability
- Robust UI states for loading/empty/error/data.
- Manual retry in UI for search/feed failures.
- Basic automatic retry policy for transient network failures.
- Firebase auth failures and missing config are handled gracefully.
- Input data is validated/sanitized before use and writes.

QA evidence and fixed defects:
- `docs/QA_LOG.md`

## Testing
Automated:
- 10+ unit tests focused on business logic and DTO parsing.
- Tests include validators, merge/scoring logic, and sanitizer behavior.

Manual:
- 15+ release-regression checks documented in QA/checklist docs.
- Includes auth, offline mode, pagination, realtime CRUD, and error states.

## Defense-ready documentation
- `docs/ARCHITECTURE.md` - layers and data flow.
- `docs/REQUIREMENTS_COVERAGE.md` - requirement-to-implementation traceability.
- `docs/DEFENSE_RUNBOOK.md` - suggested live demo flow + Q&A prep.
- `docs/RELEASE_NOTES.md` - Endterm -> Final delta + limitations.
- `docs/RELEASE_CHECKLIST.md` - release readiness.
- `docs/QA_LOG.md` - defects and fixes.
- `docs/PERFORMANCE_NOTE.md` - optimization evidence.
- `docs/STORE_LISTING.md` - draft store listing.
- `docs/FINAL_REPORT_OUTLINE.md` - report structure.
- `docs/DEFENSE_SLIDES_OUTLINE.md` - slide deck structure.

## Known limitations
- Signed `.aab` publishing/upload step is not automated in this repo.
- Firebase rule examples are assumptions and must be applied/tuned in Firebase Console.
- UI/instrumentation testing is limited; quality gate is mostly unit tests + manual QA.

## Demo credentials
- No shared demo account is stored in repository.
- Create a test account via app Auth screen.

## AI usage disclosure
- AI-assisted:
  - initial scaffolding ideas,
  - some boilerplate drafts,
  - documentation structure.
- Manually implemented/reviewed:
  - architecture wiring,
  - repositories and ViewModels,
  - validation/sanitization rules,
  - tests, QA fixes, and final documentation.
