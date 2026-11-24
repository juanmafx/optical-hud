package com.juanmafx.opticalhud.core.morse

data class MorseMessage(val words: List<String>, val droppedCharacters: Int) {
    val text: String = words.joinToString(WORD_SEPARATOR_TEXT)
    val morse: String = words.joinToString(WORD_SEPARATOR_MORSE) { word ->
        word.map { MorseAlphabet.codes.getValue(it) }.joinToString(LETTER_SEPARATOR_MORSE)
    }

    val isEmpty: Boolean get() = words.isEmpty()

    companion object {
        const val WORD_SEPARATOR_TEXT = " "
        const val WORD_SEPARATOR_MORSE = " / "
        const val LETTER_SEPARATOR_MORSE = " "
    }
}
