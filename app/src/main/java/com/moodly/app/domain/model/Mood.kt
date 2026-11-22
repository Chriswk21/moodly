package com.moodly.app.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Represents a mood entry recorded by the user.
 */
data class MoodEntry(
    val id: Long = 0,
    val moodType: MoodType,
    val rating: Int, // 1-5 scale
    val factors: List<MoodFactor> = emptyList(),
    val journalNote: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Six core mood types as defined in the brief.
 */
enum class MoodType(val displayName: String, val emoji: String) {
    HAPPY("Happy", "😊"),
    CALM("Calm", "🌿"),
    NEUTRAL("Neutral", "☁️"),
    ANXIOUS("Anxious", "😰"),
    SAD("Sad", "😢"),
    STRESSED("Stressed", "⚡")
}

/**
 * Factors that may influence mood.
 */
enum class MoodFactor(val displayName: String) {
    ACADEMIC_PRESSURE("Academic pressure"),
    ASSIGNMENT_OVERLOAD("Assignment overload"),
    UPCOMING_EXAMS("Upcoming exams"),
    LACK_OF_SLEEP("Lack of sleep"),
    SOCIAL_PROBLEMS("Social problems"),
    PERSONAL_PROBLEMS("Personal problems"),
    FINANCIAL_CONCERNS("Financial concerns"),
    PERSONAL_TIME("Personal time"),
    OTHER("Other")
}

/**
 * Mood rating labels (1-5).
 */
fun Int.toMoodRatingLabel(): String = when (this) {
    1 -> "Very low"
    2 -> "Low"
    3 -> "Okay"
    4 -> "Good"
    5 -> "Very good"
    else -> "Unknown"
}
