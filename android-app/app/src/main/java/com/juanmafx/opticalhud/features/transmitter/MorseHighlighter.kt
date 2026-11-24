package com.juanmafx.opticalhud.features.transmitter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.juanmafx.opticalhud.core.timing.FlashStep

object MorseHighlighter {

    fun highlight(morse: String, step: FlashStep): CharSequence {
        val spannable = SpannableString(morse)
        val start = step.morseRange.first
        val end = step.morseRange.last + 1
        if (start !in morse.indices || end > morse.length) return spannable

        if (start > 0) {
            spannable.setSpan(ForegroundColorSpan(Color.GRAY), 0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val activeColor = if (step.enabled) Color.RED else Color.BLUE
        spannable.setSpan(ForegroundColorSpan(activeColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }
}
