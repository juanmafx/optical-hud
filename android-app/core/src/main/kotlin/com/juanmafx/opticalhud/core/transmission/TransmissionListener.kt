package com.juanmafx.opticalhud.core.transmission

import com.juanmafx.opticalhud.core.timing.FlashStep

interface TransmissionListener {
    fun onStep(step: FlashStep, progressPercent: Int)
    fun onCompleted()
    fun onFailed()
}
