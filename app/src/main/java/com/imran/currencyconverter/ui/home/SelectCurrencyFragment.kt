package com.imran.currencyconverter.ui.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.imran.currencyconverter.data.Currency
import com.imran.currencyconverter.databinding.DialogSelectCurrencyBinding
import com.imran.currencyconverter.utils.OnItemSelectedListener
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent
import io.reactivex.observers.DisposableObserver

class SelectCurrencyFragment(private val onItemSelectedListener: OnItemSelectedListener<Currency>): DialogFragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val disposable = CompositeDisposable()
    private lateinit var currencyAdapter: CurrencyAdapter

    private var safebinding: DialogSelectCurrencyBinding? = null
    private val binding get() = safebinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        safebinding = DialogSelectCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgClose.setOnClickListener {
            hide()
        }

        currencyAdapter = CurrencyAdapter(requireContext(), homeViewModel.selectedCurrency.value, onItemSelectedListener)
        binding.rvList.adapter = currencyAdapter

        disposable.add(
            RxTextView.textChangeEvents(binding.etSearch)
                .skipInitialValue()
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchQuery())
        )


        homeViewModel.mCurrencyList.observe(viewLifecycleOwner, {
            currencyAdapter.setData(it)
        })

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun searchQuery(): DisposableObserver<TextViewTextChangeEvent?> {
        return object : DisposableObserver<TextViewTextChangeEvent?>() {
            override fun onError(e: Throwable) {}
            override fun onComplete() {}
            override fun onNext(value: TextViewTextChangeEvent?) {
                currencyAdapter.filter.filter(value?.text()?.toString()?.uppercase())
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        disposable.clear()
        super.onDismiss(dialog)
    }

    companion object {

        private var selectCurrencyFragment: SelectCurrencyFragment? = null

        fun show(onItemSelectedListener: OnItemSelectedListener<Currency>, fragmentManager: FragmentManager) {
            selectCurrencyFragment = SelectCurrencyFragment(onItemSelectedListener)
            selectCurrencyFragment?.show(fragmentManager,"SelectCurrency")
        }

        fun hide() {
            selectCurrencyFragment?.dismissAllowingStateLoss()
        }

    }


}