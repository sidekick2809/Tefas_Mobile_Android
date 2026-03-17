package com.fontakip.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fontakip.R
import com.fontakip.domain.model.PortfolioSummary
import com.fontakip.presentation.theme.CardWhite
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.FintechPrimaryGradientStart
import com.fontakip.presentation.theme.FintechPrimaryGradientEnd
import com.fontakip.presentation.theme.FintechProfitGreen
import com.fontakip.presentation.theme.FintechLossRed
import com.fontakip.presentation.theme.FintechGlassBackgroundLight
import com.fontakip.presentation.theme.FintechGlassBorder
import com.fontakip.presentation.theme.BinanceYellow
import com.fontakip.presentation.theme.BinanceDarkSurface
import com.fontakip.presentation.theme.BinanceProfitGreen
import com.fontakip.presentation.theme.BinanceLossRed
import com.fontakip.presentation.theme.BinanceTextSecondary
import com.fontakip.presentation.theme.BinanceGold
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modern 2026 FinTech Portfolio Summary Card
 * 
 * Features:
 * - Glassmorphism effect (24dp rounded, 1dp border)
 * - Gradient background
 * - Dominant Total Balance display
 * - Dimmed secondary info (0.6f alpha)
 */
@Composable
fun PortfolioSummaryCard(
    summary: PortfolioSummary,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tlFormat = DecimalFormat("#,##0.00 TL")
    
    // Button press interaction - scale down effect
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )

    // Binance dark gradient - subtle yellow tint
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            BinanceDarkSurface,
            BinanceDarkSurface.copy(alpha = 0.95f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = BinanceYellow.copy(alpha = 0.1f),
                spotColor = BinanceYellow.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(gradientBrush)
            .background(
                color = BinanceDarkSurface,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        // Arkaplan Resmi - Yarı Transparan
        Image(
            painter = painterResource(id = R.drawable.finance_background_yellow),
            contentDescription = "Finans Arkaplan",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop,
            alpha = 0.35f
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Portfolio Value Label - Binance Yellow
            Text(
                text = "Portföy Değeri",
                style = MaterialTheme.typography.bodyMedium,
                color = BinanceYellow
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            // Total Balance - DOMINANT ELEMENT (White)
            Text(
                text = tlFormat.format(summary.totalValue),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Three columns row with chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Alış Maliyeti - Gold label
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BinanceDarkSurface.copy(alpha = 0.8f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Alış Maliyeti",
                        style = MaterialTheme.typography.labelMedium,
                        color = BinanceGold
                    )
                    Text(
                        text = tlFormat.format(summary.totalCost),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                
                // Kar/Zarar (TL)
                val isProfitPositive = summary.profitLossTL >= 0
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isProfitPositive) BinanceProfitGreen.copy(alpha = 0.1f)
                            else BinanceLossRed.copy(alpha = 0.1f)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Kar/Zarar (TL)",
                        style = MaterialTheme.typography.labelMedium,
                        color = BinanceTextSecondary
                    )
                    Text(
                        text = "${if (summary.profitLossTL >= 0) "+" else ""}${tlFormat.format(summary.profitLossTL)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isProfitPositive) BinanceProfitGreen else BinanceLossRed
                    )
                }
                
                // Kar/Zaraar (%)
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isProfitPositive) BinanceProfitGreen.copy(alpha = 0.1f)
                            else BinanceLossRed.copy(alpha = 0.1f)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Kar/Zarar (%)",
                        style = MaterialTheme.typography.labelMedium,
                        color = BinanceTextSecondary
                    )
                    Text(
                        text = "${if (summary.profitLossPercent >= 0) "+" else ""}${String.format(Locale.US, "%.3f", summary.profitLossPercent)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isProfitPositive) BinanceProfitGreen else BinanceLossRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Portfolio Daily section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isDailyPositive = summary.dailyChangePercent >= 0
                Text(
                    text = "Portföy (Günlük): ${String.format(Locale.US, "%.3f", summary.dailyChangePercent)}% / ${tlFormat.format(summary.dailyChangeTL)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDailyPositive) BinanceProfitGreen else BinanceLossRed
                )
                
                // Add button with Binance Yellow
                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(CircleShape)
                        .background(BinanceYellow)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onAddClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Last update timestamp - Dimmed
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("tr", "TR"))
            Text(
                text = "Son Güncelleme Kontrolü: ${dateFormat.format(Date(summary.lastUpdateTime))}",
                style = MaterialTheme.typography.bodySmall,
                color = BinanceTextSecondary
            )
        }
    }
}
