package com.project.financialManagement.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat
import com.project.financialManagement.R
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.model.CoinModel

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Coin.newInstance] factory method to
 * create an instance of this fragment.
 */
class Coin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var list: ListView
    private lateinit var dataList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sh: SharedPreferencesHelper

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
        val view = inflater.inflate(R.layout.fragment_coin, container, false)
        list = view.findViewById(R.id.list_coin)
        sh = SharedPreferencesHelper(requireContext())
        // Inflate the layout for this fragment
        dataList = CoinModel.values().map { it.value } as ArrayList<String>

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dataList)
        list.adapter = adapter
        list.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                list.viewTreeObserver.removeOnGlobalLayoutListener(this)
                list.post { highlightItem(sh.getCoinId()) }
            }
        })

        list.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            sh.saveCoinId(position)
            highlightItem(position)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Coin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Coin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun highlightItem(position: Int) {
        // Reset background của tất cả các item
        for (i in 0 until list.childCount) {
            val view = list.getChildAt(i)
            view?.setBackgroundColor(Color.TRANSPARENT)
        }
        val firstVisiblePosition = list.firstVisiblePosition
        val lastVisiblePosition = list.lastVisiblePosition

        // Highlight item nếu nó nằm trong khoảng visible
        if (position in firstVisiblePosition..lastVisiblePosition) {
            val selectedItemView = list.getChildAt(position - firstVisiblePosition)
            selectedItemView?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_30))
        } else {
            // Nếu item không nằm trong khoảng visible, cuộn đến nó và set background sau đó
            list.setSelection(position)
            list.post {
                highlightItem(position)
            }
        }
    }
}