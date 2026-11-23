package com.moodly.app.ui.screens.tasks

import com.moodly.app.ui.components.moodlySoftShadow

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import com.moodly.app.ui.components.MomoExpression
import com.moodly.app.ui.components.MomoMascot
import java.time.LocalDate
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moodly.app.domain.model.Priority
import com.moodly.app.domain.model.Task
import com.moodly.app.domain.model.TaskStatus
import com.moodly.app.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class TaskFilter(val displayName: String) {
    ALL("All"),
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    TasksContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateToAddTask = onNavigateToAddTask,
        onNavigateToEditTask = onNavigateToEditTask
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksContent(
    uiState: TasksUiState,
    onAction: (TasksAction) -> Unit,
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by rememberSaveable { mutableStateOf(TaskFilter.ALL) }
    var isListView by rememberSaveable { mutableStateOf(true) }
    
    Scaffold(
        // Outer app Scaffold already applies system bar insets
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header: title + counts on the left, Add Task pill on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        Text(
                            text = "${uiState.pendingCount} pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = LavenderDeep,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " · ${uiState.completedCount} completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
                
                Button(
                    onClick = onNavigateToAddTask,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderDeep),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Task", fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Segmented toggle: Task List / Schedule Agenda
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AgendaTileBorder)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    ViewToggleOption(
                        icon = Icons.AutoMirrored.Filled.List,
                        label = "Task List",
                        selected = isListView,
                        onClick = { isListView = true },
                        modifier = Modifier.weight(1f)
                    )
                    ViewToggleOption(
                        icon = Icons.Filled.CalendarMonth,
                        label = "Schedule Agenda",
                        selected = !isListView,
                        onClick = { isListView = false },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (!isListView) {
                ScheduleAgenda(
                    tasks = uiState.tasks,
                    onTaskClick = onNavigateToEditTask,
                    modifier = Modifier.weight(1f)
                )
                return@Column
            }
            
            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TextDark,
                            selectedLabelColor = Color.White,
                            containerColor = Surface,
                            labelColor = TextMuted
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Task List
            val filteredTasks = when (selectedFilter) {
                TaskFilter.ALL -> uiState.tasks
                TaskFilter.TODO -> uiState.tasks.filter { it.status == TaskStatus.TODO }
                TaskFilter.IN_PROGRESS -> uiState.tasks.filter { it.status == TaskStatus.IN_PROGRESS }
                TaskFilter.COMPLETED -> uiState.tasks.filter { it.status == TaskStatus.COMPLETED }
            }
            
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedFilter == TaskFilter.ALL) 
                            "Your task list is clear! Add a task whenever something comes up."
                        else 
                            "No ${selectedFilter.displayName.lowercase()} tasks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredTasks,
                        key = { it.id }
                    ) { task ->
                        TaskListItem(
                            task = task,
                            onToggleComplete = { onAction(TasksAction.ToggleTaskComplete(task)) },
                            onClick = { onNavigateToEditTask(task.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskListItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.status == TaskStatus.COMPLETED
    
    Card(
        modifier = modifier.moodlySoftShadow(RoundedCornerShape(16.dp), 6.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(
                    checkedColor = LavenderDeep,
                    uncheckedColor = LavenderPrimary
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Course tag
                if (task.course != null) {
                    Text(
                        text = task.course,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isCompleted) TextMuted else TextDark,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Due date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isOverdue = task.dueDate.isBefore(LocalDateTime.now()) && !isCompleted
                    val isDueSoon = task.dueDate.isBefore(LocalDateTime.now().plusDays(1)) && !isCompleted
                    
                    Text(
                        text = "⏱ ${formatDueDate(task.dueDate)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            isOverdue -> StressVeryHigh
                            isDueSoon -> StressHigh
                            else -> TextMuted
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                // Status chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getStatusColor(task.status).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = task.status.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = getStatusColor(task.status)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Priority chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getPriorityChipColor(task.priority)
                ) {
                    Text(
                        text = "• ${task.priority.displayName}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = getPriorityTextColor(task.priority)
                    )
                }
            }
        }
    }
}

// Helper functions
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

private fun getStatusColor(status: TaskStatus): Color = when (status) {
    TaskStatus.TODO -> TextMuted
    TaskStatus.IN_PROGRESS -> LavenderDeep
    TaskStatus.COMPLETED -> StressLow
}

private fun getPriorityColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> PriorityLow
    Priority.MEDIUM -> PriorityMedium
    Priority.HIGH -> PriorityHigh
}

// Darker text tones so priority labels stay readable on their pastel chips
private fun getPriorityTextColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> Color(0xFF4E8A66)
    Priority.MEDIUM -> Color(0xFF9A7A1E)
    Priority.HIGH -> Color(0xFFB8673A)
}

private fun getPriorityChipColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> StressLow.copy(alpha = 0.45f)
    Priority.MEDIUM -> StressModerate.copy(alpha = 0.45f)
    Priority.HIGH -> StressHigh.copy(alpha = 0.4f)
}

private val AgendaTile = Color(0xFFF7F5FB)
private val AgendaTileBorder = Color(0xFFE9E3F5)
private val TodayTile = Color(0xFFEEF5FD)
private val TodayTileBorder = Color(0xFFCFE2F7)
private val DueDot = Color(0xFFF0A27A)

@Composable
private fun ViewToggleOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (selected) Color(0xFFEDE7FA) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "toggle_bg"
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) LavenderDeep else TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) LavenderDeep else TextMuted,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * 7-day agenda starting today. The selected day (default: today) and all following
 * days in the window are listed, so you see what's coming up from that day on.
 * Only unfinished tasks are shown; overdue ones appear first when today is selected.
 */
@Composable
private fun ScheduleAgenda(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    val days = remember(today) { (0..6).map { today.plusDays(it.toLong()) } }
    val windowEnd = days.last()
    var selectedDay by rememberSaveable { mutableStateOf(today.toEpochDay()) }
    val selectedDate = LocalDate.ofEpochDay(selectedDay)
    
    val pending = remember(tasks) {
        tasks.filter { it.status != TaskStatus.COMPLETED }.sortedBy { it.dueDate }
    }
    val daysWithTasks = remember(pending) { pending.map { it.dueDate.toLocalDate() }.toSet() }
    val overdue = if (selectedDate == today) pending.filter { it.dueDate.toLocalDate() < today } else emptyList()
    val agenda = pending.filter {
        val d = it.dueDate.toLocalDate()
        d >= selectedDate && d <= windowEnd
    }
    val monthLabel = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
    
    Card(
        modifier = modifier.moodlySoftShadow(RoundedCornerShape(16.dp), 6.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(key = "header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$monthLabel · Upcoming Agenda",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "7-Day View",
                        style = MaterialTheme.typography.labelMedium,
                        color = LavenderDeep
                    )
                }
            }
            
            item(key = "days") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    days.forEach { day ->
                        DayChip(
                            date = day,
                            selected = day == selectedDate,
                            hasTasks = day in daysWithTasks,
                            onClick = { selectedDay = day.toEpochDay() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            if (overdue.isEmpty() && agenda.isEmpty()) {
                item(key = "empty") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MomoMascot(expression = MomoExpression.HAPPY, size = 72.dp, animated = false)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nothing due from here to ${windowEnd.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH))}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            items(items = overdue, key = { "overdue-${it.id}" }) { task ->
                AgendaItem(task = task, label = "Overdue", onClick = { onTaskClick(task.id) })
            }
            
            items(items = agenda, key = { "agenda-${it.id}" }) { task ->
                val isToday = task.dueDate.toLocalDate() == today
                AgendaItem(
                    task = task,
                    label = if (isToday) "TODAY · DUE ${task.dueDate.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH))}"
                            else formatDueDate(task.dueDate),
                    highlightToday = isToday,
                    onClick = { onTaskClick(task.id) }
                )
            }
        }
    }
}

@Composable
private fun DayChip(
    date: LocalDate,
    selected: Boolean,
    hasTasks: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (selected) LavenderDeep else AgendaTile,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "day_bg"
    )
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White.copy(alpha = 0.85f) else TextMuted
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) Color.White else TextDark,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Dot marks days with a deadline; keep the space so chips stay the same height
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (hasTasks && !selected) DueDot else if (hasTasks) Color.White else Color.Transparent)
        )
    }
}

