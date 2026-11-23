package com.moodly.app.ui.screens.profile

import com.moodly.app.ui.components.moodlySoftShadow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moodly.app.ui.components.MomoExpression
import com.moodly.app.ui.components.MomoMascot
import com.moodly.app.ui.theme.*

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State for toggles
    var dailyMoodReminder by remember { mutableStateOf(true) }
    var upcomingDeadlineReminder by remember { mutableStateOf(true) }
    var studyBreakNudges by remember { mutableStateOf(false) }
    var includeJournalInAi by remember { mutableStateOf(true) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header
        Text(
            text = "Profile & Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = TextDark,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // User Profile Card
        Card(
            modifier = Modifier.moodlySoftShadow(RoundedCornerShape(16.dp), 6.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar (using Momo as placeholder)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(LavenderPrimary.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    MomoMascot(
                        expression = MomoExpression.HAPPY,
                        size = 50.dp,
                        animated = false
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Alex Rivera",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "alex.rivera@university.edu",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LavenderPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "B.S. Computer Science · Year 3",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = LavenderDeep
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Reminders & Notifications Section
        Text(
            text = "Reminders & Notifications",
            style = MaterialTheme.typography.titleMedium,
            color = TextDark,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.moodlySoftShadow(RoundedCornerShape(16.dp), 6.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                SettingsToggleItem(
                    title = "Daily Mood Check-in",
                    subtitle = "\"Take a moment for yourself — how are you feeling today?\"",
                    checked = dailyMoodReminder,
                    onCheckedChange = { dailyMoodReminder = it }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Background
                )
                
                SettingsToggleItem(
                    title = "Upcoming Deadline Reminder",
                    subtitle = "Gentle reminder 24 hours before tasks are due",
                    checked = upcomingDeadlineReminder,
                    onCheckedChange = { upcomingDeadlineReminder = it }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Background
                )
                
                SettingsToggleItem(
                    title = "Study-Break Nudges",
                    subtitle = "Optional rest reminder during busy study weeks",
                    checked = studyBreakNudges,
                    onCheckedChange = { studyBreakNudges = it }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Privacy & Data Section
        Text(
            text = "Privacy & Data",
            style = MaterialTheme.typography.titleMedium,
            color = TextDark,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier.moodlySoftShadow(RoundedCornerShape(16.dp), 6.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                SettingsToggleItem(
                    title = "Include Journal Notes in AI Summary",
                    subtitle = "Only data you enter is used to generate wellness insights",
                    checked = includeJournalInAi,
                    onCheckedChange = { includeJournalInAi = it }
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Background
                )
                
                SettingsLinkItem(
                    title = "Export My Wellness & Task Data",
                    icon = Icons.Filled.Download,
                    onClick = { /* TODO: Export data */ }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // About Moodly Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoftBlue.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About Moodly",
                    style = MaterialTheme.typography.titleSmall,
                    color = LavenderDeep,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Moodly is a student wellness and self-awareness companion designed to help you reflect on your mood and academic workload. It does not provide medical or psychological diagnoses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Logout Button
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = StressVeryHigh
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(StressVeryHigh.copy(alpha = 0.5f))
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextDark
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = LavenderDeep,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = LavenderPrimary.copy(alpha = 0.3f),
                uncheckedBorderColor = LavenderPrimary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingsLinkItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = LavenderDeep
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LavenderDeep,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MoodlyTheme {
        ProfileScreen(onLogout = {})
    }
}
