package com.project.financialManagement

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow

class DropdownMenu<T>(
    private val context: Context,
    private val items: List<T>,
    private val onItemSelected: (T, Int) -> Unit
) {
    private lateinit var popupWindow: PopupWindow

    fun show() {
        val layoutInflater = LayoutInflater.from(context)
        val menuView = layoutInflater.inflate(R.layout.dropdown_menu, null)

        val overlay: View = menuView.findViewById(R.id.overlay)
        overlay.setOnClickListener{
            dismiss()
        }

        popupWindow = PopupWindow(menuView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)

        val listView: ListView = menuView.findViewById(R.id.menu_list)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            onItemSelected(items[position], position)
            popupWindow.dismiss()
        }

        // Hiển thị menu ở giữa màn hình
        popupWindow.showAtLocation(menuView, android.view.Gravity.CENTER, 0, 0)
    }

    fun dismiss() {
        if (this::popupWindow.isInitialized && popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }
}

