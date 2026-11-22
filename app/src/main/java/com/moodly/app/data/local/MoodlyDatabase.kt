package com.moodly.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moodly.app.data.local.dao.MoodDao
import com.moodly.app.data.local.dao.TaskDao
import com.moodly.app.data.local.entity.MoodEntryEntity
import com.moodly.app.data.local.entity.TaskEntity

@Database(
    entities = [
        MoodEntryEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MoodlyDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun taskDao(): TaskDao
    
    companion object {
        const val DATABASE_NAME = "moodly_database"
    }
}