@Composable
private fun AgendaItem(
    task: Task,
    label: String,
    onClick: () -> Unit,
    highlightToday: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (highlightToday) TodayTile else AgendaTile,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (highlightToday) TodayTileBorder else AgendaTileBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        label == "Overdue" -> Color(0xFFC0504D)
                        highlightToday -> Color(0xFF2F6FA8)
                        else -> LavenderDeep
                    },
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.course != null) {
                    Text(
                        text = task.course,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getPriorityChipColor(task.priority)
            ) {
                Text(
                    text = "• ${task.priority.displayName}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = getPriorityTextColor(task.priority),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TasksContentPreview() {
    MoodlyTheme {
        TasksContent(
            uiState = TasksUiState(
                tasks = listOf(
                    Task(
                        id = 1,
                        title = "Complete Database Assignment",
                        course = "CS 304 - Database Systems",
                        dueDate = LocalDateTime.now().plusDays(1),
                        priority = Priority.HIGH,
                        status = TaskStatus.TODO
                    ),
                    Task(
                        id = 2,
                        title = "Prepare Software Metrics Quiz",
                        course = "SE 312 - Software Engineering",
                        dueDate = LocalDateTime.now().plusDays(2),
                        priority = Priority.MEDIUM,
                        status = TaskStatus.IN_PROGRESS
                    )
                ),
                pendingCount = 5,
                completedCount = 1
            ),
            onAction = {},
            onNavigateToAddTask = {},
            onNavigateToEditTask = {}
        )
    }
}
