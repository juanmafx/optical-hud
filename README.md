# Optical HUD

An off-grid optical communication prototype for Android that uses a smartphone flashlight to transmit data as visible light pulses.

## Overview

Optical HUD explores low-level communication through light instead of radio, Wi-Fi, or cellular networks.

Current prototype features:

- Morse-based flashlight transmission
- Android torch control using `CameraManager`
- Modular Kotlin architecture
- Real-device deployment and APK generation
- Foundation for future camera-based decoding and HUD targeting

## Vision

The long-term goal is to build an offline optical communication system with:

- flashlight transmission
- camera-based reception
- custom binary protocol
- HUD-style aiming assistance
- AR-assisted targeting and stabilization

## Current Status

Prototype phase.

Working today:

- Android app builds successfully
- Runs on a physical Android device
- Torch can transmit Morse patterns such as `SOS`

## Tech Stack

- Kotlin
- Android SDK
- CameraManager
- Gradle
- GitHub

## Architecture

```text
android-app/app/src/main/java/com/juanmafx/opticalhud/
├── app/
│   └── MainActivity.kt
├── data/
│   └── torch/
│       ├── TorchController.kt
│       └── AndroidTorchController.kt
├── domain/
│   └── encoder/
│       ├── FlashStep.kt
│       ├── MorseEncoder.kt
│       └── MorseTimingBuilder.kt
└── features/
    └── transmitter/
        └── TransmitterController.kt
