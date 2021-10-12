package com.imran.currencyconverter.repo

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.imran.currencyconverter.config.AppDatabase
import com.imran.currencyconverter.config.AppDatabase.Companion.TABLE_CURRENCY_RATE
import com.imran.currencyconverter.data.Currency
import android.database.DatabaseUtils

class DBHelper(context: Context) {

    private val database = AppDatabase(context)

    fun saveCurrencyUpdates(list: ArrayList<Currency>) {

        val dbWrite = database.readableDatabase

        for (data in list) {

            val content = ContentValues()
            content.put("rate", data.rate)

            val count = dbWrite.update(TABLE_CURRENCY_RATE, content, "name = '${data.name}'", null)
            Log.d(TAG, "update: $count $data.name")
            if (count == 0) {
                content.put("name", data.name)
                dbWrite.insert(TABLE_CURRENCY_RATE, null, content)
                Log.d(TAG, "insert: $data.name")
            }

        }

        dbWrite.close()

    }

    fun getCurrencyNames(): ArrayList<Currency> {

        val dbRead = database.readableDatabase

        val cursor = dbRead.rawQuery("SELECT * FROM $TABLE_CURRENCY_RATE ORDER BY name", null)

        val list = ArrayList<Currency>()

        while (cursor.moveToNext()) {
            list.add(
                Currency(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                )
            )
        }

        cursor.close()
        dbRead.close()

        return list
    }

    fun getCurrencyRate(name: String): Currency? {

        val dbRead = database.readableDatabase

        val cursor = dbRead.rawQuery("SELECT * FROM $TABLE_CURRENCY_RATE WHERE name = '$name'", null)

        var currency: Currency? = null

        if (cursor.moveToNext()) {
            currency = Currency(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getDouble(2),
            )
        }

        cursor.close()
        dbRead.close()

        return currency
    }

    fun checkData(): Long {

        val dbRead = database.readableDatabase
        val numRows = DatabaseUtils.queryNumEntries(dbRead, TABLE_CURRENCY_RATE)
        dbRead.close()

        Log.d(TAG,"$TABLE_CURRENCY_RATE count > $numRows")
        return numRows
    }

    fun closeDb() {
        database.close()
    }

    companion object {

        private var dbHelper: DBHelper? = null
        private const val TAG = "DbHelper"

        fun getInstance(context: Context): DBHelper {
            if (dbHelper == null) {
                dbHelper = DBHelper(context)
            }
            return dbHelper!!
        }

    }

}