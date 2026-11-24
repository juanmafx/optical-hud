# Test Report

- Generated: 2026-11-24T03:35:45Z
- Command: `gradle :core:test` (Gradle 9.9.2, JDK 17)
- Scope: JVM tests of the `core` module. No instrumented or physical-device tests were run.
- Total: 64 | Passed: 64 | Failed: 0 | Errors: 0 | Skipped: 0

| Suite | Tests | Failed | Errors | Skipped | Time (s) |
| --- | ---: | ---: | ---: | ---: | ---: |
| `analysis.ChannelExperimentTest` | 7 | 0 | 0 | 0 | 0.716 |
| `analysis.StatisticsTest` | 7 | 0 | 0 | 0 | 0.338 |
| `channel.OpticalChannelTest` | 5 | 0 | 0 | 0 | 0.12 |
| `channel.SeededRandomTest` | 7 | 0 | 0 | 0 | 0.146 |
| `decoder.PulseDecoderTest` | 7 | 0 | 0 | 0 | 0.14 |
| `morse.MorseAlphabetTest` | 4 | 0 | 0 | 0 | 0.009 |
| `morse.MorseCodecTest` | 9 | 0 | 0 | 0 | 0.187 |
| `timing.TransmissionPlannerTest` | 9 | 0 | 0 | 0 | 0.218 |
| `transmission.TransmissionEngineTest` | 9 | 0 | 0 | 0 | 0.222 |

## Test Cases

### ChannelExperimentTest

- passed: cli parses options and rejects unknown ones
- passed: cli rejects values the experiment cannot run
- passed: same seed reproduces identical results
- passed: csv has a header and one row per jitter level
- passed: zero jitter delivers every message
- passed: heavy jitter degrades delivery
- passed: different seeds change noisy results

### StatisticsTest

- passed: edit distance is a metric
- passed: edit distance matches known cases
- passed: wilson interval contains the observed proportion
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
- passed: gaussian stream matches strictmath reference bits
- passed: bounded integers pass a chi squared uniformity check
- passed: same seed reproduces the same stream

### PulseDecoderTest

- passed: decodes an ideal transmission
- passed: single class messages fall back to the nominal unit
- passed: blind estimation adapts to a sender at a different speed
- passed: ideal channel round trips every canonical text
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
- passed: encoding is idempotent over its normalized text
- passed: collapses leading trailing and repeated whitespace
- passed: decoder marks unknown codes
- passed: counts unsupported characters
- passed: decoding inverts encoding for canonical text
- passed: encodes the distress signal
- passed: morse output only contains the channel alphabet

### TransmissionPlannerTest

- passed: every range stays inside the rendered morse for arbitrary input
- passed: rejects units that would exceed the flash safety limit
- passed: paris occupies forty three units before its trailing word gap
- passed: word gap highlights the slash
- passed: steps alternate between lit and dark and end lit
- passed: lit steps align with their morse symbol after multiple words
- passed: follows the itu one three seven unit ratios
- passed: no one second window holds more than three flashes at the fastest unit
- passed: default speed is six words per minute

### TransmissionEngineTest

- passed: a listener restarting from a step runs only the new transmission
- passed: completes with the torch off and full progress
- passed: restarting replaces the previous transmission
- passed: stop while idle does not touch the torch
- passed: torch failure aborts and reports once
- passed: stop cancels pending steps and turns the torch off
- passed: a stalled scheduler never compresses flash onsets
- passed: progress is monotonic and the torch always ends off
- passed: rejects invalid requests without touching the torch
