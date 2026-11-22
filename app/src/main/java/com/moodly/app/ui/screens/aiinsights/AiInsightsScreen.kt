package com.moodly.app.ui.screens.aiinsights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moodly.app.domain.model.MoodTrend
import com.moodly.app.domain.model.StressLevel
import com.moodly.app.ui.components.MomoExpression
import com.moodly.app.ui.components.MomoMascot
import com.moodly.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AiInsightsScreen(
    onNavigateToMoodHistory: () -> Unit,
    onNavigateToTasks: () -> Unit,
    viewModel: AiInsightsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    AiInsightsContent(
        uiState = uiState,
        onNavigateToMoodHistory = onNavigateToMoodHistory,
        onNavigateToTasks = onNavigateToTasks
    )
}

@Composable
private fun AiInsightsContent(
    uiState: AiInsightsUiState,
    onNavigateToMoodHistory: () -> Unit,
    onNavigateToTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Weekly Summary",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "This Week · ${uiState.weekStart.format(DateTimeFormatter.ofPattern("MMM d"))} – ${uiState.weekEnd.format(DateTimeFormatter.ofPattern("MMM d"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            
            OutlinedButton(
                onClick = onNavigateToMoodHistory,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("History →", color = LavenderDeep)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MomoMascot(expression = MomoExpression.CALM, size = 90.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Momo is putting your insights together...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
            return@Column
        }
        
        if (!uiState.hasEnoughData) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MomoMascot(expression = MomoExpression.CONFUSED, size = 90.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Let's get to know your mood",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Complete a few check-ins this week to start seeing your personal insights.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            return@Column
        }
        
        // Momo Reflection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LavenderPrimary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MomoMascot(
                    expression = getMomoExpressionForStress(uiState.stressLevel),
                    size = 70.dp,
                    animated = false
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "MOMO'S REFLECTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = LavenderDeep,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${uiState.momoReflection}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stress Indicator
        StressIndicatorSection(stressLevel = uiState.stressLevel)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = String.format("%.1f", uiState.averageMoodRating),
                label = "Avg Mood",
                suffix = "/ 5",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = uiState.pendingTasks.toString(),
                label = "Pending Tasks",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = uiState.upcomingDeadlines.toString(),
                label = "Due Soon",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Weekly Mood Trend
        WeeklyMoodTrendSection(
            moodTrend = uiState.moodTrend,
            dailyRatings = uiState.dailyMoodRatings
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // AI Pattern Insight
        AiPatternInsightSection(insight = uiState.patternAnalysis)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Potential Stress Contributors
        if (uiState.potentialContributors.isNotEmpty()) {
            PotentialContributorsSection(contributors = uiState.potentialContributors)
            Spacer(modifier = Modifier.height(20.dp))
        }
        
        // Suggested Next Steps
        SuggestedNextStepsSection(steps = uiState.recommendations)
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToMoodHistory,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Mood History")
            }
            Button(
                onClick = onNavigateToTasks,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LavenderDeep)
            ) {
                Text("Review My Tasks")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Disclaimer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SoftBlue.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = LavenderDeep
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Wellness estimate based on your recorded mood check-ins and task deadlines — not a medical diagnosis.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StressIndicatorSection(stressLevel: StressLevel) {
    Column {
        Text(
            text = "Estimated Stress Indicator",
            style = MaterialTheme.typography.titleMedium,
            color = TextDark,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Stress Level Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StressLevel.entries.forEach { level ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (level.ordinal <= stressLevel.ordinal)
                                getStressColor(level)
                            else
                                getStressColor(level).copy(alpha = 0.2f)
                        )
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getStressColor(stressLevel)
            ) {
                Text(
                    text = stressLevel.displayName,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Low", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text("Moderate", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text("High", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text("Very High", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    suffix: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
                if (suffix != null) {
                    Text(
                        text = " $suffix",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun WeeklyMoodTrendSection(
    moodTrend: MoodTrend,
    dailyRatings: List<Float?>
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
                Column {
                    Text(
                        text = "Weekly Mood Trend",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Self-reported daily rating (1-5)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (moodTrend) {
                        MoodTrend.INCREASING -> StressLow.copy(alpha = 0.2f)
                        MoodTrend.STABLE -> SoftBlue
                        MoodTrend.DECREASING -> StressHigh.copy(alpha = 0.2f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (moodTrend) {
                                MoodTrend.INCREASING -> Icons.AutoMirrored.Filled.TrendingUp
                                MoodTrend.STABLE -> Icons.AutoMirrored.Filled.TrendingFlat
                                MoodTrend.DECREASING -> Icons.AutoMirrored.Filled.TrendingDown
                            },
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = when (moodTrend) {
                                MoodTrend.INCREASING -> StressLow
                                MoodTrend.STABLE -> LavenderDeep
                                MoodTrend.DECREASING -> StressHigh
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = moodTrend.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (moodTrend) {
                                MoodTrend.INCREASING -> StressLow
                                MoodTrend.STABLE -> LavenderDeep
                                MoodTrend.DECREASING -> StressHigh
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Simple Chart Representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                dailyRatings.forEachIndexed { index, rating ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Days without a check-in show a faint stub, not a made-up value
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(if (rating != null) (rating * 16).dp else 4.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (rating != null) LavenderPrimary
                                    else LavenderPrimary.copy(alpha = 0.25f)
                                )
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
        }
    }
}

@Composable
private fun AiPatternInsightSection(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✨", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Pattern Insight",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

// Light lavender-grey tile used inside the white section cards (matches mockup)
private val InnerTile = Color(0xFFF7F5FB)
private val InnerTileBorder = Color(0xFFE9E3F5)

@Composable
private fun PotentialContributorsSection(contributors: List<StressContributor>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Potential Stress Contributors",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                contributors.forEach { contributor ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = InnerTile
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = contributor.emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = contributor.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedNextStepsSection(steps: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Suggested Next Steps",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Small, realistic steps you can try today:",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.forEachIndexed { index, step ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = InnerTile,
                        border = androidx.compose.foundation.BorderStroke(1.dp, InnerTileBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(LavenderDeep),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDark
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper functions
private fun getStressColor(level: StressLevel): Color = when (level) {
    StressLevel.LOW -> StressLow
    StressLevel.MODERATE -> StressModerate
    StressLevel.HIGH -> StressHigh
    StressLevel.VERY_HIGH -> StressVeryHigh
}

private fun getMomoExpressionForStress(level: StressLevel): MomoExpression = when (level) {
    StressLevel.LOW -> MomoExpression.HAPPY
    StressLevel.MODERATE -> MomoExpression.CALM
    StressLevel.HIGH -> MomoExpression.WORRIED
    StressLevel.VERY_HIGH -> MomoExpression.CARING
}

@Preview(showBackground = true)
@Composable
private fun AiInsightsContentPreview() {
    MoodlyTheme {
        AiInsightsContent(
            uiState = AiInsightsUiState(
                stressLevel = StressLevel.HIGH,
                averageMoodRating = 2.4f,
                pendingTasks = 5,
                upcomingDeadlines = 3,
                moodTrend = MoodTrend.DECREASING,
                dailyMoodRatings = listOf(3.5f, 3f, 3f, 2.5f, 2f, 2f, 2f),
                momoReflection = "You seem to have a lot on your plate. Let's find one manageable next step.",
                patternAnalysis = "Your stress indicator appears higher this week. Your recent mood check-ins show a downward trend, while several assignment deadlines are approaching.",
                potentialContributors = listOf(
                    StressContributor("📅", "Multiple upcoming assignment deadlines (3 due within 4 days)."),
                    StressContributor("📝", "5 academic tasks are still incomplete."),
                    StressContributor("💜", "Recent mood ratings have been lower than earlier in the week.")
                ),
                recommendations = listOf(
                    "Start with the assignment that has the nearest deadline (Complete Database Assignment).",
                    "Break larger assignments into smaller, manageable 25-minute steps.",
                    "Schedule short 10-minute breaks between study sessions."
                ),
                hasEnoughData = true,
                isLoading = false
            ),
            onNavigateToMoodHistory = {},
            onNavigateToTasks = {}
        )
    }
}
