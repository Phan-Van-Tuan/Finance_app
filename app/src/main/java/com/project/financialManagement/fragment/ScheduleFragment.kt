package com.project.financialManagement.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.R
import com.project.financialManagement.broadcast.AlarmReceiver
import com.project.financialManagement.databinding.FragmentScheduleBinding
import com.project.financialManagement.helper.CategoryManager
import com.project.financialManagement.model.TransactionType
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar


class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        configRadioGroup(binding.income, binding.spending)

        binding.time.setOnClickListener {
            findNavController().navigate(R.id.action_schedule_to_date_rang_picker)
        }

        binding.total.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Đây là nơi bạn có thể thực hiện một số hành động trước khi văn bản thay đổi
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Đây là nơi bạn có thể thực hiện một số hành động khi văn bản đang thay đổi
                try {
                    val input = s.toString()
                    if (input.isNotEmpty()) {
                        val number = input.toDoubleOrNull()
                        if (number != null) {
                            val formattedNumber = DecimalFormat("#,###.##").format(number)
                            // Cập nhật EditText với giá trị định dạng
                            binding.total.removeTextChangedListener(this)
                            binding.totalDisplay.text = "$formattedNumber VND"
//                            editText.setSelection(formattedNumber.length)
                            binding.total.addTextChangedListener(this)
                        }
                    }
                    else {
                        binding.totalDisplay.text = ""
                    }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    // Xử lý lỗi, ví dụ như hiển thị thông báo lỗi cho người dùng
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Đây là nơi bạn có thể thực hiện một số hành động sau khi văn bản đã thay đổi
            }
        })
        
        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        // Check if am is null before using it
        binding.save.setOnClickListener {
//            if (am != null) {
//                val calendar = Calendar.getInstance().apply {
//                    set(Calendar.HOUR_OF_DAY, selectedHour)
//                    set(Calendar.MINUTE, selectedMinute)    // Set phút báo thức (ví dụ: 30 phút)
//                    set(Calendar.SECOND, 0)       // Set giây báo thức
//                }
//                val intent = Intent(requireContext(), AlarmReceiver::class.java)
//                val pi = PendingIntent.getBroadcast(
//                    requireActivity(),
//                    0,
//                    intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                )
//                am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
//                Toast.makeText(requireActivity(), "$selectedHour:$selectedMinute", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireActivity(), "schedule failure", Toast.LENGTH_SHORT).show()
//            }
        }
        return root
    }

    private fun configCategoryMenu(type: TransactionType) {
        val cm = CategoryManager(requireActivity())
        val categories = cm.getAllCategories()
        var titles = categories.filter{ it.type == type.value }.map { it.title }
        val categoryMenu = DropdownMenu(requireActivity(), titles) { selectedItem, _ ->
            binding.category.text = selectedItem
        }
        binding.category.setOnClickListener {
            categoryMenu.show()
        }
    }

    private fun configRadioGroup(button1: TextView, button2: TextView): TransactionType {
        button1.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.primary))
        button1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        button2.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.parent))
        button2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary))
        var type = TransactionType.INCOME
        configCategoryMenu(type)

        button1.setOnClickListener {
            type = TransactionType.INCOME
            button1.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.primary))
            button1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            button2.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.parent))
            button2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary))
            configCategoryMenu(type)
        }

        button2.setOnClickListener {
            type = TransactionType.EXPENSE
            button2.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.primary))
            button2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            button1.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.parent))
            button1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary))
            configCategoryMenu(type)
        }
        return type
    }
}