package com.project.financialManagement.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.project.financialManagement.R
import com.project.financialManagement.databinding.FragmentStatisticalBinding
import com.project.financialManagement.helper.FormatHelper
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.model.Transaction
import com.project.financialManagement.model.TransactionType
import java.time.YearMonth
import java.util.Calendar

class StatisticalFragment : Fragment() {

    private var calendar = Calendar.getInstance()
    private val dh by lazy { TransactionManager(requireContext()) }
    private lateinit var transactionsByRevenue: List<Transaction>
    private lateinit var transactionsByExpense: List<Transaction>
    private var _binding: FragmentStatisticalBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticalBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        // Set initial text for EditText
        updateMonthYearText()

        // Click listeners for previous and next month buttons
        binding.prevMonthButton.setOnClickListener { updateMonth(-1) }
        binding.nextMonthButton.setOnClickListener { updateMonth(1) }

        // Click listener for EditText to show MonthYearPickerDialog
        binding.monthYearEditText.setOnClickListener { showMonthYearPickerDialog() }

//        setupViewPager()
    }

    private fun setupViewPager() {
        val tabLayout = binding.tlTransaction
        val viewPager = binding.viewPager

        viewPager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> requireContext().getString(R.string.revenues)
                1 -> requireContext().getString(R.string.expenses)
                else -> null
            }
        }.attach()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMonth(amount: Int) {
        calendar.add(Calendar.MONTH, amount)
        updateMonthYearText()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    private fun showMonthYearPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_month_year_picker, null)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)

        setupMonthYearPickers(monthPicker, yearPicker)

        AlertDialog.Builder(requireContext())
            .setTitle("Chọn tháng và năm")
            .setView(dialogView)
            .setPositiveButton("Xác nhận") { _, _ ->
                calendar.set(yearPicker.value, monthPicker.value - 1, 1)
                updateMonthYearText()
            }
            .setNegativeButton("Hủy", null)
            .create()
            .show()
    }

    private fun setupMonthYearPickers(monthPicker: NumberPicker, yearPicker: NumberPicker) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = month + 1

        yearPicker.minValue = year - 100
        yearPicker.maxValue = year + 100
        yearPicker.value = year
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMonthYearText() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        binding.monthYearEditText.setText(String.format("%02d/%d", month, year))
        updateData()
        setupViewPager()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateData() {
        val currentYearMonth =
            YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
        val startOfMonth = currentYearMonth.atDay(1).atStartOfDay()
        val endOfMonth = currentYearMonth.atEndOfMonth().atTime(23, 59, 59)

        val expenses = dh.getTotalInTime(startOfMonth, endOfMonth, TransactionType.EXPENSE)
        val revenues = dh.getTotalInTime(startOfMonth, endOfMonth, TransactionType.INCOME)
        val total = dh.getTotalInTime(startOfMonth, endOfMonth)
        val allTransactions = dh.getAllTransactionsInTime(startOfMonth, endOfMonth)

        transactionsByRevenue = allTransactions.filter { it.type == TransactionType.INCOME }
        transactionsByExpense = allTransactions.filter { it.type == TransactionType.EXPENSE }



        binding.revenues.text = FormatHelper.formatCurrency(revenues, requireContext())
        binding.expenses.text = FormatHelper.formatCurrency(expenses, requireContext())
        binding.total.text = FormatHelper.formatCurrency(total, requireContext())
    }

    private inner class ViewPagerAdapter(
        fragment: Fragment,
    ) : FragmentStateAdapter(fragment) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int) = when (position) {
            0 -> StatisticalIncome.newInstance(transactionsByRevenue)
            1 -> StatisticalSpending.newInstance(transactionsByExpense)
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
