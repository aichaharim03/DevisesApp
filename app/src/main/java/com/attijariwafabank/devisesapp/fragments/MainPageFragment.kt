package com.attijariwafabank.devisesapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.viewModels.CurrencyViewModel
import com.attijariwafabank.devisesapp.adapter.CurrenciesAdapter
import com.attijariwafabank.devisesapp.databinding.FragmentMainPageBinding
import com.attijariwafabank.devisesapp.enums.CurrencyEnum

class MainPageFragment : Fragment() {

    private var binding: FragmentMainPageBinding? = null
    private lateinit var viewModel: CurrencyViewModel
    private lateinit var adapter: CurrenciesAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var selectedSource: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModel = ViewModelProvider(requireActivity())[CurrencyViewModel::class.java]
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        adapter = CurrenciesAdapter()
        binding?.currenciesRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MainPageFragment.adapter
        }

        viewModel.currencies.observe(viewLifecycleOwner) { currencies ->
            Log.d("Currencies", "Fetched: $currencies")
            adapter.setData(currencies)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        val sourceCurrencies = CurrencyEnum.entries
        val currencyNames = sourceCurrencies.map { it.code }

        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencyNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinner?.adapter = spinnerAdapter

        binding?.spinner?.setSelection(0)
        selectedSource = sourceCurrencies[0].code

        val allTargetCurrencies = CurrencyEnum.entries.joinToString(",") { it.code }

        viewModel.fetchCurrencies(
            accessKey = "3bdb79681826eff584ac6f3ccd1b4a82",
            source = selectedSource,
            currencies = allTargetCurrencies
        )

        binding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSource = sourceCurrencies[position].code
                viewModel.fetchCurrencies(
                    accessKey = "3bdb79681826eff584ac6f3ccd1b4a82",
                    source = selectedSource,
                    currencies = allTargetCurrencies
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.you_have_to_select_a_currency_source),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        adapter.setOnItemClickListener(object : CurrenciesAdapter.OnItemClickListener {
            override fun onItemClick(currency: String) {
                val currencyEnum = CurrencyEnum.fromString(currency)
                val currencyCode = currencyEnum?.code ?: return

                val action = MainPageFragmentDirections.actionMainPageToCurrencyGraphFragment(
                    sourceCurrency = selectedSource,
                    targetCurrency = currencyCode
                )
                findNavController().navigate(action)
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
