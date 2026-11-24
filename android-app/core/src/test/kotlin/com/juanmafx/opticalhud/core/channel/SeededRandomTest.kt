package com.juanmafx.opticalhud.core.channel

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class SeededRandomTest {

    @Test
    fun `matches the splitmix64 reference vector`() {
        val random = SeededRandom(1234567L)
        val expected = listOf(
            "6457827717110365317",
            "3203168211198807973",
            "9817491932198370423",
            "4593380528125082431",
            "16408922859458223821"
        )
        assertEquals(expected, List(5) { random.nextLong().toULong().toString() })
    }

    @Test
    fun `same seed reproduces the same stream`() {
        val first = SeededRandom(99L)
        val second = SeededRandom(99L)
        repeat(1000) { assertEquals(first.nextLong(), second.nextLong()) }
    }

    @Test
    fun `doubles stay in the unit interval`() {
        val random = SeededRandom(7L)
        repeat(100_000) { assertTrue(random.nextDouble() in 0.0..<1.0) }
    }

    @Test
    fun `bounded integers pass a chi squared uniformity check`() {
        val random = SeededRandom(11L)
        val bins = 36
        val samples = 360_000
        val counts = IntArray(bins)
        repeat(samples) { counts[random.nextInt(bins)]++ }
        val expected = samples.toDouble() / bins
        val chiSquared = counts.sumOf { (it - expected).pow(2) / expected }
        assertTrue(chiSquared < CHI_SQUARED_35_DF_CRITICAL_0_999, "chiSquared=$chiSquared")
    }

    @Test
    fun `gaussian samples match standard normal moments`() {
        val random = SeededRandom(13L)
        val samples = DoubleArray(200_000) { random.nextGaussian() }
        val mean = samples.average()
        val variance = samples.sumOf { (it - mean).pow(2) } / (samples.size - 1)
        val skewness = samples.sumOf { ((it - mean) / sqrt(variance)).pow(3) } / samples.size
        val excessKurtosis = samples.sumOf { ((it - mean) / sqrt(variance)).pow(4) } / samples.size - 3.0

        assertTrue(abs(mean) < 0.01, "mean=$mean")
        assertTrue(abs(variance - 1.0) < 0.02, "variance=$variance")
        assertTrue(abs(skewness) < 0.03, "skewness=$skewness")
        assertTrue(abs(excessKurtosis) < 0.06, "excessKurtosis=$excessKurtosis")
    }

    @Test
    fun `gaussian samples pass a kolmogorov smirnov check`() {
        val random = SeededRandom(17L)
        val samples = DoubleArray(20_000) { random.nextGaussian() }.sorted()
        val statistic = samples.indices.maxOf { index ->
            val cdf = standardNormalCdf(samples[index])
            maxOf(abs((index + 1.0) / samples.size - cdf), abs(cdf - index.toDouble() / samples.size))
        }
        assertTrue(statistic < 1.628 / sqrt(samples.size.toDouble()), "D=$statistic")
    }

    private fun standardNormalCdf(value: Double): Double {
        var term = value
        var sum = value
        var index = 1
        while (abs(term) > 1e-14 && index < 300) {
            term *= value * value / (2 * index + 1)
            sum += term
            index++
        }
        val density = kotlin.math.exp(-value * value / 2.0) / sqrt(2.0 * Math.PI)
        return (0.5 + density * sum).coerceIn(0.0, 1.0)
    }

    private companion object {
        const val CHI_SQUARED_35_DF_CRITICAL_0_999 = 66.62
    }
}
