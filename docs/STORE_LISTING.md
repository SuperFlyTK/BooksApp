# Store Listing Draft

**App name:** BooksApp Explorer

**Short description:** Browse, favorite, and review catalog hits with offline-first sync and realtime comments.

**Full description:**
BooksApp Explorer connects to Open Library to let you search, favorite, and review books with fast pagination, offline caching, and realtime Firebase comments. Sign in with email/password, toggle favorites, read cached results when offline, and contribute reviews that appear instantly for every signed-in user.

**Privacy notes:** Firebase Auth stores minimal profile (email & displayName). Realtime Database stores user-specific favorites/comments keyed by UID. No sensitive data is logged or uploaded. API keys are kept out of the repository via `local.properties`.
