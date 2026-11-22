package com.moodly.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moodly.app.domain.model.MoodEntry
import com.moodly.app.domain.model.MoodFactor
import com.moodly.app.domain.model.MoodType
import java.time.LocalDateTime

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moodType: String,
    val rating: Int,
    val factors: String, // Comma-separated list of MoodFactor names
    val journalNote: String?,
    val createdAt: Long // Epoch millis
)

fun MoodEntryEntity.toDomainModel(): MoodEntry = MoodEntry(
    id = id,
    moodType = MoodType.valueOf(moodType),
    rating = rating,
    factors = if (factors.isBlank()) emptyList() 
              else factors.split(",").map { MoodFactor.valueOf(it.trim()) },
    journalNote = journalNote,
    createdAt = LocalDateTime.ofEpochSecond(
        createdAt / 1000, 
        ((createdAt % 1000) * 1_000_000).toInt(), 
        java.time.ZoneOffset.UTC
    )
)

fun MoodEntry.toEntity(): MoodEntryEntity = MoodEntryEntity(
    id = id,
    moodType = moodType.name,
    rating = rating,
    factors = factors.joinToString(",") { it.name },
    journalNote = journalNote,
    createdAt = createdAt.toEpochSecond(java.time.ZoneOffset.UTC) * 1000 +
                createdAt.nano / 1_000_000
)
