package com.imran.currencyconverter.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object MyMessage {

    fun showToast(context: Context?, msg: String){
        context?.let {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun showBar(view: View, msg: String) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun showBar(activity: Activity?, msg: String) {
        activity?.let {
            Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show()
        }
    }

}