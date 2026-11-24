package com.juanmafx.opticalhud.core.decoder

import com.juanmafx.opticalhud.core.channel.Pulse
import com.juanmafx.opticalhud.core.morse.MorseAlphabet
import com.juanmafx.opticalhud.core.morse.MorseDecoder

class PulseDecoder(nominalUnitMs: Double) {
    private val unitEstimator = UnitEstimator(nominalUnitMs)

    fun decode(pulses: List<Pulse>): String {
        val unitMs = unitEstimator.estimate(pulses.filter { it.lit }.map { it.durationMs })
        val output = StringBuilder()
        val code = StringBuilder()

        for (pulse in pulses) {
            when {
                pulse.lit -> code.append(
                    if (pulse.durationMs < DOT_DASH_BOUNDARY_UNITS * unitMs) MorseAlphabet.DOT else MorseAlphabet.DASH
                )
                pulse.durationMs < SYMBOL_LETTER_BOUNDARY_UNITS * unitMs -> Unit
                pulse.durationMs < LETTER_WORD_BOUNDARY_UNITS * unitMs -> flush(code, output)
                else -> {
                    flush(code, output)
                    output.append(' ')
                }
            }
        }
        flush(code, output)
        return output.toString().trim()
    }

    private fun flush(code: StringBuilder, output: StringBuilder) {
        if (code.isEmpty()) return
        output.append(MorseAlphabet.characterOf(code.toString()) ?: MorseDecoder.UNKNOWN_CHARACTER)
        code.clear()
    }

    private companion object {
        const val DOT_DASH_BOUNDARY_UNITS = 2.0
        const val SYMBOL_LETTER_BOUNDARY_UNITS = 2.0
        const val LETTER_WORD_BOUNDARY_UNITS = 5.0
    }
}
