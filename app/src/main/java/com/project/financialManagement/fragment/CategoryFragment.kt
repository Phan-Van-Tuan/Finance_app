package com.project.financialManagement.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
    private lateinit var dh: CategoryManager
    private lateinit var recyclerView: RecyclerView
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
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.list_category)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) // Số 2 là số cột

        dh = CategoryManager(requireContext())
        loadData()
        return view
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog_category, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Category")

        val alertDialog = dialogBuilder.show()

        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextColor = dialogView.findViewById<EditText>(R.id.editTextColor)
        val editTextIcon = dialogView.findViewById<EditText>(R.id.editTextIcon)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val addButton = dialogView.findViewById<Button>(R.id.buttonAdd)
        val spendingButton = dialogView.findViewById<MaterialButton>(R.id.spending)
        val incomeButton = dialogView.findViewById<MaterialButton>(R.id.income)
        val typeGroup = dialogView.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.type)

        incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
        var type = 1
        typeGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(isChecked) {
                when(checkedId){
                    R.id.income -> {
                        group.clearChecked()
                        type = 1
                        incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                        spendingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.parent))
                    }
                    R.id.spending -> {
                        group.clearChecked()
                        type = -1
                        spendingButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                        incomeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.parent))
                    }
                }
            }
        }

        addButton.setOnClickListener {

//            Toast.makeText(requireContext(), type, Toast.LENGTH_SHORT).show()
            val id = UUID.randomUUID().toString()
            val title = editTextTitle.text.toString().trim()
            val color = editTextColor.text.toString().trim()
            val icon = editTextIcon.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            val newCategory = Category(id, title, color, icon, description, type)
            dh.addCategory(newCategory)
            alertDialog.dismiss()
            loadData()
        }
    }

    fun loadData() {
        val categories = dh.getAllCategories()
        val adapter = CategoryAdapter(categories) {  ->
            showAddCategoryDialog()
        }
        recyclerView.adapter = adapter
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
}