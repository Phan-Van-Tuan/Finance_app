package com.project.financialManagement.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.financialManagement.adapter.ListAdapter
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.databinding.FragmentHomeBinding
import com.project.financialManagement.helper.FormatHelper
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.model.TransactionType
import com.project.financialManagement.model.groupDataByDate
import java.time.LocalDateTime
import java.time.YearMonth


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Sử dụng FragmentHomeBinding để lấy view
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Đảm bảo binding được sử dụng sau khi onCreateView đã khởi tạo nó
        loadData()
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            load {
                loadData()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadData() {
        val dh = TransactionManager(requireContext())
        val sh = SharedPreferencesHelper(requireContext())

        // Nhóm dữ liệu theo ngày
        val currentDate = LocalDateTime.now()
        val currentYearMonth = YearMonth.from(currentDate)
        val startOfMonth = currentYearMonth.atDay(1).atStartOfDay()
        val endOfMonth = currentYearMonth.atEndOfMonth().atTime(23, 59, 59)
        val expenses = dh.getAllTransactionsInTime(startOfMonth, endOfMonth)
        val groupedDataList: List<Any> = groupDataByDate(expenses)

        // Thiết lập adapter cho RecyclerView
        val adapter = ListAdapter(requireContext(), groupedDataList)
        val listHistory = binding.rcvRecentList
        listHistory.adapter = adapter
        listHistory.layoutManager = LinearLayoutManager(requireContext())
        listHistory.setHasFixedSize(true)

        val balance = dh.getBalance(TransactionType.ALL)
        val expense = dh.getTotalInTime(startOfMonth, endOfMonth, TransactionType.EXPENSE)

        binding.asset.text = FormatHelper.formatCurrency(balance, requireContext())
        binding.spend.text = FormatHelper.formatCurrency(expense, requireContext())
        val limitDefault = sh.getLimit()
        if (limitDefault != null) {
            binding.spendPercent.text = String.format("%.1f%%", (expense/limitDefault)*100)
        }

    }

    private fun load(callback: () -> Unit) {
        // Giả lập quá trình tải dữ liệu
        // Trong thực tế, bạn có thể gọi API hoặc lấy dữ liệu từ cơ sở dữ liệu
        Thread {
            // Giả lập độ trễ của mạng
            Thread.sleep(1000)
            // Cập nhật giao diện trên luồng chính
            activity?.runOnUiThread {
                // Cập nhật adapter của RecyclerView nếu cần
                // recyclerView.adapter?.notifyDataSetChanged()

                // Gọi lại callback để dừng biểu tượng tải
                callback()
            }
        }.start()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}