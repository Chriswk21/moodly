package com.moodly.app.ui.screens.tasks

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodly.app.data.repository.TaskRepository
import com.moodly.app.di.IoDispatcher
import com.moodly.app.domain.model.Priority
import com.moodly.app.domain.model.Task
import com.moodly.app.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@Immutable
data class AddEditTaskUiState(
    val taskId: Long? = null,
    val title: String = "",
    val course: String = "",
    val description: String = "",
    val dueDate: LocalDate = LocalDate.now().plusDays(1),
    val dueTime: LocalTime = LocalTime.of(23, 59),
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val isSaving: Boolean = false
)

sealed interface AddEditTaskAction {
    data class SetTitle(val title: String) : AddEditTaskAction
    data class SetCourse(val course: String) : AddEditTaskAction
    data class SetDescription(val description: String) : AddEditTaskAction
    data class SetDueDate(val date: LocalDate) : AddEditTaskAction
    data class SetDueTime(val time: LocalTime) : AddEditTaskAction
    data class SetPriority(val priority: Priority) : AddEditTaskAction
    data class SetStatus(val status: TaskStatus) : AddEditTaskAction
    data object SaveTask : AddEditTaskAction
}

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()
    
    private var onSaveComplete: (() -> Unit)? = null
    
    fun setOnSaveComplete(callback: () -> Unit) {
        onSaveComplete = callback
    }
    
    fun loadTask(taskId: Long) {
        viewModelScope.launch(ioDispatcher) {
            taskRepository.getTaskById(taskId)?.let { task ->
                _uiState.update {
                    AddEditTaskUiState(
                        taskId = task.id,
                        title = task.title,
                        course = task.course ?: "",
                        description = task.description ?: "",
                        dueDate = task.dueDate.toLocalDate(),
                        dueTime = task.dueDate.toLocalTime(),
                        priority = task.priority,
                        status = task.status
                    )
                }
            }
        }
    }
    
    fun onAction(action: AddEditTaskAction) {
        when (action) {
            is AddEditTaskAction.SetTitle -> _uiState.update { it.copy(title = action.title) }
            is AddEditTaskAction.SetCourse -> _uiState.update { it.copy(course = action.course) }
            is AddEditTaskAction.SetDescription -> _uiState.update { it.copy(description = action.description) }
            is AddEditTaskAction.SetDueDate -> _uiState.update { it.copy(dueDate = action.date) }
            is AddEditTaskAction.SetDueTime -> _uiState.update { it.copy(dueTime = action.time) }
            is AddEditTaskAction.SetPriority -> _uiState.update { it.copy(priority = action.priority) }
            is AddEditTaskAction.SetStatus -> _uiState.update { it.copy(status = action.status) }
            is AddEditTaskAction.SaveTask -> saveTask()
        }
    }
    
    private fun saveTask() {
        val state = _uiState.value
        if (state.title.isBlank()) return
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            val dueDateTime = LocalDateTime.of(state.dueDate, state.dueTime)
            
            val task = Task(
                id = state.taskId ?: 0,
                title = state.title.trim(),
                course = state.course.trim().takeIf { it.isNotBlank() },
                description = state.description.trim().takeIf { it.isNotBlank() },
                dueDate = dueDateTime,
                priority = state.priority,
                status = state.status
            )
            
            withContext(ioDispatcher) {
                if (state.taskId != null) {
                    taskRepository.updateTask(task)
                } else {
                    taskRepository.saveTask(task)
                }
            }
            
            _uiState.update { it.copy(isSaving = false) }
            // Back on the main thread: safe to trigger navigation
            onSaveComplete?.invoke()
        }
    }
}
