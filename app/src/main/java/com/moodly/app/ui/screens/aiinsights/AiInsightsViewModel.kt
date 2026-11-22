package com.moodly.app.ui.screens.aiinsights

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodly.app.data.repository.MoodRepository
import com.moodly.app.data.repository.TaskRepository
import com.moodly.app.di.IoDispatcher
import com.moodly.app.domain.model.MoodFactor
import com.moodly.app.domain.model.MoodTrend
import com.moodly.app.domain.model.StressLevel
import com.moodly.app.domain.model.TaskStatus
import com.moodly.app.domain.usecase.EstimateStressLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@Immutable
data class AiInsightsUiState(
    val stressLevel: StressLevel = StressLevel.MODERATE,
    val averageMoodRating: Float = 3.0f,
    val pendingTasks: Int = 0,
    val upcomingDeadlines: Int = 0,
    val moodTrend: MoodTrend = MoodTrend.STABLE,
    val dailyMoodRatings: List<Float?> = List(7) { null },
    val weekStart: LocalDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
    val weekEnd: LocalDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)),
    val momoReflection: String = "Let's see how your week has been going.",
    val patternAnalysis: String = "",
    val potentialContributors: List<StressContributor> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val hasEnoughData: Boolean = false,
    val isLoading: Boolean = true
)

/** One data-supported possible contributor, shown with a small icon. */
@Immutable
data class StressContributor(val emoji: String, val text: String)

