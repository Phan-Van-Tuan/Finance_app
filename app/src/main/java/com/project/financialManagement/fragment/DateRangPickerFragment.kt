package com.project.financialManagement.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.R
import com.project.financialManagement.databinding.FragmentDateRangPickerBinding
import com.project.financialManagement.model.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateRangPickerFragment : Fragment() {
    private var _binding: FragmentDateRangPickerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDateRangPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialValues()
        setupDropdownMenu()
        setupClickListeners()
    }

    private fun setupInitialValues() {
        val current = Calendar.getInstance()
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm")

        binding.tvStartDate.text = dateTimeFormat.format(current.time)
        binding.tvEndDate.text = requireContext().getString(R.string.until_changed)
        binding.tvOption.text = requireContext().getString(R.string.monthly)
        binding.tvTime.text = timeFormat.format(current.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time)

        binding.lnDateLayout.visibility = View.VISIBLE
        binding.lnWeekdayLayout.visibility = View.GONE
        binding.lnMonthLayout.visibility = View.GONE
    }

    private fun setupDropdownMenu() {
        val options = listOf(
            requireContext().getString(R.string.daily),
            requireContext().getString(R.string.weekly),
            requireContext().getString(R.string.monthly),
            requireContext().getString(R.string.yearly))
        val optionMenu = DropdownMenu(requireActivity(), options) { selectedItem, position ->
            binding.tvOption.text = selectedItem
            when (position) {
                0 -> toggleVisibility(View.GONE, View.GONE, View.GONE)
                1 -> toggleVisibility(View.GONE, View.VISIBLE, View.GONE)
                2 -> toggleVisibility(View.VISIBLE, View.GONE, View.GONE)
                3 -> toggleVisibility(View.VISIBLE, View.GONE, View.VISIBLE)
            }
        }
        binding.tvOption.setOnClickListener { optionMenu.show() }
    }

    private fun toggleVisibility(dateVisibility: Int, weekdayVisibility: Int, monthVisibility: Int) {
        binding.lnDateLayout.visibility = dateVisibility
        binding.lnWeekdayLayout.visibility = weekdayVisibility
        binding.lnMonthLayout.visibility = monthVisibility
    }

    private fun setupClickListeners() {
        binding.tvStartDate.setOnClickListener { showDatePicker(binding.tvStartDate) }
        binding.tvEndDate.setOnClickListener { showDatePicker(binding.tvEndDate) }
        binding.tvDate.setOnClickListener { showDatePickerDialog() }
        binding.tvMonth.setOnClickListener { showMonthPickerDialog() }
        binding.tvWeekdays.setOnClickListener { showWeekdayPickerDialog() }
        binding.tvTime.setOnClickListener { showTimePicker(binding.tvTime) }

        binding.save.setOnClickListener {
            if (areFieldsValid()) {
                viewModel.option.value = binding.tvOption.text.toString()
                viewModel.time.value = binding.tvTime.text.toString()
                viewModel.weekdays.value = binding.tvWeekdays.text.toString()
                viewModel.date.value = binding.tvDate.text.toString()
                viewModel.month.value = binding.tvMonth.text.toString()
                viewModel.startDate.value = binding.tvStartDate.toString()
                viewModel.endDate.value = binding.tvEndDate.toString()
                findNavController().navigate(R.id.action_date_rang_picker_to_schedule)
            } else {
                Toast.makeText(requireContext(), requireContext().getString(R.string.please_select_all_fields), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun areFieldsValid(): Boolean {
        // Kiểm tra nếu tất cả các trường đều được điền
        if (binding.tvStartDate.text.isEmpty() || binding.tvEndDate.text.isEmpty() ||
            binding.tvOption.text.isEmpty() || binding.tvTime.text.isEmpty() ||
            binding.tvWeekdays.text.isEmpty() || binding.tvDate.text.isEmpty() ||
            binding.tvMonth.text.isEmpty()) {
            return false
        }

        // Kiểm tra tính hợp lệ của ngày bắt đầu và ngày kết thúc
        val startDate = binding.tvStartDate.text.toString()
        val endDate = binding.tvEndDate.text.toString()
        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            return false
        }

        // Kiểm tra nếu ngày bắt đầu trước ngày kết thúc
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = sdf.parse(startDate)
        val end = sdf.parse(endDate)
        if (start != null && end != null && start.after(end)) {
            return false
        }

        // Kiểm tra tính hợp lệ của ngày dựa trên tháng và năm
        val day = binding.tvDate.text.toString().toIntOrNull()
        val month = binding.tvMonth.text.toString().toIntOrNull()
        if (day != null && month != null && !isValidDayOfMonth(day, month)) {
            return false
        }

        return true
    }

    // Hàm kiểm tra tính hợp lệ của ngày
    private fun isValidDate(date: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.isLenient = false
        return try {
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Hàm kiểm tra tính hợp lệ của ngày dựa trên tháng và năm
    private fun isValidDayOfMonth(day: Int, month: Int): Boolean {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> day in 1..31
            4, 6, 9, 11 -> day in 1..30
            2 -> day in 1..29 // Giả sử luôn hợp lệ trong năm nhuận
            else -> false
        }
    }


    private fun showDatePicker(textView: TextView) {
        val current = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireActivity(),
            { _, year, month, dayOfMonth ->
                val formattedTime = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                textView.text = formattedTime
            },
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker(textView: TextView) {
        val current = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireActivity(),
            { _, hourOfDay, minute ->
                textView.text = String.format("%02d:%02d", hourOfDay, minute)
            },
            current.get(Calendar.HOUR_OF_DAY),
            current.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun showDatePickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_mumber_picker, null)
        val dayPicker = dialogView.findViewById<NumberPicker>(R.id.number_picker)
        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = 1

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.select_day_of_months))
            .setView(dialogView)
            .setPositiveButton(requireContext().getString(R.string.ok)) { _, _ ->
                binding.tvDate.text = dayPicker.value.toString()
            }
            .setNegativeButton(requireContext().getString(R.string.cancel), null)
            .create()

        alertDialog.show()
    }

    private fun showMonthPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_mumber_picker, null)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.number_picker)
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = 1

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.select_month_of_years))
            .setView(dialogView)
            .setPositiveButton(requireContext().getString(R.string.ok)) { _, _ ->
                binding.tvMonth.text = monthPicker.value.toString()
            }
            .setNegativeButton(requireContext().getString(R.string.cancel), null)
            .create()

        alertDialog.show()
    }

    private fun showWeekdayPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_mumber_picker, null)
        val weekdayPicker = dialogView.findViewById<NumberPicker>(R.id.number_picker)
        weekdayPicker.minValue = 1
        weekdayPicker.maxValue = 7
        weekdayPicker.value = 2

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.select_weekday_of_weeks))
            .setView(dialogView)
            .setPositiveButton(requireContext().getString(R.string.ok)) { _, _ ->
                binding.tvWeekdays.text = getWeekdayString(weekdayPicker.value)
            }
            .setNegativeButton(requireContext().getString(R.string.cancel), null)
            .create()

        alertDialog.show()
    }

    private fun getWeekdayString(day: Int): String {
        return if (day == 1) "CN" else "T$day"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
