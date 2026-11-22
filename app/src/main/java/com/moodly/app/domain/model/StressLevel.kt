package com.moodly.app.domain.model

/**
 * Estimated stress indicator levels.
 * Note: This is an estimate based on user-entered data, not a medical diagnosis.
 */
enum class StressLevel(val displayName: String, val description: String) {
    LOW(
        displayName = "Low",
        description = "Current inputs suggest relatively manageable stress"
    ),
    MODERATE(
        displayName = "Moderate",
        description = "Some signs of pressure may be present"
    ),
    HIGH(
        displayName = "High",
        description = "Several possible stress contributors are present"
    ),
    VERY_HIGH(
        displayName = "Very High",
        description = "Current inputs suggest substantial pressure; encourage support and rest"
    )
}

/**
 * AI-generated insight summary.
 */
data class AiInsight(
    val stressLevel: StressLevel,
    val averageMoodRating: Float,
    val pendingTasks: Int,
    val upcomingDeadlines: Int,
    val moodTrend: MoodTrend,
    val patternAnalysis: String,
    val potentialContributors: List<String>,
    val recommendations: List<String>,
    val weekStart: java.time.LocalDate,
    val weekEnd: java.time.LocalDate
)

enum class MoodTrend(val displayName: String) {
    INCREASING("Increasing"),
    STABLE("Stable"),
    DECREASING("Decreasing")
}
