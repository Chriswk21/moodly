package com.moodly.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moodly.app.ui.theme.*

/**
 * Momo expressions based on the mockup brief.
 */
enum class MomoExpression {
    HAPPY,
    CALM,
    SAD,
    WORRIED,
    CARING,
    CONFUSED,
    SLEEPY
}

/**
 * Momo - Moodly's signature mascot.
 * A small, cute, chubby, rounded blob creature with pastel lavender color.
 */
@Composable
fun MomoMascot(
    expression: MomoExpression = MomoExpression.HAPPY,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    // Gentle floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "momo_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )
    
    val starRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = if (animated) 5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_rotate"
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = floatOffset.dp)
        ) {
            val centerX = this.size.width / 2
            val centerY = this.size.height / 2 + 10f
            val bodyRadius = this.size.minDimension * 0.35f
            
            // Draw floating star above Momo
            drawStar(
                center = Offset(centerX + 30f, centerY - bodyRadius - 25f),
                radius = 12f,
                color = StarYellow,
                rotation = starRotation
            )
            
            // Draw body (main lavender blob)
            drawCircle(
                color = LavenderPrimary,
                radius = bodyRadius,
                center = Offset(centerX, centerY)
            )
            
            // Draw body shadow/highlight for 3D effect
            drawCircle(
                color = LavenderPrimary.copy(alpha = 0.7f),
                radius = bodyRadius * 0.9f,
                center = Offset(centerX - 5f, centerY - 5f)
            )
            
            // Draw tiny arms
            val armSize = bodyRadius * 0.15f
            // Left arm
            drawCircle(
                color = LavenderPrimary,
                radius = armSize,
                center = Offset(centerX - bodyRadius * 0.8f, centerY + 10f)
            )
            // Right arm
            drawCircle(
                color = LavenderPrimary,
                radius = armSize,
                center = Offset(centerX + bodyRadius * 0.8f, centerY + 10f)
            )
            
            // Draw tiny feet
            val footSize = bodyRadius * 0.12f
            // Left foot
            drawCircle(
                color = LavenderPrimary,
                radius = footSize,
                center = Offset(centerX - bodyRadius * 0.3f, centerY + bodyRadius * 0.9f)
            )
            // Right foot
            drawCircle(
                color = LavenderPrimary,
                radius = footSize,
                center = Offset(centerX + bodyRadius * 0.3f, centerY + bodyRadius * 0.9f)
            )
            
            // Draw blush on cheeks
            val blushColor = BlushPink
            val blushRadius = bodyRadius * 0.12f
            // Left blush
            drawCircle(
                color = blushColor,
                radius = blushRadius,
                center = Offset(centerX - bodyRadius * 0.45f, centerY + 5f)
            )
            // Right blush
            drawCircle(
                color = blushColor,
                radius = blushRadius,
                center = Offset(centerX + bodyRadius * 0.45f, centerY + 5f)
            )
            
            // Draw eyes based on expression
            drawMomoEyes(
                expression = expression,
                centerX = centerX,
                centerY = centerY - 10f,
                bodyRadius = bodyRadius
            )
            
            // Draw mouth based on expression
            drawMomoMouth(
                expression = expression,
                centerX = centerX,
                centerY = centerY + 15f,
                bodyRadius = bodyRadius
            )
        }
    }
}

private fun DrawScope.drawStar(
    center: Offset,
    radius: Float,
    color: Color,
    rotation: Float = 0f
) {
    val path = Path()
    val innerRadius = radius * 0.4f
    val angleOffset = Math.toRadians(rotation.toDouble()).toFloat()
    
    for (i in 0 until 5) {
        val outerAngle = angleOffset + Math.PI / 2 + i * 2 * Math.PI / 5
        val innerAngle = outerAngle + Math.PI / 5
        
        val outerX = center.x + radius * kotlin.math.cos(outerAngle).toFloat()
        val outerY = center.y - radius * kotlin.math.sin(outerAngle).toFloat()
        
        val innerX = center.x + innerRadius * kotlin.math.cos(innerAngle).toFloat()
        val innerY = center.y - innerRadius * kotlin.math.sin(innerAngle).toFloat()
        
        if (i == 0) {
            path.moveTo(outerX, outerY)
        } else {
            path.lineTo(outerX, outerY)
        }
        path.lineTo(innerX, innerY)
    }
    path.close()
    
    drawPath(path, color, style = Fill)
}

