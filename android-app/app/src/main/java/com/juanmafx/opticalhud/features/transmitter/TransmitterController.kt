package com.juanmafx.opticalhud.features.transmitter

import android.os.Handler
import android.os.Looper
import com.juanmafx.opticalhud.data.torch.TorchController
import com.juanmafx.opticalhud.domain.encoder.MorseEncoder
import com.juanmafx.opticalhud.domain.encoder.MorseTimingBuilder

class TransmitterController(
    private val torchController: TorchController,
    private val morseEncoder: MorseEncoder,
    private val morseTimingBuilder: MorseTimingBuilder
) {

    private val handler = Handler(Looper.getMainLooper())

    fun transmit(message: String) {
        stop()

        val tokens = morseEncoder.encode(message)
        val steps = morseTimingBuilder.build(tokens)

        var delayMs = 0L

        for (step in steps) {
            handler.postDelayed({
                if (step.enabled) {
                    torchController.turnOn()
                } else {
                    torchController.turnOff()
                }
            }, delayMs)
            delayMs += step.durationMs
        }

        handler.postDelayed({
            torchController.turnOff()
        }, delayMs)
    }

    fun stop() {
        handler.removeCallbacksAndMessages(null)
        torchController.turnOff()
    }
}
