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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.AssetPerformance
import com.fontakip.presentation.theme.CardWhite
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.LossRedLight
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.ProfitGreenLight
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.FintechProfitGreen
import com.fontakip.presentation.theme.FintechLossRed
import com.fontakip.presentation.theme.FintechProfitGreenLight
import com.fontakip.presentation.theme.FintechLossRedLight
import com.fontakip.presentation.theme.BinanceYellow
import com.fontakip.presentation.theme.BinanceDarkSurface
import com.fontakip.presentation.theme.BinanceProfitGreen
import com.fontakip.presentation.theme.BinanceLossRed
import com.fontakip.presentation.theme.BinanceProfitGreenLight
import com.fontakip.presentation.theme.BinanceLossRedLight
import com.fontakip.presentation.theme.BinanceTextSecondary
import com.fontakip.presentation.theme.BinanceGold
import com.fontakip.presentation.theme.BinanceBorder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modern 2026 FinTech Asset Card
 * 
 * Features:
 * - Elevated card (8dp shadow)
 * - 24dp rounded corners
 * - Status indicator with subtle background tints
 * - High-vibrancy Profit/Loss colors
 * - Micro-interactions (scale-down on press)
 */
@Composable
fun AssetCard(
    asset: Asset,
    totalPortfolioValue: Double,
    onInfoClick: () -> Unit,
    onBuyClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tlFormat = DecimalFormat("#,##0.00 TL")
    
    // Calculate portfolio percentage
    val portfolioPercentage = if (totalPortfolioValue > 0) {
        (asset.totalValue / totalPortfolioValue) * 100
    } else 0.0
    
    // Card press interaction - scale down effect
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "card_scale"
    )
    
    // Icon button press interactions
    val infoInteractionSource = remember { MutableInteractionSource() }
    val infoIsPressed by infoInteractionSource.collectIsPressedAsState()
    val infoScale by animateFloatAsState(
        targetValue = if (infoIsPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "info_scale"
    )
    
    val editInteractionSource = remember { MutableInteractionSource() }
    val editIsPressed by editInteractionSource.collectIsPressedAsState()
    val editScale by animateFloatAsState(
        targetValue = if (editIsPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "edit_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(cardScale)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = BinanceYellow.copy(alpha = 0.05f),
                spotColor = BinanceYellow.copy(alpha = 0.05f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BinanceDarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = asset.code,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BinanceYellow
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "- ${tlFormat.format(asset.totalValue)} (%${String.format(Locale.US, "%.2f", portfolioPercentage)})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = BinanceGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row {
                    // Info button with Binance Yellow
                    Box(
                        modifier = Modifier
                            .scale(infoScale)
                            .clip(CircleShape)
                            .background(BinanceDarkSurface.copy(alpha = 0.8f))
                            .clickable(
                                interactionSource = infoInteractionSource,
                                indication = null
                            ) { onInfoClick() }
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = BinanceGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Edit/Buy button with Binance Yellow
                    Box(
                        modifier = Modifier
                            .scale(editScale)
                            .clip(CircleShape)
                            .background(BinanceYellow.copy(alpha = 0.15f))
                            .clickable(
                                interactionSource = editInteractionSource,
                                indication = null
                            ) { onBuyClick() }
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = BinanceYellow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Three-column grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Yatırım Miktarı - Gold label
                Column {
                    Text(
                        text = "Yatırım Miktarı",
                        style = MaterialTheme.typography.labelMedium,
                        color = BinanceGold
                    )
                    Text(
                        text = tlFormat.format(asset.totalCost),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Yatırım Tarihi",
                        style = MaterialTheme.typography.labelMedium,
                        color = BinanceGold
                    )
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
                    Text(
                        text = dateFormat.format(Date(asset.purchaseDate)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Yatırım Süresi",
                        style = MaterialTheme.typography.labelMedium,
                        color = BinanceGold
                    )
                    Text(
                        text = "${asset.purchaseDate.let { 
                            val days = ((System.currentTimeMillis() - it) / (1000 * 60 * 60 * 24)).toInt()
                            days
                        }.coerceAtLeast(0)}. Gün",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Colored metric row with HIGH VIBRANCY status colors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Günlük Değişim - With subtle background tint
                val dailyChangePercent = asset.profitLossTL / asset.totalCost * 100
                val dailyChangeTL = asset.totalValue - asset.totalCost
                val isDailyPositive = dailyChangePercent >= 0

                MetricBoxModern(
                    label = "Günlük Değişim",
                    value = "${String.format(Locale.US, "%.2f", dailyChangePercent)}% / ${tlFormat.format(dailyChangeTL)}",
                    isPositive = isDailyPositive,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Kazanç/Zarar - HIGH VIBRANCY colors
                val totalGainLossPercent = asset.profitLossPercent
                val totalGainLossTL = asset.profitLossTL
                val isGainPositive = totalGainLossPercent >= 0

                MetricBoxModern(
                    label = "Kazanç/Zarar",
                    value = "${String.format(Locale.US, "%.3f", totalGainLossPercent)}% / ${tlFormat.format(totalGainLossTL)}",
                    isPositive = isGainPositive,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Data date - Dimmed
            val dataDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
            Text(
                text = "Veri Tarihi: ${dataDateFormat.format(Date(asset.lastUpdateDate))}",
                style = MaterialTheme.typography.bodySmall,
                color = BinanceTextSecondary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun MetricBoxModern(
    label: String,
    value: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    // Binance HIGH VIBRANCY colors with subtle background tint
    val backgroundColor = if (isPositive) BinanceProfitGreenLight else BinanceLossRedLight
    val textColor = if (isPositive) BinanceProfitGreen else BinanceLossRed

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = BinanceTextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}
