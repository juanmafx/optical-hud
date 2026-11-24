package com.juanmafx.opticalhud.core.channel

import com.juanmafx.opticalhud.core.timing.FlashStep

data class Pulse(val lit: Boolean, val durationMs: Double)

fun List<FlashStep>.toPulses(): List<Pulse> = map { Pulse(it.enabled, it.durationMs.toDouble()) }
