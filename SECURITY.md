# Security Policy

## Supported Versions

| Version | Supported |
| --- | --- |
| 2.x | Yes |
| Earlier than 2.0 | No |

## Reporting a Vulnerability

Use GitHub private vulnerability reporting on this repository if it is enabled. Otherwise, open a public issue that asks for a private contact and leave out all technical detail.

Please include the affected version, the impact, and reproduction steps in the private report. This is a personal project, so no response time is guaranteed.

## Security Model

Optical HUD transmits **plaintext** light that anyone in line of sight can read or spoof. It provides no confidentiality, integrity, or authentication, and it must not be used for secrets or safety-critical signaling.
