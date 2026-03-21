package com.fontakip.presentation.components


import com.fontakip.presentation.theme.LocalAppTheme
import com.fontakip.presentation.theme.themeProfitGreen
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fontakip.presentation.viewmodel.AssetChartData
import java.text.DecimalFormat

/**
 * Gradient Progress Bar - Modern 2026 FinTech Style
 * Features rounded caps and gradient fill
 */
@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = Color(0x6366F1FF),
    backgroundColor: Color = Color(0xFFE2E8F0),
    height: Float = 8f,
    isAnimated: Boolean = true
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(progress) {
        animationProgress = 0f
        animationProgress = progress.coerceIn(0f, 1f)
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progress_animation"
    )
    
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0x6366F1FF),
            Color(0x63E305F8)
        )
    )
    
    Canvas(modifier = modifier.height(height.dp)) {
        val barHeight = size.height
        val barWidth = size.width
        
        // Background
        drawRoundRect(
            color = backgroundColor,
            size = Size(barWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2)
        )
        
        // Progress with gradient
        if (animatedProgress > 0) {
            drawRoundRect(
                brush = gradientBrush,
                size = Size(barWidth * animatedProgress, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2)
            )
        }
    }
}

/**
 * Status Gradient Progress Bar - Profit/Loss visualization
 */
