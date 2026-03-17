package com.juanmafx.opticalhud.domain.encoder

class MorseEncoder {

    private val morseMap = mapOf(
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
        '9' to "----.",
        ' ' to "/"
    )

    fun encode(text: String): String {
        return text
            .uppercase()
            .mapNotNull { morseMap[it] }
            .joinToString(" ")
    }

    /**
     * For internal transmission use, returning tokens compatible with [MorseTimingBuilder].
     */
    fun encodeToTokens(text: String): List<String> {
        val tokens = mutableListOf<String>()
        for (char in text.uppercase()) {
            if (char == ' ') {
                if (tokens.isNotEmpty() && tokens.last() != " ") {
                    tokens.add(" ")
                }
                continue
            }
            val morse = morseMap[char] ?: continue
            tokens.add(morse)
        }
        return tokens
    }
}
