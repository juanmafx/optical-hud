# Optical HUD

An off-grid optical communication prototype for Android. It turns a phone flashlight into a Morse code transmitter and ships a seeded simulator that measures how well such a link survives timing noise.

> **Photosensitivity warning:** this app flashes a bright light. Do not use it around people with photosensitive epilepsy, and do not aim it at anyone's eyes. The timing model rejects a Morse unit below 170 ms, and each step starts only after the previous one has run for its full duration, so the app never commands more than three flashes in one second. Torch hardware latency is not measured, and this limit is not a medical safety guarantee.

## Purpose

Optical HUD explores communication through visible light when radio, Wi-Fi, and cellular networks are unavailable or undesirable.

## Why This Project Exists

A phone flashlight is a crude transmitter: it has only two states, imprecise switching, and a receiver limited by camera frame rate. That makes it a compact way to study line coding, timing recovery, and error measurement. Today the transmitter drives real torch hardware, while reception and error measurement run in simulation. The long-term direction is camera-based reception, a custom binary protocol, and HUD-style aiming assistance. **None of those three exist yet.**

## What It Does

| Capability | Status |
| --- | --- |
| Encode A-Z and 0-9 to International Morse | Implemented |
| Drive the back-camera torch with ITU 1:3:7 timing | Implemented |
| Live symbol highlighting and progress | Implemented |
| Flash-rate safety limit and input length limit | Implemented |
| Decode simulated pulses with blind speed estimation | Implemented, simulation only |
| Seeded Monte Carlo channel experiment | Implemented |
| Camera-based reception in the app | Not implemented |
| Encryption or authentication | Not implemented: **anyone who can see the light can read the message** |

## Architecture

```text
optical-hud/
├── android-app/
│   ├── core/    Pure Kotlin/JVM: codec, timing, engine, channel, decoder, analysis
│   └── app/     Android: activity, torch adapter, handler scheduler, highlighting
├── db/          Optional SQLite schema for experiment results
├── docs/        Architecture and security audit
├── reports/     Generated test report and experiment output
└── scripts/     Report generation and schema verification
```

The `core` module has no Android dependency, so the compiler enforces the domain boundary. See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for design and complexity notes.

## Quick Start

Requirements: JDK 17 and Android SDK platform 36, with `ANDROID_HOME` set or `sdk.dir` in `android-app/local.properties`. The app targets Android 8.0 (API 26) and newer.

```bash
git clone https://github.com/juanmafx/optical-hud.git
cd optical-hud/android-app
./gradlew :core:test
./gradlew :app:assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Usage Guide

1. Open **OpticalHUD** on a device with a back-camera flash.
2. Enter up to 256 characters.
3. Tap **Convert and Transmit**. The Morse output highlights the active symbol while the torch flashes.
4. Leaving the app stops the transmission and turns the torch off.

ASCII letters and digits are transmitted, case-insensitively. Any whitespace separates words. Other characters, including accented letters and punctuation, are skipped and the app reports how many.

### Running the Channel Experiment

```bash
cd android-app
./gradlew -q :core:run --args="--seed 42 --trials 2000" > ../reports/experiments/channel-seed-42.csv
```

The same seed and options produce a byte-identical CSV. The committed run simulates a 200 ms unit, a 30 fps receiver, and random 24-character messages:

| Timing jitter (sigma, in units) | Messages decoded exactly (of 2000) | 95% Wilson interval |
| ---: | ---: | --- |
| 0.20 | 100.00% | 99.81% to 100.00% |
| 0.25 | 99.40% | 98.95% to 99.66% |
| 0.30 | 91.65% | 90.36% to 92.78% |
| 0.40 | 34.60% | 32.55% to 36.71% |
| 0.50 | 2.55% | 1.94% to 3.34% |

These are **simulation results** from an independent Gaussian jitter model. They are not field measurements of real phones.

## Configuration

| Setting | Default | Location |
| --- | --- | --- |
| Morse unit | 200 ms (6 WPM) | `MorseTiming.DEFAULT_UNIT_MS` |
| Minimum unit | 170 ms | `MorseTiming.MIN_UNIT_MS` |
| Maximum input | 256 characters | `TransmissionEngine.MAX_INPUT_LENGTH` |
| Experiment options | seed 42, 500 trials, 200 ms, 33.33 ms frames | `--seed`, `--trials`, `--unit-ms`, `--frame-interval-ms` |

## Database (Optional)

The app has no database and writes no message text to storage. [db/schema.sql](db/schema.sql) is an **optional** SQLite schema for archiving experiment results. It uses `STRICT` tables, so it requires SQLite 3.37 or newer.

```bash
sqlite3 experiments.db < db/schema.sql
python3 scripts/verify_schema.py
```

The first command creates an empty database. The second is independent of that file: it applies the schema to an in-memory database, imports the committed CSV, and confirms that an invalid row is rejected. The repository does not include an import tool for a file database.

## Testing

```bash
cd android-app
./gradlew :core:test :app:lintDebug
cd ..
python3 scripts/generate_test_report.py
```

The suite combines example tests, seeded property-based tests (Kotest), and statistical tests (chi-squared, Kolmogorov-Smirnov, moment checks, and Wilson coverage against the exact binomial value). The report generator fails if any declared test did not execute. The latest real run is in [reports/tests/summary.md](reports/tests/summary.md). Android UI and torch hardware behavior are not covered by automated tests, and this version has not been run on a device or emulator.

## Security Notes

- No network permission, no accounts, and backups disabled. The app writes no message text to storage.
- The built manifest does not request `CAMERA`. Its only requested permission is the app-private `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` that AndroidX adds.
- The text field asks keyboards not to learn from input (`flagNoPersonalizedLearning`). Keyboards may ignore this request.
- Optical transmission is plaintext and observable by anyone in line of sight. Do not send secrets.
- Report vulnerabilities as described in [SECURITY.md](SECURITY.md). Audit details: [docs/SECURITY_AUDIT.md](docs/SECURITY_AUDIT.md).

## Project History

Originally created in 2026 and modernized in 2026. See [CHANGELOG.md](CHANGELOG.md).

## License

No license file is present, so all rights are reserved by the author by default. The Gradle Wrapper files (`gradlew`, `gradlew.bat`, and `gradle/wrapper/`) are unmodified Gradle project files under the Apache License 2.0.

## Credits

The implementation follows published work: SplitMix64 (Steele, Lea, and Flood, 2014), the Box-Muller transform (1958), the Wilson score interval (1927), Levenshtein distance (1966), the Morse timing of ITU-R M.1677-1, and the three-flash threshold of WCAG 2.3.1.
