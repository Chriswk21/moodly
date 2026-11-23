package com.moodly.app.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moodly.app.R
import com.moodly.app.ui.theme.MoodlyTheme

/** Expressions available in Moodly's Momo mascot artwork. */
enum class MomoExpression {
    HAPPY,
    CALM,
    SAD,
    WORRIED,
    CARING,
    CONFUSED,
    SLEEPY,
    STRESSED,
    OVERWHELMED
}

/**
 * Moodly's Momo mascot using the supplied expression artwork.
 *
 * The artwork includes Momo's expression-specific accessories, such as the
 * star, heart, sleep marks, sweat drops, and lightning bolt.
 */
@Composable
fun MomoMascot(
    expression: MomoExpression = MomoExpression.HAPPY,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "momo_float")
    val translationY by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) -5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "momo_translation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = expression.imageResource()),
            contentDescription = "Momo ${expression.name.lowercase()} mascot",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = translationY.dp)
        )
    }
}

private fun MomoExpression.imageResource(): Int = when (this) {
    MomoExpression.HAPPY -> R.drawable.momo_happy
    MomoExpression.CALM -> R.drawable.momo_calm
    MomoExpression.SAD -> R.drawable.momo_sad
    MomoExpression.WORRIED -> R.drawable.momo_worried
    MomoExpression.CARING -> R.drawable.momo_caring
    MomoExpression.CONFUSED -> R.drawable.momo_confused
    MomoExpression.SLEEPY -> R.drawable.momo_sleepy
    MomoExpression.STRESSED -> R.drawable.momo_stressed
    MomoExpression.OVERWHELMED -> R.drawable.momo_overwhelmed
}

@Preview(showBackground = true)
@Composable
private fun MomoMascotPreview() {
    MoodlyTheme {
        MomoMascot(
            expression = MomoExpression.HAPPY,
            size = 120.dp,
            animated = false
        )
    }
}
