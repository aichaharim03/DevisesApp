package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentConversionBinding
import com.attijariwafabank.devisesapp.viewModels.CurrencyViewModel

class ConversionFragment : Fragment() {

    private val viewModel: CurrencyViewModel by viewModels()
    private var _binding: FragmentConversionBinding? = null
    private val binding get() = _binding!!

    private val accessKey = "3bdb79681826eff584ac6f3ccd1b4a82"

    // Liste des devises autorisées
    private val supportedCurrencies = listOf(
        "USD", "EUR", "GBP", "CAD", "MAD", "AUD",
        "JPY", "CHF", "CNY", "SEK", "NZD", "INR", "MLR"
    )

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

        // Tu peux garder cet appel si tu veux des taux à jour
        viewModel.fetchCurrencies(accessKey, "MAD")

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_conversionFragment_to_mainPage)
        }
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            supportedCurrencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFromCurrency.adapter = adapter
        binding.spinnerToCurrency.adapter = adapter

        // Sélectionne MAD par défaut dans le spinner From
        val defaultIndex = supportedCurrencies.indexOf("MAD")
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
                Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fromCurrency = binding.spinnerFromCurrency.selectedItem?.toString()
            val toCurrency = binding.spinnerToCurrency.selectedItem?.toString()

            if (fromCurrency.isNullOrEmpty() || toCurrency.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select currencies", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.cardResult.visibility = View.GONE
            viewModel.convertCurrency(accessKey, fromCurrency, toCurrency, amount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
