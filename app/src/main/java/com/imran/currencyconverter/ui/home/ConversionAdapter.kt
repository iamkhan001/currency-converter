package com.imran.currencyconverter.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imran.currencyconverter.data.Rate
import com.imran.currencyconverter.databinding.ItemConversionBinding
import com.imran.currencyconverter.utils.OnItemSelectedListener
import com.imran.currencyconverter.utils.format

class ConversionAdapter(private val onItemSelectedListener: OnItemSelectedListener<Rate>): RecyclerView.Adapter<ConversionAdapter.MyViewHolder>() {

    private var list = ArrayList<Rate>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Rate>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        ItemConversionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    class MyViewHolder(val binding: ItemConversionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = list[position]

        holder.binding.tvName.text = item.name
        holder.binding.tvAmount.text = item.rate.format()

        holder.itemView.setOnClickListener {
            onItemSelectedListener.onItemSelect(item)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

}