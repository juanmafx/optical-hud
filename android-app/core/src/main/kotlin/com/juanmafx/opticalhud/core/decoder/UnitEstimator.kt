package com.juanmafx.opticalhud.core.decoder

class UnitEstimator(private val nominalUnitMs: Double) {

    init {
        require(nominalUnitMs > 0.0) { "nominalUnitMs must be positive" }
    }

    fun estimate(litDurations: List<Double>): Double {
        if (litDurations.size < 2) return nominalUnitMs
        val sorted = litDurations.sorted()
        val total = sorted.sum()
        var prefix = 0.0
        var bestScore = Double.NEGATIVE_INFINITY
        var bestSplit = 0
        var bestPrefix = 0.0

        for (split in 1 until sorted.size) {
            prefix += sorted[split - 1]
            val suffix = total - prefix
            val score = prefix * prefix / split + suffix * suffix / (sorted.size - split)
            if (score > bestScore) {
                bestScore = score
                bestSplit = split
                bestPrefix = prefix
            }
        }

        val dashCount = sorted.size - bestSplit
        val dashTotal = total - bestPrefix
        val ratio = (dashTotal / dashCount) / (bestPrefix / bestSplit)
        if (ratio !in MIN_DASH_DOT_RATIO..MAX_DASH_DOT_RATIO) return nominalUnitMs
        return (bestPrefix + DASH_WEIGHT * dashTotal) / (bestSplit + DASH_WEIGHT * DASH_WEIGHT * dashCount)
    }

    private companion object {
        const val MIN_DASH_DOT_RATIO = 2.0
        const val MAX_DASH_DOT_RATIO = 4.5
        const val DASH_WEIGHT = 3.0
    }
}