@HiltViewModel
class AiInsightsViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    private val taskRepository: TaskRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AiInsightsUiState())
    val uiState: StateFlow<AiInsightsUiState> = _uiState.asStateFlow()
    
    init {
        loadInsights()
    }
    
    private fun loadInsights() {
        // Reactive: recompute whenever mood entries or tasks change
        viewModelScope.launch(ioDispatcher) {
            combine(
                moodRepository.getAllMoodEntries(),
                taskRepository.getAllTasks()
            ) { allMoods, allTasks -> allMoods to allTasks }
                .collect { (allMoods, allTasks) -> computeInsights(allMoods, allTasks) }
        }
    }
    
    private fun computeInsights(
        allMoods: List<com.moodly.app.domain.model.MoodEntry>,
        allTasks: List<com.moodly.app.domain.model.Task>
    ) {
        run {
            val now = LocalDateTime.now()
            val weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val weekEnd = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            
            // Mood entries for this week
            val moodEntries = allMoods
                .filter { it.createdAt.toLocalDate() in weekStart..weekEnd }
                .sortedBy { it.createdAt }
            
            // Not enough data: show the empty state instead of inventing a stress level
            if (moodEntries.isEmpty()) {
                _uiState.value = AiInsightsUiState(
                    weekStart = weekStart,
                    weekEnd = weekEnd,
                    hasEnoughData = false,
                    isLoading = false
                )
                return
            }
            
            // Task counts
            val pendingTasksList = allTasks
                .filter { it.status != TaskStatus.COMPLETED }
                .sortedBy { it.dueDate }
            val pendingCount = pendingTasksList.size
            val tasksDueSoon = pendingTasksList.count { it.dueDate.isBefore(now.plusDays(4)) }
            val nearestTaskTitle = pendingTasksList.firstOrNull()?.title
            
            // Calculate average mood rating
            val avgRating = if (moodEntries.isNotEmpty()) {
                moodEntries.map { it.rating }.average().toFloat()
            } else 3.0f
            
            // Calculate daily ratings for chart
            val dailyRatings = (0..6).map { dayOffset ->
                val day = weekStart.plusDays(dayOffset.toLong())
                val dayEntries = moodEntries.filter { 
                    it.createdAt.toLocalDate() == day 
                }
                if (dayEntries.isNotEmpty()) {
                    dayEntries.map { it.rating }.average().toFloat()
                } else {
                    null // No check-in that day: don't invent a value
                }
            }
            
            // Determine mood trend from recorded days only
            val trend = calculateMoodTrend(dailyRatings.filterNotNull())
            
            // Shared estimate (same as Home); entries are non-empty here
            val stressLevel = EstimateStressLevel(
                weekMoodEntries = moodEntries,
                pendingTasks = pendingCount,
                tasksDueSoon = tasksDueSoon
            ) ?: StressLevel.LOW
            
            // Generate AI insights
            val contributors = generateContributors(moodEntries, pendingCount, tasksDueSoon)
            val recommendations = generateRecommendations(stressLevel, pendingCount, tasksDueSoon, nearestTaskTitle)
            val patternAnalysis = generatePatternAnalysis(stressLevel, trend, tasksDueSoon)
            val momoReflection = getMomoReflection(stressLevel)
            
            _uiState.value = AiInsightsUiState(
                stressLevel = stressLevel,
                averageMoodRating = avgRating,
                pendingTasks = pendingCount,
                upcomingDeadlines = tasksDueSoon,
                moodTrend = trend,
                dailyMoodRatings = dailyRatings,
                weekStart = weekStart,
                weekEnd = weekEnd,
                momoReflection = momoReflection,
                patternAnalysis = patternAnalysis,
                potentialContributors = contributors,
                recommendations = recommendations,
                hasEnoughData = true,
                isLoading = false
            )
        }
    }
    
    private fun calculateMoodTrend(dailyRatings: List<Float>): MoodTrend {
        if (dailyRatings.size < 3) return MoodTrend.STABLE
        
        val firstHalf = dailyRatings.take(3).average()
        val secondHalf = dailyRatings.takeLast(3).average()
        val diff = secondHalf - firstHalf
        
        return when {
            diff > 0.3 -> MoodTrend.INCREASING
            diff < -0.3 -> MoodTrend.DECREASING
            else -> MoodTrend.STABLE
        }
    }
    
    
    private fun generateContributors(
        moodEntries: List<com.moodly.app.domain.model.MoodEntry>,
        pendingTasks: Int,
        tasksDueSoon: Int
    ): List<StressContributor> {
        val contributors = mutableListOf<StressContributor>()
        
        if (tasksDueSoon > 0) {
            val text = if (tasksDueSoon == 1) "An assignment deadline is coming up within 4 days."
                       else "Multiple upcoming assignment deadlines ($tasksDueSoon due within 4 days)."
            contributors.add(StressContributor("📅", text))
        }
        
        if (pendingTasks >= 3) {
            contributors.add(StressContributor("📝", "$pendingTasks academic tasks are still incomplete."))
        }
        
        // Compare recent vs earlier ratings only when there is an earlier period to compare
        val olderRatings = moodEntries.dropLast(3).map { it.rating }
        val recentRatings = moodEntries.takeLast(3).map { it.rating }
        if (olderRatings.isNotEmpty() && recentRatings.average() < olderRatings.average() - 0.3) {
            contributors.add(StressContributor("💜", "Recent mood ratings have been lower than earlier in the week."))
        } else if (moodEntries.map { it.rating }.average() <= 2.5) {
            contributors.add(StressContributor("💜", "Your mood ratings this week have been on the lower side."))
        }
        
        val pressureFactors = setOf(MoodFactor.ACADEMIC_PRESSURE, MoodFactor.ASSIGNMENT_OVERLOAD)
        if (moodEntries.any { entry -> entry.factors.any { it in pressureFactors } }) {
            contributors.add(StressContributor("📚", "Academic pressure appeared in your recent check-ins."))
        }
        
        if (moodEntries.any { MoodFactor.LACK_OF_SLEEP in it.factors }) {
            contributors.add(StressContributor("😴", "Lack of sleep was mentioned in your check-ins."))
        }
        
        return contributors.take(4)
    }
    
    private fun generateRecommendations(
        stressLevel: StressLevel,
        pendingTasks: Int,
        tasksDueSoon: Int,
        nearestTaskTitle: String?
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (tasksDueSoon > 0 && nearestTaskTitle != null) {
            recommendations.add("Start with the task that has the nearest deadline ($nearestTaskTitle).")
        }
        
        if (pendingTasks >= 3) {
            recommendations.add("Break larger assignments into smaller, manageable 25-minute steps.")
        }
        
        if (stressLevel in listOf(StressLevel.HIGH, StressLevel.VERY_HIGH)) {
            recommendations.add("Schedule short 10-minute breaks between study sessions.")
        }
        
        if (stressLevel == StressLevel.VERY_HIGH) {
            recommendations.add("Consider reaching out to a friend or support person if you're feeling overwhelmed.")
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Keep checking in daily so your insights stay accurate.")
            recommendations.add("Make time for something you enjoy this week.")
        }
        
        return recommendations.take(3)
    }
    
    private fun generatePatternAnalysis(
        stressLevel: StressLevel,
        trend: MoodTrend,
        tasksDueSoon: Int
    ): String {
        val levelText = when (stressLevel) {
            StressLevel.LOW -> "Your stress indicator appears relatively low this week."
            StressLevel.MODERATE -> "Your stress indicator shows some signs of pressure this week."
            StressLevel.HIGH -> "Your stress indicator appears higher this week."
            StressLevel.VERY_HIGH -> "Your stress indicator suggests substantial pressure this week."
        }
        val trendText = when (trend) {
            MoodTrend.INCREASING -> "show an upward trend"
            MoodTrend.STABLE -> "have been relatively stable"
            MoodTrend.DECREASING -> "show a downward trend"
        }
        val workloadText = if (tasksDueSoon > 0) {
            "while $tasksDueSoon task deadline(s) are approaching. Your academic workload may be contributing to this pattern."
        } else {
            "and no deadlines are due in the next few days."
        }
        
        return "$levelText Your recent mood check-ins $trendText, $workloadText"
    }
    
    private fun getMomoReflection(stressLevel: StressLevel): String = when (stressLevel) {
        StressLevel.LOW -> "Great job managing your stress this week! Keep up the good work."
        StressLevel.MODERATE -> "You're doing okay. Remember to take breaks when you need them."
        StressLevel.HIGH -> "You seem to have a lot on your plate. Let's find one manageable next step."
        StressLevel.VERY_HIGH -> "I can see things have been tough. You're not alone — let's take it one step at a time together."
    }
}
