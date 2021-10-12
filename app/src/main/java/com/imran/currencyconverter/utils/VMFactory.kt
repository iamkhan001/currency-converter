package com.imran.currencyconverter.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imran.currencyconverter.ui.home.HomeViewModel
import com.imran.currencyconverter.ui.splash.SplashViewModel

class VMFactory (val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(context) as T
        }else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(context) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }

}