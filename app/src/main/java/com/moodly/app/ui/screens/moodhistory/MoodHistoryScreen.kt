package com.moodly.app.ui.screens.moodhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moodly.app.domain.model.MoodEntry
import com.moodly.app.domain.model.MoodType
import com.moodly.app.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class HistoryPeriod(val displayName: String) {
    THIS_WEEK("This Week"),
    THIS_MONTH("November 2026")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHistoryScreen(
    onBack: () -> Unit,
    viewModel: MoodHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    MoodHistoryContent(
        uiState = uiState,
        onBack = onBack,
        onPeriodChange = viewModel::setPeriod
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodHistoryContent(
    uiState: MoodHistoryUiState,
    onBack: () -> Unit,
    onPeriodChange: (HistoryPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mood History & Trends",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Period Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HistoryPeriod.entries.forEach { period ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onPeriodChange(period) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (uiState.selectedPeriod == period) LavenderPrimary else Surface
                        ) {
                            Text(
                                text = period.displayName,
                                modifier = Modifier.padding(vertical = 12.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (uiState.selectedPeriod == period) LavenderDeep else TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            // 7-Day Mood & Stress Trend
            item {
                MoodTrendCard(
                    averageRating = uiState.averageRating,
                    dailyRatings = uiState.dailyRatings
                )
            }
            
            // Estimated Stress Trend
            item {
                StressTrendCard(dailyStress = uiState.dailyStressLevels)
            }
            
            // Calendar View
            item {
                CalendarCard(
                    year = uiState.calendarYear,
                    month = uiState.calendarMonth,
                    checkIns = uiState.checkInsCount,
                    moodEntryDates = uiState.moodEntryDates,
                    selectedDate = uiState.selectedDate
                )
            }
            
            // Recent Check-ins
            item {
                Text(
                    text = "Recent Check-ins",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            if (uiState.recentCheckIns.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No check-ins yet. Start tracking your mood!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(
                    items = uiState.recentCheckIns,
                    key = { it.id }
                ) { entry ->
                    MoodEntryCard(entry = entry)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MoodTrendCard(
    averageRating: Float,
    dailyRatings: List<Float>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "7-Day Mood & Stress Trend",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Avg ${String.format("%.1f", averageRating)}/5",
                    style = MaterialTheme.typography.labelMedium,
                    color = LavenderDeep
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple line chart representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                dailyRatings.forEachIndexed { index, rating ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Line point
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LavenderDeep)
                        )
                        
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(((5 - rating) * 12).dp)
                                .background(Color.Transparent)
                        )
                        
                        Text(
                            text = days.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StressTrendCard(dailyStress: List<StressDay>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estimated Stress Trend",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LegendItem(color = StressLow, label = "Low")
                    LegendItem(color = StressModerate, label = "Mod")
                    LegendItem(color = StressHigh, label = "High")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stress bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                dailyStress.forEachIndexed { index, stressDay ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(stressDay.color)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = days.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Patterns highlight changes over time; overlapping workload and mood do not prove a single cause.",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
    }
}

@Composable
private fun CalendarCard(
    year: Int,
    month: String,
    checkIns: Int,
    moodEntryDates: Set<Int>,
    selectedDate: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$month Check-in Calendar",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$checkIns check-ins",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Day headers
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar grid (simplified - showing first 3 weeks)
            val weeks = listOf(
                listOf(1, 2, 3, 4, 5, 6, 7),
                listOf(8, 9, 10, 11, 12, 13, 14),
                listOf(15, 16, 17, 18, 19, 20, 21)
            )
            
            weeks.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    week.forEach { day ->
                        val hasMood = day in moodEntryDates
                        val isSelected = day == selectedDate
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> LavenderDeep
                                        else -> Color.Transparent
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) Color.White else TextDark
                                )
                                if (hasMood && !isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(StressHigh)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodEntryCard(entry: MoodEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Mood indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(getMoodColor(entry.moodType).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getMoodEmoji(entry.moodType),
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.moodType.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextDark,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Background
                        ) {
                            Text(
                                text = "${entry.rating}/5",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                    
                    Text(
                        text = entry.createdAt.format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                
                if (entry.journalNote != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.journalNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark
                    )
                }
                
                if (entry.factors.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        entry.factors.take(3).forEach { factor ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = LavenderPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = factor.displayName,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LavenderDeep
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper
private fun getMoodColor(moodType: MoodType): Color = when (moodType) {
    MoodType.HAPPY -> MoodHappy
    MoodType.CALM -> MoodCalm
    MoodType.NEUTRAL -> MoodNeutral
    MoodType.ANXIOUS -> MoodAnxious
    MoodType.SAD -> MoodSad
    MoodType.STRESSED -> MoodStressed
}

private fun getMoodEmoji(moodType: MoodType): String = when (moodType) {
    MoodType.HAPPY -> "🌸"
    MoodType.CALM -> "🌿"
    MoodType.NEUTRAL -> "☁️"
    MoodType.ANXIOUS -> "😰"
    MoodType.SAD -> "🌧️"
    MoodType.STRESSED -> "⚡"
}

data class StressDay(
    val level: String,
    val color: Color
)

@Preview(showBackground = true)
@Composable
private fun MoodHistoryContentPreview() {
    MoodlyTheme {
        MoodHistoryContent(
            uiState = MoodHistoryUiState(
                dailyStressLevels = listOf(
                    StressDay("Mod", StressModerate),
                    StressDay("Mod", StressModerate),
                    StressDay("High", StressHigh),
                    StressDay("High", StressHigh),
                    StressDay("High", StressHigh),
                    StressDay("High", StressHigh),
                    StressDay("High", StressHigh)
                )
            ),
            onBack = {},
            onPeriodChange = {}
        )
    }
}
