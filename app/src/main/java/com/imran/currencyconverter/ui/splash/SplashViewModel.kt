package com.imran.currencyconverter.ui.splash

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imran.currencyconverter.api.ApiCallResponseListener
import com.imran.currencyconverter.config.AppDataProvider
import com.imran.currencyconverter.repo.ApiRepository
import com.imran.currencyconverter.repo.DBHelper
import java.util.concurrent.Executors

class SplashViewModel(context: Context): ViewModel() {

    private var apiRepository: ApiRepository
    private var dbHelper: DBHelper = DBHelper.getInstance(context)
    private var appDataProvider: AppDataProvider = AppDataProvider.getInstance(context)
    private val executorService = Executors.newSingleThreadExecutor()

    val isDataLoaded = MutableLiveData<Boolean>()

    init {
        apiRepository = ApiRepository.getInstance(dbHelper, appDataProvider)
    }

    fun getLastCurrencyUpdateTime(): Long = appDataProvider.getLastUpdateTime()

    fun isDataExists() = executorService.submit { isDataLoaded.postValue(dbHelper.checkData() > 0) }

    fun updateCurrencyRate(apiCallResponseListener: ApiCallResponseListener) {
        executorService.submit {
            apiRepository.updateCurrencyRate(apiCallResponseListener)
        }
    }

}