package com.juanmafx.opticalhud.core.timing

import com.juanmafx.opticalhud.core.morse.MorseAlphabet
import com.juanmafx.opticalhud.core.morse.MorseMessage

class TransmissionPlanner(private val timing: MorseTiming = MorseTiming()) {

    fun plan(message: MorseMessage): List<FlashStep> {
        val steps = mutableListOf<FlashStep>()
        var cursor = 0

        message.words.forEachIndexed { wordIndex, word ->
            word.forEachIndexed { letterIndex, character ->
                val code = MorseAlphabet.codes.getValue(character)
                code.forEachIndexed { symbolIndex, symbol ->
                    val range = cursor..cursor
                    val duration = if (symbol == MorseAlphabet.DOT) timing.dotMs else timing.dashMs
                    steps += FlashStep(true, duration, character, symbol, range)
                    if (symbolIndex < code.lastIndex) {
                        steps += FlashStep(false, timing.symbolGapMs, character, symbol, range)
                    }
                    cursor++
                }
                if (letterIndex < word.lastIndex) {
                    steps += FlashStep(false, timing.letterGapMs, character, null, cursor..cursor)
                    cursor += MorseMessage.LETTER_SEPARATOR_MORSE.length
                }
            }
            if (wordIndex < message.words.lastIndex) {
                val slashIndex = cursor + 1
                steps += FlashStep(false, timing.wordGapMs, null, null, slashIndex..slashIndex)
                cursor += MorseMessage.WORD_SEPARATOR_MORSE.length
            }
        }
        return steps
    }
}
