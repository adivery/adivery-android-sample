@file:JvmName("SystemBars")

package com.adivery.sample

import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

/**
 * Keeps content clear of the status and navigation bars, which apps must handle themselves now that
 * targeting SDK 35+ makes edge to edge mandatory.
 *
 * Callable from the Java samples as `SystemBars.applySystemBarInsets(view)`.
 */
fun View.applySystemBarInsets() {
    // The padding declared in XML is captured once, before the first inset pass, so the insets are
    // added to it rather than replacing it and so repeated passes cannot accumulate.
    val declared = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = declared.left + bars.left,
            top = declared.top + bars.top,
            right = declared.right + bars.right,
            bottom = declared.bottom + bars.bottom,
        )
        insets
    }
}
