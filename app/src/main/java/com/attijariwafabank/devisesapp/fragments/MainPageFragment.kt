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
import com.attijariwafabank.devisesapp.CurrencyViewModel
import com.attijariwafabank.devisesapp.adapter.CurrenciesAdapter
import com.attijariwafabank.devisesapp.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {

    private var binding: FragmentMainPageBinding? = null
    private lateinit var viewModel: CurrencyViewModel
    private lateinit var adapter: CurrenciesAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding!!.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CurrenciesAdapter()
        binding?.currenciesRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MainPageFragment.adapter
        }

        viewModel = ViewModelProvider(requireActivity())[CurrencyViewModel::class.java]

        viewModel.currencies.observe(viewLifecycleOwner) { currencies ->
            Log.d("Currencies", "Fetched: $currencies")
            adapter.setData(currencies)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        val sourceCurrencies = listOf("USD", "MAD", "EUR", "GBP", "CAD")
        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sourceCurrencies)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinner?.adapter = spinnerAdapter

        binding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSource = sourceCurrencies[position]
                viewModel.fetchCurrencies("ca153fc53a18d844476abcc90b57143c", selectedSource ,currencies = "USD,EUR,GBP,CAD,MAD,AUD,JPY,CHF,CNY,SEK,NZD,INR,MLR")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "You have to select a currency source", Toast.LENGTH_SHORT).show()
            }


        }

        viewModel.fetchCurrencies("ca153fc53a18d844476abcc90b57143c", "MAD")

        adapter.setOnItemClickListener(object : CurrenciesAdapter.OnItemClickListener {
            override fun onItemClick(currency: String) {
                val action = MainPageFragmentDirections.actionMainPageToCurrencyGraphFragment()
                findNavController().navigate(action)
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
