package com.project.financialManagement.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.project.financialManagement.model.Category
import com.project.financialManagement.model.Transaction
import com.project.financialManagement.model.TransactionType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "transactionsManager"
        const val TABLE_TRANSACTIONS = "transactions"
        const val COLUMN_ID = "id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_TIME = "time"
        const val COLUMN_TOTAL = "total"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DESCRIPTION = "description"
        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_TITLE = "title"
        const val COLUMN_COLOR = "color"
        const val COLUMN_ICON = "icon"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TRANSACTION_TABLE = ("CREATE TABLE $TABLE_TRANSACTIONS ("
                + "$COLUMN_ID TEXT PRIMARY KEY,"
                + "$COLUMN_TYPE INTEGER,"
                + "$COLUMN_TIME TEXT,"
                + "$COLUMN_TOTAL REAL,"
                + "$COLUMN_CATEGORY TEXT,"
                + "$COLUMN_DESCRIPTION TEXT)")
        db.execSQL(CREATE_TRANSACTION_TABLE)

        val CREATE_CATEGORY_TABLE = ("CREATE TABLE $TABLE_CATEGORIES ("
                + "$COLUMN_ID TEXT PRIMARY KEY,"
                + "$COLUMN_TITLE TEXT,"
                + "$COLUMN_COLOR TEXT,"
                + "$COLUMN_ICON TEXT,"
                + "$COLUMN_DESCRIPTION TEXT,"
                + "$COLUMN_TYPE INTEGER)")
        db.execSQL(CREATE_CATEGORY_TABLE)

        val INSERT_DEFAULT_CATEGORIES = ("INSERT INTO $TABLE_CATEGORIES ($COLUMN_ID, $COLUMN_TITLE, $COLUMN_COLOR, $COLUMN_ICON, $COLUMN_DESCRIPTION, $COLUMN_TYPE) VALUES "
                + "('1', 'Food', '#FF0000', 'icon_food', 'Expenses on food', 1),"
                + "('2', 'Transport', '#00FF00', 'icon_transport', 'Expenses on transport', 1),"
                + "('3', 'Entertainment', '#0000FF', 'icon_entertainment', 'Expenses on entertainment', 1)")
        db.execSQL(INSERT_DEFAULT_CATEGORIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }
}

// DAO class for CRUD operations
class TransactionDAO(private val dbHelper: DatabaseHelper) {

    fun addTransaction(transaction: Transaction): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ID, transaction.id)
            put(DatabaseHelper.COLUMN_TYPE, transaction.type.value)
            put(DatabaseHelper.COLUMN_TIME, transaction.time)
            put(DatabaseHelper.COLUMN_TOTAL, transaction.total)
            put(DatabaseHelper.COLUMN_CATEGORY, transaction.category)
            put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.description)
        }
        return db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values)
    }
    fun getTransaction(id: String): Transaction? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TRANSACTIONS, arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_TYPE,
                DatabaseHelper.COLUMN_TIME,
                DatabaseHelper.COLUMN_TOTAL,
                DatabaseHelper.COLUMN_CATEGORY,
                DatabaseHelper.COLUMN_DESCRIPTION,
            ), "${DatabaseHelper.COLUMN_ID}=?", arrayOf(id), null, null, null, null)
        cursor?.moveToFirst()
        val transaction = cursor?.let {
            Transaction(
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                TransactionType.values().first { type -> type.value == it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE)) },
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL)),
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)),
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
            )
        }
        cursor?.close()
        return transaction
    }

    fun updateTransaction(transaction: Transaction): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TIME, transaction.time)
            put(DatabaseHelper.COLUMN_TOTAL, transaction.total)
            put(DatabaseHelper.COLUMN_CATEGORY, transaction.category)
            put(DatabaseHelper.COLUMN_DESCRIPTION, transaction.description)
        }
        return db.update(DatabaseHelper.TABLE_TRANSACTIONS, values, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(transaction.id))
    }

    fun deleteTransaction(id: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_TRANSACTIONS, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(id))
    }

    fun getAllTransactions(): List<Transaction> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_TRANSACTIONS}", null)

        val transactions = mutableListOf<Transaction>()

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    TransactionType.values().first { type -> type.value == cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE)) },
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                )
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return transactions
    }
}

