package com.imran.currencyconverter.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.imran.currencyconverter.R
import com.imran.currencyconverter.data.Currency
import com.imran.currencyconverter.data.Rate
import com.imran.currencyconverter.databinding.ActivityMainBinding
import com.imran.currencyconverter.utils.MyMessage
import com.imran.currencyconverter.utils.OnItemSelectedListener
import com.imran.currencyconverter.utils.VMFactory
import com.imran.currencyconverter.utils.format
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels {
        VMFactory(applicationContext)
    }

    private val disposable = CompositeDisposable()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onCurrencySelectedListener = object : OnItemSelectedListener<Currency> {
            override fun onItemSelect(item: Currency) {
                homeViewModel.selectedCurrency.postValue(item)
                SelectCurrencyFragment.hide()
            }
        }

        binding.btnCurrency.setOnClickListener {
            SelectCurrencyFragment.show(onCurrencySelectedListener, supportFragmentManager)
        }

        binding.imgClear.setOnClickListener {
            binding.etValue.setText("")
        }

        //observe for currency change by user and update conversion rates
        homeViewModel.selectedCurrency.observe(this, {
            binding.btnCurrency.text = it.name
            if (binding.etValue.text.isNotEmpty()) {
                convertAmount(binding.etValue.text?.toString())
            }
        })

        //show value on toast when user clicks on any value useful if value is too large to fit in textview
        val onItemSelectedListener = object : OnItemSelectedListener<Rate> {
            override fun onItemSelect(item: Rate) {
                MyMessage.showToast(this@HomeActivity, "${item.name}: ${item.rate.format()}")
            }
        }

        val adapter = ConversionAdapter(onItemSelectedListener)
        binding.rvList.adapter = adapter

        homeViewModel.mConversionList.observe(this, {
            adapter.setData(it)
        })


        homeViewModel.mCurrencyList.observe(this, {
            binding.etValue.setText(getString(R.string.default_value))
        })

        //using debounce feature of RxJava to perform conversion only when user finish typing instead of doing calculations for
        //every input value by user, I set timeout it 400 ms
        disposable.add(
            RxTextView.textChangeEvents(binding.etValue)
                .skipInitialValue()
                .debounce(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(convertAmount())
        )

        homeViewModel.loadCurrencyData()

    }

    private fun convertAmount(): DisposableObserver<TextViewTextChangeEvent?> {
        return object : DisposableObserver<TextViewTextChangeEvent?>() {
            override fun onError(e: Throwable) {}
            override fun onComplete() {}
            override fun onNext(value: TextViewTextChangeEvent?) {
                try {

                    if (value?.text().isNullOrEmpty()) {
                        binding.imgClear.visibility = View.INVISIBLE
                        return
                    }
                    binding.imgClear.visibility = View.VISIBLE

                    val amountText = value?.text()?.trim()
                    if (amountText.isNullOrEmpty()) {
                        binding.etValue.error = getString(R.string.value_error)
                        return
                    }
                    convertAmount(amountText.toString())
                }catch (e: Exception) {
                    e.printStackTrace()
                    binding.etValue.error = getString(R.string.value_error)
                }
            }
        }
    }

    private fun convertAmount(amountText: String?) {
        if (amountText.isNullOrEmpty()) {
            return
        }
        val amount = amountText.toString().toDouble()
        homeViewModel.convertAmount(amount)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

}