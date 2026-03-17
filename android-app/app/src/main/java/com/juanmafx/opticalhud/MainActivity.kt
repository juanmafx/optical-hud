package com.juanmafx.opticalhud

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private var cameraId: String? = null
    private var isFlashlightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) {
                    Toast.makeText(
                        this,
                        "Camera permission is required to use the flashlight",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "Toggle Flashlight"
            setOnClickListener { toggleFlashlight() }
        }

        layout.addView(button)
        setContentView(layout)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val lensFacing =
                characteristics.get(CameraCharacteristics.LENS_FACING)
            val hasFlash =
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true

            lensFacing == CameraCharacteristics.LENS_FACING_BACK && hasFlash
        }

        if (cameraId == null) {
            Toast.makeText(this, "No back camera with flashlight found", Toast.LENGTH_LONG).show()
        }

        ensureCameraPermission()
    }

    private fun toggleFlashlight() {
        if (!hasCameraPermission()) {
            ensureCameraPermission()
            return
        }

        val id = cameraId ?: run {
            Toast.makeText(this, "Flashlight not available", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            isFlashlightOn = !isFlashlightOn
            cameraManager.setTorchMode(id, isFlashlightOn)
        } catch (e: Exception) {
            isFlashlightOn = false
            Toast.makeText(this, "Failed to toggle flashlight: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        turnFlashlightOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        turnFlashlightOff()
    }

    private fun turnFlashlightOff() {
        val id = cameraId ?: return

        if (!isFlashlightOn) return

        try {
            cameraManager.setTorchMode(id, false)
        } catch (_: Exception) {
        } finally {
            isFlashlightOn = false
        }
    }

    private fun ensureCameraPermission() {
        if (!hasCameraPermission()) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun hasCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
}