// TransactionManager class
class TransactionManager(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val transactionDAO = TransactionDAO(dbHelper)

    fun addTransaction(transaction: Transaction): Long {
        return transactionDAO.addTransaction(transaction)
    }

    fun getTransaction(id: String): Transaction? {
        return transactionDAO.getTransaction(id)
    }

    fun updateTransaction(transaction: Transaction): Int {
        return transactionDAO.updateTransaction(transaction)
    }

    fun deleteTransaction(id: String): Int {
        return transactionDAO.deleteTransaction(id)
    }

    fun getAllTransactions(): List<Transaction> {
        return transactionDAO.getAllTransactions()
    }

    fun getBalance(type: TransactionType? = TransactionType.ALL): Double {
        val transactions = getAllTransactions()
        if (type != null) {
            return when(type.value) {
                0 -> transactions.filter { it.type == TransactionType.INCOME || it.type ==TransactionType.EXPENSE }
                    .sumOf { it.total * it.type.value }

                else -> transactions.filter { it.type == type }.sumOf { it.total }
            }
        }
        return 0.0
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getTotalInTime(start: LocalDateTime? = null, end: LocalDateTime? = null, type: TransactionType? = TransactionType.ALL): Double {
        val transactions = getAllTransactionsInTime(start, end)
        if (type != null) {
            return when(type.value) {
                0 -> transactions.filter { it.type == TransactionType.INCOME || it.type ==TransactionType.EXPENSE }
                    .sumOf { it.total * it.type.value }

                else -> transactions.filter { it.type == type }.sumOf { it.total }
            }
        }
        return 0.0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllTransactionsInTime(
        start: LocalDateTime? = null,
        end: LocalDateTime? = null,
    ): List<Transaction> {
        val transactions = getAllTransactions()
        return transactions.filter { transaction ->
            val transactionDateTime = LocalDateTime.parse(transaction.time, DateTimeFormatter.ISO_DATE_TIME)
            (start == null || transactionDateTime >= start) &&
                    (end == null || transactionDateTime <= end)
        }
    }



}

//val (startDateTime, endDateTime) = when (timeRange) {
//    com.project.financialManagement.helper.TransactionManager.TimeRange.WEEK -> {
//        val startOfWeek = currentDate.toLocalDate().minusDays(currentDate.dayOfWeek.value.toLong() - 1).atStartOfDay()
//        val endOfWeek = startOfWeek.plusDays(6).plusHours(23).plusMinutes(59).plusSeconds(59)
//        kotlin.Pair(startOfWeek, endOfWeek)
//    }
//    com.project.financialManagement.helper.TransactionManager.TimeRange.MONTH -> {
//        val startOfMonth = currentDate.toLocalDate().withDayOfMonth(1).atStartOfDay()
//        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1).plusHours(23).plusMinutes(59).plusSeconds(59)
//        kotlin.Pair(startOfMonth, endOfMonth)
//    }
//    com.project.financialManagement.helper.TransactionManager.TimeRange.YEAR -> {
//        val startOfYear = currentDate.toLocalDate().withDayOfYear(1).atStartOfDay()
//        val endOfYear = startOfYear.plusYears(1).minusDays(1).plusHours(23).plusMinutes(59).plusSeconds(59)
//        kotlin.Pair(startOfYear, endOfYear)
//    }
//    com.project.financialManagement.helper.TransactionManager.TimeRange.ALL -> kotlin.Pair(
//        null,
//        null
//    )
//    null -> kotlin.Pair(start, end)
//}

class CategoryDAO(private val dbHelper: DatabaseHelper) {

    fun addCategory(category: Category): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ID, category.id)
            put(DatabaseHelper.COLUMN_TITLE, category.title)
            put(DatabaseHelper.COLUMN_COLOR, category.color)
            put(DatabaseHelper.COLUMN_ICON, category.icon)
            put(DatabaseHelper.COLUMN_DESCRIPTION, category.description)
            put(DatabaseHelper.COLUMN_TYPE, category.type)
        }
        return db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values)
    }
    fun getCategory(id: String): Category? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CATEGORIES, arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_COLOR,
                DatabaseHelper.COLUMN_ICON,
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_TYPE,
            ), "${DatabaseHelper.COLUMN_ID}=?", arrayOf(id), null, null, null, null)
        cursor?.moveToFirst()
        val category = cursor?.let {
            Category(
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COLOR)),
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ICON)),
                it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE)),
            )
        }
        cursor?.close()
        return category
    }

    fun updateCategory(category: Category): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, category.title)
            put(DatabaseHelper.COLUMN_COLOR, category.color)
            put(DatabaseHelper.COLUMN_ICON, category.icon)
            put(DatabaseHelper.COLUMN_DESCRIPTION, category.description)
        }
        return db.update(DatabaseHelper.TABLE_CATEGORIES, values, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(category.id))
    }

    fun deleteCategory(id: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_CATEGORIES, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(id))
    }

    fun getAllCategories(): List<Category> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_CATEGORIES}", null)

        val categories = mutableListOf<Category>()

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COLOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ICON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE)),
                )
                categories.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return categories
    }
}

class CategoryManager(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val categoryDAO = CategoryDAO(dbHelper)

    fun addCategory(category: Category): Long {
        return categoryDAO.addCategory(category)
    }

    fun getCategory(id: String): Category? {
        return categoryDAO.getCategory(id)
    }

    fun updateCategory(category: Category): Int {
        return categoryDAO.updateCategory(category)
    }

    fun deleteCategory(id: String): Int {
        return categoryDAO.deleteCategory(id)
    }

    fun getAllCategories(): List<Category> {
        return categoryDAO.getAllCategories()
    }
}

