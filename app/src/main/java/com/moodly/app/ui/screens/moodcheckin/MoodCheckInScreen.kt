package com.moodly.app.ui.screens.moodcheckin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moodly.app.domain.model.MoodFactor
import com.moodly.app.domain.model.MoodType
import com.moodly.app.domain.model.toMoodRatingLabel
import com.moodly.app.ui.components.MomoExpression
import com.moodly.app.ui.components.MomoMascot
import com.moodly.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodCheckInScreen(
    onBack: () -> Unit,
    onCheckInSaved: () -> Unit,
    viewModel: MoodCheckInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.setOnSaveComplete(onCheckInSaved)
    }
    
    MoodCheckInContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun MoodCheckInContent(
    uiState: MoodCheckInUiState,
    onAction: (MoodCheckInAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        // Outer app Scaffold already applies system bar insets
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(
                        text = "Daily Check-in",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Momo Message Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LavenderPrimary.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MomoMascot(
                        expression = getMomoExpressionForMood(uiState.selectedMood),
                        size = 50.dp,
                        animated = false
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Momo says",
                            style = MaterialTheme.typography.labelSmall,
                            color = LavenderDeep
                        )
                        Text(
                            text = "\"${uiState.momoMessage}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDark
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Mood Selection
            Text(
                text = "How are you feeling today?",
                style = MaterialTheme.typography.titleMedium,
                color = TextDark,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mood Grid (2 rows x 3 columns)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MoodOption(
                        moodType = MoodType.HAPPY,
                        isSelected = uiState.selectedMood == MoodType.HAPPY,
                        onClick = { onAction(MoodCheckInAction.SelectMood(MoodType.HAPPY)) },
                        modifier = Modifier.weight(1f)
                    )
                    MoodOption(
                        moodType = MoodType.CALM,
                        isSelected = uiState.selectedMood == MoodType.CALM,
                        onClick = { onAction(MoodCheckInAction.SelectMood(MoodType.CALM)) },
                        modifier = Modifier.weight(1f)
                    )
                    MoodOption(
                        moodType = MoodType.NEUTRAL,
                        isSelected = uiState.selectedMood == MoodType.NEUTRAL,
                        onClick = { onAction(MoodCheckInAction.SelectMood(MoodType.NEUTRAL)) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MoodOption(
                        moodType = MoodType.ANXIOUS,
                        isSelected = uiState.selectedMood == MoodType.ANXIOUS,
                        onClick = { onAction(MoodCheckInAction.SelectMood(MoodType.ANXIOUS)) },
                        modifier = Modifier.weight(1f)
                    )
                    MoodOption(
                        moodType = MoodType.SAD,
                        isSelected = uiState.selectedMood == MoodType.SAD,
                        onClick = { onAction(MoodCheckInAction.SelectMood(MoodType.SAD)) },
                        modifier = Modifier.weight(1f)
                    )
                    MoodOption(
                        moodType = MoodType.STRESSED,
                        isSelected = uiState.selectedMood == MoodType.STRESSED,
                        onClick = { onAction(MoodCheckInAction.SelectMood(MoodType.STRESSED)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Mood Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood Rating",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${uiState.moodRating} — ${uiState.moodRating.toMoodRatingLabel()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = LavenderDeep
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Rating Selector (1-5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..5).forEach { rating ->
                    RatingButton(
                        rating = rating,
                        isSelected = uiState.moodRating == rating,
                        onClick = { onAction(MoodCheckInAction.SetRating(rating)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Very low", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("Okay", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Text("Very good", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Mood Factors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "What's affecting your mood?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Optional",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Factor Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoodFactor.entries.forEach { factor ->
                    FactorChip(
                        factor = factor,
                        isSelected = factor in uiState.selectedFactors,
                        onClick = { onAction(MoodCheckInAction.ToggleFactor(factor)) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Journal Note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Want to tell Momo a little more?",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = LavenderDeep
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Private",
                        style = MaterialTheme.typography.labelSmall,
                        color = LavenderDeep
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = uiState.journalNote,
                onValueChange = { onAction(MoodCheckInAction.SetJournalNote(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = "What's on your mind today? (Optional — no pressure to write)",
                        color = TextMuted
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LavenderDeep,
                    unfocusedBorderColor = LavenderPrimary.copy(alpha = 0.5f),
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save Button
            Button(
                onClick = { onAction(MoodCheckInAction.SaveCheckIn) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LavenderDeep
                ),
                enabled = uiState.selectedMood != null && !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Check-in",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MoodOption(
    moodType: MoodType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Opaque lavender tint when selected (matches mockup); no shadow bleeding through
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFF3EEFC) else Surface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "mood_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) LavenderDeep else LavenderPrimary.copy(alpha = 0.3f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "mood_border"
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = getMoodEmoji(moodType),
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = moodType.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) LavenderDeep else TextDark,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun RatingButton(
    rating: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) LavenderDeep else Surface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "rating_bg"
    )
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) LavenderDeep else LavenderPrimary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) Color.White else TextDark,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun FactorChip(
    factor: MoodFactor,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) LavenderPrimary.copy(alpha = 0.3f) else Surface,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "factor_bg"
    )
    
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isSelected) LavenderDeep else LavenderPrimary.copy(alpha = 0.5f)
            )
        )
    ) {
        Text(
            text = if (isSelected) "✓ ${factor.displayName}" else factor.displayName,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) LavenderDeep else TextDark
        )
    }
}

// Helper functions
private fun getMoodColor(moodType: MoodType): Color = when (moodType) {
    MoodType.HAPPY -> MoodHappy
    MoodType.CALM -> MoodCalm
    MoodType.NEUTRAL -> MoodNeutral
    MoodType.ANXIOUS -> MoodAnxious
    MoodType.SAD -> MoodSad
    MoodType.STRESSED -> MoodStressed
}

private fun getMoodEmoji(moodType: MoodType): String = when (moodType) {
    MoodType.HAPPY -> "🌸"
    MoodType.CALM -> "🌿"
    MoodType.NEUTRAL -> "☁️"
    MoodType.ANXIOUS -> "😰"
    MoodType.SAD -> "🌧️"
    MoodType.STRESSED -> "⚡"
}

private fun getMomoExpressionForMood(mood: MoodType?): MomoExpression = when (mood) {
    MoodType.HAPPY -> MomoExpression.HAPPY
    MoodType.CALM -> MomoExpression.CALM
    MoodType.NEUTRAL -> MomoExpression.CALM
    MoodType.ANXIOUS -> MomoExpression.WORRIED
    MoodType.SAD -> MomoExpression.SAD
    MoodType.STRESSED -> MomoExpression.WORRIED
    null -> MomoExpression.CALM
}

@Preview(showBackground = true)
@Composable
private fun MoodCheckInContentPreview() {
    MoodlyTheme {
        MoodCheckInContent(
            uiState = MoodCheckInUiState(
                selectedMood = MoodType.STRESSED,
                moodRating = 2,
                selectedFactors = setOf(MoodFactor.ACADEMIC_PRESSURE, MoodFactor.ASSIGNMENT_OVERLOAD),
                momoMessage = "That sounds like a lot. Let's take it one step at a time."
            ),
            onAction = {},
            onBack = {}
        )
    }
}
