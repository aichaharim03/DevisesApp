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
    private var _binding: FragmentConversionBinding? = null
    private val binding get() = _binding!!

    private val accessKey = "a351491abe4e7fab9e83c472eb04bdac"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConversionBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.spinnerFromCurrency.adapter = adapter
        binding.spinnerToCurrency.adapter = adapter

        val defaultIndex = currencies.indexOf(CurrencyEnum.MAD)
        if (defaultIndex != -1) {
            binding.spinnerFromCurrency.setSelection(defaultIndex)
        }
    }

    private fun setupObservers() {
        viewModel.conversionResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.cardResult.visibility = View.VISIBLE
            binding.tvConvertedAmount.text = getString(R.string.conversion_result, result)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        binding.btnConvert.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()

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

            val fromCurrency = binding.spinnerFromCurrency.selectedItem as? CurrencyEnum
            val toCurrency = binding.spinnerToCurrency.selectedItem as? CurrencyEnum

            if (fromCurrency == null || toCurrency == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.please_select_currencies), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.cardResult.visibility = View.GONE
            viewModel.convertCurrency(accessKey, fromCurrency.code, toCurrency.code, amount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
