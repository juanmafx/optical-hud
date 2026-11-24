# Test Report

- Generated: 2026-11-24T02:15:19Z
- Command: `gradle :core:test` (Gradle 9.9.2, JDK 17)
- Scope: JVM tests of the `core` module. No instrumented or physical-device tests were run.
- Total: 48 | Passed: 48 | Failed: 0 | Errors: 0 | Skipped: 0

| Suite | Tests | Failed | Errors | Skipped | Time (s) |
| --- | ---: | ---: | ---: | ---: | ---: |
| `analysis.ChannelExperimentTest` | 6 | 0 | 0 | 0 | 0.97 |
| `analysis.StatisticsTest` | 5 | 0 | 0 | 0 | 0.158 |
| `channel.OpticalChannelTest` | 5 | 0 | 0 | 0 | 0.151 |
| `channel.SeededRandomTest` | 6 | 0 | 0 | 0 | 0.278 |
| `decoder.PulseDecoderTest` | 5 | 0 | 0 | 0 | 0.046 |
| `morse.MorseAlphabetTest` | 4 | 0 | 0 | 0 | 0.009 |
| `morse.MorseCodecTest` | 6 | 0 | 0 | 0 | 0.014 |
| `timing.TransmissionPlannerTest` | 5 | 0 | 0 | 0 | 0.018 |
| `transmission.TransmissionEngineTest` | 6 | 0 | 0 | 0 | 0.028 |

## Test Cases

### ChannelExperimentTest

- passed: cli parses options and rejects unknown ones
- passed: same seed reproduces identical results
- passed: csv has a header and one row per jitter level
- passed: zero jitter delivers every message
- passed: heavy jitter degrades delivery
- passed: different seeds change noisy results

### StatisticsTest

- passed: edit distance matches known cases
- passed: simulated wilson coverage agrees with exact binomial coverage
- passed: rejects invalid proportions
- passed: wilson interval matches a hand computed value
- passed: wilson interval is pinned at the boundaries

### OpticalChannelTest

- passed: rejects negative parameters
- passed: quantization lands on whole frames and never erases a pulse
- passed: preserves pulse polarity and count
- passed: jitter has the configured mean and deviation
- passed: an ideal channel is the identity

### SeededRandomTest

- passed: matches the splitmix64 reference vector
- passed: gaussian samples match standard normal moments
- passed: gaussian samples pass a kolmogorov smirnov check
- passed: doubles stay in the unit interval
- passed: bounded integers pass a chi squared uniformity check
- passed: same seed reproduces the same stream

### PulseDecoderTest

- passed: decodes an ideal transmission
- passed: single class messages fall back to the nominal unit
- passed: unit estimate recovers the true unit under jitter
- passed: decodes reliably under moderate seeded jitter
- passed: empty input decodes to empty text

### MorseAlphabetTest

- passed: covers all latin letters and digits
- passed: codes are unique
- passed: dichotomic tree inverts every code
- passed: rejects unassigned malformed and oversized codes

### MorseCodecTest

- passed: blank and unsupported input yields an empty message
- passed: separates words with a slash
- passed: collapses leading trailing and repeated whitespace
- passed: decoder marks unknown codes
- passed: counts unsupported characters
- passed: encodes the distress signal

### TransmissionPlannerTest

- passed: rejects units that would exceed the flash safety limit
- passed: paris occupies forty three units before its trailing word gap
- passed: word gap highlights the slash
- passed: follows the itu one three seven unit ratios
- passed: default speed is six words per minute

### TransmissionEngineTest

- passed: completes with the torch off and full progress
- passed: restarting replaces the previous transmission
- passed: stop while idle does not touch the torch
- passed: torch failure aborts and reports once
- passed: stop cancels pending steps and turns the torch off
- passed: rejects invalid requests without touching the torch
