package com.moodly.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moodly.app.domain.model.*
import com.moodly.app.ui.components.MomoExpression
import com.moodly.app.ui.components.MomoMascot
import com.moodly.app.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    onNavigateToMoodCheckIn: () -> Unit,
    onNavigateToAddTask: () -> Unit,
    onNavigateToAiInsights: () -> Unit,
    onNavigateToAllTasks: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    HomeContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateToMoodCheckIn = onNavigateToMoodCheckIn,
        onNavigateToAddTask = onNavigateToAddTask,
        onNavigateToAiInsights = onNavigateToAiInsights,
        onNavigateToAllTasks = onNavigateToAllTasks
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    onNavigateToMoodCheckIn: () -> Unit,
    onNavigateToAddTask: () -> Unit,
    onNavigateToAiInsights: () -> Unit,
    onNavigateToAllTasks: () -> Unit,
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
        
        // Header Section
        HeaderSection(
            greeting = uiState.greeting,
            userName = uiState.userName,
            currentDate = uiState.currentDate,
            momoMessage = uiState.momoMessage
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Today's Mood Card
        TodaysMoodCard(
            moodEntry = uiState.todaysMoodEntry,
            onUpdateMood = onNavigateToMoodCheckIn
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stress Overview Card
        StressOverviewCard(
            stressLevel = uiState.estimatedStressLevel,
            pendingTasksCount = uiState.pendingTasksCount,
            stressMessage = uiState.stressMessage,
            onViewAiSummary = onNavigateToAiInsights
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Upcoming Tasks Section
        UpcomingTasksSection(
            tasks = uiState.upcomingTasks,
            totalPending = uiState.pendingTasksCount,
            onViewAll = onNavigateToAllTasks,
            onTaskComplete = { task -> onAction(HomeAction.CompleteTask(task)) }
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Quick Actions
        QuickActionsSection(
            onCheckInMood = onNavigateToMoodCheckIn,
            onAddTask = onNavigateToAddTask,
            onAiInsights = onNavigateToAiInsights
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HeaderSection(
    greeting: String,
    userName: String,
    currentDate: String,
    momoMessage: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Date chip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LavenderPrimary.copy(alpha = 0.2f)
            ) {
                Text(
                    text = currentDate,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = LavenderDeep
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Greeting
            Text(
                text = "$greeting, $userName!",
                style = MaterialTheme.typography.headlineMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "\"$momoMessage\"",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
        
        // Momo with star
        Box {
            MomoMascot(
                expression = MomoExpression.HAPPY,
                size = 70.dp
            )
        }
    }
}

@Composable
private fun TodaysMoodCard(
    moodEntry: MoodEntry?,
    onUpdateMood: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Today's Mood",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (moodEntry != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Today, ${moodEntry.createdAt.format(DateTimeFormatter.ofPattern("h:mm a"))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (moodEntry != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Mood icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(getMoodColor(moodEntry.moodType).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getMoodEmoji(moodEntry.moodType),
                                fontSize = 20.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = moodEntry.moodType.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextDark,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Background
                                ) {
                                    Text(
                                        text = "${moodEntry.rating} / 5",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                            }
                            if (moodEntry.factors.isNotEmpty()) {
                                Text(
                                    text = moodEntry.factors.take(2).joinToString(", ") { it.displayName } + 
                                           if (moodEntry.factors.size > 2) " ..." else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "How are you feeling today?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
            
            OutlinedButton(
                onClick = onUpdateMood,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = LavenderDeep
                )
            ) {
                Text(if (moodEntry != null) "Update" else "Check In")
            }
        }
    }
}

@Composable
private fun StressOverviewCard(
    stressLevel: StressLevel?,
    pendingTasksCount: Int,
    stressMessage: String,
    onViewAiSummary: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stress Overview",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Estimated",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                
                TextButton(onClick = onViewAiSummary) {
                    Text(
                        text = "View AI Summary →",
                        style = MaterialTheme.typography.labelMedium,
                        color = LavenderDeep
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stress Indicator
            if (stressLevel != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = getStressColor(stressLevel).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(getStressColor(stressLevel))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${stressLevel.displayName} Stress Indicator",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = stressMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Surface
                        ) {
                            Text(
                                text = "$pendingTasksCount pending tasks",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SoftBlue.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = "Complete a few mood check-ins to start seeing your insights.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingTasksSection(
    tasks: List<Task>,
    totalPending: Int,
    onViewAll: () -> Unit,
    onTaskComplete: (Task) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Upcoming Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LavenderPrimary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$totalPending",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = LavenderDeep
                    )
                }
            }
            
            TextButton(onClick = onViewAll) {
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.labelMedium,
                    color = LavenderDeep
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (tasks.isEmpty()) {
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
                        text = "Your task list is clear! Add a task whenever something comes up.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEach { task ->
                    TaskCard(
                        task = task,
                        onComplete = { onTaskComplete(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = task.status == TaskStatus.COMPLETED,
                onCheckedChange = { onComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = LavenderDeep,
                    uncheckedColor = LavenderPrimary
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Due: ${formatDueDate(task.dueDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (task.dueDate.isBefore(LocalDateTime.now().plusDays(1))) 
                        StressHigh else TextMuted
                )
            }
            
            // Priority chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getPriorityColor(task.priority).copy(alpha = 0.2f)
            ) {
                Text(
                    text = "• ${task.priority.displayName}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (task.priority) {
                        Priority.LOW -> Color(0xFF4E8A66)
                        Priority.MEDIUM -> LavenderDeep
                        Priority.HIGH -> Color(0xFFB8673A)
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection(
    onCheckInMood: () -> Unit,
    onAddTask: () -> Unit,
    onAiInsights: () -> Unit
) {
    Text(
        text = "QUICK ACTIONS",
        style = MaterialTheme.typography.labelMedium,
        color = TextMuted,
        letterSpacing = 1.sp
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            icon = Icons.Filled.Favorite,
            iconColor = StressVeryHigh,
            label = "Check In Mood",
            onClick = onCheckInMood,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            icon = Icons.Filled.Add,
            iconColor = LavenderDeep,
            label = "Add a Task",
            onClick = onAddTask,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            icon = Icons.Filled.Insights,
            iconColor = StarYellow,
            label = "AI Insights",
            onClick = onAiInsights,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextDark
            )
        }
    }
}

// Helper functions
private fun getMoodEmoji(moodType: MoodType): String = when (moodType) {
    MoodType.HAPPY -> "🌸"
    MoodType.CALM -> "🌿"
    MoodType.NEUTRAL -> "☁️"
    MoodType.ANXIOUS -> "😰"
    MoodType.SAD -> "🌧️"
    MoodType.STRESSED -> "⚡"
}

private fun getMoodColor(moodType: MoodType): androidx.compose.ui.graphics.Color = when (moodType) {
    MoodType.HAPPY -> MoodHappy
    MoodType.CALM -> MoodCalm
    MoodType.NEUTRAL -> MoodNeutral
    MoodType.ANXIOUS -> MoodAnxious
    MoodType.SAD -> MoodSad
    MoodType.STRESSED -> MoodStressed
}

private fun getStressColor(level: StressLevel): androidx.compose.ui.graphics.Color = when (level) {
    StressLevel.LOW -> StressLow
    StressLevel.MODERATE -> StressModerate
    StressLevel.HIGH -> StressHigh
    StressLevel.VERY_HIGH -> StressVeryHigh
}

private fun getPriorityColor(priority: Priority): androidx.compose.ui.graphics.Color = when (priority) {
    Priority.LOW -> PriorityLow
    Priority.MEDIUM -> PriorityMedium
    Priority.HIGH -> PriorityHigh
}

private fun formatDueDate(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val daysDiff = ChronoUnit.DAYS.between(now.toLocalDate(), dateTime.toLocalDate())
    
    return when {
        daysDiff < 0 -> "Overdue"
        daysDiff == 0L -> "Today, ${dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))}"
        daysDiff == 1L -> "Tomorrow, ${dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))}"
        daysDiff <= 7 -> "In $daysDiff days, ${dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))}"
        else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    MoodlyTheme {
        HomeContent(
            uiState = HomeUiState(
                userName = "Alex",
                greeting = "Good morning",
                currentDate = "Thursday, Nov 19",
                momoMessage = "Good morning! Let's take today one step at a time.",
                todaysMoodEntry = MoodEntry(
                    id = 1,
                    moodType = MoodType.STRESSED,
                    rating = 2,
                    factors = listOf(MoodFactor.ACADEMIC_PRESSURE, MoodFactor.ASSIGNMENT_OVERLOAD)
                ),
                estimatedStressLevel = StressLevel.HIGH,
                stressMessage = "Multiple upcoming deadlines and recent mood ratings suggest elevated pressure.",
                pendingTasksCount = 5,
                upcomingTasks = listOf(
                    Task(
                        id = 1,
                        title = "Complete Database Assignment",
                        course = "CS 304",
                        dueDate = LocalDateTime.now().plusDays(1),
                        priority = Priority.HIGH
                    ),
                    Task(
                        id = 2,
                        title = "Prepare Software Metrics Quiz",
                        course = "SE 312",
                        dueDate = LocalDateTime.now().plusDays(2),
                        priority = Priority.MEDIUM,
                        status = TaskStatus.IN_PROGRESS
                    )
                ),
                isLoading = false
            ),
            onAction = {},
            onNavigateToMoodCheckIn = {},
            onNavigateToAddTask = {},
            onNavigateToAiInsights = {},
            onNavigateToAllTasks = {}
        )
    }
}
