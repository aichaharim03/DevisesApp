package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.adapter.CurrencySpinnerAdapter
import com.attijariwafabank.devisesapp.enums.CurrencyEnum
import com.attijariwafabank.devisesapp.viewModels.CurrencyViewModel
import com.attijariwafabank.devisesapp.databinding.FragmentConversionBinding
import java.util.*

class ConversionFragment : Fragment() {

    private val viewModel: CurrencyViewModel by viewModels()
    private var binding: FragmentConversionBinding? = null
    private val accessKey = "59fbbd15c97f28c0e122fa9c95db1df1"
    private lateinit var currencyAdapter: CurrencySpinnerAdapter

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

        val allTargetCurrencies = CurrencyEnum.entries.joinToString(",") { it.code }
        val initialCurrency = CurrencyEnum.USD.code
        viewModel.fetchCurrencies(
            accessKey = accessKey,
            source = initialCurrency,
            currencies = allTargetCurrencies
        )
    }

    private fun setupSpinners() {
        val currencies = CurrencyEnum.entries.filter { it != CurrencyEnum.MAD }

        currencyAdapter = CurrencySpinnerAdapter(requireContext(), currencies)
        binding?.spinnerFromCurrency?.adapter = currencyAdapter

        val usdIndex = currencies.indexOfFirst { it == CurrencyEnum.USD }
        val defaultIndex = if (usdIndex >= 0) usdIndex else 0
        binding?.spinnerFromCurrency?.setSelection(defaultIndex)

        updateCurrencyCode(currencies[defaultIndex].code)

        binding?.spinnerFromCurrency?.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCurrency = currencies[position]
                    updateCurrencyCode(selectedCurrency.code)

                    val allTargetCurrencies = CurrencyEnum.entries.joinToString(",") { it.code }
                    viewModel.fetchCurrencies(
                        accessKey = accessKey,
                        source = selectedCurrency.code,
                        currencies = allTargetCurrencies
                    )

                    val amountStr = binding?.etAmount?.text.toString()
                    if (amountStr.isNotEmpty()) {
                        performConversion()
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    private fun setupObservers() {
        viewModel.conversionResult.observe(viewLifecycleOwner) { result ->
            binding?.progressBar?.visibility = View.GONE
            updateConvertedAmount(result)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            binding?.progressBar?.visibility = View.GONE
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }

        viewModel.currencies.observe(viewLifecycleOwner) { currencyList ->
            val selectedPosition = binding?.spinnerFromCurrency?.selectedItemPosition ?: 0
            val currencies = CurrencyEnum.entries.filter { it != CurrencyEnum.MAD }
            val selectedCurrency = currencies.getOrNull(selectedPosition)

            selectedCurrency?.let { fromCurrency ->
                val madEntry = currencyList.find {
                    it.startsWith("MAD") ||
                            it.startsWith("${fromCurrency.code}MAD") ||
                            it.contains("MAD")
                }
                madEntry?.let {
                    val parts = it.split(":")
                    if (parts.size == 2) {
                        val rate = parts[1].trim().toDoubleOrNull()
                        if (rate != null) {
                            val formattedRate = String.format("%.2f", rate)
                            binding?.tvExchangeRate?.text =
                                getString(R.string._1_mad, fromCurrency.code, formattedRate)
                        }
                    }
                } ?: run {
                    binding?.tvExchangeRate?.text = getString(R.string.taux_indisponible)
                }
            }
        }
    }

    private fun setupListeners() {
        binding?.btnConvert?.setOnClickListener {
            performConversion()
        }

        binding?.etAmount?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding?.etAmount?.text?.isNotEmpty() == true) {
                performConversion()
            }
        }
    }

    private fun performConversion() {
        val amountStr = binding?.etAmount?.text.toString()

        if (amountStr.isEmpty()) {
            binding?.etAmount?.hint = "0.00"
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_amount),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val selectedPosition = binding?.spinnerFromCurrency?.selectedItemPosition ?: 0
        val currencies = CurrencyEnum.entries.filter { it != CurrencyEnum.MAD }
        val selectedCurrency = currencies.getOrNull(selectedPosition)

        if (selectedCurrency == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_select_currencies),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.convertCurrency(accessKey, selectedCurrency.code, CurrencyEnum.MAD.code, amount)
    }

    private fun updateCurrencyCode(currencyCode: String) {
        binding?.tvCurrencyCode?.text = currencyCode
        binding?.tvExchangeRate?.text = getString(R.string._1_mad_, currencyCode)
    }

    private fun updateConvertedAmount(amount: Double) {
        val formattedAmount = String.format(Locale.getDefault(), "%.2f", amount)
        binding?.tvConvertedAmount?.text = formattedAmount.replace(".", ".")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}