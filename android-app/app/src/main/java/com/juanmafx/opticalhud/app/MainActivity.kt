package com.juanmafx.opticalhud.app

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.juanmafx.opticalhud.data.torch.AndroidTorchController
import com.juanmafx.opticalhud.domain.encoder.MorseEncoder
import com.juanmafx.opticalhud.domain.encoder.MorseTimingBuilder
import com.juanmafx.opticalhud.features.transmitter.TransmitterController

class MainActivity : AppCompatActivity() {

    private lateinit var transmitterController: TransmitterController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val torchController = AndroidTorchController(this)
        transmitterController = TransmitterController(
            torchController = torchController,
            morseEncoder = MorseEncoder(),
            morseTimingBuilder = MorseTimingBuilder()
        )

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "Transmit SOS"
            setOnClickListener {
                transmitterController.transmit("SOS")
            }
        }

        layout.addView(button)
        setContentView(layout)

        if (!torchController.isAvailable()) {
            Toast.makeText(this, "No back camera with flashlight found", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        transmitterController.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        transmitterController.stop()
    }
}
