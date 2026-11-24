package com.juanmafx.opticalhud.core.timing

data class FlashStep(
    val enabled: Boolean,
    val durationMs: Long,
    val character: Char?,
    val symbol: Char?,
    val morseRange: IntRange
)
