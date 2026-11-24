package com.juanmafx.opticalhud.core.analysis

import kotlin.math.min
import kotlin.math.sqrt

data class ConfidenceInterval(val lower: Double, val upper: Double)

object Statistics {
    const val Z_95 = 1.959963984540054

    fun wilsonInterval(successes: Int, trials: Int, z: Double = Z_95): ConfidenceInterval {
        require(trials > 0) { "trials must be positive" }
        require(successes in 0..trials) { "successes must be within 0..trials" }
        val proportion = successes.toDouble() / trials
        val zSquared = z * z
        val denominator = 1.0 + zSquared / trials
        val center = (proportion + zSquared / (2.0 * trials)) / denominator
        val margin = z * sqrt(proportion * (1.0 - proportion) / trials + zSquared / (4.0 * trials * trials)) / denominator
        return ConfidenceInterval(
            lower = if (successes == 0) 0.0 else center - margin,
            upper = if (successes == trials) 1.0 else center + margin
        )
    }

    fun editDistance(source: String, target: String): Int {
        val (longer, shorter) = if (source.length >= target.length) source to target else target to source
        var previous = IntArray(shorter.length + 1) { it }
        var current = IntArray(shorter.length + 1)

        for (row in 1..longer.length) {
            current[0] = row
            for (column in 1..shorter.length) {
                val substitution = previous[column - 1] + if (longer[row - 1] == shorter[column - 1]) 0 else 1
                current[column] = min(substitution, min(previous[column], current[column - 1]) + 1)
            }
            val swap = previous
            previous = current
            current = swap
        }
        return previous[shorter.length]
    }
}
