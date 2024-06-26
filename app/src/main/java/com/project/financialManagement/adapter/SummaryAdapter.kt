package com.project.financialManagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.financialManagement.R
import com.project.financialManagement.model.Summary
class SummaryAdapter(var Summary: List<Summary>) : RecyclerView.Adapter<SummaryAdapter.listsummary>() {

    inner class listsummary(itemView: View) :RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): listsummary {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.component_summary,parent,false)
        return listsummary(view)
    }

    override fun onBindViewHolder(holder: listsummary, position: Int) {
        holder.itemView.apply {
            var nameCategory = findViewById<TextView>(R.id.txt_name_category)
            var total = findViewById<TextView>(R.id.txt_total)
            var percent = findViewById<TextView>(R.id.txt_percent)
            var avatarContact = findViewById<ImageView>(R.id.img_content)
            nameCategory.setText(Summary[position].category)
            total.setText(Summary[position].total)
            percent.setText(Summary[position].percent)
        }
    }

    override fun getItemCount(): Int {
        return Summary.size
    }
}