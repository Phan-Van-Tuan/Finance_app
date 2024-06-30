package com.project.financialManagement.fragment


import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.R
import com.project.financialManagement.databinding.FragmentScheduleBinding
import com.project.financialManagement.helper.CategoryManager
import com.project.financialManagement.helper.CurrencyTextWatcher
import com.project.financialManagement.model.SharedViewModel
import com.project.financialManagement.model.TransactionType


class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()

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

        binding.total.addTextChangedListener(CurrencyTextWatcher(binding.total))
        
        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        viewModel.option.observe(viewLifecycleOwner) { option->
            // Sử dụng dữ liệu từ ViewModel
            binding.time.text = "Option: $option on"
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}