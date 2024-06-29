package com.project.financialManagement.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.project.financialManagement.R
import com.project.financialManagement.helper.CategoryManager
import com.project.financialManagement.helper.FormatHelper
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.model.DataItem1
import com.project.financialManagement.model.DataItem2
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListAdapter (private val context: Context, private var dataList: List<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM_1 = 1
        private const val TYPE_ITEM_2 = 2
    }

    inner class Item1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.list_date)

        fun bind(data: DataItem1) {
            dateTextView.text = data.date
        }
    }

    inner class Item2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.item_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.item_description)
        private val priceTextView: TextView = itemView.findViewById(R.id.item_price)
        private val dateTextView: TextView = itemView.findViewById(R.id.item_time)
        private val color: LinearLayout = itemView.findViewById(R.id.item_color)
        private val icon: ImageView = itemView.findViewById(R.id.item_icon)
        private val sh = SharedPreferencesHelper(context)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: DataItem2) {
            typeTextView.text = data.type
            if (data.description == null) {
                descriptionTextView.text = "Empty"
            } else {
                descriptionTextView.text = data.description
            }

            priceTextView.text = FormatHelper.formatCurrency(data.total, context)

            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val parsedDate = LocalDateTime.parse(data.date, dateFormatter)
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm")
            val formattedDate = parsedDate.format(timeFormatter)

            dateTextView.text = formattedDate // Đặt giá trị định dạng vào TextView

            val context = itemView.context
            val categories = CategoryManager(context).getAllCategories()
            val category = categories.find { it.title ==  data.type}
            val bgId = context.resources.getIdentifier(category?.color ?: "bg_primary", "drawable", context.packageName)
            val iconId = context.resources.getIdentifier(category?.icon ?: "icon_home", "drawable", context.packageName)

            if (iconId != 0 || bgId != 0) { // kiểm tra nếu drawableId hợp lệ
                icon.setImageResource(iconId)
                color.setBackgroundResource(bgId)
            } else {
                icon.setImageResource(R.drawable.icon_check)
                color.setBackgroundResource(R.drawable.bg_primary)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM_1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.component_time, parent, false)
                Item1ViewHolder(view)
            }
            TYPE_ITEM_2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.component_item, parent, false)
                Item2ViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_ITEM_1 -> (holder as Item1ViewHolder).bind(dataList[position] as DataItem1)
            TYPE_ITEM_2 -> (holder as Item2ViewHolder).bind(dataList[position] as DataItem2)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is DataItem1 -> TYPE_ITEM_1
            is DataItem2 -> TYPE_ITEM_2
            else -> throw IllegalArgumentException("Invalid data type at position $position")
        }
    }
}