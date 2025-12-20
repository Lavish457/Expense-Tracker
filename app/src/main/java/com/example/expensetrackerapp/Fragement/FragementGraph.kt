package com.example.expensetrackerapp.Fragement

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.RetrofitClient
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragementGraph : Fragment() {
    private val API_KEY1 = "\$2a\$10$"
    private val API_KEY2 = "hPDzuJOstFCGQJp/WyXF/OCUkVjzUbrXHE1W6CMVm4jMb.MXdAz92"
    private val API_KEY = API_KEY1 + API_KEY2
    private val Trips_BIN_ID = "691875ae43b1c97be9af0b54"
    private var createrId : Long = -1
    private lateinit var totalSpent : TextView
    private lateinit var totalAvg : TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_fragement_graph, container, false)
        createrId = requireActivity()
            .getSharedPreferences("memberId", Context.MODE_PRIVATE)
            .getLong("memberId", -1)
        totalAvg = view.findViewById(R.id.spentAvg)
        totalSpent = view.findViewById(R.id.spentTotal)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getTrips(Trips_BIN_ID, API_KEY)
                val tripList = response.record.trips.toMutableList()
                val filtered = tripList.filter { it.memberID == createrId}
                val totalSum = filtered.sumOf { it.totalAmount}
                var length = tripList.size
                totalSpent.text = totalSum.toString()
                totalAvg.text = (totalSum / length).toString()
                Log.d("SUM", "${totalSum}")
                withContext(Dispatchers.Main) {

                    val barChart = view.findViewById<BarChart>(R.id.barChart)

                    val labels = filtered.map { it.tripName }

                    val entries = filtered.mapIndexed { index, trip ->
                        BarEntry(index.toFloat(), trip.totalAmount.toFloat())
                    }

                    val dataSet = BarDataSet(entries, "Trip Expenses")
                    dataSet.color = Color.parseColor("#7C3BED")
                    dataSet.valueTextSize = 12f

                    val barData = BarData(dataSet)
                    barChart.data = barData

                    barChart.xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        granularity = 1f
                        position = XAxis.XAxisPosition.BOTTOM
                        labelRotationAngle = -45f
                        textSize = 12f
                        setAvoidFirstLastClipping(true)
                    }

                    barChart.setExtraBottomOffset(20f)
                    barChart.setExtraLeftOffset(10f)
                    barChart.setExtraRightOffset(10f)

                    barChart.axisLeft.textSize = 12f
                    barChart.axisRight.isEnabled = false
                    barChart.description.isEnabled = false
                    barChart.legend.isEnabled = false

                    barChart.setFitBars(true)
                    barChart.invalidate()
                }

            }
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }
}