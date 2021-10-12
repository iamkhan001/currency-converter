package com.imran.currencyconverter.config

import android.content.Context

class AppDataProvider(context: Context) {

    private val preferences = context.getSharedPreferences("appData", Context.MODE_PRIVATE)

    fun setCurrencyUpdated(timestamp: Long) {
        preferences.edit().apply {
            putLong("currency_update_time", timestamp)
            apply()
        }
    }

    fun setLastUpdateTime() {
        preferences.edit().apply {
            putLong("currency_check_time", System.currentTimeMillis())
            apply()
        }
    }

    fun getCurrencyUpdateTime(): Long = preferences.getLong("currency_update_time", 0)

    fun getLastUpdateTime(): Long = preferences.getLong("currency_check_time", 0)

    companion object {

        private var appDataProvider: AppDataProvider? = null

        fun getInstance(context: Context): AppDataProvider {
            if (appDataProvider == null) {
                appDataProvider = AppDataProvider(context)
            }

            return appDataProvider!!
        }
    }
}