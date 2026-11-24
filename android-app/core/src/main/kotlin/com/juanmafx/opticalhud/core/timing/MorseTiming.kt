package com.juanmafx.opticalhud.core.timing

data class MorseTiming(val unitMs: Long = DEFAULT_UNIT_MS) {
    init {
        require(unitMs >= MIN_UNIT_MS) { "unitMs must be at least $MIN_UNIT_MS" }
    }

    val dotMs: Long get() = unitMs * DOT_UNITS
    val dashMs: Long get() = unitMs * DASH_UNITS
    val symbolGapMs: Long get() = unitMs * SYMBOL_GAP_UNITS
    val letterGapMs: Long get() = unitMs * LETTER_GAP_UNITS
    val wordGapMs: Long get() = unitMs * WORD_GAP_UNITS
    val wordsPerMinute: Double get() = PARIS_MS_PER_MINUTE_FACTOR / unitMs

    companion object {
        const val DEFAULT_UNIT_MS = 200L
        const val MIN_UNIT_MS = 170L
        const val DOT_UNITS = 1
        const val DASH_UNITS = 3
        const val SYMBOL_GAP_UNITS = 1
        const val LETTER_GAP_UNITS = 3
        const val WORD_GAP_UNITS = 7
        private const val PARIS_MS_PER_MINUTE_FACTOR = 1200.0
    }
}
