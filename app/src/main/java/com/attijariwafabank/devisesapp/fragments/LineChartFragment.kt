package com.attijariwafabank.devisesapp.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.attijariwafabank.devisesapp.R
import com.attijariwafabank.devisesapp.databinding.FragmentLineChartBinding
import com.attijariwafabank.devisesapp.viewModels.CurrencyViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class LineChartFragment : Fragment() {

    private lateinit var viewModel: CurrencyViewModel
    private var binding: FragmentLineChartBinding? = null
    private lateinit var sourceCurrency: String
    private lateinit var targetCurrency: String
    private val args: LineChartFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLineChartBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sourceCurrency = args.sourceCurrency
        targetCurrency = args.targetCurrency
        viewModel = ViewModelProvider(requireActivity())[CurrencyViewModel::class.java]

        binding?.chartTitle?.text = getString(R.string.to_exchange_rate, sourceCurrency, targetCurrency)
        showLoading()

        val endCalendar = Calendar.getInstance()
        val startCalendar = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }

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
            binding?.dataPointsCount?.text = rateMap.size.toString()
            if (rateMap.isEmpty()) {
                showNoData()
                binding?.noDataText?.text = getString(R.string.no_data_available_for_the_selected_date_range)
            } else {
                showChart()
                val sortedLabels = rateMap.keys.sorted()
                setupBeautifulChart(rateMap, labels = sortedLabels)
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            showNoData()
            binding?.noDataText?.text = errorMessage
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }

        binding?.backButton?.setOnClickListener {
            findNavController().navigate(R.id.action_currencyGraphFragment_to_mainPage)
        }
    }

    @SuppressLint("PrivateResource")
    private fun setupBeautifulChart(rateMap: Map<String, Double>, labels: List<String>) {
        val entries = labels.mapIndexed { index, date ->
            Entry(index.toFloat(), (rateMap[date] ?: 0.0).toFloat())
        }

        val dataSet = LineDataSet(entries, getString(R.string.to_rate, sourceCurrency, targetCurrency)).apply {
            color = ContextCompat.getColor(requireContext(), R.color.orange)
            lineWidth = 3f
            setCircleColor(ContextCompat.getColor(requireContext(), androidx.appcompat.R.color.material_blue_grey_800))
            circleRadius = 5f
            circleHoleRadius = 3f
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.orange)
            fillAlpha = 30
            setDrawValues(true)
            valueTextSize = 10f
            valueTextColor = Color.BLACK
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float) = String.format("%.4f", value)
            }
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            highlightLineWidth = 2f
            highLightColor = Color.RED
            setDrawHighlightIndicators(true)
            isHighlightEnabled = true
        }

        binding?.lineChart?.apply {
            data = LineData(dataSet)
            setBackgroundColor(Color.WHITE)
            setDrawGridBackground(false)
            setDrawBorders(false)
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
                setDrawAxisLine(true)
                axisLineColor = Color.GRAY
                axisLineWidth = 1f
                granularity = 1f
                labelRotationAngle = -45f
                textSize = 10f
                textColor = Color.DKGRAY
                typeface = Typeface.DEFAULT
                valueFormatter = IndexAxisValueFormatter(labels.map { formatDateLabel(it) })
                labelCount = 7
                isGranularityEnabled = true
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
                setDrawAxisLine(true)
                axisLineColor = Color.GRAY
                axisLineWidth = 1f
                textSize = 10f
                textColor = Color.DKGRAY
                typeface = Typeface.DEFAULT
                spaceTop = 10f
                spaceBottom = 10f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float) = String.format("%.4f", value)
                }
            }

            axisRight.isEnabled = false

            legend.apply {
                isEnabled = true
                textSize = 12f
                textColor = Color.DKGRAY
                typeface = Typeface.DEFAULT_BOLD
                form = com.github.mikephil.charting.components.Legend.LegendForm.LINE
                formSize = 12f
            }

            description = Description().apply {
                text = context.getString(
                    R.string.historical_exchange_rate_to,
                    formatDateLabel(labels.first()),
                    formatDateLabel(labels.last())
                )
                textSize = 11f
                textColor = Color.GRAY
                typeface = Typeface.DEFAULT
            }

            animateX(1200)
            animateY(1200)
            invalidate()
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

    private fun showLoading() {
        binding?.loadingContainer?.visibility = View.VISIBLE
        binding?.lineChart?.visibility = View.GONE
        binding?.noDataContainer?.visibility = View.GONE
    }

    private fun showChart() {
        binding?.loadingContainer?.visibility = View.GONE
        binding?.lineChart?.visibility = View.VISIBLE
        binding?.noDataContainer?.visibility = View.GONE
    }

    private fun showNoData() {
        binding?.loadingContainer?.visibility = View.GONE
        binding?.lineChart?.visibility = View.GONE
        binding?.noDataContainer?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
