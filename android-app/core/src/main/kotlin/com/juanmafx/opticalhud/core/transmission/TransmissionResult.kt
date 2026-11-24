package com.juanmafx.opticalhud.core.transmission

import com.juanmafx.opticalhud.core.morse.MorseMessage

sealed interface TransmissionResult {
    data class Started(val message: MorseMessage) : TransmissionResult
    data object EmptyMessage : TransmissionResult
    data object MessageTooLong : TransmissionResult
    data object TorchUnavailable : TransmissionResult
}
