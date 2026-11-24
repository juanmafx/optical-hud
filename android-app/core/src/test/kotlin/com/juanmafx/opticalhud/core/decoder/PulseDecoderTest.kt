package com.juanmafx.opticalhud.core.decoder

import com.juanmafx.opticalhud.core.channel.OpticalChannel
import com.juanmafx.opticalhud.core.channel.Pulse
import com.juanmafx.opticalhud.core.channel.SeededRandom
import com.juanmafx.opticalhud.core.channel.toPulses
import com.juanmafx.opticalhud.core.morse.MorseEncoder
import com.juanmafx.opticalhud.core.support.canonicalText
import com.juanmafx.opticalhud.core.support.propertyConfig
import com.juanmafx.opticalhud.core.timing.MorseTiming
import com.juanmafx.opticalhud.core.timing.TransmissionPlanner
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

class PulseDecoderTest {
    private val encoder = MorseEncoder()

    private fun pulses(text: String, unitMs: Long = MorseTiming.DEFAULT_UNIT_MS) =
        TransmissionPlanner(MorseTiming(unitMs)).plan(encoder.encode(text)).toPulses()

    @Test
    fun `decodes an ideal transmission`() {
        assertEquals("SOS HELP 42", PulseDecoder(200.0).decode(pulses("SOS HELP 42")))
    }

    @Test
    fun `empty input decodes to empty text`() {
        assertEquals("", PulseDecoder(200.0).decode(emptyList()))
    }

    @Test
    fun `single class messages fall back to the nominal unit`() {
        assertEquals("EEE", PulseDecoder(200.0).decode(pulses("EEE")))
        assertEquals("TTT", PulseDecoder(200.0).decode(pulses("TTT")))
    }

    @Test
    fun `ideal channel round trips every canonical text`(): Unit = runBlocking {
        checkAll(propertyConfig(), canonicalText) { text ->
            assertEquals(text, PulseDecoder(200.0).decode(pulses(text)))
        }
    }

    @Test
    fun `blind estimation adapts to a sender at a different speed`(): Unit = runBlocking {
        checkAll(propertyConfig(200), canonicalText, Arb.long(170L..400L)) { text, unitMs ->
            val hasBothSymbols = encoder.encode(text).morse.let { '.' in it && '-' in it }
            if (hasBothSymbols) {
                assertEquals(text, PulseDecoder(200.0).decode(pulses(text, unitMs)))
            }
        }
    }

    @Test
    fun `unit estimate recovers the true unit under jitter`() {
        val random = SeededRandom(5L)
        val noisy = OpticalChannel(15.0).transmit(pulses("THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG 0123456789", 250), random)
        val estimate = UnitEstimator(200.0).estimate(noisy.filter(Pulse::lit).map(Pulse::durationMs))
        assertTrue(abs(estimate - 250.0) < 5.0, "estimate=$estimate")
    }

    @Test
    fun `decodes reliably under moderate seeded jitter`() {
        val random = SeededRandom(6L)
        val channel = OpticalChannel(20.0, 1000.0 / 30.0)
        val text = "OPTICAL LINK TEST 2026"
        repeat(200) {
            assertEquals(text, PulseDecoder(200.0).decode(channel.transmit(pulses(text), random)))
        }
    }
}
