package com.minimal.todo.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.minimal.todo.R
import com.minimal.todo.data.Todo

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "todo_channel"
        const val ACTION_COMPLETE = "com.minimal.todo.ACTION_COMPLETE"
        const val EXTRA_TODO_ID = "extra_todo_id"
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableLights(false)
                enableVibration(false)
            }
            manager?.createNotificationChannel(channel)
        }
    }

    fun showTodoNotification(todo: Todo) {
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_COMPLETE
            putExtra(EXTRA_TODO_ID, todo.id)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id.toInt(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val collapsedView = RemoteViews(context.packageName, R.layout.notification_todo).apply {
            setTextViewText(R.id.notification_title, todo.title)
            setOnClickPendingIntent(R.id.notification_check, completePendingIntent)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(collapsedView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(false)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)

        NotificationManagerCompat.from(context).notify(todo.id.toInt(), builder.build())
    }

    fun cancelNotification(id: Long) {
        NotificationManagerCompat.from(context).cancel(id.toInt())
    }
}
