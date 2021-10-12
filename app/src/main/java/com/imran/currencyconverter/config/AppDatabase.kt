package com.imran.currencyconverter.config

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AppDatabase(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "currency"
        private const val DB_VERSION = 1
        const val TABLE_CURRENCY_RATE = "currency_rate"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val tableCurrencyRate = "CREATE TABLE IF NOT EXISTS $TABLE_CURRENCY_RATE ( " +
                " id INTEGER PRIMARY KEY, " +
                " name VARCHAR(10), " +
                " rate DOUBLE NOT NULL " +
                ")"

        Log.d("DB", "TABLE_CURRENCY_RATE > $tableCurrencyRate")
        db.execSQL(tableCurrencyRate)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }


}