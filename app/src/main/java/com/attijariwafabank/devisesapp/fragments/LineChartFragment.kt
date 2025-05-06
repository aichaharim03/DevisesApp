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
import com.attijariwafabank.devisesapp.CurrencyViewModel
import com.attijariwafabank.devisesapp.databinding.FragmentLineChartBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class LineChartFragment : Fragment() {

    private lateinit var viewModel: CurrencyViewModel
    private var binding: FragmentLineChartBinding? = null
    private lateinit var sourceCurrency: String
    private lateinit var targetCurrency: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLineChartBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: LineChartFragmentArgs by navArgs()
        sourceCurrency = args.sourceCurrency
        targetCurrency = args.targetCurrency

        // Set title to show what exchange rate we're viewing
        binding?.chartTitle?.text = "$sourceCurrency to $targetCurrency Exchange Rate"

        // Show loading indicator
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.lineChart?.visibility = View.GONE
        binding?.noDataText?.visibility = View.GONE

        viewModel = ViewModelProvider(requireActivity())[CurrencyViewModel::class.java]

        // Get dates for the last month
        val endCalendar = Calendar.getInstance()
        val startCalendar = Calendar.getInstance()
        startCalendar.add(Calendar.MONTH, -1)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = dateFormat.format(startCalendar.time)
        val endDate = dateFormat.format(endCalendar.time)

        viewModel.requestTimeFrame(
            accessKey = "c30d334a99b84799e1521abbc4b15e4a",
            source = sourceCurrency,
            targetCurrency = targetCurrency,
            startDate = startDate,
            endDate = endDate
        )

        viewModel.historicalRates.observe(viewLifecycleOwner) { rateMap ->
            binding?.progressBar?.visibility = View.GONE

            if (rateMap.isEmpty()) {
                binding?.noDataText?.visibility = View.VISIBLE
                binding?.lineChart?.visibility = View.GONE
                return@observe
            }

            binding?.lineChart?.visibility = View.VISIBLE

            val entries = mutableListOf<Entry>()
            val labels = rateMap.keys.sorted()

            labels.forEachIndexed { index, date ->
                val rate = rateMap[date] ?: 0.0
                entries.add(Entry(index.toFloat(), rate.toFloat()))
            }

            val dataSet = LineDataSet(entries, "$sourceCurrency to $targetCurrency Rate")
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
                    text = "Historical Exchange Rate ($startDate to $endDate)"
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
    }

    private fun formatDateLabel(dateStr: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("MMM dd", Locale.US)
            val date = inputFormat.parse(dateStr)
            return date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            return dateStr
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}