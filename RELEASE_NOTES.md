# Optical HUD 2.0.0 Release Notes

Status: prepared, not tagged or released.

## Highlights

- **Correct signaling.** Timing now follows the ITU 1:3:7 standard, and on-screen highlighting stays aligned across words.
- **Clean architecture.** Signal logic lives in a pure Kotlin module that is testable without Android.
- **Measurable link quality.** A seeded simulator reports decode success with confidence intervals.
- **Smaller attack surface.** The app requests no camera or network permission and disables backups.
- **Enforced flash limit.** A stalled UI thread can no longer release queued flashes in a burst.

## Upgrade Notes

- Requires JDK 17 to build. Gradle is 9.9.2 and AGP is 9.5.2.
- Word gaps are longer than in 1.0, so a 2.0.0 stream is not timed identically to a 1.0 stream.
- Scheduling latency now accumulates into total transmission time in exchange for the flash limit.

## Known Limitations

- Camera-based reception is not implemented. The decoder runs only on simulated pulses.
- Optical messages are plaintext.
- Release signing is not configured, so release builds are unsigned.
- Not yet verified on a physical device.
- `androidx.core` stays at 1.18.0 because 1.19 requires compileSdk 37.
