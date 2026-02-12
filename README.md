# BooksApp (Android, Kotlin, Compose)

BooksApp is a production-like mobile app for browsing books from Open Library with offline-first caching, Firebase authentication, and realtime comments/favorites.

## Features
- Authentication: sign up, sign in, sign out (Firebase Auth), session persistence after restart.
- Screens: Auth, Feed, Search, Details, Review Editor, Profile.
- Deep navigation: Feed -> Details -> Review Editor.
- Search: debounced input (450ms), pagination ("load more"), loading/empty/error/retry states.
- Offline-first: Room cache for feed/search data, stale-cache refresh policy.
- Realtime: Firebase Realtime Database CRUD for comments and user-scoped favorites.
- Images: loaded from URL via Coil.
- Business logic: recommendation scoring, input sanitization, validation, merge policy for cache updates.

## Architecture
- Pattern: MVVM + Repository.
- UI never calls Retrofit/Firebase directly.
- DI: Hilt modules for API, Room, Firebase, and use cases.
- Concurrency: Coroutines + Flow/StateFlow with ViewModel lifecycle cancellation.

See `docs/ARCHITECTURE.md` for data-flow details.

## External API
- Open Library search endpoint:
  - `GET https://openlibrary.org/search.json?q={query}&page={page}&limit={limit}`

## Project setup
1. Create Firebase project with:
   - Authentication (Email/Password enabled)
   - Realtime Database
2. Add local secrets to `local.properties` (file is ignored by git):

```properties
FIREBASE_API_KEY=...
FIREBASE_APP_ID=...
FIREBASE_PROJECT_ID=...
FIREBASE_DATABASE_URL=https://<project>.firebaseio.com
```

3. Open project in Android Studio (JDK 17 recommended for AGP 9).
4. Sync Gradle.

## Build and test
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Release behavior:
- `debug`: verbose network logs enabled.
- `release`: verbose logs disabled (`BuildConfig.ENABLE_VERBOSE_LOGS=false`), production config.

## Firebase data model and rules assumptions
- Favorites path: `/users/{uid}/favorites/{bookId}`
- Comments path: `/comments/{bookId}/{commentId}`
- Client-side validation/sanitization:
  - Search input sanitized (`QuerySanitizer`)
  - Review text sanitized and length-limited
  - Firebase path keys sanitized for unsafe characters

Expected rules model (documented assumption):
- Auth required for write operations.
- User can read/write only own favorites under `/users/{uid}` where `uid == auth.uid`.
- Comment create/update/delete allowed only for comment owner (`userId == auth.uid`).

## Documentation
- `docs/ARCHITECTURE.md` - layers and data flow.
- `docs/RELEASE_NOTES.md` - Endterm -> Final changes.
- `docs/RELEASE_CHECKLIST.md` - release readiness checklist.
- `docs/QA_LOG.md` - QA log and fixed issues.
- `docs/PERFORMANCE_NOTE.md` - performance improvements and evidence.
- `docs/FIREBASE_RULES.md` - Firebase security assumptions and sample rules.
- `docs/STORE_LISTING.md` - draft store listing.
- `docs/FINAL_REPORT_OUTLINE.md` - structure for Final PDF report.
- `docs/DEFENSE_SLIDES_OUTLINE.md` - suggested 8-12 slide defense deck.

## Demo credentials
- No shared demo account in repository.
- Create account from app Auth screen.

## AI usage disclosure
- AI-assisted: initial scaffolding ideas, some boilerplate drafts, and documentation outline.
- Manually implemented/reviewed: architecture wiring, repositories, ViewModels, validation logic, tests, and bug fixes.
