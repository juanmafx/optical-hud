package com.juanmafx.opticalhud.data.scheduler

import android.os.Handler
import android.os.Looper
import com.juanmafx.opticalhud.core.transmission.StepScheduler

class HandlerStepScheduler(private val handler: Handler = Handler(Looper.getMainLooper())) : StepScheduler {

    override fun schedule(delayMs: Long, action: () -> Unit) {
        handler.postDelayed(action, delayMs)
    }

    override fun cancelAll() {
        handler.removeCallbacksAndMessages(null)
    }
}
