package com.project.financialManagement.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.project.financialManagement.R
import com.project.financialManagement.adapter.CategoryAdapter
import com.project.financialManagement.helper.CategoryManager
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.model.Category
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Category.newInstance] factory method to
 * create an instance of this fragment.
 */
class CategoryFragment : Fragment() {
    private lateinit var categoryManager: CategoryManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        setupRecyclerView(view)
        categoryManager = CategoryManager(requireContext())
        updateData()
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            loadData {
                updateData()
                swipeRefreshLayout.isRefreshing = false
            }
        }
        return view
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.list_category)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun showAddCategoryDialog() {
        showCategoryDialog(null) { newCategory ->
            categoryManager.addCategory(newCategory)
            categoryAdapter.addItem(newCategory)
        }
    }

    private fun showDetailCategoryDialog(category: Category) {
        // Inflate layout cho AlertDialog từ file XML tạo sẵn
        val view = LayoutInflater.from(context).inflate(R.layout.layout_category_detail, null)

        // Tạo AlertDialog và thiết lập các thuộc tính
        val dialog = AlertDialog.Builder(context)
            .setView(view) // Đặt layout đã inflate vào AlertDialog
            .create()

        // Ánh xạ các view từ layout
        val titleTextView = view.findViewById<TextView>(R.id.tvTitle)
        val typeCategoryTextView = view.findViewById<TextView>(R.id.tvTypeCategory)
        val descriptionTextView = view.findViewById<TextView>(R.id.tvDescription)
        val btnClose = view.findViewById<ImageView>(R.id.btn_close)
        val imgIcon = view.findViewById<ImageView>(R.id.img_icon)

        titleTextView.text = category.title

        typeCategoryTextView.text = if(category.type == 1) {
             requireContext().getString(R.string.revenues)
        } else {
            requireContext().getString(R.string.expenses)
        }

        descriptionTextView.text = if (category.description.isNullOrEmpty()) {
            requireContext().getString(R.string.empty)
        } else {
            category.description
        }


        val iconId =
            requireContext().resources.getIdentifier("icon_back", "drawable", requireContext().packageName)
        imgIcon.setImageResource(iconId)
        btnClose.setOnClickListener {
            dialog.dismiss() // Đóng AlertDialog khi hủy
        }

        // Hiển thị AlertDialog
        dialog.show()
    }


    private fun showEditCategoryDialog(category: Category) {
        showCategoryDialog(category) { updatedCategory ->
            categoryManager.updateCategory(updatedCategory)
            categoryAdapter.updateItem(updatedCategory)
        }
    }

    private fun showDeleteCategoryDialog(category: Category) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.delete_category))
            .setMessage("${requireContext().getString(R.string.delete_category_confirmation_1)} '${category.title}' ${requireContext().getString(R.string.delete_category_confirmation_2)}")
            .setIcon(R.drawable.icon_warning)
            .setPositiveButton(requireContext().getString(R.string.delete)) { dialog, _ ->
                categoryManager.deleteCategory(category.id)
                val tm = TransactionManager(requireContext())
                val transactions = tm.getAllTransactions()
                val listTransactionIdOfCategory = transactions.filter { it.category == category.title }.map {it.id}
                listTransactionIdOfCategory.forEach { transactionId ->
                    tm.deleteTransaction(transactionId)
                }
                categoryAdapter.deleteItem(category)
                dialog.dismiss()
            }
            .setNegativeButton(requireContext().getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun showCategoryDialog(category: Category?, onSave: (Category) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_category, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (category == null) requireContext().getString(R.string.add_category) else requireContext().getString(R.string.edit_category))

        val alertDialog = dialogBuilder.show()

        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle).apply {
            setText(category?.title)
        }
        val editTextIcon = dialogView.findViewById<EditText>(R.id.editTextIcon).apply {
            setText(category?.icon)
        }
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription).apply {
            setText(category?.description)
        }
        val addButton = dialogView.findViewById<Button>(R.id.buttonAdd).apply {
            text = if (category == null) requireContext().getString(R.string.add) else requireContext().getString(R.string.update)
        }
        val spendingButton = dialogView.findViewById<MaterialButton>(R.id.spending)
        val incomeButton = dialogView.findViewById<MaterialButton>(R.id.income)
        val typeGroup = dialogView.findViewById<MaterialButtonToggleGroup>(R.id.type)

        setupToggleButtons(typeGroup, incomeButton, spendingButton, category?.type ?: 1)
        Toast.makeText(requireContext(), category?.type.toString(), Toast.LENGTH_SHORT).show()

        addButton.setOnClickListener {
            val id = category?.id ?: UUID.randomUUID().toString()
            val title = editTextTitle.text.toString().trim()
            val color = "bg_primary"
            val icon = editTextIcon.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val type = if (incomeButton.isChecked) 1 else -1

            val newCategory = Category(id, title, color, icon, description, type)
            Toast.makeText(requireContext(), type.toString(), Toast.LENGTH_SHORT).show()
            onSave(newCategory)
            alertDialog.dismiss()
        }
    }

    private fun setupToggleButtons(
        typeGroup: MaterialButtonToggleGroup,
        incomeButton: MaterialButton,
        spendingButton: MaterialButton,
        initialType: Int
    ) {
        var type = initialType
        updateButtonColors(incomeButton, spendingButton, type)

        typeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                type = when (checkedId) {
                    R.id.income -> 1
                    R.id.spending -> -1
                    else -> type
                }
                updateButtonColors(incomeButton, spendingButton, type)
            }
        }
    }

    private fun updateButtonColors(incomeButton: MaterialButton, spendingButton: MaterialButton, type: Int) {
        if (type == 1) {
            incomeButton.isChecked = true
            spendingButton.isChecked = false
            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            spendingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.parent))
        } else {
            spendingButton.isChecked = true
            incomeButton.isChecked = false
            spendingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.parent))
        }
    }

    private fun updateData() {
        val categories = categoryManager.getAllCategories().toMutableList()
        categoryAdapter = CategoryAdapter(categories, ::showAddCategoryDialog, ::showDetailCategoryDialog,::showEditCategoryDialog, ::showDeleteCategoryDialog )
        recyclerView.adapter = categoryAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loadData(callback: () -> Unit) {
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
}