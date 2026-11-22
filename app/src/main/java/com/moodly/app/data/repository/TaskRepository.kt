package com.moodly.app.data.repository

import com.moodly.app.data.local.dao.TaskDao
import com.moodly.app.data.local.entity.toDomainModel
import com.moodly.app.data.local.entity.toEntity
import com.moodly.app.domain.model.Task
import com.moodly.app.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasksFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasksFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasksFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        return taskDao.getTasksByStatusFlow(status.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getUpcomingTasks(limit: Int = 5): Flow<List<Task>> {
        return taskDao.getUpcomingTasksFlow(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    suspend fun saveTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }
    
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }
    
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }
    
    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.toDomainModel()
    }
    
    suspend fun markTaskCompleted(task: Task) {
        val completedTask = task.copy(
            status = TaskStatus.COMPLETED,
            completedAt = LocalDateTime.now()
        )
        taskDao.updateTask(completedTask.toEntity())
    }
    
    suspend fun getPendingTasksCount(): Int {
        return taskDao.getPendingTasksCount()
    }
    
    suspend fun getCompletedTasksCount(): Int {
        return taskDao.getCompletedTasksCount()
    }
    
    suspend fun getTasksDueSoonCount(days: Int = 4): Int {
        val dueDateMillis = LocalDateTime.now()
            .plusDays(days.toLong())
            .toEpochSecond(ZoneOffset.UTC) * 1000
        return taskDao.getTasksDueBeforeCount(dueDateMillis)
    }
}
