package com.project.financialManagement.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.project.financialManagement.R
import com.project.financialManagement.adapter.ListAdapter
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.databinding.FragmentStatisticalSpendingBinding
import com.project.financialManagement.model.groupDataByDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Statistical_spending.newInstance] factory method to
 * create an instance of this fragment.
 */
class Statistical_spending : Fragment() {
    private var _binding: FragmentStatisticalSpendingBinding? = null
    private val binding get() = _binding!!
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticalSpendingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Inflate the layout for this fragment
        val underlineWeek: View = binding.underlineWeek
        val underlineMonth: View = binding.underlineMonth
        val underlineYear: View = binding.underlineYear

        underlineWeek.visibility = View.VISIBLE
        updateChartData("week")
        val textViewWeek: TextView = binding.textViewWeek
        val textViewMonth: TextView = binding.textViewMonth
        val textViewYear: TextView = binding.textViewYear

        val onClickListener = View.OnClickListener { view ->
            underlineWeek.visibility = View.GONE
            underlineMonth.visibility = View.GONE
            underlineYear.visibility = View.GONE

            when (view.id) {
                R.id.textViewWeek -> {
                    underlineWeek.visibility = View.VISIBLE
                    updateChartData("week")
                }
                R.id.textViewMonth -> {
                    underlineMonth.visibility = View.VISIBLE
                    updateChartData("month")
                }
                R.id.textViewYear -> {
                    underlineYear.visibility = View.VISIBLE
                    updateChartData("year")
                }
            }
        }

        textViewWeek.setOnClickListener(onClickListener)
        textViewMonth.setOnClickListener(onClickListener)
        textViewYear.setOnClickListener(onClickListener)

        // Hiển thị dữ liệu tháng mặc định
        updateChartData("week")



        val dh = TransactionManager(requireContext())
        val expenses = dh.getAllTransactions()
        // Nhóm dữ liệu theo ngày
        val groupedDataList: List<Any> = groupDataByDate(expenses)
        // Thiết lập adapter cho RecyclerView
        val adapter = ListAdapter(requireContext(), groupedDataList)
        val listHistory = binding.listSpending
        listHistory.adapter = adapter
        listHistory.layoutManager = LinearLayoutManager(requireContext())
        listHistory.setHasFixedSize(true)
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Statistical_spending.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Statistical_spending().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun updateChartData(period: String) {
        val entries = ArrayList<Entry>()
        val xAxis: XAxis = binding.lineChartExpense.xAxis
        when (period) {
            "week" -> {
                // Thêm dữ liệu cho 7 ngày
                entries.add(Entry(0f, 5f))
                entries.add(Entry(1f, 10f))
                entries.add(Entry(2f, 5f))
                entries.add(Entry(3f, 30f))
                entries.add(Entry(4f, 25f))
                entries.add(Entry(5f, 30f))
                entries.add(Entry(6f, 5f))
                val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                xAxis.valueFormatter = IndexAxisValueFormatter(days)
            }
            "month" -> {
                // Thêm dữ liệu cho 12 tháng
                entries.add(Entry(0f, 20f))
                entries.add(Entry(1f, 35f))
                entries.add(Entry(2f, 15f))
                entries.add(Entry(3f, 25f))
                entries.add(Entry(4f, 30f))
                entries.add(Entry(5f, 10f))
                entries.add(Entry(6f, 50f))
                entries.add(Entry(7f, 45f))
                entries.add(Entry(8f, 55f))
                entries.add(Entry(9f, 40f))
                entries.add(Entry(10f, 60f))
                entries.add(Entry(11f, 70f))
                val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                xAxis.valueFormatter = IndexAxisValueFormatter(months)
            }
            "year" -> {
                // Thêm dữ liệu cho 5 năm
                entries.add(Entry(0f, 150f))
                entries.add(Entry(1f, 200f))
                entries.add(Entry(2f, 50f))
                entries.add(Entry(3f, 300f))
                entries.add(Entry(4f, 80f))
                val years = arrayOf("2020", "2021", "2022", "2023", "2024")
                xAxis.valueFormatter = IndexAxisValueFormatter(years)
            }
        }

        val dataSet = LineDataSet(entries, "Label")
        dataSet.color = Color.RED
        dataSet.valueTextColor = Color.BLUE
        dataSet.setDrawCircles(false)

        val lineData = LineData(dataSet)
        binding.lineChartExpense.data = lineData

        val yAxisLeft: YAxis = binding.lineChartExpense.axisLeft
        val yAxisRight: YAxis = binding.lineChartExpense.axisRight
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = if (period == "week") 40f else if (period == "month") 80f else 400f
        yAxisLeft.granularity = if (period == "week") 5f else if (period == "month") 10f else 50f
        yAxisRight.isEnabled = false

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        binding.lineChartExpense.legend.isEnabled = false
        binding.lineChartExpense.description.isEnabled = false

        binding.lineChartExpense.invalidate()
    }
}