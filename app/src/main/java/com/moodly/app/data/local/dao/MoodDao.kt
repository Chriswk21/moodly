package com.moodly.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moodly.app.data.local.entity.MoodEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntryEntity): Long
    
    @Update
    suspend fun updateMoodEntry(entry: MoodEntryEntity)
    
    @Delete
    suspend fun deleteMoodEntry(entry: MoodEntryEntity)
    
    @Query("SELECT * FROM mood_entries ORDER BY createdAt DESC")
    fun getAllMoodEntriesFlow(): Flow<List<MoodEntryEntity>>
    
    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getMoodEntryById(id: Long): MoodEntryEntity?
    
    @Query("SELECT * FROM mood_entries ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestMoodEntry(): MoodEntryEntity?
    
    @Query("SELECT * FROM mood_entries ORDER BY createdAt DESC LIMIT 1")
    fun getLatestMoodEntryFlow(): Flow<MoodEntryEntity?>
    
    @Query("""
        SELECT * FROM mood_entries 
        WHERE createdAt >= :startMillis AND createdAt <= :endMillis
        ORDER BY createdAt ASC
    """)
    fun getMoodEntriesInRangeFlow(startMillis: Long, endMillis: Long): Flow<List<MoodEntryEntity>>
    
    @Query("""
        SELECT * FROM mood_entries 
        WHERE createdAt >= :startMillis AND createdAt <= :endMillis
        ORDER BY createdAt ASC
    """)
    suspend fun getMoodEntriesInRange(startMillis: Long, endMillis: Long): List<MoodEntryEntity>
    
    @Query("""
        SELECT * FROM mood_entries 
        WHERE date(createdAt / 1000, 'unixepoch') = date(:dateMillis / 1000, 'unixepoch')
        ORDER BY createdAt DESC LIMIT 1
    """)
    suspend fun getMoodEntryForDate(dateMillis: Long): MoodEntryEntity?
    
    @Query("SELECT AVG(rating) FROM mood_entries WHERE createdAt >= :startMillis AND createdAt <= :endMillis")
    suspend fun getAverageRatingInRange(startMillis: Long, endMillis: Long): Float?
    
    @Query("SELECT COUNT(*) FROM mood_entries")
    suspend fun getMoodEntriesCount(): Int
}
