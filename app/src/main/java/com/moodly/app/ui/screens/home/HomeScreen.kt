package com.moodly.app.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moodly.app.domain.model.*
import com.moodly.app.ui.components.MomoExpression
import com.moodly.app.ui.components.MomoMascot
import com.moodly.app.ui.components.MoodlyCard
import com.moodly.app.ui.components.MoodlyTile
import com.moodly.app.ui.components.MoodlyTileBorder
import com.moodly.app.ui.components.moodlySoftShadow
import com.moodly.app.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        
        GreetingCard(
            greeting = uiState.greeting,
            userName = uiState.userName,
            currentDate = uiState.currentDate,
            momoMessage = uiState.momoMessage
        )
        
        TodaysMoodCard(
            moodEntry = uiState.todaysMoodEntry,
            onCheckIn = onNavigateToMoodCheckIn
        )
        
        StressOverviewCard(
            stressLevel = uiState.estimatedStressLevel,
            pendingTasksCount = uiState.pendingTasksCount,
            stressMessage = uiState.stressMessage,
            onViewAiSummary = onNavigateToAiInsights,
            onCheckIn = onNavigateToMoodCheckIn
        )
        
        UpcomingTasksCard(
            tasks = uiState.upcomingTasks,
            totalPending = uiState.pendingTasksCount,
            onViewAll = onNavigateToAllTasks,
            onTaskComplete = { task -> onAction(HomeAction.CompleteTask(task)) }
        )
        
        QuickActionsSection(
            onCheckInMood = onNavigateToMoodCheckIn,
            onAddTask = onNavigateToAddTask,
            onAiInsights = onNavigateToAiInsights
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ---------------------------------------------------------------------------
// Greeting
// ---------------------------------------------------------------------------

private val GreetingGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF4F0FC), Color(0xFFEDE6FA))
)

@Composable
private fun GreetingCard(
    greeting: String,
    userName: String,
    currentDate: String,
    momoMessage: String
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .moodlySoftShadow(shape)
            .clip(shape)
            .background(GreetingGradient)
            .border(1.dp, Color(0xFFEDE7F8), shape)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(shape = RoundedCornerShape(10.dp), color = Color.White) {
                    Text(
                        text = currentDate,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = LavenderDeep,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$greeting, $userName!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"$momoMessage\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            MomoMascot(expression = MomoExpression.HAPPY, size = 76.dp)
        }
    }
}

// ---------------------------------------------------------------------------
// Today's mood
// ---------------------------------------------------------------------------

