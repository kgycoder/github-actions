package com.minimal.todo

import android.app.Application
import com.minimal.todo.data.TodoDatabase
import com.minimal.todo.data.TodoRepository

class TodoApplication : Application() {

    val repository: TodoRepository by lazy {
        TodoRepository(TodoDatabase.getInstance(this).todoDao(), this)
    }

    companion object {
        lateinit var instance: TodoApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
