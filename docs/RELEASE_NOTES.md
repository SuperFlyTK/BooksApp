# Release Notes (Endterm -> Final)

## Major upgrades

- Added release-ready build separation:
  - Debug vs Release verbose logging behavior.
  - Versioning set to `1.1.0` (`versionCode=2`).
- Fixed Kotlin 2.0 compatibility by enabling Compose compiler plugin.
- Strengthened reliability:
  - explicit retry action for search error state,
  - automatic retry policy for transient network failures,
  - safer auth navigation side effects.
- Improved data consistency:
  - integrated `BookMergePolicy` in repository pagination flow,
  - fixed comment edit flow (removed double URI encoding issue).
- Security hardening:
  - Firebase node-key sanitization for dynamic path values,
  - input validation and sanitization kept in use cases.
- Performance:
  - query indices already used in Room model,
  - added measured performance note and evidence in docs.
- Quality gates:
  - expanded business unit tests to 10+,
  - documented QA defects and fixes (>=5) in `docs/QA_LOG.md`.
- Documentation:
  - rewritten README with full setup and Firebase rules assumptions,
  - updated architecture and release checklist docs.

## Known limitations

- Signed `.aab` creation and upload are manual steps and must be done from Android Studio/CI with local keystore.
- No UI/instrumentation test suite yet (unit tests only).
- Firebase security rules are documented assumptions and must be applied in Firebase Console.
