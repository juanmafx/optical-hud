package com.juanmafx.opticalhud.core.channel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class OpticalChannelTest {
    private val pulses = List(50_000) { Pulse(it % 2 == 0, 1000.0) }

    @Test
    fun `an ideal channel is the identity`() {
        assertEquals(pulses, OpticalChannel(0.0).transmit(pulses, SeededRandom(1L)))
    }

    @Test
    fun `jitter has the configured mean and deviation`() {
        val durations = OpticalChannel(25.0).transmit(pulses, SeededRandom(2L)).map { it.durationMs }
        val mean = durations.average()
        val deviation = sqrt(durations.sumOf { (it - mean).pow(2) } / (durations.size - 1))
        assertTrue(abs(mean - 1000.0) < 0.5, "mean=$mean")
        assertTrue(abs(deviation - 25.0) < 0.5, "deviation=$deviation")
    }

    @Test
    fun `quantization lands on whole frames and never erases a pulse`() {
        val interval = 1000.0 / 30.0
        val received = OpticalChannel(400.0, interval).transmit(pulses.take(5000), SeededRandom(3L))
        received.forEach { pulse ->
            val frames = pulse.durationMs / interval
            assertTrue(abs(frames - Math.round(frames)) < 1e-6)
            assertTrue(frames >= 1.0 - 1e-6)
        }
    }

    @Test
    fun `preserves pulse polarity and count`() {
        val received = OpticalChannel(50.0).transmit(pulses, SeededRandom(4L))
        assertEquals(pulses.map { it.lit }, received.map { it.lit })
    }

    @Test
    fun `rejects negative parameters`() {
        assertThrows(IllegalArgumentException::class.java) { OpticalChannel(-1.0) }
        assertThrows(IllegalArgumentException::class.java) { OpticalChannel(0.0, -1.0) }
    }
}
