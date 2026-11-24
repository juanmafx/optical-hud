package com.juanmafx.opticalhud.core.channel

import kotlin.math.max
import kotlin.math.roundToLong

class OpticalChannel(
    private val jitterSigmaMs: Double,
    private val frameIntervalMs: Double = 0.0
) {
    init {
        require(jitterSigmaMs >= 0.0) { "jitterSigmaMs must not be negative" }
        require(frameIntervalMs >= 0.0) { "frameIntervalMs must not be negative" }
    }

    fun transmit(pulses: List<Pulse>, random: SeededRandom): List<Pulse> = pulses.map { pulse ->
        val jittered = pulse.durationMs + jitterSigmaMs * random.nextGaussian()
        pulse.copy(durationMs = quantize(max(jittered, MIN_DURATION_MS)))
    }

    private fun quantize(durationMs: Double): Double {
        if (frameIntervalMs == 0.0) return durationMs
        val frames = max((durationMs / frameIntervalMs).roundToLong(), 1L)
        return frames * frameIntervalMs
    }

    private companion object {
        const val MIN_DURATION_MS = 1.0
    }
}
