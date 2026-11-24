package com.juanmafx.opticalhud.core.analysis

import com.juanmafx.opticalhud.core.channel.OpticalChannel
import com.juanmafx.opticalhud.core.channel.SeededRandom
import com.juanmafx.opticalhud.core.channel.toPulses
import com.juanmafx.opticalhud.core.decoder.PulseDecoder
import com.juanmafx.opticalhud.core.morse.MorseAlphabet
import com.juanmafx.opticalhud.core.morse.MorseEncoder
import com.juanmafx.opticalhud.core.timing.MorseTiming
import com.juanmafx.opticalhud.core.timing.TransmissionPlanner

data class ExperimentConfig(
    val seed: Long = 42L,
    val trialsPerPoint: Int = 500,
    val unitMs: Long = MorseTiming.DEFAULT_UNIT_MS,
    val frameIntervalMs: Double = 1000.0 / 30.0,
    val messageLength: Int = 24,
    val jitterFractions: List<Double> = listOf(0.0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4, 0.5)
) {
    init {
        require(trialsPerPoint > 0) { "trialsPerPoint must be positive" }
        require(unitMs >= MorseTiming.MIN_UNIT_MS) { "unitMs must be at least ${MorseTiming.MIN_UNIT_MS}" }
        require(frameIntervalMs.isFinite() && frameIntervalMs >= 0.0) {
            "frameIntervalMs must be finite and non-negative"
        }
        require(messageLength > 0) { "messageLength must be positive" }
        require(jitterFractions.isNotEmpty() && jitterFractions.all { it >= 0.0 }) {
            "jitterFractions must be non-empty and non-negative"
        }
    }
}

data class ExperimentPoint(
    val jitterFraction: Double,
    val jitterSigmaMs: Double,
    val trials: Int,
    val successfulMessages: Int,
    val messageSuccessRate: Double,
    val successInterval: ConfidenceInterval,
    val characterErrorRate: Double
)

class ChannelExperiment(private val config: ExperimentConfig) {
    private val encoder = MorseEncoder()
    private val planner = TransmissionPlanner(MorseTiming(config.unitMs))
    private val decoder = PulseDecoder(config.unitMs.toDouble())
    private val symbols = MorseAlphabet.codes.keys.toList()

    fun run(): List<ExperimentPoint> {
        val root = SeededRandom(config.seed)
        return config.jitterFractions.map { fraction -> runPoint(fraction, SeededRandom(root.nextLong())) }
    }

    private fun runPoint(fraction: Double, random: SeededRandom): ExperimentPoint {
        val sigmaMs = fraction * config.unitMs
        val channel = OpticalChannel(sigmaMs, config.frameIntervalMs)
        var successes = 0
        var editErrors = 0L
        var referenceCharacters = 0L

        repeat(config.trialsPerPoint) {
            val reference = randomMessage(random)
            val message = encoder.encode(reference)
            val received = decoder.decode(channel.transmit(planner.plan(message).toPulses(), random))
            if (received == message.text) successes++
            editErrors += Statistics.editDistance(message.text, received)
            referenceCharacters += message.text.length
        }

        return ExperimentPoint(
            jitterFraction = fraction,
            jitterSigmaMs = sigmaMs,
            trials = config.trialsPerPoint,
            successfulMessages = successes,
            messageSuccessRate = successes.toDouble() / config.trialsPerPoint,
            successInterval = Statistics.wilsonInterval(successes, config.trialsPerPoint),
            characterErrorRate = editErrors.toDouble() / referenceCharacters
        )
    }

    private fun randomMessage(random: SeededRandom): String {
        val builder = StringBuilder(config.messageLength)
        for (position in 0 until config.messageLength) {
            val insertSpace = position in 1 until config.messageLength - 1 &&
                builder.last() != ' ' &&
                random.nextInt(WORD_BREAK_ODDS) == 0
            builder.append(if (insertSpace) ' ' else symbols[random.nextInt(symbols.size)])
        }
        return builder.toString()
    }

    private companion object {
        const val WORD_BREAK_ODDS = 6
    }
}
