package com.attijariwafabank.devisesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.attijariwafabank.devisesapp.databinding.ItemCurrencyBinding

class CurrenciesAdapter : RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    private val currenciesList = mutableListOf<String>()

    fun setData(newCurrencies: List<String>) {
        currenciesList.clear()
        currenciesList.addAll(newCurrencies)
        notifyDataSetChanged()
    }





    inner class CurrencyViewHolder(private val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: String) {
            binding.currencyNameTextView.text = currency
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(currenciesList[position])
    }

    override fun getItemCount() = currenciesList.size
}
