package com.project.financialManagement.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.project.financialManagement.adapter.SummaryAdapter
import com.project.financialManagement.databinding.FragmentStatisticalSpendingBinding
import com.project.financialManagement.model.Summary
import com.project.financialManagement.model.Transaction
import java.io.Serializable

class StatisticalIncome : Fragment() {
    private var _binding: FragmentStatisticalSpendingBinding? = null
    private val binding get() = _binding!!
    private var transactionsByCategory: List<Transaction> ? = null

    companion object {
        private const val ARG_TRANSACTIONS_BY_CATEGORY = "transactions_by_category"

        fun newInstance(transactionsByCategory: List<Transaction>) = StatisticalIncome().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TRANSACTIONS_BY_CATEGORY, transactionsByCategory as Serializable)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionsByCategory = arguments?.getSerializable(ARG_TRANSACTIONS_BY_CATEGORY) as?  List<Transaction>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticalSpendingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupPieChart()
        loadPieChartData()
//        val dh = TransactionManager(requireContext())
//        val expenses = dh.getAllTransactions()
//        // Nhóm dữ liệu theo ngày
////        val groupedDataList: List<Any> = groupDataByDate(expenses)
//        // Thiết lập adapter cho RecyclerView
//        val adapter = ListAdapter(requireContext(), groupedDataList)
//        val listHistory = binding.listSpending
//        listHistory.adapter = adapter
//        listHistory.layoutManager = LinearLayoutManager(requireContext())
//        listHistory.setHasFixedSize(true)

        val listSummary = mutableListOf<Summary>()
        listSummary.add(Summary("Xăng dầu", "100000VND", "30%", ""))
        listSummary.add(Summary("Đồ ăn", "10000VND", "30%", ""))
        listSummary.add(Summary("Học hành", "20000VND", "30%", ""))
        listSummary.add(Summary("Trọ", "50000VND", "30%", ""))
        listSummary.add(Summary("Điện nước", "20000VND", "30%", ""))
        val summaryAdapter = SummaryAdapter(listSummary)
        var listsummary = binding.listSpending
        listsummary.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listsummary.adapter = summaryAdapter
        listsummary.setHasFixedSize(true)
        return root
    }

    private fun setupPieChart() {
        binding.pieChartExpense.isDrawHoleEnabled = true
        binding.pieChartExpense.setHoleColor(Color.WHITE)
        binding.pieChartExpense.setEntryLabelTextSize(12f)
        binding.pieChartExpense.setEntryLabelColor(Color.BLACK)
        binding.pieChartExpense.centerText = "Sales by Region"
        binding.pieChartExpense.setCenterTextSize(16f)
        binding.pieChartExpense.description.isEnabled = false

        val legend = binding.pieChartExpense.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.isWordWrapEnabled = true
    }

    private fun loadPieChartData() {
        val entries = listOf(
            PieEntry(0.2f, "USA"),
            PieEntry(0.15f, "China"),
            PieEntry(0.25f, "UK"),
            PieEntry(0.3f, "Germany"),
            PieEntry(0.1f, "Others")
        )

        val dataSet = PieDataSet(entries, "Sales by region")

        // Custom color list
        val colors = listOf(
            Color.rgb(255, 99, 71),   // Tomato
            Color.rgb(135, 206, 250), // Light Sky Blue
            Color.rgb(60, 179, 113),  // Medium Sea Green
            Color.rgb(238, 130, 238), // Violet
            Color.rgb(255, 215, 0)    // Gold
        )
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(binding.pieChartExpense))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        val total = 10
        binding.pieChartExpense.centerText = "Total: $total"

        binding.pieChartExpense.data = data
        binding.pieChartExpense.invalidate()
    }
}