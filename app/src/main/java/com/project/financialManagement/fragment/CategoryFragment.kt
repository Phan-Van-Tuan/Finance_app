package com.project.financialManagement.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
        TODO()
    }

    private fun showEditCategoryDialog(category: Category) {
        showCategoryDialog(category) { updatedCategory ->
            categoryManager.updateCategory(updatedCategory)
            categoryAdapter.updateItem(updatedCategory)
        }
    }

    private fun showDeleteCategoryDialog(category: Category) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete the category '${category.title}'?")
            .setPositiveButton("Delete") { dialog, _ ->
                categoryManager.deleteCategory(category.id)
//                categoryAdapter.deleteItem(category)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun showCategoryDialog(category: Category?, onSave: (Category) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_category, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (category == null) "Add Category" else "Edit Category")

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
            text = if (category == null) "Add" else "Update"
        }
        val spendingButton = dialogView.findViewById<MaterialButton>(R.id.spending)
        val incomeButton = dialogView.findViewById<MaterialButton>(R.id.income)
        val typeGroup = dialogView.findViewById<MaterialButtonToggleGroup>(R.id.type)

        setupToggleButtons(typeGroup, incomeButton, spendingButton, category?.type ?: 1)

        addButton.setOnClickListener {
            val id = category?.id ?: UUID.randomUUID().toString()
            val title = editTextTitle.text.toString().trim()
            val color = "bg_primary"
            val icon = editTextIcon.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val type = if (incomeButton.isChecked) 1 else -1

            val newCategory = Category(id, title, color, icon, description, type)
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
        incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
        var type = initialType
        if (type == 1) {
            incomeButton.isChecked = true
        } else {
            spendingButton.isChecked = true
        }

        typeGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
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
            incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            spendingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.parent))
        } else {
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