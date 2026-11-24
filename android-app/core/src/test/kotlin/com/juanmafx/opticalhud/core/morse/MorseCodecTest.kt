package com.juanmafx.opticalhud.core.morse

import com.juanmafx.opticalhud.core.support.arbitraryText
import com.juanmafx.opticalhud.core.support.canonicalText
import com.juanmafx.opticalhud.core.support.propertyConfig
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MorseCodecTest {
    private val encoder = MorseEncoder()
    private val decoder = MorseDecoder()

    @Test
    fun `encodes the distress signal`() {
        assertEquals("... --- ...", encoder.encode("SOS").morse)
    }

    @Test
    fun `separates words with a slash`() {
        assertEquals(".- / -...", encoder.encode("a b").morse)
    }

    @Test
    fun `collapses leading trailing and repeated whitespace`() {
        assertEquals(".- / -...", encoder.encode("  a \n\t b  ").morse)
    }

    @Test
    fun `counts unsupported characters`() {
        val message = encoder.encode("a!b?")
        assertEquals("AB", message.text)
        assertEquals(2, message.droppedCharacters)
    }

    @Test
    fun `blank and unsupported input yields an empty message`() {
        assertTrue(encoder.encode("   ").isEmpty)
        assertTrue(encoder.encode("!!!").isEmpty)
    }

    @Test
    fun `decoder marks unknown codes`() {
        assertEquals("A?", decoder.decode(".- ......"))
    }

    @Test
    fun `decoding inverts encoding for canonical text`() = runBlocking {
        checkAll(propertyConfig(), canonicalText) { text ->
            assertEquals(text, decoder.decode(encoder.encode(text).morse))
        }
    }

    @Test
    fun `encoding is idempotent over its normalized text`() = runBlocking {
        checkAll(propertyConfig(), arbitraryText) { text ->
            val first = encoder.encode(text)
            assertEquals(first.morse, encoder.encode(first.text).morse)
        }
    }

    @Test
    fun `morse output only contains the channel alphabet`() = runBlocking {
        checkAll(propertyConfig(), arbitraryText) { text ->
            assertTrue(encoder.encode(text).morse.all { it in ".-/ " })
        }
    }
}
