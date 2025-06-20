package com.attijariwafabank.devisesapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.enums.CurrencyEnum

class CurrencySpinnerAdapter(
    private val context: Context,
    private val currencies: List<CurrencyEnum>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = currencies.size

    override fun getItem(position: Int): Any = currencies[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.spinner_currency_item, parent, false)

        val currency = currencies[position]
        val flagImageView = view.findViewById<ImageView>(R.id.iv_flag)

        flagImageView.setImageResource(getFlagResource(currency))

        return view
    }

    private fun getFlagResource(currency: CurrencyEnum): Int {
        return when (currency) {
            CurrencyEnum.USD -> R.drawable.usa
            CurrencyEnum.EUR -> R.drawable.eur
            CurrencyEnum.GBP -> R.drawable.gbp
            CurrencyEnum.JPY -> R.drawable.jpy
            CurrencyEnum.CHF -> R.drawable.chf
            CurrencyEnum.CAD -> R.drawable.cad
            CurrencyEnum.AUD -> R.drawable.aud
            CurrencyEnum.CNY -> R.drawable.cny
            CurrencyEnum.SEK -> R.drawable.sek
            CurrencyEnum.NZD -> R.drawable.nzd
            CurrencyEnum.INR -> R.drawable.inr
            CurrencyEnum.MLR -> R.drawable.mlr
            CurrencyEnum.MAD -> R.drawable.flag_of_morocco_waving
        }
    }
}