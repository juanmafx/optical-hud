package com.juanmafx.opticalhud.data.torch

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

class AndroidTorchController(context: Context) : TorchController {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraId: String? = cameraManager.cameraIdList.firstOrNull { id ->
        val characteristics = cameraManager.getCameraCharacteristics(id)
        val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
        val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        lensFacing == CameraCharacteristics.LENS_FACING_BACK && hasFlash
    }

    override fun turnOn() {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, true)
        } catch (_: Exception) {
        }
    }

    override fun turnOff() {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, false)
        } catch (_: Exception) {
        }
    }

    override fun isAvailable(): Boolean = cameraId != null
}