private fun DrawScope.drawMomoEyes(
    expression: MomoExpression,
    centerX: Float,
    centerY: Float,
    bodyRadius: Float
) {
    val eyeSpacing = bodyRadius * 0.25f
    val eyeRadius = bodyRadius * 0.08f
    val eyeColor = TextDark
    
    when (expression) {
        MomoExpression.HAPPY, MomoExpression.CALM, MomoExpression.CARING -> {
            // Bright, happy eyes
            drawCircle(eyeColor, eyeRadius, Offset(centerX - eyeSpacing, centerY))
            drawCircle(eyeColor, eyeRadius, Offset(centerX + eyeSpacing, centerY))
            // Eye shine
            drawCircle(Color.White, eyeRadius * 0.4f, Offset(centerX - eyeSpacing - 2f, centerY - 2f))
            drawCircle(Color.White, eyeRadius * 0.4f, Offset(centerX + eyeSpacing - 2f, centerY - 2f))
        }
        MomoExpression.SAD -> {
            // Slightly lowered, sad eyes
            drawCircle(eyeColor, eyeRadius, Offset(centerX - eyeSpacing, centerY + 3f))
            drawCircle(eyeColor, eyeRadius, Offset(centerX + eyeSpacing, centerY + 3f))
        }
        MomoExpression.WORRIED -> {
            // Concerned eyes
            drawCircle(eyeColor, eyeRadius, Offset(centerX - eyeSpacing, centerY))
            drawCircle(eyeColor, eyeRadius, Offset(centerX + eyeSpacing, centerY))
            // Worried eyebrows (small arcs above eyes)
        }
        MomoExpression.CONFUSED -> {
            // Puzzled eyes - one higher than other
            drawCircle(eyeColor, eyeRadius, Offset(centerX - eyeSpacing, centerY - 2f))
            drawCircle(eyeColor, eyeRadius, Offset(centerX + eyeSpacing, centerY + 2f))
        }
        MomoExpression.SLEEPY -> {
            // Half-closed eyes (horizontal lines)
            drawLine(
                eyeColor,
                Offset(centerX - eyeSpacing - eyeRadius, centerY),
                Offset(centerX - eyeSpacing + eyeRadius, centerY),
                strokeWidth = 3f
            )
            drawLine(
                eyeColor,
                Offset(centerX + eyeSpacing - eyeRadius, centerY),
                Offset(centerX + eyeSpacing + eyeRadius, centerY),
                strokeWidth = 3f
            )
        }
    }
}

private fun DrawScope.drawMomoMouth(
    expression: MomoExpression,
    centerX: Float,
    centerY: Float,
    bodyRadius: Float
) {
    val mouthWidth = bodyRadius * 0.2f
    val mouthColor = TextDark
    
    when (expression) {
        MomoExpression.HAPPY, MomoExpression.CARING -> {
            // Cheerful smile - curved line
            val path = Path().apply {
                moveTo(centerX - mouthWidth, centerY)
                quadraticBezierTo(centerX, centerY + 10f, centerX + mouthWidth, centerY)
            }
            drawPath(path, mouthColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
        }
        MomoExpression.CALM -> {
            // Gentle smile
            val path = Path().apply {
                moveTo(centerX - mouthWidth * 0.8f, centerY)
                quadraticBezierTo(centerX, centerY + 6f, centerX + mouthWidth * 0.8f, centerY)
            }
            drawPath(path, mouthColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
        }
        MomoExpression.SAD -> {
            // Small frown
            val path = Path().apply {
                moveTo(centerX - mouthWidth * 0.7f, centerY + 5f)
                quadraticBezierTo(centerX, centerY - 3f, centerX + mouthWidth * 0.7f, centerY + 5f)
            }
            drawPath(path, mouthColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
        }
        MomoExpression.WORRIED -> {
            // Small open mouth (surprise/concern)
            drawCircle(mouthColor, 4f, Offset(centerX, centerY))
        }
        MomoExpression.CONFUSED -> {
            // Sideways squiggle
            drawLine(
                mouthColor,
                Offset(centerX - mouthWidth * 0.5f, centerY),
                Offset(centerX + mouthWidth * 0.5f, centerY + 3f),
                strokeWidth = 2f
            )
        }
        MomoExpression.SLEEPY -> {
            // Small o mouth
            drawCircle(mouthColor, 5f, Offset(centerX, centerY), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MomoMascotPreview() {
    MoodlyTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            MomoMascot(expression = MomoExpression.HAPPY, size = 80.dp, animated = false)
            MomoMascot(expression = MomoExpression.SAD, size = 80.dp, animated = false)
            MomoMascot(expression = MomoExpression.WORRIED, size = 80.dp, animated = false)
        }
    }
}
