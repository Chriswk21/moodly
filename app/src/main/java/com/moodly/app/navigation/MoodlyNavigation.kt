package com.moodly.app.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations using @Serializable objects/classes.
 * Following Navigation Compose 2.8+ best practices.
 */

// Onboarding
@Serializable
object WelcomeRoute

@Serializable
object LoginRoute

@Serializable
object SignUpRoute

// Main App - Bottom Navigation Destinations
@Serializable
object HomeRoute

@Serializable
object TasksRoute

@Serializable
object AiInsightsRoute

@Serializable
object ProfileRoute

// Detail Screens
@Serializable
object MoodCheckInRoute

@Serializable
data class AddEditTaskRoute(
    val taskId: Long? = null
)

@Serializable
object MoodHistoryRoute

@Serializable
object WeeklySummaryRoute
