package com.project.financialManagement.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.databinding.FragmentDateRangPickerBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class DateRangPickerFragment : Fragment() {
    private var _binding: FragmentDateRangPickerBinding?  = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDateRangPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val current = Calendar.getInstance()
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd")
        val time = dateTimeFormat.format(current.time)
        binding.tvStartDate.text = time
        binding.tvStartDate.setOnClickListener {
            showDatePicker(binding.tvStartDate)
        }
        binding.tvEndDate.text = "Cho đến khi được thay đổi"
        binding.tvEndDate.setOnClickListener {
            showDatePicker(binding.tvEndDate)
        }
    }

    private fun showDatePicker(time: TextView) {
        // Lấy thời gian hiện tại
        val current = Calendar.getInstance()

        // Tạo DatePickerDialog
        val datePicker = DatePickerDialog(requireActivity(),
            { _, year, month, dayOfMonth ->
                val formattedTime = String.format(
                    "%04d-%02d-%02d",
                    year, month + 1, dayOfMonth,
                )
                time.text = formattedTime
            },
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePicker(time: TextView) {
        val current = Calendar.getInstance()
        val timePicker = TimePickerDialog(requireActivity(),
            { _, hourOfDay, minute ->
                // Hiển thị giờ đã chọn lên TextView
                time.text = String.format("%02d:%02d", hourOfDay, minute)
            },
            current.get(Calendar.HOUR_OF_DAY),
            current.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}