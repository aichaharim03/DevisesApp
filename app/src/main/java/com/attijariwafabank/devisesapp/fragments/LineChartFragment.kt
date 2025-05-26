package com.attijariwafabank.devisesapp.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.attijariwafabank.devisesapp.viewModels.CurrencyViewModel
import com.attijariwafabank.devisesapp.databinding.FragmentLineChartBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.attijariwafabank.devisesapp.R

class LineChartFragment : Fragment() {

    private lateinit var viewModel: CurrencyViewModel
    private var binding: FragmentLineChartBinding? = null
    private lateinit var sourceCurrency: String
    private lateinit var targetCurrency: String
    private val args: LineChartFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLineChartBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sourceCurrency = args.sourceCurrency
        targetCurrency = args.targetCurrency

        binding?.chartTitle?.text = getString(R.string.to_exchange_rate, sourceCurrency, targetCurrency)


        binding?.progressBar?.visibility = View.VISIBLE
        binding?.lineChart?.visibility = View.GONE
        binding?.noDataText?.visibility = View.GONE

        viewModel = ViewModelProvider(requireActivity())[CurrencyViewModel::class.java]

        val endCalendar = Calendar.getInstance()
        val startCalendar = Calendar.getInstance()
        startCalendar.add(Calendar.MONTH, -1)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = dateFormat.format(startCalendar.time)
        val endDate = dateFormat.format(endCalendar.time)


        viewModel.requestTimeFrame(
            accessKey = "a351491abe4e7fab9e83c472eb04bdac",
            source = sourceCurrency,
            targetCurrency = targetCurrency,
            startDate = startDate,
            endDate = endDate
        )

        viewModel.historicalRates.observe(viewLifecycleOwner) { rateMap ->
            binding?.progressBar?.visibility = View.GONE

            Log.d("LineChartFragment", "Rate map size: ${rateMap.size}")
            rateMap.forEach { (date, rate) ->
                Log.d("LineChartFragment", "Date: $date -> Rate: $rate")
            }


            if (rateMap.isEmpty()) {
                binding?.noDataText?.visibility = View.VISIBLE
                binding?.lineChart?.visibility = View.GONE
                binding?.noDataText?.text = getString(R.string.no_data_available_for_the_selected_date_range)
                return@observe
            }

            binding?.lineChart?.visibility = View.VISIBLE

            val entries = mutableListOf<Entry>()
            val labels = rateMap.keys.sorted()

            labels.forEachIndexed { index, date ->
                val rate = rateMap[date] ?: 0.0
                entries.add(Entry(index.toFloat(), rate.toFloat()))
            }

            val dataSet = LineDataSet(entries, getString(R.string.to_rate, sourceCurrency, targetCurrency))
            dataSet.color = Color.BLUE
            dataSet.valueTextColor = Color.BLACK
            dataSet.setCircleColor(Color.RED)
            dataSet.lineWidth = 2f
            dataSet.circleRadius = 4f
            dataSet.setDrawValues(true)

            binding?.lineChart?.apply {
                data = LineData(dataSet)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelRotationAngle = -45f
                    valueFormatter = IndexAxisValueFormatter(labels.map { formatDateLabel(it) })
                }
                axisRight.isEnabled = false
                description = Description().apply {
                    text =
                        context.getString(R.string.historical_exchange_rate_to, startDate, endDate)
                }
                animateX(1000)
                invalidate()
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            binding?.progressBar?.visibility = View.GONE
            binding?.noDataText?.visibility = View.VISIBLE
            binding?.noDataText?.text = errorMessage
            binding?.lineChart?.visibility = View.GONE
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
        binding?.backButton?.setOnClickListener {
            findNavController().navigate(R.id.action_currencyGraphFragment_to_mainPage)
        }
    }

    private fun formatDateLabel(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("MMM dd", Locale.US)
            val date = inputFormat.parse(dateStr)
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}