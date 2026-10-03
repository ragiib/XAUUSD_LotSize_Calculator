package com.example.xauusdlotsizecalculator.theme

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Atmospheric Terminal Background:
 * Renders an ultra-deep obsidian canvas with subtle radial illumination
 * and microscopic precision terminal grid dots, giving the depth of a hardware trading monitor.
 */
@Composable
fun TerminalBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050408))
    ) {
        // High-performance canvas for subtle atmospheric depth
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Atmospheric violet ambient glow at top-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF7E22CE).copy(alpha = 0.12f),
                        Color(0xFF3B0764).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.85f, height * 0.12f),
                    radius = width * 0.75f
                ),
                center = Offset(width * 0.85f, height * 0.12f),
                radius = width * 0.75f
            )

            // 2. Subtle deep plum atmospheric counter-glow at bottom-left
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF581C87).copy(alpha = 0.08f),
                        Color(0xFF1E1035).copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.15f, height * 0.82f),
                    radius = width * 0.65f
                ),
                center = Offset(width * 0.15f, height * 0.82f),
                radius = width * 0.65f
            )

            // 3. Faint engineering grid pattern (subtle 32dp dots)
            val step = 36.dp.toPx()
            var x = step / 2
            while (x < width) {
                var y = step / 2
                while (y < height) {
                    drawCircle(
                        color = Color(0xFFC084FC).copy(alpha = 0.035f),
                        radius = 1.0f,
                        center = Offset(x, y)
                    )
                    y += step
                }
                x += step
            }
        }

        content()
    }
}

/**
 * Terminal Glass Card Modifier:
 * Applies multi-layered translucent glass with specular top-edge sheen,
 * beveled gradient borders, and optional interactive scale response.
 */
fun Modifier.terminalGlass(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color(0xDC0C0A14),
    borderColor: Color = Color(0xFF3B1D5A),
    specularHighlight: Color = Color(0x60C084FC),
    elevation: Dp = 4.dp
): Modifier = this
    .shadow(elevation = elevation, shape = shape, spotColor = Color(0xFF7E22CE).copy(alpha = 0.25f))
    .clip(shape)
    .background(
        brush = Brush.linearGradient(
            colors = listOf(
                backgroundColor,
                Color(0xEE120E1E),
                Color(0xF508060E)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    )
    .drawBehind {
        // Specular top-edge micro-sheen (physical glass rim reflection)
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    specularHighlight,
                    specularHighlight.copy(alpha = 0.1f),
                    Color.Transparent
                )
            ),
            start = Offset(16.dp.toPx(), 1f),
            end = Offset(size.width - 16.dp.toPx(), 1f),
            strokeWidth = 1.5f
        )
    }
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                specularHighlight,
                borderColor,
                borderColor.copy(alpha = 0.3f),
                Color(0x20241C2E)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        ),
        shape = shape
    )

/**
 * Interactive Tactile Scale modifier for buttons and cards:
 * Creates smooth micro-compression when tapped.
 */
@Composable
fun Modifier.tactileClickable(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    pressedScale: Float = 0.97f,
    onClick: () -> Unit
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tactileScale"
    )

    return this
        .scale(scale)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Smooth Animated Number Roller:
 * Animates text updates with sliding transitions instead of instant jumps.
 */
@Composable
fun AnimatedNumericalValue(
    value: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            (slideInVertically { height -> height / 2 } + fadeIn(spring(stiffness = Spring.StiffnessMediumLow)))
                .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut(spring(stiffness = Spring.StiffnessMediumLow)))
                .using(SizeTransform(clip = false))
        },
        label = "numTransition",
        modifier = modifier
    ) { targetValue ->
        androidx.compose.material3.Text(
            text = targetValue,
            style = style
        )
    }
}
