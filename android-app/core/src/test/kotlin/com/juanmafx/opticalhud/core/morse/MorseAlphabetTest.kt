package com.juanmafx.opticalhud.core.morse

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MorseAlphabetTest {

    @Test
    fun `covers all latin letters and digits`() {
        assertEquals((('A'..'Z') + ('0'..'9')).toSet(), MorseAlphabet.codes.keys)
    }

    @Test
    fun `codes are unique`() {
        assertEquals(MorseAlphabet.codes.size, MorseAlphabet.codes.values.toSet().size)
    }

    @Test
    fun `dichotomic tree inverts every code`() {
        MorseAlphabet.codes.forEach { (character, code) ->
            assertEquals(character, MorseAlphabet.characterOf(code))
        }
    }

    @Test
    fun `rejects unassigned malformed and oversized codes`() {
        listOf("", "......", "..--", "abc", ".-x").forEach { assertNull(MorseAlphabet.characterOf(it)) }
    }
}
