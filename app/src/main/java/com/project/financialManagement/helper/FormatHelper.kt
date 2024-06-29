package com.project.financialManagement.helper

import android.content.Context
import com.project.financialManagement.model.CoinModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object FormatHelper {
    fun formatCurrency(amount: Double, context: Context): String {
        val sh = SharedPreferencesHelper(context)
        val coinCode = CoinModel.values().first { coin -> coin.id == sh.getCoinId()}
        val symbols = DecimalFormatSymbols()
        symbols.groupingSeparator = ','
        val decimalFormat = DecimalFormat("#,###", symbols)
        return "${decimalFormat.format(amount)} $coinCode"
    }

    fun formatCurrency(amount: Int, context: Context): String {
        return formatCurrency(amount.toDouble(), context)
    }
}
