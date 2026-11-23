package com.moodly.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Lavender-tinted shadow colours: softer than Material's default grey. */
private val ShadowAmbient = Color(0x147663A8)
private val ShadowSpot = Color(0x1F7663A8)

/** Very light lavender-grey tile used for rows inside white cards. */
val MoodlyTile = Color(0xFFF7F5FB)
val MoodlyTileBorder = Color(0xFFEDE8F6)

/**
 * Soft, lavender-tinted shadow used for all white cards in Moodly.
 * Only apply to opaque surfaces – shadows show through translucent ones.
 */
fun Modifier.moodlySoftShadow(
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 10.dp
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = ShadowAmbient,
    spotColor = ShadowSpot
)

/** White rounded card with the soft Moodly shadow and 16dp inner padding. */
@Composable
fun MoodlyCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    color: Color = Color.White,
    contentPadding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.moodlySoftShadow(shape),
        shape = shape,
        color = color
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}
