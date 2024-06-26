package com.project.financialManagement.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.financialManagement.R
import com.project.financialManagement.adapter.ListAdapter
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.helper.TransactionManager
import com.project.financialManagement.model.groupDataByDate

class HistoryFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var list: RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        loadData(view)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            load {
                loadData(view)
                swipeRefreshLayout.isRefreshing = false
            }
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData(view: View) {
        list = view.findViewById(R.id.rcv_history_list)
        val dh = TransactionManager(requireContext())
        val expenses = dh.getAllTransactions()
        val groupedDataList: List<Any> = groupDataByDate(expenses)

        // Thiết lập adapter cho RecyclerView
        val adapter = ListAdapter(requireContext(), groupedDataList)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(requireContext())
        list.setHasFixedSize(true)
    }

    private fun load(callback: () -> Unit) {
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

//    private fun highlightItem(position: Int) {
//        // Reset background của tất cả các item
//        for (i in 0 until list.childCount) {
//            val view = list.getChildAt(i)
//            view?.setBackgroundColor(Color.TRANSPARENT)
//        }
//        val firstVisiblePosition = list.firstVisiblePosition
//        val lastVisiblePosition = list.lastVisiblePosition
//
//        // Highlight item nếu nó nằm trong khoảng visible
//        if (position in firstVisiblePosition..lastVisiblePosition) {
//            val selectedItemView = list.getChildAt(position - firstVisiblePosition)
//            selectedItemView?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_30))
//        } else {
//            // Nếu item không nằm trong khoảng visible, cuộn đến nó và set background sau đó
//            list.setSelection(position)
//            list.post {
//                highlightItem(position)
//            }
//        }
//    }
}