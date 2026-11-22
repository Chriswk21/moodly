package com.moodly.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moodly.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?
    
    @Query("SELECT * FROM tasks WHERE status != 'COMPLETED' ORDER BY dueDate ASC")
    fun getPendingTasksFlow(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE status = 'COMPLETED' ORDER BY completedAt DESC")
    fun getCompletedTasksFlow(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDate ASC")
    fun getTasksByStatusFlow(status: String): Flow<List<TaskEntity>>
    
    @Query("""
        SELECT * FROM tasks 
        WHERE status != 'COMPLETED' AND dueDate <= :dueDateMillis
        ORDER BY dueDate ASC
    """)
    fun getTasksDueBefore(dueDateMillis: Long): Flow<List<TaskEntity>>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE status != 'COMPLETED'")
    suspend fun getPendingTasksCount(): Int
    
    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'")
    suspend fun getCompletedTasksCount(): Int
    
    @Query("""
        SELECT COUNT(*) FROM tasks 
        WHERE status != 'COMPLETED' AND dueDate <= :dueDateMillis
    """)
    suspend fun getTasksDueBeforeCount(dueDateMillis: Long): Int
    
    @Query("""
        SELECT * FROM tasks 
        WHERE status != 'COMPLETED'
        ORDER BY dueDate ASC
        LIMIT :limit
    """)
    fun getUpcomingTasksFlow(limit: Int): Flow<List<TaskEntity>>
}
