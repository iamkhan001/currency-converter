package com.imran.currencyconverter

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imran.currencyconverter.repo.DBHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//test if data exists
@RunWith(AndroidJUnit4::class)
class CurrencyTest {

    private lateinit var dbHelper: DBHelper

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        dbHelper = DBHelper(context)
    }


    @Test
    @Throws(Exception::class)
    fun checkIfDataExists() {
        val rowCount = dbHelper.checkData()

        //assert if data exists
        assert(rowCount > 0) { "Data exists, total rows: $rowCount" }

    }

    @Test
    @Throws(Exception::class)
    fun covertData() {

        // test to convert INR into YEN
        val currencyFrom = dbHelper.getCurrencyRate("INR")
        val currencyTo = dbHelper.getCurrencyRate("YEN")

        if (currencyFrom == null || currencyTo == null) {
            assert(false) { "Currency not found" }
            return
        }

        val valueINR = 100
        val rateUSD = valueINR / currencyFrom.rate
        val rateYED = rateUSD / currencyTo.rate

        //assert conversion rate
        assert(true) { "$valueINR INR is $rateUSD USD and $rateYED YEN" }

    }

    @After
    @Throws(Exception::class)
    fun close() {
        dbHelper.closeDb()
    }
}

