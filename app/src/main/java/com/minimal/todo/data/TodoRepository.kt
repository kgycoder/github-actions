package com.minimal.todo.data

import android.content.Context
import com.minimal.todo.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow

class TodoRepository(
    private val dao: TodoDao,
    context: Context
) {
    val allTodos: Flow<List<Todo>> = dao.observeAll()
    private val notificationHelper = NotificationHelper(context.applicationContext)

    suspend fun addTodo(title: String, memo: String = ""): Long {
        val todo = Todo(title = title, memo = memo)
        val id = dao.insert(todo)
        notificationHelper.showTodoNotification(todo.copy(id = id))
        return id
    }

    suspend fun completeTodo(id: Long) {
        val todo = dao.getById(id) ?: return
        val updated = todo.copy(isCompleted = true, completedAt = System.currentTimeMillis())
        dao.update(updated)
        notificationHelper.cancelNotification(id)
    }

    suspend fun reopenTodo(id: Long) {
        val todo = dao.getById(id) ?: return
        val updated = todo.copy(isCompleted = false, completedAt = null)
        dao.update(updated)
        notificationHelper.showTodoNotification(updated)
    }

    suspend fun deleteTodo(todo: Todo) {
        dao.delete(todo)
        notificationHelper.cancelNotification(todo.id)
    }

    suspend fun restoreActiveNotifications() {
        dao.getActiveOnce().forEach { notificationHelper.showTodoNotification(it) }
    }
}
