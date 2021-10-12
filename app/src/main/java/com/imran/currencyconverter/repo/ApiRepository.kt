package com.imran.currencyconverter.repo

import android.util.Log
import com.imran.currencyconverter.api.ApiCallResponseListener
import com.imran.currencyconverter.api.ApiClient
import com.imran.currencyconverter.api.ApiInterface
import com.imran.currencyconverter.config.AppDataProvider
import com.imran.currencyconverter.data.Currency
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException

class ApiRepository(private val dbHelper: DBHelper, private val appDataProvider: AppDataProvider) {

    private val compositeDisposable = CompositeDisposable()
    private val apiInterface = ApiClient.getRetrofitInstance().create(ApiInterface::class.java)

    fun updateCurrencyRate(apiCallResponseListener: ApiCallResponseListener) {

        Log.e(TAG,"updateCurrencyRate")

        compositeDisposable.add(
            apiInterface.liveCurrencyUpdates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val response = it.string().trim()
                    Log.e(TAG, "fetchData > $response")
                    try {
                        val data = JSONObject(response)
                        if (data.getBoolean("success")) {

                            val timestamp = data.getLong("timestamp")
                            val quotes = data.getJSONObject("quotes")

                            val currencyList = ArrayList<Currency>()
                            val keys = quotes.keys()
                            currencyList.add(Currency("USD", 1.0))

                            for (key in keys) {
                                val name = key.substring(3)
                                val rate = quotes.getDouble(key)

                                Log.d(TAG,"Key $key > $name : $rate ")

                                currencyList.add(
                                    Currency(
                                        name,
                                        rate
                                    )
                                )

                            }

                            dbHelper.saveCurrencyUpdates(currencyList)
                            appDataProvider.setCurrencyUpdated(timestamp*1000)
                            appDataProvider.setLastUpdateTime()
                            apiCallResponseListener.onSuccess()
                            return@subscribe
                        }

                        val error = data.getJSONObject("error").getString("info")
                        apiCallResponseListener.onFailed(error)

                        return@subscribe
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    apiCallResponseListener.onFailed("Error")

                }, {

                    if (it is HttpException) {
                        getError(it, apiCallResponseListener)
                        return@subscribe
                    }

                    apiCallResponseListener.onFailed("Unknown error")
                    it.printStackTrace()
                })
        )

    }

    private fun getError(exception: HttpException, apiCallResponseListener: ApiCallResponseListener) {
        try {

            val response = exception.response()
            Log.e(TAG, "Error: ${response?.code()} \n${response?.errorBody()}")

            val msg = if (response != null) {
                val r = response.errorBody()
                if (r != null) {
                    val obj = JSONObject(r.string())
                    obj.getJSONObject("error").getString("info")
                } else {
                    "Server error"
                }
            } else {
                "Server Error"
            }
            apiCallResponseListener.onFailed(msg)
            return
        } catch (e: Exception) {
            e.printStackTrace()
        }

        apiCallResponseListener.onFailed("Server Error")
    }

    companion object {

        private const val TAG = "ApiRepository"

        private var apiRepository: ApiRepository? = null

        fun getInstance(dbHelper: DBHelper, appDataProvider: AppDataProvider): ApiRepository {
            if (apiRepository == null) {
                apiRepository = ApiRepository(dbHelper, appDataProvider)
            }

            return apiRepository!!
        }

    }

}