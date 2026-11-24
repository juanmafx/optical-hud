package com.juanmafx.opticalhud.core.morse

class MorseEncoder {

    fun encode(text: String): MorseMessage {
        val words = mutableListOf<String>()
        val currentWord = StringBuilder()
        var droppedCharacters = 0

        for (character in text.uppercase()) {
            when {
                character.isWhitespace() -> flush(currentWord, words)
                MorseAlphabet.codeOf(character) != null -> currentWord.append(character)
                else -> droppedCharacters++
            }
        }
        flush(currentWord, words)
        return MorseMessage(words, droppedCharacters)
    }

    private fun flush(currentWord: StringBuilder, words: MutableList<String>) {
        if (currentWord.isNotEmpty()) {
            words.add(currentWord.toString())
            currentWord.clear()
        }
    }
}
