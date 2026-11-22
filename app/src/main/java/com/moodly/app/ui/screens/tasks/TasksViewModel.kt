package com.moodly.app.ui.screens.tasks

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodly.app.data.repository.TaskRepository
import com.moodly.app.di.IoDispatcher
import com.moodly.app.domain.model.Task
import com.moodly.app.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@Immutable
data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val isLoading: Boolean = true
)

sealed interface TasksAction {
    data object Refresh : TasksAction
    data class ToggleTaskComplete(val task: Task) : TasksAction
    data class DeleteTask(val task: Task) : TasksAction
}

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch(ioDispatcher) {
            taskRepository.getAllTasks().collect { tasks ->
                val pending = tasks.count { it.status != TaskStatus.COMPLETED }
                val completed = tasks.count { it.status == TaskStatus.COMPLETED }
                
                _uiState.value = TasksUiState(
                    tasks = tasks,
                    pendingCount = pending,
                    completedCount = completed,
                    isLoading = false
                )
            }
        }
    }
    
    fun onAction(action: TasksAction) {
        when (action) {
            is TasksAction.Refresh -> loadTasks()
            is TasksAction.ToggleTaskComplete -> toggleTaskComplete(action.task)
            is TasksAction.DeleteTask -> deleteTask(action.task)
        }
    }
    
    private fun toggleTaskComplete(task: Task) {
        viewModelScope.launch(ioDispatcher) {
            val updatedTask = if (task.status == TaskStatus.COMPLETED) {
                task.copy(status = TaskStatus.TODO, completedAt = null)
            } else {
                task.copy(status = TaskStatus.COMPLETED, completedAt = LocalDateTime.now())
            }
            taskRepository.updateTask(updatedTask)
        }
    }
    
    private fun deleteTask(task: Task) {
        viewModelScope.launch(ioDispatcher) {
            taskRepository.deleteTask(task)
        }
    }
}
