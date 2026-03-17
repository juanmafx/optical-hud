package com.juanmafx.opticalhud

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashlightOn = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "Transmit SOS"
            setOnClickListener { transmitSOS() }
        }

        layout.addView(button)
        setContentView(layout)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        cameraId = cameraManager.cameraIdList.firstOrNull { id ->
            val characteristics = cameraManager.getCameraCharacteristics(id)
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            lensFacing == CameraCharacteristics.LENS_FACING_BACK && hasFlash
        }

        if (cameraId == null) {
            Toast.makeText(this, "No back camera with flashlight found", Toast.LENGTH_LONG).show()
        }
    }

    private fun transmitSOS() {
        val id = cameraId ?: run {
            Toast.makeText(this, "Flashlight not available", Toast.LENGTH_SHORT).show()
            return
        }

        val pattern = listOf(
            200L, 200L, 200L, 200L, 200L, 600L,   // S: dot dot dot
            600L, 200L, 600L, 200L, 600L, 600L,   // O: dash dash dash
            200L, 200L, 200L, 200L, 200L, 1000L   // S: dot dot dot
        )

        var delay = 0L
        var turnOn = true

        for (duration in pattern) {
            handler.postDelayed({
                try {
                    cameraManager.setTorchMode(id, turnOn)
                    isFlashlightOn = turnOn
                } catch (e: Exception) {
                    Toast.makeText(this, "Flash error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                turnOn = !turnOn
            }, delay)

            delay += duration
        }

        handler.postDelayed({
            turnFlashlightOff()
        }, delay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
        turnFlashlightOff()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        turnFlashlightOff()
    }

    private fun turnFlashlightOff() {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, false)
        } catch (_: Exception) {
        } finally {
            isFlashlightOn = false
        }
    }
}