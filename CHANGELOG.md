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

### Security

- Removed the unused `CAMERA` permission.
- Disabled app backup and device transfer.
- Added an input length limit and a flash-rate limit.
- Enabled release code and resource shrinking.
- Upgraded Gradle to 9.9.2 with distribution checksum verification, replacing a wrapper JAR from the 6.6 to 7.0 era.
- Upgraded AGP, Kotlin, and AndroidX dependencies.
- Stopped tracking IDE configuration.

## [1.0] - 2026-03-17

- Initial prototype: Android torch control and Morse transmission.
