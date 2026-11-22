package com.moodly.app.domain.model

/**
 * User profile model.
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val major: String? = null,
    val year: Int? = null
)

/**
 * User preferences for notifications and privacy.
 */
data class UserPreferences(
    val dailyMoodReminder: Boolean = true,
    val deadlineReminder: Boolean = true,
    val studyBreakNudges: Boolean = false,
    val includeJournalInAiSummary: Boolean = true
)
