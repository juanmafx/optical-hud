package com.juanmafx.opticalhud.app

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.juanmafx.opticalhud.R
import com.juanmafx.opticalhud.data.torch.AndroidTorchController
import com.juanmafx.opticalhud.domain.encoder.MorseEncoder
import com.juanmafx.opticalhud.domain.encoder.MorseTimingBuilder
import com.juanmafx.opticalhud.features.transmitter.TransmitterController

class MainActivity : AppCompatActivity() {

    private lateinit var transmitterController: TransmitterController
    private val morseEncoder = MorseEncoder()
    private var currentMorseText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val torchController = AndroidTorchController(this)
        transmitterController = TransmitterController(
            torchController = torchController,
            morseEncoder = morseEncoder,
            morseTimingBuilder = MorseTimingBuilder()
        )

        val inputText = findViewById<EditText>(R.id.inputText)
        val convertButton = findViewById<Button>(R.id.convertButton)
        val outputMorse = findViewById<TextView>(R.id.outputMorse)
        val progressBar = findViewById<ProgressBar>(R.id.transmissionProgress)
        val currentStatus = findViewById<TextView>(R.id.currentStatus)

        transmitterController.onStepUpdate = { step, progress ->
            progressBar.progress = progress
            currentStatus.text = step.label ?: ""

            val spannable = SpannableString(currentMorseText)
            
            // Highlight completed part (before current range)
            step.range?.let { range ->
                if (range.first > 0) {
                    spannable.setSpan(
                        ForegroundColorSpan(Color.GRAY),
                        0,
                        range.first,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                
                // Highlight active symbol
                val activeColor = if (step.enabled) Color.RED else Color.BLUE
                spannable.setSpan(
                    ForegroundColorSpan(activeColor),
                    range.first,
                    range.last + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            
            outputMorse.text = spannable
        }

        transmitterController.onCompletion = {
            currentStatus.text = "Done"
            outputMorse.text = currentMorseText // Reset to original color or keep gray
            Toast.makeText(this, "Transmission complete", Toast.LENGTH_SHORT).show()
        }

        convertButton.setOnClickListener {
            val plainText = inputText.text.toString()
            if (plainText.isNotBlank()) {
                currentMorseText = morseEncoder.encode(plainText)
                outputMorse.text = currentMorseText
                progressBar.progress = 0
                transmitterController.transmit(plainText)
            } else {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }

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
