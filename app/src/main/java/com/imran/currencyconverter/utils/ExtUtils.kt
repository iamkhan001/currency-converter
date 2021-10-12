package com.imran.currencyconverter.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

fun Context.isInternetAvailable(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    val capabilities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        manager?.getNetworkCapabilities(manager.activeNetwork) ?: return false
    } else {
        val cap = manager?.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)    }

    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}

fun Double.format():String{
    val format: NumberFormat = NumberFormat.getInstance()
    format.maximumFractionDigits = 2
    return format.format(this)
}