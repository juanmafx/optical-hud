package com.juanmafx.opticalhud.core.transmission

import com.juanmafx.opticalhud.core.support.canonicalText
import com.juanmafx.opticalhud.core.support.propertyConfig
import com.juanmafx.opticalhud.core.timing.FlashStep
import io.kotest.property.checkAll
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TransmissionEngineTest {

    private class VirtualScheduler : StepScheduler {
        private val queue = mutableListOf<Pair<Long, () -> Unit>>()

        override fun schedule(delayMs: Long, action: () -> Unit) {
            queue += delayMs to action
        }

        override fun cancelAll() = queue.clear()

        fun advanceUntilIdle() {
            while (queue.isNotEmpty()) {
                val next = queue.minBy { it.first }
                queue.remove(next)
                next.second()
            }
        }

        fun advanceSteps(count: Int) {
            repeat(count) {
                val next = queue.minBy { it.first }
                queue.remove(next)
                next.second()
            }
        }

        val pending: Int get() = queue.size
    }

    private class FakeTorch(
        override val isAvailable: Boolean = true,
        private val failAfterCalls: Int = Int.MAX_VALUE
    ) : TorchController {
        val calls = mutableListOf<Boolean>()

        override fun setEnabled(enabled: Boolean): Boolean {
            calls += enabled
            return calls.size <= failAfterCalls
        }
    }

    private class RecordingListener : TransmissionListener {
        val steps = mutableListOf<Pair<FlashStep, Int>>()
        var completed = 0
        var failed = 0

        override fun onStep(step: FlashStep, progressPercent: Int) {
            steps += step to progressPercent
        }

        override fun onCompleted() {
            completed++
        }

        override fun onFailed() {
            failed++
        }
    }

    @Test
    fun `completes with the torch off and full progress`() {
        val torch = FakeTorch()
        val scheduler = VirtualScheduler()
        val listener = RecordingListener()
        val engine = TransmissionEngine(torch, scheduler).also { it.listener = listener }

        assertInstanceOf(TransmissionResult.Started::class.java, engine.transmit("SOS"))
        scheduler.advanceUntilIdle()

        assertFalse(torch.calls.last())
        assertEquals(100, listener.steps.last().second)
        assertEquals(1, listener.completed)
        assertFalse(engine.isTransmitting)
    }

    @Test
    fun `stop cancels pending steps and turns the torch off`() {
        val torch = FakeTorch()
        val scheduler = VirtualScheduler()
        val listener = RecordingListener()
        val engine = TransmissionEngine(torch, scheduler).also { it.listener = listener }

        engine.transmit("SOS")
        scheduler.advanceSteps(1)
        engine.stop()

        assertEquals(0, scheduler.pending)
        assertFalse(torch.calls.last())
        assertEquals(0, listener.completed)
    }

    @Test
    fun `stop while idle does not touch the torch`() {
        val torch = FakeTorch()
        TransmissionEngine(torch, VirtualScheduler()).stop()
        assertTrue(torch.calls.isEmpty())
    }

    @Test
    fun `torch failure aborts and reports once`() {
        val torch = FakeTorch(failAfterCalls = 2)
        val scheduler = VirtualScheduler()
        val listener = RecordingListener()
        val engine = TransmissionEngine(torch, scheduler).also { it.listener = listener }

        engine.transmit("SOS")
        scheduler.advanceUntilIdle()

        assertEquals(1, listener.failed)
        assertEquals(0, listener.completed)
        assertFalse(torch.calls.last())
        assertFalse(engine.isTransmitting)
    }

    @Test
    fun `restarting replaces the previous transmission`() {
        val scheduler = VirtualScheduler()
        val listener = RecordingListener()
        val engine = TransmissionEngine(FakeTorch(), scheduler).also { it.listener = listener }

        engine.transmit("SOS")
        engine.transmit("E")
        scheduler.advanceUntilIdle()

        assertEquals(1, listener.steps.size)
        assertEquals(1, listener.completed)
    }

    @Test
    fun `rejects invalid requests without touching the torch`() {
        val torch = FakeTorch()
        val engine = TransmissionEngine(torch, VirtualScheduler())

        assertEquals(TransmissionResult.EmptyMessage, engine.transmit("!!!"))
        assertEquals(TransmissionResult.MessageTooLong, engine.transmit("A".repeat(TransmissionEngine.MAX_INPUT_LENGTH + 1)))
        assertEquals(TransmissionResult.TorchUnavailable, TransmissionEngine(FakeTorch(isAvailable = false), VirtualScheduler()).transmit("A"))
        assertTrue(torch.calls.isEmpty())
    }

    @Test
    fun `progress is monotonic and the torch always ends off`() = runBlocking {
        checkAll(propertyConfig(200), canonicalText) { text ->
            val torch = FakeTorch()
            val scheduler = VirtualScheduler()
            val listener = RecordingListener()
            TransmissionEngine(torch, scheduler).also { it.listener = listener }.transmit(text)
            scheduler.advanceUntilIdle()

            val progress = listener.steps.map { it.second }
            assertEquals(progress.sorted(), progress)
            assertEquals(100, progress.last())
            assertFalse(torch.calls.last())
        }
    }
}
