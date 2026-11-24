package com.juanmafx.opticalhud.core.morse

object MorseAlphabet {
    const val DOT = '.'
    const val DASH = '-'

    val codes: Map<Char, String> = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.",
        'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..",
        'M' to "--", 'N' to "-.", 'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.",
        'S' to "...", 'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..", '0' to "-----", '1' to ".----", '2' to "..---",
        '3' to "...--", '4' to "....-", '5' to ".....", '6' to "-....", '7' to "--...",
        '8' to "---..", '9' to "----."
    )

    private const val MAX_CODE_LENGTH = 5
    private val dichotomicTree = CharArray(1 shl (MAX_CODE_LENGTH + 1)).also { tree ->
        codes.forEach { (character, code) -> tree[heapIndex(code)!!] = character }
    }

    fun codeOf(character: Char): String? = codes[character]

    fun characterOf(code: String): Char? {
        val index = heapIndex(code) ?: return null
        return dichotomicTree[index].takeIf { it != '\u0000' }
    }

    private fun heapIndex(code: String): Int? {
        if (code.isEmpty() || code.length > MAX_CODE_LENGTH) return null
        var index = 1
        for (symbol in code) {
            index = when (symbol) {
                DOT -> index * 2
                DASH -> index * 2 + 1
                else -> return null
            }
        }
        return index
    }
}
