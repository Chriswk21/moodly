package com.moodly.app.ui.screens.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodly.app.data.repository.MoodRepository
import com.moodly.app.data.repository.TaskRepository
import com.moodly.app.di.IoDispatcher
import com.moodly.app.domain.model.MoodEntry
import com.moodly.app.domain.model.StressLevel
import com.moodly.app.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.moodly.app.domain.usecase.EstimateStressLevel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@Immutable
data class HomeUiState(
    val userName: String = "Alex",
    val greeting: String = "",
    val currentDate: String = "",
    val momoMessage: String = "",
    val todaysMoodEntry: MoodEntry? = null,
    val estimatedStressLevel: StressLevel? = null,
    val stressMessage: String = "",
    val pendingTasksCount: Int = 0,
    val upcomingTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface HomeAction {
    data object Refresh : HomeAction
    data class CompleteTask(val task: Task) : HomeAction
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val taskRepository: TaskRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        // Combine latest mood entry and upcoming tasks
        viewModelScope.launch(ioDispatcher) {
            combine(
                moodRepository.getAllMoodEntries(),
                taskRepository.getPendingTasks()
            ) { allMoods, allPendingTasks ->
                val now = LocalDateTime.now()
                val greeting = getGreeting(now.hour)
                val dateString = now.format(
                    DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.ENGLISH)
                )
                
                // Latest entry counts as "today's mood" only if it was recorded today
                val latestMood = allMoods.maxByOrNull { it.createdAt }
                    ?.takeIf { it.createdAt.toLocalDate() == now.toLocalDate() }
                
                // Same estimate as AI Insights: this week's check-ins + workload
                val weekStart = now.toLocalDate()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val weekMoods = allMoods.filter {
                    !it.createdAt.toLocalDate().isBefore(weekStart)
                }
                val upcomingTasks = allPendingTasks.sortedBy { it.dueDate }
                val stressLevel = EstimateStressLevel(
                    weekMoodEntries = weekMoods,
                    pendingTasks = allPendingTasks.size,
                    tasksDueSoon = allPendingTasks.count { it.dueDate.isBefore(now.plusDays(4)) }
                )
                
                val momoMessage = getMomoMessage(latestMood, stressLevel)
                val stressMessage = getStressMessage(stressLevel, allPendingTasks.size)
                
                HomeUiState(
                    userName = "Alex",
                    greeting = greeting,
                    currentDate = dateString,
                    momoMessage = momoMessage,
                    todaysMoodEntry = latestMood,
                    estimatedStressLevel = stressLevel,
                    stressMessage = stressMessage,
                    pendingTasksCount = allPendingTasks.size,
                    upcomingTasks = upcomingTasks.take(3),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.Refresh -> loadHomeData()
            is HomeAction.CompleteTask -> completeTask(action.task)
        }
    }
    
    private fun completeTask(task: Task) {
        viewModelScope.launch(ioDispatcher) {
            taskRepository.markTaskCompleted(task)
        }
    }
    
    private fun getGreeting(hour: Int): String = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
    
    private fun getMomoMessage(mood: MoodEntry?, stressLevel: StressLevel?): String {
        return when {
            mood == null -> "How are you feeling today?"
            stressLevel == StressLevel.VERY_HIGH -> 
                "You seem to have a lot on your plate. Let's take it one step at a time."
            stressLevel == StressLevel.HIGH -> 
                "That sounds like a lot. Let's take it one step at a time."
            mood.rating <= 2 -> 
                "It's okay to have tough days. I'm here for you."
            mood.rating >= 4 -> 
                "That's lovely! Keep enjoying the little things."
            else -> 
                "Let's take today one step at a time."
        }
    }
    
    private fun getStressMessage(stressLevel: StressLevel?, pendingTasks: Int): String {
        return when (stressLevel) {
            StressLevel.LOW -> 
                "Current inputs suggest relatively manageable stress"
            StressLevel.MODERATE -> 
                "Some signs of pressure may be present"
            StressLevel.HIGH -> 
                "Multiple upcoming deadlines and recent mood ratings suggest elevated pressure."
            StressLevel.VERY_HIGH -> 
                "Several stress indicators are present; consider taking a break."
            null -> 
                "Complete a few mood check-ins to start seeing your insights."
        }
    }
}
