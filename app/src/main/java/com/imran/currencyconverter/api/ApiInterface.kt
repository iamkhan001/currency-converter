package com.imran.currencyconverter.api

import com.imran.currencyconverter.config.CURRENCY_ACCESS_KEY
import okhttp3.ResponseBody
import retrofit2.http.GET
import io.reactivex.Observable

interface ApiInterface {

    @GET("live?access_key=$CURRENCY_ACCESS_KEY")
    fun liveCurrencyUpdates(): Observable<ResponseBody>

}