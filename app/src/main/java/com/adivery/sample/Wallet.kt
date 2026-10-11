package com.adivery.sample

import android.content.Context
import androidx.core.content.edit

/**
 * The user's balance in the in-app currency the offer wall pays out, backed by
 * [android.content.SharedPreferences].
 *
 * It stands in for wherever your app keeps the user's coins. Nothing here is part of the Adivery
 * integration.
 */
class Wallet(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val balance: Int
        get() = prefs.getInt(KEY_BALANCE, 0)

    fun add(amount: Int) {
        prefs.edit { putInt(KEY_BALANCE, balance + amount) }
    }

    private companion object {
        const val PREFS_NAME = "wallet"
        const val KEY_BALANCE = "balance"
    }
}
