# Defense Runbook (Android Track)

Use this script for a 7-8 minute live demo and 3-5 minute Q&A.

## 1. Demo Plan (7-8 minutes)

1. App intro (30s)
- "BooksApp: Compose + MVVM + Repository + Room + Retrofit + Firebase."
- Mention key goal: offline-first + realtime + release readiness.

2. Authentication flow (1 min)
- Sign in / sign up.
- Show session persistence (relaunch app or explain auth state listener path).
- Mention user-scoped data in Firebase.

3. Main flow + navigation (1 min)
- Open Feed.
- Navigate `Feed -> Details -> Review Editor`.
- Return and switch tabs (`Feed/Search/Profile`).

4. Networking requirements (1.5 min)
- Search with debounce (pause typing to trigger request).
- Show pagination via "Load more".
- Trigger error state (optional network off) and use retry button.

5. Offline-first behavior (1 min)
- Disable internet.
- Show cached feed/search still visible.
- Explain stale cache/refresh behavior in repository and cache policy.

6. Firebase realtime feature (1.5 min)
- Add/edit/delete comment on Details screen.
- Toggle favorite for signed-in user.
- Explain realtime path model:
  - `/users/{uid}/favorites/{bookId}`
  - `/comments/{bookId}/{commentId}`

7. Final readiness close (30s)
- Show release artifacts/docs:
  - versioning + build types,
  - release checklist,
  - QA log and performance note.

## 2. Likely Q&A and Strong Answers

Q: Why MVVM + Repository?
- Separation of concerns. UI is state-driven and does not call API/Firebase directly.
- Repositories centralize source coordination and simplify testing.

Q: How do you avoid duplicate books in pagination?
- `BookMergePolicy` merges cached/remote items by ID and update timestamp policy.
- Repository applies merge before persistence and query mapping updates.

Q: How does offline mode work?
- Room stores query-tagged snapshots.
- On refresh/load failure, cached data is served if available.
- Error is surfaced only when cache is empty.

Q: How is search optimized?
- Debounce (`450ms`) + distinct query stream.
- Pagination and retry support.

Q: How is security handled?
- No secrets in git; local config in `local.properties`.
- Firebase rules assumptions enforce auth + user-scoped access.
- Inputs are validated and sanitized before requests/writes.

Q: What changed in Final vs Endterm?
- Release-ready config split, stronger retry strategy, logging controls, merge policy integration, performance improvements, expanded tests/QA/docs.

## 3. File References for Live Inspection

- Navigation: `app/src/main/java/com/example/booksapp/ui/navigation/BooksNavGraph.kt`
- Search debounce/pagination/retry: `app/src/main/java/com/example/booksapp/ui/search/SearchViewModel.kt`
- Offline/sync/merge policy: `app/src/main/java/com/example/booksapp/data/repository/BooksRepositoryImpl.kt`
- Firebase setup: `app/src/main/java/com/example/booksapp/di/FirebaseModule.kt`
- Favorites/comments repositories:
  - `app/src/main/java/com/example/booksapp/data/repository/FavoritesRepositoryImpl.kt`
  - `app/src/main/java/com/example/booksapp/data/repository/CommentsRepositoryImpl.kt`
- Validators/use-cases:
  - `app/src/main/java/com/example/booksapp/domain/usecase/CredentialsValidator.kt`
  - `app/src/main/java/com/example/booksapp/domain/usecase/ReviewValidator.kt`
  - `app/src/main/java/com/example/booksapp/domain/usecase/QuerySanitizer.kt`
  - `app/src/main/java/com/example/booksapp/domain/usecase/RecommendationScorer.kt`
  - `app/src/main/java/com/example/booksapp/domain/usecase/BookMergePolicy.kt`

## 4. Pre-Defense Checklist (Quick)

- Confirm `local.properties` has valid Firebase keys.
- Run unit tests (`testDebugUnitTest`) before demo day.
- Have at least one cached query prepared for offline demo.
- Prepare one user account for auth/favorites/comments flow.
- Keep docs open:
  - `README.md`
  - `docs/REQUIREMENTS_COVERAGE.md`
  - `docs/RELEASE_CHECKLIST.md`
  - `docs/QA_LOG.md`
