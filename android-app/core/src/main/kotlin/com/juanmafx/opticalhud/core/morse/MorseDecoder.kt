package com.juanmafx.opticalhud.core.morse

class MorseDecoder {

    fun decode(morse: String): String =
        morse.trim()
            .split(MorseMessage.WORD_SEPARATOR_MORSE)
            .filter { it.isNotBlank() }
            .joinToString(MorseMessage.WORD_SEPARATOR_TEXT) { word ->
                word.trim()
                    .split(MorseMessage.LETTER_SEPARATOR_MORSE)
                    .filter { it.isNotEmpty() }
                    .map { MorseAlphabet.characterOf(it) ?: UNKNOWN_CHARACTER }
                    .joinToString("")
            }

    companion object {
        const val UNKNOWN_CHARACTER = '?'
    }
}
