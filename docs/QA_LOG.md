# QA Log

## A. Manual checklist (release regression)

| # | Test case | Result |
| --- | --- | --- |
| 1 | Launch app with missing Firebase keys | App does not crash; shows "Firebase not configured" handling |
| 2 | Sign up with valid data | Account created and user redirected to feed |
| 3 | Sign in with wrong password | User-friendly auth error shown |
| 4 | Feed initial load | Loading -> data states work correctly |
| 5 | Feed pagination | Additional page loads without UI freeze |
| 6 | Search debounce | API request starts only after typing pause (~450ms) |
| 7 | Search retry from error state | Retry button triggers fresh search request |
| 8 | Airplane mode on cached feed/search | Cached content displayed, no crash |
| 9 | Toggle favorite (signed in) | Realtime Firebase update under user-scoped path |
| 10 | Toggle favorite (signed out) | UI shows "sign in required" message |
| 11 | Create review/comment | New comment appears in realtime |
| 12 | Edit own comment | Text/rating updated in realtime |
| 13 | Delete own comment | Comment removed in realtime |
| 14 | Try edit/delete another user's comment | Operation rejected with error |
| 15 | Sign out and relaunch app | Session cleared after logout |

## B. Defects found and fixed (>=5)

| ID | Defect found during QA | Fix implemented |
| --- | --- | --- |
| B1 | Build failed on Kotlin 2.0 because Compose compiler plugin was missing | Added `alias(libs.plugins.compose.compiler)` in `app/build.gradle.kts` |
| B2 | `AuthScreen` had invalid/unused imports and navigation side effect directly in composition | Removed invalid imports; moved auth navigation to `LaunchedEffect` |
| B3 | Search retry button could fail due to `distinctUntilChanged` when reusing same query | Added `retryCurrentQuery()` in `SearchViewModel`; wired `SearchScreen` retry button to it |
| B4 | Editing a comment produced encoded text artifacts (`%20`) due to double URI encoding | Removed pre-encoding in details screen and kept single encode/decode path |
| B5 | Pagination merge/update policy not consistently applied for duplicate IDs across pages | Integrated `BookMergePolicy` into `BooksRepositoryImpl` for dedup/merge before persistence |
| B6 | Firebase keys for paths could contain unsafe symbols in edge cases | Added Firebase node-key sanitization in favorites/comments repositories |

## C. Automated tests

- Unit tests expanded to 10+ business tests:
  - `CredentialsValidatorTest`
  - `QuerySanitizerTest`
  - `ReviewValidatorTest`
  - `BookMergePolicyTest`
  - `RecommendationScorerTest`
