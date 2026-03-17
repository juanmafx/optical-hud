package com.juanmafx.opticalhud.features.transmitter

import android.os.Handler
import android.os.Looper
import com.juanmafx.opticalhud.data.torch.TorchController
import com.juanmafx.opticalhud.domain.encoder.FlashStep
import com.juanmafx.opticalhud.domain.encoder.MorseEncoder
import com.juanmafx.opticalhud.domain.encoder.MorseTimingBuilder

class TransmitterController(
    private val torchController: TorchController,
    private val morseEncoder: MorseEncoder,
    private val morseTimingBuilder: MorseTimingBuilder
) {

    private val handler = Handler(Looper.getMainLooper())
    var onStepUpdate: ((step: FlashStep, progress: Int) -> Unit)? = null
    var onCompletion: (() -> Unit)? = null

    fun transmit(message: String) {
        stop()

        val morseOutput = morseEncoder.encode(message)
        val tokens = morseEncoder.encodeToTokens(message)
        val steps = morseTimingBuilder.build(tokens, message, morseOutput)

        if (steps.isEmpty()) return

        var delayMs = 0L

        for (i in steps.indices) {
            val step = steps[i]
            val progress = ((i + 1).toFloat() / steps.size * 100).toInt()

            handler.postDelayed({
                if (step.enabled) {
                    torchController.turnOn()
                } else {
                    torchController.turnOff()
                }
                onStepUpdate?.invoke(step, progress)
            }, delayMs)
            delayMs += step.durationMs
        }

        handler.postDelayed({
            torchController.turnOff()
            onCompletion?.invoke()
        }, delayMs)
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        torchController.turnOff()
    }
}