@Composable
private fun TodaysMoodCard(
    moodEntry: MoodEntry?,
    onCheckIn: () -> Unit
) {
    MoodlyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Today's Mood", style = MaterialTheme.typography.titleMedium, color = TextDark)
            if (moodEntry != null) {
                Text(
                    text = "Today, " + moodEntry.createdAt.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MoodlyTile,
            border = BorderStroke(1.dp, MoodlyTileBorder)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (moodEntry != null) getMoodEmoji(moodEntry.moodType) else "💜",
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    if (moodEntry != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = moodEntry.moodType.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEDE7FA)) {
                                Text(
                                    text = "${moodEntry.rating} / 5",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LavenderDeep,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (moodEntry.factors.isNotEmpty()) {
                            Text(
                                text = moodEntry.factors.joinToString(" · ") { it.displayName },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = "How are you feeling today?",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextDark
                        )
                        Text(
                            text = "A quick check-in helps Momo understand your week.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                if (moodEntry != null) {
                    OutlinedButton(
                        onClick = onCheckIn,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LavenderPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = LavenderDeep
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Update", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    Button(
                        onClick = onCheckIn,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LavenderDeep),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Check in", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Stress overview
// ---------------------------------------------------------------------------

@Composable
private fun StressOverviewCard(
    stressLevel: StressLevel?,
    pendingTasksCount: Int,
    stressMessage: String,
    onViewAiSummary: () -> Unit,
    onCheckIn: () -> Unit
) {
    MoodlyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Stress Overview", style = MaterialTheme.typography.titleMedium, color = TextDark)
            Spacer(modifier = Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(6.dp), color = MoodlyTile) {
                Text(
                    text = "Estimated",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "View AI Summary →",
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onViewAiSummary)
                    .padding(4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = LavenderDeep,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (stressLevel != null) {
            val tone = getStressColor(stressLevel)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = tone.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, tone.copy(alpha = 0.55f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(10.dp), color = tone.copy(alpha = 0.6f)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(getStressTextColor(stressLevel))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${stressLevel.displayName} Stress Indicator",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = getStressTextColor(stressLevel),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (pendingTasksCount == 1) "1 pending task" else "$pendingTasksCount pending tasks",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stressMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SoftBlue.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MomoMascot(expression = MomoExpression.CONFUSED, size = 44.dp, animated = false)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Complete a few mood check-ins to start seeing your insights.",
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onCheckIn),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDark.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Upcoming tasks
// ---------------------------------------------------------------------------

@Composable
private fun UpcomingTasksCard(
    tasks: List<Task>,
    totalPending: Int,
    onViewAll: () -> Unit,
    onTaskComplete: (Task) -> Unit
) {
    MoodlyCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Upcoming Tasks", style = MaterialTheme.typography.titleMedium, color = TextDark)
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDE7FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$totalPending",
                    style = MaterialTheme.typography.labelSmall,
                    color = LavenderDeep,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "View all",
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onViewAll)
                    .padding(4.dp),
                style = MaterialTheme.typography.labelMedium,
                color = LavenderDeep,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (tasks.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MoodlyTile
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MomoMascot(expression = MomoExpression.HAPPY, size = 44.dp, animated = false)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Your task list is clear for now!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tasks.forEach { task ->
                    UpcomingTaskRow(task = task, onComplete = { onTaskComplete(task) })
                }
            }
        }
    }
}

@Composable
private fun UpcomingTaskRow(
    task: Task,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MoodlyTile
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundCheck(
                checked = task.status == TaskStatus.COMPLETED,
                onClick = onComplete
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Due: ${formatDueDate(task.dueDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            PriorityChip(task.priority)
        }
    }
}

/** Circular checkbox matching the mockup (lavender outline, filled when done). */
@Composable
private fun RoundCheck(checked: Boolean, onClick: () -> Unit) {
    val fill by animateColorAsState(
        targetValue = if (checked) LavenderDeep else Color.White,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "check_fill"
    )
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(fill)
            .border(1.5.dp, if (checked) LavenderDeep else LavenderPrimary, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun PriorityChip(priority: Priority) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = when (priority) {
            Priority.LOW -> StressLow.copy(alpha = 0.45f)
            Priority.MEDIUM -> StressModerate.copy(alpha = 0.45f)
            Priority.HIGH -> StressHigh.copy(alpha = 0.4f)
        }
    ) {
        Text(
            text = "• ${priority.displayName}",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = when (priority) {
                Priority.LOW -> Color(0xFF4E8A66)
                Priority.MEDIUM -> Color(0xFF9A7A1E)
                Priority.HIGH -> Color(0xFFB8673A)
            },
            fontWeight = FontWeight.Bold
        )
    }
}

// ---------------------------------------------------------------------------
// Quick actions
// ---------------------------------------------------------------------------

@Composable
private fun QuickActionsSection(
    onCheckInMood: () -> Unit,
    onAddTask: () -> Unit,
    onAiInsights: () -> Unit
) {
    Column {
        Text(
            text = "QUICK ACTIONS",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Filled.Favorite,
                iconTint = Color(0xFFB667C9),
                tileColor = BlushPink,
                label = "Check In Mood",
                onClick = onCheckInMood,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Filled.Add,
                iconTint = LavenderDeep,
                tileColor = SoftBlue,
                label = "Add a Task",
                onClick = onAddTask,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Filled.AutoAwesome,
                iconTint = Color(0xFFE0A72E),
                tileColor = Color(0xFFEDE7FA),
                label = "AI Insights",
                onClick = onAiInsights,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    iconTint: Color,
    tileColor: Color,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        onClick = onClick,
        modifier = modifier.moodlySoftShadow(shape, elevation = 6.dp),
        shape = shape,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tileColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun getMoodEmoji(moodType: MoodType): String = when (moodType) {
    MoodType.HAPPY -> "🌸"
    MoodType.CALM -> "🌿"
    MoodType.NEUTRAL -> "☁️"
    MoodType.ANXIOUS -> "😰"
    MoodType.SAD -> "🌧️"
    MoodType.STRESSED -> "⚡"
}

private fun getStressColor(level: StressLevel): Color = when (level) {
    StressLevel.LOW -> StressLow
    StressLevel.MODERATE -> StressModerate
    StressLevel.HIGH -> StressHigh
    StressLevel.VERY_HIGH -> StressVeryHigh
}

/** Darker shade of each stress colour, readable on the pastel background. */
private fun getStressTextColor(level: StressLevel): Color = when (level) {
    StressLevel.LOW -> Color(0xFF3F7A57)
    StressLevel.MODERATE -> Color(0xFF8A6B12)
    StressLevel.HIGH -> Color(0xFF9C5228)
    StressLevel.VERY_HIGH -> Color(0xFF9E3F3F)
}

private fun formatDueDate(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val daysDiff = ChronoUnit.DAYS.between(now.toLocalDate(), dateTime.toLocalDate())
    val time = dateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH))
    return when {
        daysDiff < 0 -> "Overdue"
        daysDiff == 0L -> "Today, $time"
        daysDiff == 1L -> "Tomorrow, $time"
        daysDiff <= 7 -> "In $daysDiff days, $time"
        else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.ENGLISH))
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun HomeContentPreview() {
    MoodlyTheme {
        HomeContent(
            uiState = HomeUiState(
                userName = "Alex",
                greeting = "Good morning",
                currentDate = "Thursday, Nov 19",
                momoMessage = "Let's take today one step at a time.",
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
                    Task(id = 1, title = "Complete Database Assignment", dueDate = LocalDateTime.now().plusDays(1), priority = Priority.HIGH),
                    Task(id = 2, title = "Prepare Software Metrics Quiz", dueDate = LocalDateTime.now().plusDays(2), priority = Priority.MEDIUM),
                    Task(id = 3, title = "Work on Mobile App Project", dueDate = LocalDateTime.now().plusDays(4), priority = Priority.HIGH)
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
