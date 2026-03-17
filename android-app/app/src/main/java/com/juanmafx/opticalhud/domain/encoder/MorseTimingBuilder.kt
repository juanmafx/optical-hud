package com.juanmafx.opticalhud.domain.encoder

class MorseTimingBuilder(
    private val dotMs: Long = 200L,
    private val dashMs: Long = 600L,
    private val symbolGapMs: Long = 200L,
    private val letterGapMs: Long = 600L,
    private val wordGapMs: Long = 1000L
) {

    fun build(tokens: List<String>): List<FlashStep> {
        val steps = mutableListOf<FlashStep>()

        for (index in tokens.indices) {
            val token = tokens[index]

            if (token == " ") {
                if (steps.isNotEmpty()) {
                    steps.add(FlashStep(false, wordGapMs))
                }
                continue
            }

            for (symbolIndex in token.indices) {
                when (token[symbolIndex]) {
                    '.' -> steps.add(FlashStep(true, dotMs))
                    '-' -> steps.add(FlashStep(true, dashMs))
                    else -> continue
                }

                if (symbolIndex < token.lastIndex) {
                    steps.add(FlashStep(false, symbolGapMs))
                }
            }

            val nextToken = tokens.getOrNull(index + 1)
            if (nextToken != null && nextToken != " ") {
                steps.add(FlashStep(false, letterGapMs))
            }
        }

        return steps
    }
}
