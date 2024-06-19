package com.project.financialManagement.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.financialManagement.adapter.ListAdapter
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.databinding.FragmentHomeBinding
import com.project.financialManagement.model.CoinModel
import com.project.financialManagement.model.TransactionType
import com.project.financialManagement.model.groupDataByDate
import java.text.DecimalFormat
import java.time.LocalDateTime


class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        updateData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData() {
        Toast.makeText(requireActivity(), "update data", Toast.LENGTH_SHORT).show()
        val sh = SharedPreferencesHelper(requireContext())
        val coinCode = CoinModel.values().first { coin -> coin.id == sh.getCoinId()}
        binding.typeCoin.text = coinCode.toString()

        val dh = TransactionManager(requireContext())
        val balance = dh.getBalance(TransactionType.ALL) // Giả sử bạn đã có số tiền cần định dạng

        val df = DecimalFormat("#,###")
        val formattedBalance: String = df.format(balance)
        binding.asset.text = formattedBalance
        binding.income.text = String.format("%.0f", dh.getBalance(TransactionType.INCOME))
        binding.spend.text = String.format("%.0f", dh.getBalance(TransactionType.EXPENSE))

        // Nhóm dữ liệu theo ngày
        val currentDate = LocalDateTime.now()
        val startOfWeek = currentDate.toLocalDate().minusDays(currentDate.dayOfWeek.value.toLong() - 1).atStartOfDay()
        val endOfWeek = startOfWeek.plusDays(6).plusHours(23).plusMinutes(59).plusSeconds(59)
        val expenses = dh.getAllTransactionsInTime(startOfWeek, endOfWeek)
        val groupedDataList: List<Any> = groupDataByDate(expenses)

        // Thiết lập adapter cho RecyclerView
        val adapter = ListAdapter(requireContext(), groupedDataList)
        val listHistory = binding.rcvRecentList
        listHistory.adapter = adapter
        listHistory.layoutManager = LinearLayoutManager(requireContext())
        listHistory.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}