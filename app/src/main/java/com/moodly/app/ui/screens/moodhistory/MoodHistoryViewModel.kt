package com.moodly.app.ui.screens.moodhistory

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moodly.app.data.repository.MoodRepository
import com.moodly.app.di.IoDispatcher
import com.moodly.app.domain.model.MoodEntry
import com.moodly.app.domain.model.MoodType
import com.moodly.app.ui.theme.StressHigh
import com.moodly.app.ui.theme.StressLow
import com.moodly.app.ui.theme.StressModerate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

@Immutable
data class MoodHistoryUiState(
    val selectedPeriod: HistoryPeriod = HistoryPeriod.THIS_WEEK,
    val averageRating: Float = 2.4f,
    val dailyRatings: List<Float> = listOf(3.5f, 3f, 3f, 2.5f, 2f, 2f, 2f),
    val dailyStressLevels: List<StressDay> = emptyList(),
    val calendarYear: Int = LocalDate.now().year,
    val calendarMonth: String = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
    val checkInsCount: Int = 0,
    /** Day of month -> mood of the latest check-in on that day. */
    val moodEntryDates: Map<Int, MoodType> = emptyMap(),
    val selectedDate: Int? = LocalDate.now().dayOfMonth,
    val recentCheckIns: List<MoodEntry> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MoodHistoryViewModel @Inject constructor(
    private val moodRepository: MoodRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MoodHistoryUiState())
    val uiState: StateFlow<MoodHistoryUiState> = _uiState.asStateFlow()
    
    init {
        loadHistory()
    }
    
    fun setPeriod(period: HistoryPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadHistory()
    }
    
    private fun loadHistory() {
        viewModelScope.launch(ioDispatcher) {
            val now = LocalDate.now()
            val weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            
            // Get mood entries for the month
            val monthStart = now.withDayOfMonth(1)
            val monthEnd = now.withDayOfMonth(now.lengthOfMonth())
            
            val entries = moodRepository.getMoodEntriesInRange(
                monthStart.atStartOfDay(),
                monthEnd.plusDays(1).atStartOfDay()
            )
            
            // Calculate average rating
            val avgRating = if (entries.isNotEmpty()) {
                entries.map { it.rating }.average().toFloat()
            } else 3.0f
            
            // Calculate daily ratings for the week
            val dailyRatings = (0..6).map { dayOffset ->
                val day = weekStart.plusDays(dayOffset.toLong())
                val dayEntries = entries.filter { it.createdAt.toLocalDate() == day }
                if (dayEntries.isNotEmpty()) {
                    dayEntries.map { it.rating }.average().toFloat()
                } else {
                    3f
                }
            }
            
            // Calculate stress levels for each day
            val dailyStress = (0..6).map { dayOffset ->
                val day = weekStart.plusDays(dayOffset.toLong())
                val dayEntries = entries.filter { it.createdAt.toLocalDate() == day }
                if (dayEntries.isEmpty()) {
                    // No check-in that day: show a neutral "no data" block
                    return@map StressDay("None", Color(0xFFEDEBF2))
                }
                val avgDayRating = dayEntries.map { it.rating }.average()
                
                when {
                    avgDayRating <= 2.0 -> StressDay("High", StressHigh)
                    avgDayRating <= 3.0 -> StressDay("Mod", StressModerate)
                    else -> StressDay("Low", StressLow)
                }
            }
            
            // Get dates with mood entries
            val moodDates = entries
                .groupBy { it.createdAt.dayOfMonth }
                .mapValues { (_, dayEntries) -> dayEntries.maxBy { it.createdAt }.moodType }
            
            // Recent check-ins
            val recentEntries = entries.sortedByDescending { it.createdAt }.take(10)
            
            _uiState.update {
                MoodHistoryUiState(
                    selectedPeriod = it.selectedPeriod,
                    averageRating = avgRating,
                    dailyRatings = dailyRatings,
                    dailyStressLevels = dailyStress,
                    calendarYear = now.year,
                    calendarMonth = now.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                    checkInsCount = entries.size,
                    moodEntryDates = moodDates,
                    selectedDate = now.dayOfMonth,
                    recentCheckIns = recentEntries,
                    isLoading = false
                )
            }
        }
    }
}
