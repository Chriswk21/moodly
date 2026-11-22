package com.moodly.app.domain.usecase

import com.moodly.app.domain.model.MoodEntry
import com.moodly.app.domain.model.MoodFactor
import com.moodly.app.domain.model.StressLevel

/**
 * Single source of truth for the estimated stress indicator.
 * Used by both Home and AI Insights so they always agree.
 *
 * This is an estimate from user-entered data, not a diagnosis.
 * Returns null when there are no mood check-ins (not enough data).
 */
object EstimateStressLevel {

    private val stressFactors = setOf(
        MoodFactor.ACADEMIC_PRESSURE,
        MoodFactor.ASSIGNMENT_OVERLOAD,
        MoodFactor.UPCOMING_EXAMS,
        MoodFactor.LACK_OF_SLEEP
    )

    operator fun invoke(
        weekMoodEntries: List<MoodEntry>,
        pendingTasks: Int,
        tasksDueSoon: Int
    ): StressLevel? {
        if (weekMoodEntries.isEmpty()) return null

        val avgRating = weekMoodEntries.map { it.rating }.average()
        var score = when {
            avgRating <= 2.0 -> 4
            avgRating <= 2.5 -> 3
            avgRating <= 3.0 -> 2
            avgRating <= 3.5 -> 1
            else -> 0
        }

        score += when {
            pendingTasks >= 5 -> 3
            pendingTasks >= 3 -> 2
            pendingTasks >= 1 -> 1
            else -> 0
        }

        score += when {
            tasksDueSoon >= 3 -> 3
            tasksDueSoon >= 2 -> 2
            tasksDueSoon >= 1 -> 1
            else -> 0
        }

        val factorCount = weekMoodEntries.flatMap { it.factors }.count { it in stressFactors }
        score += minOf((factorCount + 1) / 2, 3)

        return when {
            score >= 10 -> StressLevel.VERY_HIGH
            score >= 6 -> StressLevel.HIGH
            score >= 3 -> StressLevel.MODERATE
            else -> StressLevel.LOW
        }
    }
}
