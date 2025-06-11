package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentConversionBinding
import com.attijariwafabank.devisesapp.enums.CurrencyEnum
import com.attijariwafabank.devisesapp.viewModels.CurrencyViewModel

class ConversionFragment : Fragment() {

    private val viewModel: CurrencyViewModel by viewModels()
    private var binding: FragmentConversionBinding? = null
    private val accessKey = "3bdb79681826eff584ac6f3ccd1b4a82"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversionBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupObservers()
        setupListeners()

        viewModel.fetchCurrencies(accessKey, CurrencyEnum.MAD.code)



    }

    private fun setupSpinners() {
        val currencies = CurrencyEnum.entries
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            currencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.binding?.spinnerFromCurrency?.adapter = adapter
        this.binding?.spinnerToCurrency?.adapter = adapter

        val defaultIndex = currencies.indexOf(CurrencyEnum.MAD)
        if (defaultIndex != -1) {
            this.binding?.spinnerFromCurrency?.setSelection(defaultIndex)
        }
    }

    private fun setupObservers() {
        viewModel.conversionResult.observe(viewLifecycleOwner) { result ->
            this.binding?.progressBar?.visibility = View.GONE
            this.binding?.cardResult?.visibility = View.VISIBLE
            this.binding?.tvConvertedAmount?.text = getString(R.string.conversion_result, result)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            this.binding?.progressBar?.visibility = View.GONE
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        this.binding?.btnConvert?.setOnClickListener {
            val amountStr = this.binding?.etAmount?.text.toString()

            if (amountStr.isEmpty()) {
                Toast.makeText(requireContext(),
                    getString(R.string.please_enter_an_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fromCurrency = this.binding?.spinnerFromCurrency?.selectedItem as? CurrencyEnum
            val toCurrency = this.binding?.spinnerToCurrency?.selectedItem as? CurrencyEnum

            if (fromCurrency == null || toCurrency == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.please_select_currencies), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            this.binding?.progressBar?.visibility = View.VISIBLE
            this.binding?.cardResult?.visibility = View.GONE
            viewModel.convertCurrency(accessKey, fromCurrency.code, toCurrency.code, amount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}
