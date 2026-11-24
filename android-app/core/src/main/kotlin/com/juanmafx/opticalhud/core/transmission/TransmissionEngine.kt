package com.juanmafx.opticalhud.core.transmission

import com.juanmafx.opticalhud.core.morse.MorseEncoder
import com.juanmafx.opticalhud.core.timing.FlashStep
import com.juanmafx.opticalhud.core.timing.TransmissionPlanner

class TransmissionEngine(
    private val torch: TorchController,
    private val scheduler: StepScheduler,
    private val encoder: MorseEncoder = MorseEncoder(),
    private val planner: TransmissionPlanner = TransmissionPlanner()
) {
    var listener: TransmissionListener? = null

    var isTransmitting: Boolean = false
        private set

    fun transmit(text: String): TransmissionResult {
        stop()
        if (text.length > MAX_INPUT_LENGTH) return TransmissionResult.MessageTooLong
        if (!torch.isAvailable) return TransmissionResult.TorchUnavailable

        val message = encoder.encode(text)
        if (message.isEmpty) return TransmissionResult.EmptyMessage

        val steps = planner.plan(message)
        isTransmitting = true
        var delayMs = 0L
        steps.forEachIndexed { index, step ->
            val progressPercent = (index + 1) * PERCENT / steps.size
            scheduler.schedule(delayMs) { execute(step, progressPercent) }
            delayMs += step.durationMs
        }
        scheduler.schedule(delayMs, ::complete)
        return TransmissionResult.Started(message)
    }

    fun stop() {
        if (!isTransmitting) return
        scheduler.cancelAll()
        torch.setEnabled(false)
        isTransmitting = false
    }

    private fun execute(step: FlashStep, progressPercent: Int) {
        if (torch.setEnabled(step.enabled)) {
            listener?.onStep(step, progressPercent)
        } else {
            stop()
            listener?.onFailed()
        }
    }

    private fun complete() {
        val turnedOff = torch.setEnabled(false)
        isTransmitting = false
        if (turnedOff) listener?.onCompleted() else listener?.onFailed()
    }

    companion object {
        const val MAX_INPUT_LENGTH = 256
        private const val PERCENT = 100
    }
}
