package com.moodly.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moodly.app.domain.model.Priority
import com.moodly.app.domain.model.Task
import com.moodly.app.domain.model.TaskStatus
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val course: String?,
    val description: String?,
    val dueDate: Long, // Epoch millis
    val priority: String,
    val status: String,
    val createdAt: Long,
    val completedAt: Long?
)

fun TaskEntity.toDomainModel(): Task = Task(
    id = id,
    title = title,
    course = course,
    description = description,
    dueDate = LocalDateTime.ofEpochSecond(dueDate / 1000, 0, ZoneOffset.UTC),
    priority = Priority.valueOf(priority),
    status = TaskStatus.valueOf(status),
    createdAt = LocalDateTime.ofEpochSecond(createdAt / 1000, 0, ZoneOffset.UTC),
    completedAt = completedAt?.let { 
        LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC) 
    }
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    course = course,
    description = description,
    dueDate = dueDate.toEpochSecond(ZoneOffset.UTC) * 1000,
    priority = priority.name,
    status = status.name,
    createdAt = createdAt.toEpochSecond(ZoneOffset.UTC) * 1000,
    completedAt = completedAt?.toEpochSecond(ZoneOffset.UTC)?.times(1000)
)
