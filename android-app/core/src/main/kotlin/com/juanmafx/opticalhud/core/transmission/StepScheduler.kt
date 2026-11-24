package com.juanmafx.opticalhud.core.transmission

interface StepScheduler {
    fun schedule(delayMs: Long, action: () -> Unit)
    fun cancelAll()
}
