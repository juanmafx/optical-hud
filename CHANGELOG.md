# Changelog

This project follows [Semantic Versioning](https://semver.org/).

## [2.0.0] - Unreleased

### Breaking

- Split the project into a pure Kotlin `core` module and an Android `app` module.
- Replaced `TransmitterController` with `TransmissionEngine` and explicit scheduler and torch ports.
- Changed word gaps from 1000 ms to the ITU 7-unit value (1400 ms at the default speed).

### Added

- Morse decoder, pulse decoder with blind speed estimation, seeded channel simulator, and Monte Carlo experiment CLI.
- Property-based and statistical test suite, committed test report, and experiment results.
- Optional SQLite schema with verification script.
- CI workflow with read-only permissions and SHA-pinned actions.
- Architecture, security policy, security audit, and release notes.

### Fixed

- Symbol highlighting drifted after the first word and with repeated or leading spaces.
- Newlines and tabs merged adjacent words instead of separating them.
- Torch errors were silently ignored and now stop the transmission with a visible error.
- A camera access failure during startup could crash the app.
- Pausing the app while idle could turn off a torch that another app had enabled.
- A stalled UI thread could release queued flashes in a burst above the three-per-second limit. Steps are now chained.
- Twelve property tests were silently skipped by JUnit because they returned a value. They now run, and the report generator fails if a declared test does not execute.
- Experiment options that the simulation cannot run now exit with a usage error instead of a stack trace.
- Gaussian sampling uses `StrictMath` so results do not depend on JVM math intrinsics.

### Security

- Removed the unused `CAMERA` permission.
- Disabled app backup and device transfer.
- Added an input length limit and a flash-rate limit.
- Enabled release code and resource shrinking.
- Upgraded Gradle to 9.9.2 with distribution checksum verification, replacing a wrapper JAR from the 6.6 to 7.0 era.
- Upgraded AGP, Kotlin, and AndroidX dependencies.
- Stopped tracking IDE configuration.
- Constrained vulnerable transitive build-tool dependencies (Bouncy Castle, Guava, Commons Lang, HttpClient, JDOM, jose4j) to patched versions.
- Asked keyboards not to learn from message input.

## [1.0] - 2026-03-17

- Initial prototype: Android torch control and Morse transmission. The app's version name was 1.0, and this state carries the Git tag `v0.1.0`.
