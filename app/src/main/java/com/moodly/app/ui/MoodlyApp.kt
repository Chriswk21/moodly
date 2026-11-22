package com.moodly.app.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.moodly.app.navigation.*
import com.moodly.app.ui.components.MoodlyBottomNavBar
import com.moodly.app.ui.screens.aiinsights.AiInsightsScreen
import com.moodly.app.ui.screens.home.HomeScreen
import com.moodly.app.ui.screens.login.LoginScreen
import com.moodly.app.ui.screens.moodcheckin.MoodCheckInScreen
import com.moodly.app.ui.screens.moodhistory.MoodHistoryScreen
import com.moodly.app.ui.screens.profile.ProfileScreen
import com.moodly.app.ui.screens.tasks.AddEditTaskScreen
import com.moodly.app.ui.screens.tasks.TasksScreen
import com.moodly.app.ui.screens.welcome.WelcomeScreen

@Composable
fun MoodlyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    
    // Determine if we should show bottom nav (only on main screens)
    val showBottomBar = when (navBackStackEntry?.destination?.route) {
        HomeRoute::class.qualifiedName,
        TasksRoute::class.qualifiedName,
        AiInsightsRoute::class.qualifiedName,
        ProfileRoute::class.qualifiedName -> true
        else -> false
    }
    
    // Check if user has completed onboarding (simplified for now)
    var hasCompletedOnboarding by rememberSaveable { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MoodlyBottomNavBar(
                    navController = navController,
                    currentRoute = navBackStackEntry?.destination?.route
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (hasCompletedOnboarding) HomeRoute else WelcomeRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
            },
            exitTransition = {
                fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
            }
        ) {
            // Onboarding
            composable<WelcomeRoute> {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(LoginRoute)
                    },
                    onAlreadyHaveAccount = {
                        navController.navigate(LoginRoute)
                    }
                )
            }
            
            composable<LoginRoute> {
                LoginScreen(
                    onLoginSuccess = {
                        hasCompletedOnboarding = true
                        navController.navigate(HomeRoute) {
                            popUpTo(WelcomeRoute) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(SignUpRoute)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Sign Up reuses the login form until real auth (Firebase) is added
            composable<SignUpRoute> {
                LoginScreen(
                    onLoginSuccess = {
                        hasCompletedOnboarding = true
                        navController.navigate(HomeRoute) {
                            popUpTo(WelcomeRoute) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {},
                    onBack = { navController.popBackStack() }
                )
            }
            
            // Main App Screens
            composable<HomeRoute> {
                HomeScreen(
                    onNavigateToMoodCheckIn = {
                        navController.navigate(MoodCheckInRoute)
                    },
                    onNavigateToAddTask = {
                        navController.navigate(AddEditTaskRoute())
                    },
                    onNavigateToAiInsights = {
                        navController.navigate(AiInsightsRoute)
                    },
                    onNavigateToAllTasks = {
                        navController.navigate(TasksRoute)
                    }
                )
            }
            
            composable<TasksRoute> {
                TasksScreen(
                    onNavigateToAddTask = {
                        navController.navigate(AddEditTaskRoute())
                    },
                    onNavigateToEditTask = { taskId ->
                        navController.navigate(AddEditTaskRoute(taskId = taskId))
                    }
                )
            }
            
            composable<AiInsightsRoute> {
                AiInsightsScreen(
                    onNavigateToMoodHistory = {
                        navController.navigate(MoodHistoryRoute)
                    },
                    onNavigateToTasks = {
                        navController.navigate(TasksRoute)
                    }
                )
            }
            
            composable<ProfileRoute> {
                ProfileScreen(
                    onLogout = {
                        hasCompletedOnboarding = false
                        navController.navigate(WelcomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    }
                )
            }
            
            // Detail Screens
            composable<MoodCheckInRoute> {
                MoodCheckInScreen(
                    onBack = { navController.popBackStack() },
                    onCheckInSaved = { navController.popBackStack() }
                )
            }
            
            composable<AddEditTaskRoute> { backStackEntry ->
                val route: AddEditTaskRoute = backStackEntry.toRoute()
                AddEditTaskScreen(
                    taskId = route.taskId,
                    onBack = { navController.popBackStack() },
                    onTaskSaved = { navController.popBackStack() }
                )
            }
            
            composable<MoodHistoryRoute> {
                MoodHistoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