@Composable
fun StatusGradientProgressBar(
    progress: Float,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    height: Float = 8f
) {
    val progressColor = if (isPositive) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error
    val backgroundColor = progressColor.copy(alpha = 0.2f)
    
    val gradientBrush = Brush.horizontalGradient(
        colors = if (isPositive) {
            listOf(
                MaterialTheme.colorScheme.themeProfitGreen,
                MaterialTheme.colorScheme.themeProfitGreen.copy(alpha = 0.7f)
            )
        } else {
            listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    )
    
    Canvas(modifier = modifier.height(height.dp)) {
        val barHeight = size.height
        val barWidth = size.width
        
        // Background
        drawRoundRect(
            color = backgroundColor,
            size = Size(barWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2)
        )
        
        // Progress with gradient
        if (progress > 0) {
            drawRoundRect(
                brush = gradientBrush,
                size = Size(barWidth * progress.coerceIn(0f, 1f), barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2)
            )
        }
    }
}

/**
 * Dikey bar grafik bileşeni - X ekseni asset kodu, Y ekseni %
 */
@Composable
fun HorizontalBarChart(
    title: String,
    data: List<AssetChartData>,
    modifier: Modifier = Modifier,
    showNegativeValues: Boolean = true
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(data) {
        animationProgress = 0f
        animationProgress = 1f
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 800),
        label = "bar_animation"
    )
    
    // Farklı renkler
    val barColors = listOf(
        Color(0xFFBEF102),
        Color(0xFF2196F3), // Blue
        Color(0xFFFFC107), // Amber
        Color(0xFF9C27B0), // Purple
        Color(0xFF4CAF50), // Green
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF00BCD4), // Cyan
        Color(0xFFE91E63), // Pink
        Color(0xFF795548), // Brown
        Color(0xFF607D8B)  // Blue Grey
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Veri bulunmuyor",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val maxValue = data.maxOfOrNull { kotlin.math.abs(it.value) } ?: 1.0
                val decimalFormat = DecimalFormat("#.##")
                
                // Her bir asset için dikey bar
                data.forEachIndexed { index, item ->
                    VerticalBarChartItem(
                        code = item.code,
                        value = item.value,
                        maxValue = maxValue,
                        animationProgress = animatedProgress,
                        barColor = barColors[index % barColors.size],
                        showNegativeValues = showNegativeValues,
                        decimalFormat = decimalFormat
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun VerticalBarChartItem(
    code: String,
    value: Double,
    maxValue: Double,
    animationProgress: Float,
    barColor: Color,
    showNegativeValues: Boolean,
    decimalFormat: DecimalFormat
) {
    val isNegative = value < 0
    
    // Bar rengini değere göre belirle
    val actualBarColor = when {
        !showNegativeValues && isNegative -> Color.Gray
        isNegative -> MaterialTheme.colorScheme.error
        else -> barColor
    }
    
    // Değeri normalize et
    val normalizedValue = if (maxValue > 0) {
        (kotlin.math.abs(value) / maxValue * animationProgress).coerceIn(0.0, 1.0)
    } else 0.0
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Asset kodu - sola sabit
        Text(
            text = code,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(60.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Dikey bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(kotlin.math.abs(normalizedValue).toFloat())
                    .align(if (isNegative && showNegativeValues) Alignment.CenterEnd else Alignment.CenterStart)
                    .clip(RoundedCornerShape(4.dp))
                    .background(actualBarColor)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Değer - sağa sabit
        Text(
            text = "${if (value >= 0) "+" else ""}${decimalFormat.format(value)}%",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.themeProfitGreen,
            modifier = Modifier.width(65.dp),
            textAlign = TextAlign.End
        )
    }
}

/**
 * Pie Chart bileşeni
 */
@Composable
fun PieChart(
    data: List<AssetChartData>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 60f
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(data) {
        animationProgress = 0f
        animationProgress = 1f
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "pie_animation"
    )
    
    val colors = listOf(
        Color(0xFFBEF102),
        Color(0xFF2196F3), // Blue
        Color(0xFFFFC107), // Amber
        Color(0xFF9C27B0), // Purple
        Color(0xFF4CAF50), // Green
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF00BCD4), // Cyan
        Color(0xFFE91E63), // Pink
        Color(0xFF795548), // Brown
        Color(0xFF607D8B)  // Blue Grey
    )
    
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Veri bulunmuyor",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val total = data.sumOf { it.value }.coerceAtLeast(0.001)
        
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pie Chart
            Canvas(
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
            ) {
                var startAngle = -90f
                
                data.forEachIndexed { index, item ->
                    val sweepAngle = ((item.value / total) * 360f * animatedProgress).toFloat()
                    val color = colors[index % colors.size]
                    
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(
                            size.width - strokeWidth,
                            size.height - strokeWidth
                        )
                    )
                    
                    startAngle += sweepAngle
                }
            }
            
            // Legend
            Column(
                modifier = Modifier.weight(1f).padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val decimalFormat = DecimalFormat("#.#")
                
                data.take(6).forEachIndexed { index, item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(12.dp)
                                .background(colors[index % colors.size], RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${item.code} (${decimalFormat.format(item.value)}%)",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                if (data.size > 6) {
                    Text(
                        text = "+${data.size - 6} daha",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Basit dairesel pie chart (daha küçük boyutlu)
 */
@Composable
fun SimplePieChart(
    data: List<AssetChartData>,
    modifier: Modifier = Modifier,
    size: Int = 100
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(data) {
        animationProgress = 0f
        animationProgress = 1f
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "simple_pie_animation"
    )
    
    val colors = listOf(
        Color(0xFFBEF102),
        Color(0xFF2196F3),
        Color(0xFFFFC107),
        Color(0xFF9C27B0),
        Color(0xFF4CAF50),
        Color(0xFFFF5722),
        Color(0xFF00BCD4),
        Color(0xFFE91E63)
    )
    
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val total = data.sumOf { it.value }.coerceAtLeast(0.001)
        
        Canvas(
            modifier = modifier
                .width(size.dp)
                .height(size.dp)
        ) {
            var startAngle = -90f
            val strokeWidth = this.size.minDimension / 4
            
            data.forEachIndexed { index, item ->
                val sweepAngle = ((item.value / total) * 360f * animatedProgress).toFloat()
                val color = colors[index % colors.size]
                
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(
                        this.size.width - strokeWidth,
                        this.size.height - strokeWidth
                    )
                )
                
                startAngle += sweepAngle
            }
        }
    }
}
