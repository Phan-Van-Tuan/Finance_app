package com.project.financialManagement.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.R
import com.project.financialManagement.broadcast.AlarmReceiver
import com.project.financialManagement.databinding.FragmentScheduleBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvOption.text = "Hằng tháng"
        val optionMenu = DropdownMenu(requireActivity(), listOf(
            "Hằng ngày",
            "Hằng tuần",
            "Hằng tháng",
            "Hằng năm",
            "Tùy chỉnh"
        )) { selectedItem, position ->
            binding.tvOption.text = selectedItem
            if(position == 5) {
                findNavController().navigate(R.id.action_schedule_to_date_rang_picker)
            }
        }
        binding.tvOption.setOnClickListener {
            optionMenu.show()
        }

        val current = Calendar.getInstance()
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm")
        val time = dateTimeFormat.format(current.time)
        binding.time.text = time
        binding.time.setOnClickListener {
            showDateTimePicker(binding.time)
        }

        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        // Check if am is null before using it
        binding.save.setOnClickListener {
            if (am != null) {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)    // Set phút báo thức (ví dụ: 30 phút)
                    set(Calendar.SECOND, 0)       // Set giây báo thức
                }
                val intent = Intent(requireContext(), AlarmReceiver::class.java)
                val pi = PendingIntent.getBroadcast(
                    requireActivity(),
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
                Toast.makeText(requireActivity(), "$selectedHour:$selectedMinute", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "schedule failure", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun showDateTimePicker(time: TextView) {
        // Lấy thời gian hiện tại
        val current = Calendar.getInstance()

        // Tạo DatePickerDialog
        val datePicker = DatePickerDialog(requireActivity(),
            { _, year, month, dayOfMonth ->
                // Khi người dùng chọn ngày xong, tiếp tục hiển thị TimePickerDialog
                val timePicker = TimePickerDialog(requireActivity(),
                    { _, hourOfDay, minute ->
                        // Hiển thị giờ đã chọn lên TextView
                        val formattedTime = String.format(
                            "%04d-%02d-%02dT%02d:%02d:00",
                            year, month + 1, dayOfMonth, hourOfDay, minute
                        )
                        time.text = formattedTime
                    },
                    current.get(Calendar.HOUR_OF_DAY),
                    current.get(Calendar.MINUTE),
                    true
                )
                timePicker.show()
            },
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}