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

    private var activeRun = 0

    fun transmit(text: String): TransmissionResult {
        stop()
        if (text.length > MAX_INPUT_LENGTH) return TransmissionResult.MessageTooLong
        if (!torch.isAvailable) return TransmissionResult.TorchUnavailable

        val message = encoder.encode(text)
        if (message.isEmpty) return TransmissionResult.EmptyMessage

        val steps = planner.plan(message)
        isTransmitting = true
        val run = ++activeRun
        scheduler.schedule(0L) { execute(steps, 0, run) }
        return TransmissionResult.Started(message)
    }

    fun stop() {
        if (!isTransmitting) return
        activeRun++
        scheduler.cancelAll()
        torch.setEnabled(false)
        isTransmitting = false
    }

    private fun execute(steps: List<FlashStep>, index: Int, run: Int) {
        val step = steps[index]
        if (!torch.setEnabled(step.enabled)) {
            stop()
            listener?.onFailed()
            return
        }
        listener?.onStep(step, (index + 1) * PERCENT / steps.size)
        if (run != activeRun) return
        val nextIndex = index + 1
        scheduler.schedule(step.durationMs) {
            if (nextIndex < steps.size) execute(steps, nextIndex, run) else complete()
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
