package com.juanmafx.opticalhud.domain.encoder

class MorseEncoder {

    private val map = mapOf(
        'A' to ".-",
        'B' to "-...",
        'C' to "-.-.",
        'D' to "-..",
        'E' to ".",
        'F' to "..-.",
        'G' to "--.",
        'H' to "....",
        'I' to "..",
        'J' to ".---",
        'K' to "-.-",
        'L' to ".-..",
        'M' to "--",
        'N' to "-.",
        'O' to "---",
        'P' to ".--.",
        'Q' to "--.-",
        'R' to ".-.",
        'S' to "...",
        'T' to "-",
        'U' to "..-",
        'V' to "...-",
        'W' to ".--",
        'X' to "-..-",
        'Y' to "-.--",
        'Z' to "--..",
        '0' to "-----",
        '1' to ".----",
        '2' to "..---",
        '3' to "...--",
        '4' to "....-",
        '5' to ".....",
        '6' to "-....",
        '7' to "--...",
        '8' to "---..",
        '9' to "----."
    )

    fun encode(text: String): List<String> {
        val tokens = mutableListOf<String>()

        for (char in text.uppercase()) {
            if (char == ' ') {
                if (tokens.isNotEmpty() && tokens.last() != " ") {
                    tokens.add(" ")
                }
                continue
            }

            val morse = map[char] ?: continue
            tokens.add(morse)
        }

        if (tokens.lastOrNull() == " ") {
            tokens.removeAt(tokens.lastIndex)
        }

        return tokens
    }
}
