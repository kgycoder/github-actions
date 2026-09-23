package com.minimal.todo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.minimal.todo.TodoApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        val app = context.applicationContext as TodoApplication
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.repository.restoreActiveNotifications()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
