package com.project.financialManagement.adapter

import android.content.Context
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
    private val categoriesList: MutableList<Category>,
    private val onAddItemClicked: () -> Unit,
    private val onDetailItemClicked: (Category) -> Unit,
    private val onEditItemClicked: (Category) -> Unit,
    private val onDeleteItemClicked: (Category) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_ADD_BUTTON = 1
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryBackground: LinearLayout = itemView.findViewById(R.id.category_color)
        val categoryIcon: ImageView = itemView.findViewById(R.id.category_icon)
        val categoryTitle: TextView = itemView.findViewById(R.id.category_title)
        val categoryDescription: TextView = itemView.findViewById(R.id.category_description)
        val categoryOptions: LinearLayout = itemView.findViewById(R.id.category_options)
    }

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
            val bgId =
                context.resources.getIdentifier(currentItem.color, "drawable", context.packageName)
            val iconId =
                context.resources.getIdentifier(currentItem.icon, "drawable", context.packageName)

            if (iconId != 0 || bgId != 0) {
                holder.categoryIcon.setImageResource(iconId)
                holder.categoryBackground.setBackgroundResource(bgId)
            } else {
                holder.categoryIcon.setImageResource(R.drawable.icon_check)
                holder.categoryBackground.setBackgroundResource(R.drawable.bg_primary)
            }

            holder.categoryOptions.setOnClickListener {
                showCategoryOptionsMenu(context, holder.categoryOptions, position)
            }

        } else if (holder is AddButtonViewHolder) {
            holder.addButton.setOnClickListener {
                onAddItemClicked()
            }
        }
    }

    private fun showCategoryOptionsMenu(context: Context, anchor: View, position: Int) {
        val popupMenu = android.widget.PopupMenu(context, anchor)
        popupMenu.inflate(R.menu.popup_category_options)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    onEditItemClicked(categoriesList[position])
                    true
                }

                R.id.action_delete -> {
                    onDeleteItemClicked(categoriesList[position])
                    true
                }

                R.id.action_view_details -> {
                    onDetailItemClicked(categoriesList[position])
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int = categoriesList.size + 1 // Plus one for the add button

    fun addItem(item: Category) {
        categoriesList.add(item)
        notifyItemInserted(categoriesList.size - 1)
    }

    fun updateItem(item: Category) {
        val index = categoriesList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            categoriesList[index] = item
            notifyItemChanged(index)
        }
    }

    fun deleteItem(item: Category) {
        val index = categoriesList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            categoriesList.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
