package com.juanmafx.opticalhud.core.transmission

interface TorchController {
    val isAvailable: Boolean
    fun setEnabled(enabled: Boolean): Boolean
}
