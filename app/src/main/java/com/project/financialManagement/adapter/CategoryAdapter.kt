package com.project.financialManagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.financialManagement.R
import com.project.financialManagement.model.Category

class CategoryAdapter(
    private val categoriesList: List<Category>, // Make it MutableList to add items
    private val onAddItemClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD_BUTTON = 1
    }

    // Define a view holder for the category items
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryBackground: LinearLayout = itemView.findViewById(R.id.category_color)
        val categoryIcon: ImageView = itemView.findViewById(R.id.category_icon)
        val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        val categoryDescription: TextView = itemView.findViewById(R.id.category_description)
    }

    // Define a view holder for the "Add Item" button
    class AddButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addButton: CardView = itemView.findViewById(R.id.add_category_btn)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == categoriesList.size) VIEW_TYPE_ADD_BUTTON else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.component_category, parent, false)
                MyViewHolder(itemView)
            }
            VIEW_TYPE_ADD_BUTTON -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.component_add_category, parent, false)
                AddButtonViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            val currentItem = categoriesList[position]
            holder.categoryTitle.text = currentItem.title
            holder.categoryDescription.text = currentItem.description
            val context = holder.itemView.context
            val bgId = context.resources.getIdentifier(currentItem.color, "drawable", context.packageName)
            val iconId = context.resources.getIdentifier(currentItem.icon, "drawable", context.packageName)

            if (iconId != 0 || bgId != 0) { // kiểm tra nếu drawableId hợp lệ
                holder.categoryIcon.setImageResource(iconId)
                holder.categoryBackground.setBackgroundResource(bgId)
            } else {
                holder.categoryIcon.setImageResource(R.drawable.ic_check)
                holder.categoryBackground.setBackgroundResource(R.drawable.bg_primary)
            }
        } else if (holder is AddButtonViewHolder) {
            holder.addButton.setOnClickListener {
                onAddItemClicked()
            }
        }
    }

    override fun getItemCount(): Int = categoriesList.size + 1 // Plus one for the add button

    fun addItem(item: Category) {
        notifyItemInserted(categoriesList.size - 1)
    }
}
