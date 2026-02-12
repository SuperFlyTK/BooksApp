# Release Checklist

1. [x] `versionCode` / `versionName` set (`2` / `1.1.0`) in `app/build.gradle.kts`.
2. [x] Debug and Release configs separated (`ENABLE_VERBOSE_LOGS`, environment name).
3. [x] Compose compiler plugin configured for Kotlin 2.0 builds.
4. [x] `assembleRelease` succeeds with release config.
5. [x] Firebase secrets loaded from `local.properties` (not stored in git).
6. [x] Auth flow works: sign in, sign up, sign out, session persistence.
7. [x] Main navigation works: Auth -> Feed -> Details -> Review Editor + bottom tabs.
8. [x] Feed supports loading/empty/error/data states and retry.
9. [x] Search supports debounce, pagination, and retry behavior.
10. [x] Offline-first behavior verified with cached Room data.
11. [x] Realtime favorites implemented under `/users/{uid}/favorites`.
12. [x] Realtime comments CRUD implemented under `/comments/{bookId}`.
13. [x] Form validations implemented for auth and review forms.
14. [x] Retry policy applied for transient network failures.
15. [x] Structured logging is present and verbose logs are disabled in release.
16. [x] Performance improvement documented in `docs/PERFORMANCE_NOTE.md`.
17. [x] QA fixes log (>=5 issues) documented in `docs/QA_LOG.md`.
18. [x] Unit tests expanded to 10+ business tests and executed.
19. [x] Store listing draft exists in `docs/STORE_LISTING.md`.
20. [x] Release notes updated in `docs/RELEASE_NOTES.md`.
21. [x] Firebase rules assumptions documented in `docs/FIREBASE_RULES.md`.
