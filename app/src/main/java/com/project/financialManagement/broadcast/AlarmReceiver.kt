package com.project.financialManagement.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.project.financialManagement.service.BudgetReminderService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, BudgetReminderService::class.java).apply {
            putExtra("notificationId", 1)
            putExtra("notificationTitle", "Testing notification")
            putExtra("notificationText", "This is the first notification")
            }
        context.startService(serviceIntent)
    }
}