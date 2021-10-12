package com.imran.currencyconverter.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imran.currencyconverter.config.AppDataProvider
import com.imran.currencyconverter.data.Currency
import com.imran.currencyconverter.data.Rate
import com.imran.currencyconverter.repo.ApiRepository
import com.imran.currencyconverter.repo.DBHelper
import java.util.concurrent.Executors

class HomeViewModel(context: Context): ViewModel() {

    val mCurrencyList = MutableLiveData<ArrayList<Currency>>()
    var selectedCurrency = MutableLiveData<Currency>()
    val mConversionList = MutableLiveData<ArrayList<Rate>>()

    private var apiRepository: ApiRepository
    private var dbHelper: DBHelper = DBHelper.getInstance(context)
    private var appDataProvider: AppDataProvider = AppDataProvider.getInstance(context)

    private val executorService = Executors.newSingleThreadExecutor()

    init {
        apiRepository = ApiRepository.getInstance(dbHelper, appDataProvider)
        selectedCurrency.postValue(Currency(1, "USD", 1.0))
    }

    fun loadCurrencyData() {
        executorService.submit { mCurrencyList.postValue(dbHelper.getCurrencyNames()) }
    }

    fun convertAmount(amount: Double) {

       executorService.submit {

           selectedCurrency.value?.let { currency ->

               val usdAmount = amount / currency.rate
               
               Log.d(TAG,"Amount ${currency.name} $amount > USD $usdAmount")
               
               val conversionList = ArrayList<Rate>()
               mCurrencyList.value?.let {

                   for (item in it) {
                       conversionList.add(
                           Rate(
                               item.name,
                               item.rate * usdAmount
                           )
                       )
                   }

               }

               mConversionList.postValue(conversionList)
           }
       }

    }

    companion object {
        private const val TAG = "HomeViewModel"
    }

}