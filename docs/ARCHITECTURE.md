# Architecture Overview

## Layered structure

- UI layer (Compose screens):
  - `AuthScreen`, `FeedScreen`, `SearchScreen`, `BookDetailsScreen`, `ReviewEditorScreen`, `ProfileScreen`
- Presentation layer (ViewModels):
  - `AuthViewModel`, `FeedViewModel`, `SearchViewModel`, `DetailsViewModel`, `ReviewEditorViewModel`, `ProfileViewModel`
- Domain layer (interfaces + business rules):
  - Repository interfaces in `domain/repository`
  - Use cases: `CredentialsValidator`, `ReviewValidator`, `QuerySanitizer`, `RecommendationScorer`, `BookMergePolicy`
- Data layer:
  - Remote: Retrofit (`OpenLibraryApi`)
  - Local: Room (`BooksDao`, `BookEntity`, `BookQueryEntity`)
  - Cloud: Firebase Auth + Realtime Database
- Infrastructure:
  - Hilt modules (`AppModule`, `NetworkModule`, `DatabaseModule`, `FirebaseModule`, `RepositoryModule`)
  - Shared utils (`RetryPolicy`, `CachePolicy`, `DispatchersProvider`, `AppLogger`)

## Data flow diagram

```text
Compose UI
   -> ViewModel (StateFlow + intents)
      -> Repository interface (domain)
         -> Repository implementation (data)
            -> Retrofit API (Open Library)
            -> Room DAO (offline cache)
            -> Firebase Auth/Realtime DB (user data + realtime)
         <- mapped domain models
      <- UI state (loading/empty/error/data)
   <- rendered screen
```

## Offline-first and sync policy

- Feed/search data is cached in Room with a `queryTag`.
- Query metadata (`page`, `fetchedAt`, `position`) is stored in `BookQueryEntity`.
- `CachePolicy` decides whether cached data is stale.
- If cache is fresh and `force=false`, network call is skipped.
- If network fails:
  - show cache when available;
  - return error only when cache is empty.
- `BookMergePolicy` prevents duplicate records and defines update strategy by timestamp (`lastUpdatedAt`).

## Firebase model

- User favorites (scoped): `/users/{uid}/favorites/{bookId}`
- Realtime comments: `/comments/{bookId}/{commentId}`
- All writes go through repositories; UI never writes directly to Firebase.
- Path keys are sanitized to avoid invalid Firebase node characters.

## Concurrency and cancellation

- Coroutines are used for all async operations.
- `Flow`/`StateFlow` drives realtime UI updates.
- Repository I/O runs on `Dispatchers.IO` via `DispatchersProvider`.
- Operations launched in `viewModelScope` are cancelled automatically when ViewModel is cleared.
