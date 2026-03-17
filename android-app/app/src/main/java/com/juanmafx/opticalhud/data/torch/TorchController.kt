package com.juanmafx.opticalhud.data.torch

interface TorchController {
    fun turnOn()
    fun turnOff()
    fun isAvailable(): Boolean
}
