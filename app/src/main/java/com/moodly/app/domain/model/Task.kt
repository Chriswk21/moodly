package com.moodly.app.domain.model

import java.time.LocalDateTime

/**
 * Represents an academic task.
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val course: String? = null,
    val description: String? = null,
    val dueDate: LocalDateTime,
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

/**
 * Task priority levels.
 */
enum class Priority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

/**
 * Task status.
 */
enum class TaskStatus(val displayName: String) {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}
