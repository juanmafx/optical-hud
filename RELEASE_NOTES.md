# Optical HUD 2.0.1 Release Notes

Version 2.0.1 is a documentation-only release. It contains no code behaviour change from 2.0.0. The Android `versionName` is 2.0.1 and the `versionCode` is 3.

## Changes

- The changelog and release notes no longer describe 2.0.0 as unreleased.
- The security audit marks the earlier wrapper JAR in Git history as resolved. On 2026-11-24 the public history was rewritten to keep only the 2026 modernization commits.

## Upgrading

No action is needed when upgrading from 2.0.0. For the changes introduced in 2.0.0, see [CHANGELOG.md](CHANGELOG.md).

## Known Limitations

- Camera-based reception is not implemented. The decoder runs only on simulated pulses.
- Optical messages are plaintext.
- Release signing is not configured, so release builds are unsigned.
- Not yet verified on a physical device.
- `androidx.core` stays at 1.18.0 because 1.19 requires compileSdk 37.
