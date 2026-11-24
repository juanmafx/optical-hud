package com.juanmafx.opticalhud.core.analysis

import com.juanmafx.opticalhud.core.channel.SeededRandom
import com.juanmafx.opticalhud.core.support.canonicalWord
import com.juanmafx.opticalhud.core.support.propertyConfig
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class StatisticsTest {

    @Test
    fun `wilson interval matches a hand computed value`() {
        val interval = Statistics.wilsonInterval(8, 10)
        assertEquals(0.4902, interval.lower, 1e-4)
        assertEquals(0.9433, interval.upper, 1e-4)
    }

    @Test
    fun `wilson interval is pinned at the boundaries`() {
        assertEquals(0.0, Statistics.wilsonInterval(0, 20).lower)
        assertEquals(1.0, Statistics.wilsonInterval(20, 20).upper)
    }

    @Test
    fun `wilson interval contains the observed proportion`() = runBlocking {
        checkAll(propertyConfig(), Arb.int(1..5000), Arb.int(0..5000)) { trials, raw ->
            val successes = raw % (trials + 1)
            val interval = Statistics.wilsonInterval(successes, trials)
            val proportion = successes.toDouble() / trials
            assertTrue(interval.lower in 0.0..proportion && interval.upper in proportion..1.0)
        }
    }

    @Test
    fun `simulated wilson coverage agrees with exact binomial coverage`() {
        val random = SeededRandom(23L)
        val probability = 0.3
        val trials = 100
        val replications = 20_000
        val exactCoverage = exactCoverage(probability, trials)
        var covered = 0
        repeat(replications) {
            val successes = (0 until trials).count { random.nextDouble() < probability }
            val interval = Statistics.wilsonInterval(successes, trials)
            if (probability in interval.lower..interval.upper) covered++
        }
        val standardError = sqrt(exactCoverage * (1.0 - exactCoverage) / replications)
        assertTrue(abs(covered.toDouble() / replications - exactCoverage) < 4.0 * standardError, "covered=$covered")
    }

    private fun exactCoverage(probability: Double, trials: Int): Double {
        var mass = (1.0 - probability).pow(trials)
        var coverage = 0.0
        for (successes in 0..trials) {
            val interval = Statistics.wilsonInterval(successes, trials)
            if (probability in interval.lower..interval.upper) coverage += mass
            mass *= (trials - successes).toDouble() / (successes + 1) * probability / (1.0 - probability)
        }
        return coverage
    }

    @Test
    fun `rejects invalid proportions`() {
        assertThrows(IllegalArgumentException::class.java) { Statistics.wilsonInterval(1, 0) }
        assertThrows(IllegalArgumentException::class.java) { Statistics.wilsonInterval(5, 4) }
    }

    @Test
    fun `edit distance matches known cases`() {
        assertEquals(3, Statistics.editDistance("KITTEN", "SITTING"))
        assertEquals(4, Statistics.editDistance("", "ABCD"))
        assertEquals(0, Statistics.editDistance("", ""))
    }

    @Test
    fun `edit distance is a metric`() = runBlocking {
        checkAll(propertyConfig(), canonicalWord, canonicalWord, canonicalWord) { a, b, c ->
            val ab = Statistics.editDistance(a, b)
            assertEquals(0, Statistics.editDistance(a, a))
            assertEquals(ab, Statistics.editDistance(b, a))
            assertTrue(ab <= Statistics.editDistance(a, c) + Statistics.editDistance(c, b))
            assertTrue(ab <= max(a.length, b.length))
        }
    }
}
