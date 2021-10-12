package com.imran.currencyconverter.ui.splash

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.imran.currencyconverter.R
import com.imran.currencyconverter.api.ApiCallResponseListener
import com.imran.currencyconverter.config.MIN_UPDATE_FREQUENCY
import com.imran.currencyconverter.databinding.ActivitySplashBinding
import com.imran.currencyconverter.ui.home.HomeActivity
import com.imran.currencyconverter.utils.MyMessage
import com.imran.currencyconverter.utils.VMFactory
import com.imran.currencyconverter.utils.isInternetAvailable
import com.mirobotic.dialog.myDialog.SweetAlertDialog

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val apiCallResponseListener = object : ApiCallResponseListener {

        override fun onSuccess() {
            goToMainScreen()
        }

        override fun onFailed(msg: String) {
            runOnUiThread {
                //data sync failed so check if database exists and if yes, move to home screen else show network error
                MyMessage.showBar(this@SplashActivity, msg)
                binding.progressBar.visibility = View.INVISIBLE
                if (splashViewModel.isDataLoaded.value == true) {
                    MyMessage.showToast(this@SplashActivity, msg)
                    goToMainScreen()
                }else {
                    binding.tvStatus.text = msg
                }
            }
        }

    }

    private val splashViewModel: SplashViewModel  by viewModels {
        VMFactory(applicationContext)
    }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        //check if database exists
        splashViewModel.isDataLoaded.observe(this, {

            Log.d(TAG,"isDataAvailable $it")

            if (it) {
                //database exists so check last sync time from server,
                    // if time difference is greater than 30 min then sync updates

                val timeDiff = System.currentTimeMillis() - splashViewModel.getLastCurrencyUpdateTime()

                Log.d(TAG,"timeDiff $timeDiff")

                if (timeDiff > MIN_UPDATE_FREQUENCY && applicationContext.isInternetAvailable()) {
                    updateData()
                    return@observe
                }

                goToMainScreen()

            }else {
                //here database is not exists, so download data from server

                if (applicationContext.isInternetAvailable()) {
                    updateData()
                }else {
                    showNetworkError()
                }

                return@observe
            }
        })

        splashViewModel.isDataExists()

    }

    private fun updateData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatus.text = getString(R.string.update_currency)
        splashViewModel.updateCurrencyRate(apiCallResponseListener)
    }

    private fun showNetworkError() {

        binding.progressBar.visibility = View.INVISIBLE
        val alertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
        alertDialog.titleText = getString(R.string.no_internet)
        alertDialog.contentText = getString(R.string.connect_to_internet)
        alertDialog.setCancelable(false)
        alertDialog.confirmText = getString(R.string.retry)
        alertDialog.cancelText = getString(R.string.exit)
        alertDialog.setConfirmClickListener {
            it.dismissWithAnimation()
            if (applicationContext.isInternetAvailable()) {
                updateData()
            }else {
                showNetworkError()
            }
        }
        alertDialog.setCancelClickListener {
            it.dismissWithAnimation()
            finish()
        }
        alertDialog.show()
    }

    private fun goToMainScreen() {

        Handler(Looper.getMainLooper()).postDelayed({
            HomeActivity.start(this)
        }, 1000)

    }

    companion object {
        private const val TAG = "SplashActivity"
    }

}