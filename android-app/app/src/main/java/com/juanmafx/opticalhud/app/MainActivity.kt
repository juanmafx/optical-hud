package com.juanmafx.opticalhud.app

import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.juanmafx.opticalhud.R
import com.juanmafx.opticalhud.core.morse.MorseMessage
import com.juanmafx.opticalhud.core.timing.FlashStep
import com.juanmafx.opticalhud.core.transmission.TransmissionEngine
import com.juanmafx.opticalhud.core.transmission.TransmissionListener
import com.juanmafx.opticalhud.core.transmission.TransmissionResult
import com.juanmafx.opticalhud.data.scheduler.HandlerStepScheduler
import com.juanmafx.opticalhud.data.torch.AndroidTorchController
import com.juanmafx.opticalhud.features.transmitter.MorseHighlighter

class MainActivity : AppCompatActivity(), TransmissionListener {

    private lateinit var engine: TransmissionEngine
    private lateinit var inputText: EditText
    private lateinit var outputMorse: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var currentStatus: TextView
    private var currentMorse: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputText = findViewById(R.id.inputText)
        outputMorse = findViewById(R.id.outputMorse)
        progressBar = findViewById(R.id.transmissionProgress)
        currentStatus = findViewById(R.id.currentStatus)
        inputText.filters = arrayOf(InputFilter.LengthFilter(TransmissionEngine.MAX_INPUT_LENGTH))

        val torch = AndroidTorchController(this)
        engine = TransmissionEngine(torch, HandlerStepScheduler())
        engine.listener = this

        findViewById<Button>(R.id.convertButton).setOnClickListener {
            handleResult(engine.transmit(inputText.text.toString()))
        }

        if (!torch.isAvailable) showToast(R.string.error_no_torch, Toast.LENGTH_LONG)
    }

    override fun onPause() {
        super.onPause()
        engine.stop()
    }

    override fun onDestroy() {
        engine.listener = null
        engine.stop()
        super.onDestroy()
    }

    override fun onStep(step: FlashStep, progressPercent: Int) {
        progressBar.progress = progressPercent
        currentStatus.text = describe(step)
        outputMorse.text = MorseHighlighter.highlight(currentMorse, step)
    }

    override fun onCompleted() {
        currentStatus.setText(R.string.status_done)
        outputMorse.text = currentMorse
        showToast(R.string.toast_transmission_complete)
    }

    override fun onFailed() {
        currentStatus.setText(R.string.status_failed)
        outputMorse.text = currentMorse
        showToast(R.string.error_torch_failed, Toast.LENGTH_LONG)
    }

    private fun handleResult(result: TransmissionResult) {
        when (result) {
            is TransmissionResult.Started -> showStarted(result.message)
            TransmissionResult.EmptyMessage -> showToast(R.string.error_empty_message)
            TransmissionResult.MessageTooLong -> showToast(R.string.error_message_too_long)
            TransmissionResult.TorchUnavailable -> showToast(R.string.error_no_torch, Toast.LENGTH_LONG)
        }
    }

    private fun showStarted(message: MorseMessage) {
        currentMorse = message.morse
        outputMorse.text = currentMorse
        progressBar.progress = 0
        currentStatus.text = if (message.droppedCharacters > 0) {
            resources.getQuantityString(
                R.plurals.status_dropped_characters,
                message.droppedCharacters,
                message.droppedCharacters
            )
        } else {
            ""
        }
    }

    private fun describe(step: FlashStep): String {
        val character = step.character ?: return getString(R.string.status_word_gap)
        val symbol = step.symbol ?: return character.toString()
        return getString(R.string.status_symbol, character.toString(), symbol.toString())
    }

    private fun showToast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
}
