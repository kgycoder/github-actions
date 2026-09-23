package com.minimal.todo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.minimal.todo.ui.TodoListScreen
import com.minimal.todo.ui.TodoViewModel
import com.minimal.todo.ui.components.AddTodoSheet
import com.minimal.todo.ui.theme.PureWhite
import com.minimal.todo.ui.theme.TodoListTheme

class MainActivity : ComponentActivity() {

    private val viewModel: TodoViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            TodoListTheme {
                var showAddSheet by remember { mutableStateOf(false) }
                val active by viewModel.activeTodos.collectAsState()
                val completed by viewModel.completedTodos.collectAsState()

                Scaffold(
                    containerColor = PureWhite,
                    topBar = {
                        LargeTopAppBar(
                            title = { Text("Todo") },
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = PureWhite,
                                titleContentColor = Color.Black
                            )
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showAddSheet = true },
                            containerColor = Color.Black,
                            contentColor = PureWhite,
                            shape = CircleShape
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "추가")
                        }
                    }
                ) { padding ->
                    TodoListScreen(
                        activeTodos = active,
                        completedTodos = completed,
                        onToggle = { todo ->
                            if (todo.isCompleted) viewModel.reopenTodo(todo) else viewModel.completeTodo(todo)
                        },
                        onDelete = { todo -> viewModel.deleteTodo(todo) },
                        modifier = Modifier.padding(padding)
                    )
                }

                if (showAddSheet) {
                    AddTodoSheet(
                        onDismiss = { showAddSheet = false },
                        onConfirm = { title, memo ->
                            viewModel.addTodo(title, memo)
                            showAddSheet = false
                        }
                    )
                }
            }
        }
    }
}
