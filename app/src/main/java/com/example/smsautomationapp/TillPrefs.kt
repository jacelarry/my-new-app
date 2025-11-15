package com.example.smsautomationapp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object TillPrefs {
    private const val PREFS_NAME = "mpesa_prefs"
    private const val KEY_TILL = "till_number"
    private const val KEY_CONSUMER_KEY = "consumer_key"
    private const val KEY_CONSUMER_SECRET = "consumer_secret"
    private const val KEY_PASSKEY = "passkey"
    private const val KEY_LAST_TRANSACTION_STATUS = "last_transaction_status"
    private const val KEY_PHONE_NUMBER = "phone_number"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setTill(ctx: Context, till: String) {
        prefs(ctx).edit(commit = true) { putString(KEY_TILL, till.trim()) }
    }

    fun getTill(ctx: Context): String? = prefs(ctx).getString(KEY_TILL, null)

    fun setConsumerKey(ctx: Context, key: String) {
        prefs(ctx).edit(commit = true) { putString(KEY_CONSUMER_KEY, key.trim()) }
    }

    fun getConsumerKey(ctx: Context): String? = prefs(ctx).getString(KEY_CONSUMER_KEY, null)

    fun setConsumerSecret(ctx: Context, secret: String) {
        prefs(ctx).edit(commit = true) { putString(KEY_CONSUMER_SECRET, secret.trim()) }
    }

    fun getConsumerSecret(ctx: Context): String? = prefs(ctx).getString(KEY_CONSUMER_SECRET, null)

    fun setPasskey(ctx: Context, passkey: String) {
        prefs(ctx).edit(commit = true) { putString(KEY_PASSKEY, passkey.trim()) }
    }

    fun getPasskey(ctx: Context): String? = prefs(ctx).getString(KEY_PASSKEY, null)

    fun setLastTransactionStatus(ctx: Context, status: String) {
        prefs(ctx).edit(commit = true) { putString(KEY_LAST_TRANSACTION_STATUS, status) }
    }

    fun getLastTransactionStatus(ctx: Context): String? = prefs(ctx).getString(KEY_LAST_TRANSACTION_STATUS, "No transactions yet.")

    fun setPhoneNumber(ctx: Context, phoneNumber: String) {
        prefs(ctx).edit(commit = true) { putString(KEY_PHONE_NUMBER, phoneNumber) }
    }

    fun getPhoneNumber(ctx: Context): String? = prefs(ctx).getString(KEY_PHONE_NUMBER, null)
}
