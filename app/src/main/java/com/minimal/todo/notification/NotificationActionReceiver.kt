package com.minimal.todo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.minimal.todo.TodoApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationHelper.ACTION_COMPLETE) return
        val id = intent.getLongExtra(NotificationHelper.EXTRA_TODO_ID, -1L)
        if (id == -1L) return

        val pendingResult = goAsync()
        val app = context.applicationContext as TodoApplication
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.repository.completeTodo(id)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
