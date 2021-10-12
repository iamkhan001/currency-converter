package com.imran.currencyconverter.api

interface ApiCallResponseListener {
    
    fun onSuccess()
    
    fun onFailed(msg: String)
    
}