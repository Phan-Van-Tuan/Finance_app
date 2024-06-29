package com.project.financialManagement.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.project.financialManagement.DropdownMenu
import com.project.financialManagement.R
import com.project.financialManagement.databinding.ActivityAddTransactionBinding
import com.project.financialManagement.helper.CategoryManager
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.model.Transaction
import com.project.financialManagement.model.TransactionType
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID


class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.back.setOnClickListener { finish() }

        configRadioGroup(binding.income, binding.spending)

        // get current time
        val current = Calendar.getInstance()
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val time = dateTimeFormat.format(current.time)
        binding.time.text = time

        // set custom time
        binding.time.setOnClickListener {
            showDateTimePicker(binding.time)
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

        // handle when click save button
        val dh = TransactionManager(this)
        val newId = UUID.randomUUID().toString()
        binding.save.setOnClickListener {
            val category = binding.category.text.toString().trim() // khoan thu = true, khoan chi = false
            val time = binding.time.text.toString()
            val total = binding.total.text.toString().trim()
            val description = binding.description.text.toString().trim()
            val type = configRadioGroup(binding.income, binding.spending)
            if(category.isNullOrEmpty() || time.isNullOrEmpty() || total.isNullOrEmpty()) {
                Toast.makeText(this, this.getString(R.string.please_select_all_fields), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            dh.addTransaction(Transaction(newId, type, time, total.toDouble(), category, description))
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

    private fun showDateTimePicker(time: TextView) {
        // Lấy thời gian hiện tại
        val current = Calendar.getInstance()

        // Tạo DatePickerDialog
        val datePicker = DatePickerDialog(this,
            { _, year, month, dayOfMonth ->
                // Khi người dùng chọn ngày xong, tiếp tục hiển thị TimePickerDialog
                val timePicker = TimePickerDialog(this,
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

    private fun configCategoryMenu(type: Int) {
        val cm = CategoryManager(this)
        val categories = cm.getAllCategories()
        var titles = categories.filter{ it.type == type }.map { it.title }
        val categoryMenu = DropdownMenu(this, titles) { selectedItem, _ ->
            binding.category.text = selectedItem
        }
        binding.category.setOnClickListener {
            categoryMenu.show()
        }
    }

    private fun configRadioGroup(button1: TextView, button2: TextView): TransactionType {
        button1.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
        button1.setTextColor(ContextCompat.getColor(this, R.color.white))
        button2.setBackgroundColor(ContextCompat.getColor(this, R.color.parent))
        button2.setTextColor(ContextCompat.getColor(this, R.color.primary))
        var type = TransactionType.INCOME
        configCategoryMenu(type.value)

        button1.setOnClickListener {
            type = TransactionType.INCOME
            button1.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
            button1.setTextColor(ContextCompat.getColor(this, R.color.white))
            button2.setBackgroundColor(ContextCompat.getColor(this, R.color.parent))
            button2.setTextColor(ContextCompat.getColor(this, R.color.primary))
            configCategoryMenu(type.value)
        }

        button2.setOnClickListener {
            type = TransactionType.EXPENSE
            button2.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
            button2.setTextColor(ContextCompat.getColor(this, R.color.white))
            button1.setBackgroundColor(ContextCompat.getColor(this, R.color.parent))
            button1.setTextColor(ContextCompat.getColor(this, R.color.primary))
            configCategoryMenu(type.value)
        }
        return type
    }
}