package com.fontakip.presentation.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fontakip.presentation.theme.FintechGlassBorder
import com.fontakip.presentation.theme.FintechGlassBackgroundLight
import com.fontakip.presentation.theme.FintechGlassBackgroundDark
import com.fontakip.presentation.theme.FintechPrimaryGradientStart
import com.fontakip.presentation.theme.FintechPrimaryGradientEnd

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
        FintechGlassBackgroundDark
    } else {
        FintechGlassBackgroundLight
    }
    
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.1f)
    } else {
        FintechGlassBorder
    }
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(shape)
            .then(
                if (useGradient) {
                    Modifier.background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                FintechPrimaryGradientStart.copy(alpha = 0.1f),
                                FintechPrimaryGradientEnd.copy(alpha = 0.05f)
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
            FintechPrimaryGradientStart,
            FintechPrimaryGradientEnd
        )
    )
    
    val shape = RoundedCornerShape(cornerRadius)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = FintechPrimaryGradientStart.copy(alpha = 0.3f),
                spotColor = FintechPrimaryGradientStart.copy(alpha = 0.3f)
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
        com.fontakip.presentation.theme.FintechProfitGreen
    } else {
        com.fontakip.presentation.theme.FintechLossRed
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
