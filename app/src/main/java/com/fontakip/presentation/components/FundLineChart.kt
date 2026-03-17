package com.fontakip.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fontakip.data.remote.model.ChartDataPoint
import com.fontakip.data.remote.model.ChartPeriod
import com.fontakip.presentation.theme.BinanceDarkSurface
import com.fontakip.presentation.theme.BinanceLossRed
import com.fontakip.presentation.theme.BinanceProfitGreen
import com.fontakip.presentation.theme.BinanceTextPrimary
import com.fontakip.presentation.theme.BinanceTextSecondary
import com.fontakip.presentation.theme.BinanceYellowDark
import com.fontakip.presentation.theme.FintechPrimaryGradientEnd
import com.fontakip.presentation.theme.FintechPrimaryGradientStart
import java.text.DecimalFormat
import java.util.Locale

/**
 * Fund Line Chart component for displaying fund performance over time
 * Supports multiple time periods and shows price/return data
 */
@Composable
fun FundLineChart(
    dataPoints: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    selectedPeriod: ChartPeriod = ChartPeriod.ONE_MONTH,
    onPeriodSelected: (ChartPeriod) -> Unit = {},
    showGradient: Boolean = true,
    isPositive: Boolean = true
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(dataPoints) {
        animationProgress = 0f
        animationProgress = 1f
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "chart_animation"
    )
    
    val lineColor = if (isPositive) FintechPrimaryGradientStart else BinanceLossRed
    val gradientColors = if (isPositive) {
        listOf(
            FintechPrimaryGradientStart.copy(alpha = 0.4f),
            FintechPrimaryGradientStart.copy(alpha = 0.0f)
        )
    } else {
        listOf(
            BinanceLossRed.copy(alpha = 0.4f),
            BinanceLossRed.copy(alpha = 0.0f)
        )
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BinanceDarkSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Period selector
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = onPeriodSelected
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dataPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Grafik verisi bulunmuyor",
                        color = BinanceTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // Chart
                LineChartContent(
                    dataPoints = dataPoints,
                    animatedProgress = animatedProgress,
                    lineColor = lineColor,
                    gradientColors = gradientColors,
                    showGradient = showGradient,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price info
                val latestPrice = dataPoints.lastOrNull()?.value ?: 0.0
                val firstPrice = dataPoints.firstOrNull()?.value ?: 0.0
                val priceChange = if (firstPrice > 0) ((latestPrice - firstPrice) / firstPrice) * 100 else 0.0
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(Locale.US, "%.4f TL", latestPrice),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) BinanceProfitGreen else BinanceLossRed
                    )
                    
                    Text(
                        text = "${if (priceChange >= 0) "+" else ""}${String.format(Locale.US, "%.2f", priceChange)}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (priceChange >= 0) BinanceProfitGreen else BinanceLossRed
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: ChartPeriod,
    onPeriodSelected: (ChartPeriod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChartPeriod.entries.forEach { period ->
            PeriodChip(
                period = period,
                isSelected = period == selectedPeriod,
                onClick = { onPeriodSelected(period) }
            )
        }
    }
}

@Composable
private fun PeriodChip(
    period: ChartPeriod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) FintechPrimaryGradientStart 
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = period.label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else BinanceTextSecondary
        )
    }
}

@Composable
private fun LineChartContent(
    dataPoints: List<ChartDataPoint>,
    animatedProgress: Float,
    lineColor: Color,
    gradientColors: List<Color>,
    showGradient: Boolean,
    modifier: Modifier = Modifier
) {
    if (dataPoints.size < 2) return
    
    val minValue = dataPoints.minOfOrNull { it.value } ?: 0.0
    val maxValue = dataPoints.maxOfOrNull { it.value } ?: 1.0
    val valueRange = maxValue - minValue
    
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val padding = 8.dp.toPx()
        
        val chartWidth = width - (padding * 2)
        val chartHeight = height - (padding * 2)
        
        // Calculate points
        val points = dataPoints.mapIndexed { index, point ->
            val x = padding + (index.toFloat() / (dataPoints.size - 1)) * chartWidth * animatedProgress
            val normalizedValue = if (valueRange > 0) {
                (point.value - minValue) / valueRange
            } else 0.5
            val y = padding + chartHeight - (normalizedValue.toFloat() * chartHeight)
            Offset(x, y)
        }
        
        // Draw gradient fill
        if (showGradient && points.size > 1) {
            val gradientPath = Path().apply {
                moveTo(points.first().x, height - padding)
                points.forEach { point ->
                    lineTo(point.x, point.y)
                }
                lineTo(points.last().x, height - padding)
                close()
            }
            
            drawPath(
                path = gradientPath,
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = padding,
                    endY = height - padding
                )
            )
        }
        
        // Draw line
        if (points.size > 1) {
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    // Smooth curve using cubic bezier
                    val prevPoint = points[i - 1]
                    val currentPoint = points[i]
                    val controlX = (prevPoint.x + currentPoint.x) / 2
                    
                    cubicTo(
                        controlX, prevPoint.y,
                        controlX, currentPoint.y,
                        currentPoint.x, currentPoint.y
                    )
                }
            }
            
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
        
        // Draw end point dot
        val lastPoint = points.lastOrNull()
        lastPoint?.let {
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = it
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = it
            )
        }
    }
}
