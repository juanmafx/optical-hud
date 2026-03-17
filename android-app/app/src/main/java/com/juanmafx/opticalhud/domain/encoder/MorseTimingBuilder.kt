package com.juanmafx.opticalhud.domain.encoder

class MorseTimingBuilder(
    private val dotMs: Long = 200L,
    private val dashMs: Long = 600L,
    private val symbolGapMs: Long = 200L,
    private val letterGapMs: Long = 600L,
    private val wordGapMs: Long = 1000L
) {

    fun build(tokens: List<String>, originalText: String, morseOutput: String): List<FlashStep> {
        val steps = mutableListOf<FlashStep>()
        val filteredText = originalText.uppercase().filter { it.isLetterOrDigit() || it == ' ' }
        var charIndex = 0
        var morseCursor = 0

        for (index in tokens.indices) {
            val token = tokens[index]
            val currentChar = filteredText.getOrNull(charIndex)?.toString() ?: ""

            if (token == " ") {
                if (steps.isNotEmpty()) {
                    steps.add(FlashStep(false, wordGapMs, "Space", morseCursor until morseCursor + 1))
                }
                morseCursor += 2 // "/" and " "
                charIndex++
                continue
            }

            for (symbolIndex in token.indices) {
                val symbol = token[symbolIndex]
                val label = "$currentChar ($symbol)"
                val range = morseCursor until morseCursor + 1

                when (symbol) {
                    '.' -> steps.add(FlashStep(true, dotMs, label, range))
                    '-' -> steps.add(FlashStep(true, dashMs, label, range))
                    else -> continue
                }

                morseCursor++

                if (symbolIndex < token.lastIndex) {
                    steps.add(FlashStep(false, symbolGapMs, label, range))
                }
            }

            val nextToken = tokens.getOrNull(index + 1)
            if (nextToken != null && nextToken != " ") {
                steps.add(FlashStep(false, letterGapMs, currentChar, morseCursor until morseCursor + 1))
                morseCursor++ // space between letters
            }
            charIndex++
        }

        return steps
    }
}
