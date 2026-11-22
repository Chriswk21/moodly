package com.moodly.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.moodly.app.navigation.AiInsightsRoute
import com.moodly.app.navigation.HomeRoute
import com.moodly.app.navigation.ProfileRoute
import com.moodly.app.navigation.TasksRoute
import com.moodly.app.ui.theme.LavenderPrimary

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any,
    val routeName: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        route = HomeRoute,
        routeName = HomeRoute::class.qualifiedName ?: ""
    ),
    BottomNavItem(
        label = "Tasks",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        route = TasksRoute,
        routeName = TasksRoute::class.qualifiedName ?: ""
    ),
    BottomNavItem(
        label = "AI Insights",
        selectedIcon = Icons.Filled.Insights,
        unselectedIcon = Icons.Outlined.Insights,
        route = AiInsightsRoute,
        routeName = AiInsightsRoute::class.qualifiedName ?: ""
    ),
    BottomNavItem(
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        route = ProfileRoute,
        routeName = ProfileRoute::class.qualifiedName ?: ""
    )
)

@Composable
fun MoodlyBottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.routeName
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        if (item.route == HomeRoute) {
                            // Home is the root of the tab stack: pop back to it directly.
                            // Navigating to it with restoreState would restore the stack
                            // that was saved under Home (i.e. the tab we came from).
                            navController.popBackStack(HomeRoute, inclusive = false, saveState = true)
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(HomeRoute) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = LavenderPrimary.copy(alpha = 0.2f)
                )
            )
        }
    }
}
