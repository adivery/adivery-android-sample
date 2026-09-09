package com.adivery.sample

import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Mirrors Adivery callbacks into an on-screen text view and into Logcat.
 *
 * Adivery delivers every callback on the main thread, so the views can be touched directly here.
 * Shared by the Kotlin and the Java samples.
 */
class AdEventLog(private val output: TextView) {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    fun log(message: String) {
        Log.d(TAG, message)
        output.append("${timeFormat.format(Date())}  $message\n")
        // Posted so the scroll runs after the appended line has been laid out, not for thread safety.
        (output.parent as? ScrollView)?.let { it.post { it.fullScroll(ScrollView.FOCUS_DOWN) } }
    }

    fun clear() {
        output.text = ""
    }

    private companion object {
        const val TAG = "AdiverySample"
    }
}
