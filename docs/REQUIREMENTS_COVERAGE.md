# Requirements Coverage Matrix (Endterm + Final)

This document maps strict assignment requirements to concrete implementation and evidence in this repository.

## Endterm Requirements Mapping

| Requirement | Status | Where implemented / documented |
| --- | --- | --- |
| 5+ screens + deep navigation | Done | `app/src/main/java/com/example/booksapp/ui/navigation/BooksNavGraph.kt`, screens in `ui/*` |
| Form validation + friendly errors | Done | `domain/usecase/CredentialsValidator.kt`, `domain/usecase/ReviewValidator.kt`, auth/review screens |
| 3+ entities + business logic | Done | `domain/model/Book.kt`, `domain/model/BookComment.kt`, `domain/model/AppUser.kt`, `domain/usecase/RecommendationScorer.kt` |
| Sign in / sign out + session persistence | Done | `data/repository/AuthRepositoryImpl.kt`, Firebase auth listener usage |
| User-specific data | Done | favorites scoped by `/users/{uid}/favorites/{bookId}` in `FavoritesRepositoryImpl` |
| Robust networking + JSON handling | Done | `data/remote/api/OpenLibraryApi.kt`, DTO mapping tests |
| Pagination | Done | feed/search `loadMore` in `BooksRepositoryImpl`, `FeedViewModel`, `SearchViewModel` |
| Debounced search | Done | `SearchViewModel` (`debounce(450)` + `distinctUntilChanged`) |
| UI states (loading/empty/error/retry) | Done | `ui/common/UiState.kt`, screen state rendering + retry actions |
| Offline-first cache | Done | Room entities/DAO + repository cache strategy |
| Sync strategy (online refresh + offline cache fallback) | Done | `BooksRepositoryImpl` + `core/common/CachePolicy.kt` |
| Duplicate prevention / merge policy | Done | `domain/usecase/BookMergePolicy.kt`, repository integration |
| Firebase realtime feature + CRUD | Done | comments/favorites repositories + details/feed screens |
| Realtime UI updates | Done | flows observing Firebase-backed data in ViewModels |
| Image loading from URL | Done | Coil usage in UI components/screens |
| Additional platform/library quality feature | Done | Navigation Compose + Hilt + structured retry/cache policies |
| Architecture MVVM + Repository | Done | project structure + docs |
| DI | Done | `di/*` modules using Hilt |
| Concurrency (coroutines + Flow/StateFlow) | Done | ViewModels/repositories |
| No hardcoded secrets | Done | `local.properties` usage in `app/build.gradle.kts` |
| >=5 unit tests | Done | unit tests under `app/src/test/...` (10+) |
| Manual checklist >=10 | Done | `docs/QA_LOG.md` |

## Final Requirements Mapping

| Requirement | Status | Where implemented / documented |
| --- | --- | --- |
| Versioning + release/debug split | Done | `app/build.gradle.kts` (`versionCode=2`, `versionName=1.1.0`, build types) |
| Release configuration without verbose logs | Done | `BuildConfig.ENABLE_VERBOSE_LOGS` + `BooksApplication.kt` |
| Signed release evidence requirement | Partially external | build config in repo; signing/upload evidence should be attached in report/slides or release artifacts |
| Store listing draft in `/docs` | Done | `docs/STORE_LISTING.md` |
| Global error handling (no crashes common flows) | Done | QA results + guarded repository/viewmodel paths |
| Consistent retry strategy | Done | manual retry in UI + automatic retry policy in `core/common/RetryPolicy.kt` |
| Structured logging | Done | `core/logging/AppLogger.kt` and debug/release toggles |
| Performance improvement + evidence | Done | `docs/PERFORMANCE_NOTE.md`, DB indexing + merge tuning |
| Security basics + Firebase path assumptions | Done | `docs/FIREBASE_RULES.md`, validators/sanitizers |
| 10 unit tests total | Done | test suite in `app/src/test/...` |
| Release checklist >=15 | Done | `docs/RELEASE_CHECKLIST.md` (21 items) |
| QA log with >=5 fixed issues | Done | `docs/QA_LOG.md` |
| Updated README + architecture + release notes | Done | `README.md`, `docs/ARCHITECTURE.md`, `docs/RELEASE_NOTES.md` |

## Defense Evidence Quick Links

- Architecture and data flow: `docs/ARCHITECTURE.md`
- Release deltas and limitations: `docs/RELEASE_NOTES.md`
- Release readiness checks: `docs/RELEASE_CHECKLIST.md`
- QA checklist and fixed issues: `docs/QA_LOG.md`
- Performance evidence: `docs/PERFORMANCE_NOTE.md`
- Firebase assumptions and sample rules: `docs/FIREBASE_RULES.md`

## Notes for Submission

- Keep this repository link + PDF report + slides aligned.
- If signed `.aab` is not attached in repo releases, add screenshots/evidence in report/slides.
- During defense, be ready to open the exact files listed above and explain implementation decisions.
