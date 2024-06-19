package com.project.financialManagement.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Transaction(
    val id: String,
    val type: TransactionType,
    val time: String,
    val total: Double,
    val category: String,
    val description: String,
)

enum class TransactionType(val value: Int) {
    INCOME(1),
    EXPENSE(-1),
    ALL(0)
}

data class DataItem1(val date: String)
data class DataItem2(
    val id: String,
    val type: String,
    val description: String,
    val total: Double,
    val date: String)

@RequiresApi(Build.VERSION_CODES.O)
fun groupDataByDate(dataHistoryList: List<Transaction>): List<Any> {
    val groupedData = mutableListOf<Any>()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateMap = dataHistoryList.groupBy { LocalDate.parse(it.time.substring(0, 10), dateFormatter) }

    for ((date, items) in dateMap) {
        groupedData.add(DataItem1(date.toString()))
        for (item in items) {
            groupedData.add(DataItem2(item.id, item.category, item.description, item.total, item.time))
        }
    }
    return groupedData
}