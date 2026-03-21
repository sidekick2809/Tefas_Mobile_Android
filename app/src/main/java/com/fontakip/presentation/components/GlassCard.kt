package com.fontakip.presentation.components


import androidx.compose.material3.MaterialTheme
import com.fontakip.presentation.theme.LocalAppTheme
import com.fontakip.presentation.theme.themeProfitGreen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * Modern 2026 FinTech Glassmorphism Card
 * 
 * Features:
 * - 24dp rounded corners (main cards)
 * - 12dp rounded corners (chips/internal)
 * - 1dp white/light border
 * - Glass gradient background effect
 * - Elevation shadow
 * - Internal padding: 16dp
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    padding: Dp = 16.dp,
    isDarkTheme: Boolean = false,
    useGradient: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.background
    }
    
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
            )
            .clip(shape)
            .then(
                if (useGradient) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                            )
                        )
                    )
                } else {
                    Modifier.background(backgroundColor)
                }
            )
            .border(borderWidth, borderColor, shape)
            .padding(padding),
        content = content
    )
}

/**
 * Gradient Card - Primary Purple gradient background
 * For premium portfolio cards
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    padding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(gradientBrush)
            .padding(padding),
        content = content
    )
}

/**
 * Status Card with tint background
 * For profit/loss indicators
 */
@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    isPositive: Boolean = true,
    cornerRadius: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val tintColor = if (isPositive) {
        MaterialTheme.colorScheme.themeProfitGreen
    } else {
        MaterialTheme.colorScheme.error
    }
    
    val backgroundColor = tintColor.copy(alpha = 0.1f)
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        content = content
    )
}
