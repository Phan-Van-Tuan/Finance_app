package com.project.financialManagement.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.project.financialManagement.MainActivity
import com.project.financialManagement.R

class BudgetReminderService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Thực hiện logic kiểm tra ngân sách và gửi thông báo nếu cần
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0
        val notificationTitle = intent?.getStringExtra("notificationTitle") ?: "Notification"
        val notificationText = intent?.getStringExtra("notificationText") ?: "Notification content"
        val channelId = "budget_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Budget Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Tạo Intent để mở MainActivity khi nhấn vào thông báo
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle(notificationTitle)
                .bigText(notificationText))
            .setSmallIcon(R.drawable.df_ic_dollar)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Đặt PendingIntent vào thông báo
            .build()
        notificationManager.notify(notificationId, notification)

        // Dừng dịch vụ sau khi gửi thông báo
        stopSelf()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
