package com.juanmafx.opticalhud.core.timing

import com.juanmafx.opticalhud.core.morse.MorseEncoder
import com.juanmafx.opticalhud.core.support.arbitraryText
import com.juanmafx.opticalhud.core.support.canonicalText
import com.juanmafx.opticalhud.core.support.propertyConfig
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TransmissionPlannerTest {
    private val encoder = MorseEncoder()
    private val planner = TransmissionPlanner()

    @Test
    fun `follows the itu one three seven unit ratios`() {
        val timing = MorseTiming(200)
        assertEquals(listOf(200L, 600L, 200L, 600L, 1400L), listOf(timing.dotMs, timing.dashMs, timing.symbolGapMs, timing.letterGapMs, timing.wordGapMs))
    }

    @Test
    fun `paris occupies forty three units before its trailing word gap`() {
        val steps = planner.plan(encoder.encode("PARIS"))
        assertEquals(43 * MorseTiming.DEFAULT_UNIT_MS, steps.sumOf { it.durationMs })
    }

    @Test
    fun `default speed is six words per minute`() {
        assertEquals(6.0, MorseTiming().wordsPerMinute, 1e-9)
    }

    @Test
    fun `rejects units that would exceed the flash safety limit`() {
        assertThrows(IllegalArgumentException::class.java) { MorseTiming(MorseTiming.MIN_UNIT_MS - 1) }
    }

    @Test
    fun `word gap highlights the slash`() {
        val message = encoder.encode("E E")
        val gap = planner.plan(message).single { it.character == null }
        assertEquals('/', message.morse[gap.morseRange.first])
    }

    @Test
    fun `lit steps align with their morse symbol after multiple words`() = runBlocking {
        checkAll(propertyConfig(), canonicalText) { text ->
            val message = encoder.encode(text)
            val litSteps = planner.plan(message).filter { it.enabled }
            litSteps.forEach { assertEquals(it.symbol, message.morse[it.morseRange.first]) }
            assertEquals(message.morse.count { it == '.' || it == '-' }, litSteps.size)
        }
    }

    @Test
    fun `every range stays inside the rendered morse for arbitrary input`() = runBlocking {
        checkAll(propertyConfig(), arbitraryText) { text ->
            val message = encoder.encode(text)
            planner.plan(message).forEach {
                assertTrue(it.morseRange.first >= 0 && it.morseRange.last < message.morse.length)
            }
        }
    }

    @Test
    fun `steps alternate between lit and dark and end lit`() = runBlocking {
        checkAll(propertyConfig(), canonicalText) { text ->
            val steps = planner.plan(encoder.encode(text))
            assertTrue(steps.first().enabled && steps.last().enabled)
            steps.zipWithNext().forEach { (current, next) -> assertTrue(current.enabled != next.enabled) }
        }
    }

    @Test
    fun `no one second window holds more than three flashes at the fastest unit`() = runBlocking {
        val fastest = TransmissionPlanner(MorseTiming(MorseTiming.MIN_UNIT_MS))
        checkAll(propertyConfig(200), canonicalText) { text ->
            var clock = 0L
            val onsets = mutableListOf<Long>()
            fastest.plan(encoder.encode(text)).forEach { step ->
                if (step.enabled) onsets += clock
                clock += step.durationMs
            }
            onsets.forEach { start -> assertTrue(onsets.count { it in start until start + 1000 } <= 3) }
        }
    }
}
