package com.imran.currencyconverter.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.imran.currencyconverter.R
import com.imran.currencyconverter.data.Currency
import com.imran.currencyconverter.databinding.ItemCurrencyBinding
import com.imran.currencyconverter.utils.OnItemSelectedListener
import kotlin.collections.ArrayList

class CurrencyAdapter(private val context: Context, private val selected: Currency?, private val onItemSelectedListener: OnItemSelectedListener<Currency>): RecyclerView.Adapter<CurrencyAdapter.MyViewHolder>(),
    Filterable {

    private var list = ArrayList<Currency>()
    private var filterList = list

    fun setData(list: ArrayList<Currency>) {
        this.list = list
        this.filterList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        ItemCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    class MyViewHolder(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = filterList[position]

        holder.binding.tvName.text = item.name

        selected?.let {
            if (it.id == item.id) {
                holder.binding.cvParent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                holder.binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.white))
            }else {
                holder.binding.cvParent.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                holder.binding.tvName.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
            }
        }

        holder.itemView.setOnClickListener {
            onItemSelectedListener.onItemSelect(item)
        }

    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString().trim()

                filterList = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList = ArrayList<Currency>()

                    for (item in list) {
                        Log.d("search","${item.name} / $charString")
                        if (item.name.contains(charString)) {
                            filteredList.add(item)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                filterList = filterResults.values as ArrayList<Currency>
                notifyDataSetChanged()

            }
        }
    }

}