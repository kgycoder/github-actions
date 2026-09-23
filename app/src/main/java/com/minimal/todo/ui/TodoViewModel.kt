package com.minimal.todo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.minimal.todo.TodoApplication
import com.minimal.todo.data.Todo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as TodoApplication).repository

    val activeTodos = repository.allTodos
        .map { list -> list.filter { !it.isCompleted }.sortedByDescending { it.createdAt } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTodos = repository.allTodos
        .map { list -> list.filter { it.isCompleted }.sortedByDescending { it.completedAt ?: 0L } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTodo(title: String, memo: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch { repository.addTodo(title.trim(), memo.trim()) }
    }

    fun completeTodo(todo: Todo) {
        viewModelScope.launch { repository.completeTodo(todo.id) }
    }

    fun reopenTodo(todo: Todo) {
        viewModelScope.launch { repository.reopenTodo(todo.id) }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch { repository.deleteTodo(todo) }
    }
}
