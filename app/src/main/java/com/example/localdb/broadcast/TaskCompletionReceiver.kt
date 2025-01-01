package com.example.localdb.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.localdb.R

class TaskCompletionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("taskName") ?: "Unknown Task"
        val message = intent.getStringExtra("message") ?: "Task updated."

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(context, "task_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Task Update")
                .setContentText("$taskName: $message")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

            Log.d("TaskCompletionReceiver", "Notification sent for task: $taskName")
        } else {
            Log.e("TaskCompletionReceiver", "Notification permission not granted.")
        }
    }
}
