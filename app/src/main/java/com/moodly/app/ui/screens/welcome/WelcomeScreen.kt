package com.moodly.app.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moodly.app.ui.components.MomoMascot
import com.moodly.app.ui.components.MomoExpression
import com.moodly.app.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onAlreadyHaveAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Background,
                        LavenderPrimary.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // App Name with Star
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "✦",
                fontSize = 24.sp,
                color = StarYellow
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Moodly",
                style = MaterialTheme.typography.headlineLarge,
                color = LavenderDeep,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.weight(0.3f))
        
        // Momo Mascot
        MomoMascot(
            expression = MomoExpression.HAPPY,
            size = 180.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Speech Bubble
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(LavenderPrimary.copy(alpha = 0.2f))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Hi! I'm Momo ✨",
                style = MaterialTheme.typography.bodyLarge,
                color = LavenderDeep,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Main Headline
        Text(
            text = "Your little companion for a\nbalanced student life.",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = TextDark,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 32.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Subtitle
        Text(
            text = "Track your mood, manage your tasks, and\nunderstand your stress patterns.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = TextMuted
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Feature Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeatureChip(emoji = "💜", text = "Daily Mood")
            FeatureChip(emoji = "📚", text = "Study Tasks")
            FeatureChip(emoji = "✨", text = "AI Insights")
        }
        
        Spacer(modifier = Modifier.weight(0.5f))
        
        // Get Started Button
        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LavenderDeep
            )
        ) {
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Already have account
        OutlinedButton(
            onClick = onAlreadyHaveAccount,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = LavenderDeep
            )
        ) {
            Text(
                text = "I already have an account",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FeatureChip(
    emoji: String,
    text: String
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = TextDark
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    MoodlyTheme {
        WelcomeScreen(
            onGetStarted = {},
            onAlreadyHaveAccount = {}
        )
    }
}
