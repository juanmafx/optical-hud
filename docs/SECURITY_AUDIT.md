# Security Audit

- Date: 2026-11-23
- Scope: application code, manifest, build configuration, wrapper, tracked files, and resolved Maven dependencies for version 2.0.0.
- Method: manual review, tracked-file secret search, Android Lint, wrapper checksum comparison against Gradle's published values, and an OSV query.

## Results

| Check | Result |
| --- | --- |
| Secrets or private keys in tracked files | None found in the current tree |
| Injection, XSS, CSRF, and `eval` | Not applicable: no web, SQL, or network surface in the app |
| Weak hashing | Not applicable: the app does no hashing |
| OSV advisories, 102 resolved packages | 0 reported on 2026-11-23 |
| Android Lint | 0 errors, 3 warnings (version recommendations only) |
| Gradle wrapper JAR | Matches the published 9.9.2 checksum |

## Fixed

| Severity | Finding | Resolution |
| --- | --- | --- |
| Medium | Unused `CAMERA` permission was declared | Removed; the torch API does not need it |
| Medium | Build tooling was outdated and the distribution was not checksum-pinned | Upgraded and pinned with SHA-256 |
| Low | App backup was enabled | Disabled, with exclusion rules for backup and transfer |
| Low | Torch exceptions were swallowed, so failures looked like success | Narrow handling with a visible failure state |
| Low | Camera lookup could crash at startup | Handled as no torch available |
| Low | Input length was unbounded | Limited to 256 characters |
| Low | Flash rate had no enforced limit | Minimum unit keeps flashes at or below three per second |
| Info | Release shrinking was disabled | Enabled |
| Info | IDE configuration was tracked | Untracked |

## Open

| Severity | Item |
| --- | --- |
| Accepted by design | Optical messages are plaintext and observable in line of sight |
| Low | Release signing is not configured |
| Low | `androidx.core` is one minor line behind because of the compileSdk 36 target |
| Info | Earlier wrapper JAR remains in Git history. Its checksum matches an official Gradle release, so it is not a compromise indicator |
| Info | Behavior on physical devices was not re-verified for this release |
