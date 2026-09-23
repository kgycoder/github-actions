package com.minimal.todo.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minimal.todo.data.Todo
import com.minimal.todo.ui.components.TodoRow
import com.minimal.todo.ui.theme.SystemGray

@Composable
fun TodoListScreen(
    activeTodos: List<Todo>,
    completedTodos: List<Todo>,
    onToggle: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCompleted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (activeTodos.isEmpty() && completedTodos.isEmpty()) {
            item { EmptyState() }
        }

        items(activeTodos, key = { it.id }) { todo ->
            TodoRow(
                todo = todo,
                onToggle = { onToggle(todo) },
                onDelete = { onDelete(todo) },
                modifier = Modifier.animateItemPlacement(tween(300))
            )
        }

        if (completedTodos.isNotEmpty()) {
            item {
                CompletedHeader(
                    count = completedTodos.size,
                    expanded = showCompleted,
                    onClick = { showCompleted = !showCompleted }
                )
            }
            if (showCompleted) {
                items(completedTodos, key = { it.id }) { todo ->
                    TodoRow(
                        todo = todo,
                        onToggle = { onToggle(todo) },
                        onDelete = { onDelete(todo) },
                        modifier = Modifier.animateItemPlacement(tween(300))
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedHeader(count: Int, expanded: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "완료됨 $count",
                style = MaterialTheme.typography.labelMedium,
                color = SystemGray
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = SystemGray
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "할 일이 없습니다\n＋ 버튼을 눌러 새로운 Todo를 추가해보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = SystemGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
