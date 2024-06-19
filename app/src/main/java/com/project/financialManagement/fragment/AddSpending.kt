package com.project.financialManagement.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.helper.CategoryManager
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.databinding.FragmentAddSpendingBinding
import com.project.financialManagement.model.Transaction
import com.project.financialManagement.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddSpending : Fragment() {
    private var _binding: FragmentAddSpendingBinding? = null
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
        _binding = FragmentAddSpendingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // dropdown label
        val cm = CategoryManager(requireContext())
        val categories = cm.getAllCategories()
        val titles = categories.filter{ it.type == -1 }.map { it.title }
        val labelMenu = DropdownMenu(requireContext(), titles) { selectedItem, _ ->
            binding.category.text = "$selectedItem"
        }
        binding.categoriesLayout.setOnClickListener {
            labelMenu.show()
        }

        // dropdown repeat
        val repeatMenu = DropdownMenu(requireContext(), listOf(
            "Lặp lại theo ngày",
            "Lặp lại theo tháng",
            "Lặp lại theo năm"
        )) { selectedItem, _ ->
            binding.repeat.text = "$selectedItem"
        }
        binding.repeatLayout.setOnClickListener {
            repeatMenu.show()
        }

        // get current time
        val current = Calendar.getInstance()
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val time = dateTimeFormat.format(current.time)
        binding.time.text = time

        // set custom time
        val timeLayout = binding.timeLayout
        timeLayout.setOnClickListener {
            showDateTimePicker(binding.time)
        }

        // config create schedule
        val scheduleSwitch = binding.schedule
        val repeatLayout = binding.repeatLayout
        val timeDefaultLayout = binding.timeTransactionDefaultLayout
        val timeStartLayout = binding.timeTransactionStartLayout
        val timeEndLayout = binding.timeTransactionEndLayout
        scheduleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Khi Switch được bật, hiển thị repeat_layout
                repeatLayout.visibility = View.VISIBLE
                timeDefaultLayout.visibility = View.VISIBLE
                timeStartLayout.visibility = View.VISIBLE
                timeEndLayout.visibility = View.VISIBLE
                timeLayout.visibility = View.GONE
            } else {
                // Khi Switch được tắt, ẩn repeat_layout
                repeatLayout.visibility = View.GONE
                timeDefaultLayout.visibility = View.GONE
                timeStartLayout.visibility = View.GONE
                timeEndLayout.visibility = View.GONE
                timeLayout.visibility = View.VISIBLE
            }
        }

        val timeFormat = SimpleDateFormat("'T'HH:mm:ss")
        current.set(Calendar.HOUR_OF_DAY, 0) // Đặt giờ thành 0
        current.set(Calendar.MINUTE, 0) // Đặt phút thành 0
        current.set(Calendar.SECOND, 0) // Đặt giây thành 0
        binding.timeDefault.text = timeFormat.format(current.time)
        timeDefaultLayout.setOnClickListener {
            showTimePicker(binding.timeDefault)
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        binding.timeStart.text = dateFormat.format(current.time)
        timeStartLayout.setOnClickListener {
            showDatePicker(binding.timeStart)
        }

        current.add(Calendar.MONTH, 1)
        binding.timeEnd.text = dateFormat.format(current.time)
        timeEndLayout.setOnClickListener {
            showDatePicker(binding.timeEnd)
        }

        // handle when click save button
        val dh = TransactionManager(requireContext())
        val newId = UUID.randomUUID().toString()
        binding.save.setOnClickListener {
            val category = binding.category.text.toString().trim() // khoan thu = true, khoan chi = false
            val time = binding.time.text.toString()
            val total = binding.total.text.toString().trim()
            val description = binding.description.text.toString().trim()
            if(category.isNullOrEmpty() || time.isNullOrEmpty() || total.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ các trường", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "$newId \n $category \n $time \n $total \n $description", Toast.LENGTH_LONG).show()
            dh.addTransaction(Transaction(newId, TransactionType.EXPENSE, time, total.toDouble(), category, description))
        }
        return root
    }

    private fun showDateTimePicker(time: TextView) {
        // Lấy thời gian hiện tại
        val current = Calendar.getInstance()

        // Tạo DatePickerDialog
        val datePicker = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                // Khi người dùng chọn ngày xong, tiếp tục hiển thị TimePickerDialog
                val timePicker = TimePickerDialog(requireContext(),
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

    private fun showDatePicker(time: TextView) {
        // Lấy thời gian hiện tại
        val current = Calendar.getInstance()

        // Tạo DatePickerDialog
        val datePicker = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format(
                    "%04d-%02d-%02d",
                    year, month + 1, dayOfMonth
                )
                time.text = formattedDate
            },
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
    private fun showTimePicker(time: TextView) {
        // Khi người dùng chọn ngày xong, tiếp tục hiển thị TimePickerDialog
        val current = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                // Hiển thị giờ đã chọn lên TextView
                val formattedTime = String.format(
                    "T%02d:%02d:00",
                    hourOfDay, minute
                )
                time.text = formattedTime
            },
            current.get(Calendar.HOUR_OF_DAY),
            current.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddSpending.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddSpending().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}