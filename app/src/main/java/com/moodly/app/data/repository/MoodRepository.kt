package com.moodly.app.data.repository

import com.moodly.app.data.local.dao.MoodDao
import com.moodly.app.data.local.entity.toDomainModel
import com.moodly.app.data.local.entity.toEntity
import com.moodly.app.domain.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val moodDao: MoodDao
) {
    fun getAllMoodEntries(): Flow<List<MoodEntry>> {
        return moodDao.getAllMoodEntriesFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    fun getLatestMoodEntry(): Flow<MoodEntry?> {
        return moodDao.getLatestMoodEntryFlow().map { it?.toDomainModel() }
    }
    
    suspend fun saveMoodEntry(entry: MoodEntry): Long {
        return moodDao.insertMoodEntry(entry.toEntity())
    }
    
    suspend fun updateMoodEntry(entry: MoodEntry) {
        moodDao.updateMoodEntry(entry.toEntity())
    }
    
    suspend fun deleteMoodEntry(entry: MoodEntry) {
        moodDao.deleteMoodEntry(entry.toEntity())
    }
    
    suspend fun getMoodEntryById(id: Long): MoodEntry? {
        return moodDao.getMoodEntryById(id)?.toDomainModel()
    }
    
    suspend fun getTodaysMoodEntry(): MoodEntry? {
        val todayMillis = LocalDate.now()
            .atStartOfDay()
            .toEpochSecond(ZoneOffset.UTC) * 1000
        return moodDao.getMoodEntryForDate(todayMillis)?.toDomainModel()
    }
    
    fun getMoodEntriesForWeek(weekStart: LocalDate): Flow<List<MoodEntry>> {
        val startMillis = weekStart.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        val endMillis = weekStart.plusDays(7).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        return moodDao.getMoodEntriesInRangeFlow(startMillis, endMillis).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    suspend fun getMoodEntriesInRange(startDate: LocalDateTime, endDate: LocalDateTime): List<MoodEntry> {
        val startMillis = startDate.toEpochSecond(ZoneOffset.UTC) * 1000
        val endMillis = endDate.toEpochSecond(ZoneOffset.UTC) * 1000
        return moodDao.getMoodEntriesInRange(startMillis, endMillis).map { it.toDomainModel() }
    }
    
    suspend fun getAverageRatingForWeek(weekStart: LocalDate): Float? {
        val startMillis = weekStart.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        val endMillis = weekStart.plusDays(7).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        return moodDao.getAverageRatingInRange(startMillis, endMillis)
    }
    
    suspend fun getMoodEntriesCount(): Int {
        return moodDao.getMoodEntriesCount()
    }
}
