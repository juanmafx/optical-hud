package com.juanmafx.opticalhud.data.torch

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.juanmafx.opticalhud.core.transmission.TorchController

class AndroidTorchController(context: Context) : TorchController {

    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private val cameraId: String? = findBackCameraWithFlash()

    override val isAvailable: Boolean get() = cameraId != null

    override fun setEnabled(enabled: Boolean): Boolean {
        val id = cameraId ?: return false
        return try {
            cameraManager.setTorchMode(id, enabled)
            true
        } catch (_: CameraAccessException) {
            false
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    private fun findBackCameraWithFlash(): String? = try {
        cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK &&
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    } catch (_: CameraAccessException) {
        null
    }
}
