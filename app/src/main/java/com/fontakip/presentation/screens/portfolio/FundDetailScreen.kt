package com.fontakip.presentation.screens.portfolio


import com.fontakip.presentation.theme.LocalAppTheme
import com.fontakip.presentation.theme.themeProfitGreen
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fontakip.data.local.entities.TransactionEntity
import com.fontakip.domain.model.Asset
import com.fontakip.presentation.theme.Background
import com.fontakip.presentation.theme.CardWhite
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.getThemeBackgroundColor
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.White
import com.fontakip.data.remote.model.FundHistoryItem
import com.fontakip.presentation.viewmodel.PortfolioViewModel
import com.fontakip.presentation.screens.portfolio.TransactionHandler
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundDetailScreen(
    asset: Asset,
    viewModel: TransactionHandler,
    onBackClick: () -> Unit
) {
    // Swipe state
    var offsetX by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 200f
    
    // Chart state
    var showChart by remember { mutableStateOf(false) }
    var chartHistory by remember { mutableStateOf<List<FundHistoryItem>>(emptyList()) }
    var isLoadingChart by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf("1AY") }

    // Distribution state
    var showDistribution by remember { mutableStateOf(false) }
    var distributionData by remember { mutableStateOf<com.fontakip.data.remote.model.FundDistributionResponse?>(null) }
    var isLoadingDistribution by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = getPrimaryColor()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Title
                        Text(
                            text = asset.code,
                            color = White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (showChart) {
            FundChartDialog(
                fundCode = asset.code,
                history = chartHistory,
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it },
                onDismiss = { showChart = false }
            )
        }

        if (showDistribution) {
            FundDistributionDialog(
                fundCode = asset.code,
                distributionResponse = distributionData,
                isLoading = isLoadingDistribution,
                onDismiss = { showDistribution = false }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(getThemeBackgroundColor())
                .padding(paddingValues)
                .padding(12.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > swipeThreshold) {
                                onBackClick()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX += dragAmount
                        }
                    )
                }
                .offset { IntOffset(offsetX.roundToInt(), 0) },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fund Name (full width)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = asset.name,
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = getPrimaryColor(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Price and Changes - 3 columns
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Current Price
                    GridBox(
                        label = "Fiyat",
                        value = "${String.format(Locale.US, "%.4f", asset.currentPrice)} TL",
                        modifier = Modifier.weight(1f),
                        valueColor = MaterialTheme.colorScheme.themeProfitGreen
                    )
                    // Daily Change
                    GridBox(
                        label = "Günlük",
                        value = "${String.format(Locale.US, "%+.3f", asset.dailyChangePercent)}%",
                        valueColor = if (asset.dailyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    // Weekly Change
                    GridBox(
                        label = "Haftalık",
                        value = "${String.format(Locale.US, "%+.3f", asset.weeklyChangePercent)}%",
                        valueColor = if (asset.weeklyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Monthly Changes - 3 columns
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GridBox(
                        label = "1 Ay",
                        value = "${String.format(Locale.US, "%+.3f", asset.monthlyChangePercent)}%",
                        valueColor = if (asset.monthlyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "3 Ay",
                        value = "${String.format(Locale.US, "%+.3f", asset.threeMonthChangePercent)}%",
                        valueColor = if (asset.threeMonthChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "6 Ay",
                        value = "${String.format(Locale.US, "%+.3f", asset.sixMonthChangePercent)}%",
                        valueColor = if (asset.sixMonthChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Year Changes - 3 columns
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GridBox(
                        label = "YTD",
                        value = "${String.format(Locale.US, "%+.3f", asset.yearToDateChangePercent)}%",
                        valueColor = if (asset.yearToDateChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "1 Yıl",
                        value = "${String.format(Locale.US, "%+.3f", asset.oneYearChangePercent)}%",
                        valueColor = if (asset.oneYearChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "3 Yıl",
                        value = "${String.format(Locale.US, "%+.3f", asset.threeYearChangePercent)}%",
                        valueColor = if (asset.threeYearChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Additional Info - 3 columns
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GridBox(
                        label = "5 Yıl",
                        value = "${String.format(Locale.US, "%+.3f", asset.fiveYearChangePercent)}%",
                        valueColor = if (asset.fiveYearChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    // TEFAS Status with indicator
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TEFAS",
                                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Circle with icon
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = if (asset.tefasStatus == "EVET") ProfitGreen else LossRed,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (asset.tefasStatus == "EVET") Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = if (asset.tefasStatus == "EVET") "Aktif" else "Pasif",
                                    tint = White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    GridBox(
                        label = "Tür",
                        value = asset.fontip.ifEmpty { "-" },
                        modifier = Modifier.weight(1f),
                        valueColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }

            // Last Update
            item {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("tr", "TR"))
                GridBox(
                    label = "Son Güncelleme",
                    value = dateFormat.format(Date(asset.lastUpdateDate)),
                    modifier = Modifier.fillMaxWidth(),
                    valueColor = MaterialTheme.colorScheme.primaryContainer
                )
            }

            // Grafik Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        isLoadingChart = true
                        showChart = true
                        viewModel.getFundPriceHistory(asset.code, 12) { result ->
                            chartHistory = result
                            isLoadingChart = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getPrimaryColor()
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Grafik",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }

            // HisseDetay Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        isLoadingDistribution = true
                        showDistribution = true
                        viewModel.getFundDistribution(asset.code) { result ->
                            distributionData = result
                            isLoadingDistribution = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getPrimaryColor()
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HisseDetay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }

            // Transactions Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Son İşlemler",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = getPrimaryColor(),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                TransactionListSection(
                    fundCode = asset.code,
                    portfolioId = asset.portfolioId,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun TransactionListSection(
    fundCode: String,
    portfolioId: Long,
    viewModel: TransactionHandler
) {
    var transactions by remember { mutableStateOf<List<TransactionEntity>>(emptyList()) }
    val tlFormat = DecimalFormat("#,##0.00 TL")
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))

    LaunchedEffect(fundCode, portfolioId) {
        viewModel.getTransactionsByFundCodeAndPortfolioId(fundCode, portfolioId) { result ->
            transactions = result
        }
    }

    if (transactions.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = "Bu fon için işlem bulunmuyor",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            transactions.forEach { transaction ->
                val isBuy = transaction.transactionType == "BUY"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBuy) "AL" else "SAT",
                                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                                    color = White,
                                    modifier = Modifier
                                        .background(
                                            color = if (isBuy) ProfitGreen else LossRed,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dateFormat.format(Date(transaction.date)),
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = getPrimaryColor()
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${String.format(Locale.US, "%.0f", transaction.quantity)} Adet - ${tlFormat.format(transaction.price)}",
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.deleteTransaction(transaction) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "İşlemi Sil",
                                tint = LossRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GridBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.ifEmpty { "-" },
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                //color = if (value.toDoubleOrNull() ?: 0.0 >= 0) getThemeColors(LocalAppTheme.current).profitGreen else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FundChartDialog(
    fundCode: String,
    history: List<FundHistoryItem>,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = getThemeBackgroundColor()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$fundCode Grafiği",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = getPrimaryColor()
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = getPrimaryColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Period Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1AY", "3AY", "6AY", "1YIL").forEach { period ->
                        val isSelected = selectedPeriod == period
                        Button(
                            onClick = { onPeriodSelected(period) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) getPrimaryColor() else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) White else TextSecondary
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(text = period, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Chart Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (history.isEmpty()) {
                        CircularProgressIndicator(color = getPrimaryColor())
                    } else {
                        val filteredHistory = remember(history, selectedPeriod) {
                            filterHistory(history, selectedPeriod)
                        }
                        
                        if (filteredHistory.size < 2) {
                            Text("Veri bulunamadı", color = TextSecondary)
                        } else {
                            LineChart(
                                data = filteredHistory,
                                color = ProfitGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Info Section
                if (history.isNotEmpty()) {
                    val filteredHistory = filterHistory(history, selectedPeriod)
                    if (filteredHistory.isNotEmpty()) {
                        val minPrice = filteredHistory.minOf { it.price }
                        val maxPrice = filteredHistory.maxOf { it.price }
                        val lastPrice = filteredHistory.last().price
                        val firstPrice = filteredHistory.first().price
                        val changePercent = ((lastPrice - firstPrice) / firstPrice) * 100

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("En Düşük", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                    Text("En Yüksek", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("${String.format(Locale.US, "%.2f", minPrice)} TL", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = getPrimaryColor())
                                    Text("${String.format(Locale.US, "%.2f", maxPrice)} TL", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = getPrimaryColor())
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Dönem Getirisi", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                    Text(
                                        text = "${String.format(Locale.US, "%+.2f", changePercent)}%",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (changePercent >= 0) ProfitGreen else LossRed
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Back Button (Alternative to top close)
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Geri Dön", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun filterHistory(history: List<FundHistoryItem>, period: String): List<FundHistoryItem> {
    if (history.isEmpty()) return emptyList()
    
    // Sort history by date just in case
    val sortedHistory = history.sortedBy { it.date }
    
    val daysToTake = when (period) {
        "1AY" -> 30
        "3AY" -> 90
        "6AY" -> 180
        "1YIL" -> 365
        else -> 30
    }
    
    return if (sortedHistory.size > daysToTake) {
        sortedHistory.takeLast(daysToTake)
    } else {
        sortedHistory
    }
}

@Composable
fun LineChart(
    data: List<FundHistoryItem>,
    color: Color
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    val prices = data.map { it.price.toFloat() }
    val minPrice = prices.minOrNull() ?: 0f
    val maxPrice = prices.maxOrNull() ?: 0f
    val range = maxPrice - minPrice
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Padding for labels
        val paddingLeft = 80f
        val paddingBottom = 120f // Slightly reduced since dd.MM is shorter
        val paddingTop = 40f
        val paddingRight = 80f  // Increased to prevent clipping of the last date
        
        val chartWidth = width - paddingLeft - paddingRight
        val chartHeight = height - paddingTop - paddingBottom
        
        // Draw Y-axis Price Labels (5 grid lines)
        if (prices.isNotEmpty()) {
            val pricePoints = if (range > 0) {
                listOf(minPrice, minPrice + range * 0.25f, minPrice + range * 0.5f, minPrice + range * 0.75f, maxPrice)
            } else {
                listOf(minPrice)
            }
            
            pricePoints.forEach { price ->
                val y = if (range > 0) {
                    paddingTop + chartHeight - ((price - minPrice) / range * chartHeight)
                } else {
                    paddingTop + chartHeight / 2
                }
                
                // Grid line
                drawLine(
                    color = labelColor.copy(alpha = 0.05f),
                    start = Offset(paddingLeft, y),
                    end = Offset(width - paddingRight, y),
                    strokeWidth = 1f
                )
                
                // Label
                drawText(
                    textMeasurer = textMeasurer,
                    text = String.format(Locale.US, "%.2f", price),
                    style = textStyle.copy(color = labelColor.copy(alpha = 0.6f)),
                    topLeft = Offset(10f, y - 15f)
                )
            }
        }

        // Draw X-axis Date Labels (Start, Middle, End)
        if (data.isNotEmpty()) {
            val dateIndices = if (data.size >= 3) {
                listOf(0, data.size / 2, data.size - 1)
            } else if (data.size >= 2) {
                listOf(0, data.size - 1)
            } else {
                listOf(0)
            }
            
            dateIndices.forEach { index ->
                val item = data.getOrNull(index) ?: return@forEach
                val x = if (data.size > 1) {
                    paddingLeft + (index * (chartWidth / (data.size - 1)))
                } else {
                    paddingLeft + chartWidth / 2
                }
                
                // Extract dd.MM from yyyy-MM-dd or use original dd.MM
                val dateLabel = try {
                    if (item.date.contains("-")) {
                        val parts = item.date.split("-")
                        if (parts.size >= 3) "${parts[2]}.${parts[1]}" else item.date
                    } else if (item.date.length >= 5) {
                        item.date.substring(0, 5)
                    } else {
                        item.date
                    }
                } catch (e: Exception) {
                    item.date
                }
                
                rotate(degrees = 45f, pivot = Offset(x, height - paddingBottom + 20f)) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = dateLabel,
                        style = textStyle.copy(color = labelColor.copy(alpha = 0.6f)),
                        topLeft = Offset(x, height - paddingBottom + 20f)
                    )
                }
            }
        }

        // Draw the line path and fill
        if (data.size > 1) {
            val spacing = chartWidth / (data.size - 1)
            val path = Path()
            val fillPath = Path()
            
            prices.forEachIndexed { index, price ->
                val x = paddingLeft + (index * spacing)
                val y = if (range != 0f) {
                    paddingTop + chartHeight - ((price - minPrice) / range * chartHeight)
                } else {
                    paddingTop + chartHeight / 2
                }
                
                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, paddingTop + chartHeight)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
                
                if (index == data.size - 1) {
                    fillPath.lineTo(x, paddingTop + chartHeight)
                    fillPath.close()
                }
            }
            
            // Draw fill area
            drawPath(
                path = fillPath,
                color = color.copy(alpha = 0.1f)
            )
            
            // Draw main line
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Draw points (circles)
            prices.forEachIndexed { index, price ->
                val x = paddingLeft + (index * spacing)
                val y = if (range != 0f) {
                    paddingTop + chartHeight - ((price - minPrice) / range * chartHeight)
                } else {
                    paddingTop + chartHeight / 2
                }
                
                // Point circle
                drawCircle(
                    color = color,
                    radius = 3.dp.toPx(),
                    center = Offset(x, y)
                )
                // Inner white circle for better look
                drawCircle(
                    color = White,
                    radius = 1.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}

private fun formatAciklamaTarihi(dateStr: String?): String {
    if (dateStr.isNullOrEmpty()) return ""
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = parser.parse(dateStr)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        formatter.format(date ?: Date())
    } catch (e: Exception) {
        try {
            if (dateStr.length >= 10) {
                val parts = dateStr.substring(0, 10).split("-")
                if (parts.size == 3) {
                    "${parts[2]}.${parts[1]}.${parts[0]}"
                } else dateStr
            } else dateStr
        } catch (ex: Exception) {
            dateStr
        }
    }
}

@Composable
fun FundDistributionDialog(
    fundCode: String,
    distributionResponse: com.fontakip.data.remote.model.FundDistributionResponse?,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    val items = distributionResponse?.data?.items ?: emptyList()
    val meta = distributionResponse?.data?.meta
    val formattedDate = remember(meta?.aciklamaTarihi) {
        formatAciklamaTarihi(meta?.aciklamaTarihi)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = getThemeBackgroundColor()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$fundCode Hisse Dağılımı",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = getPrimaryColor()
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = getPrimaryColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = getPrimaryColor())
                    } else if (distributionResponse == null || items.isEmpty()) {
                        Text("Veri bulunamadı", color = TextSecondary)
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            VerticalBarChart(
                                items = items,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            
                            if (formattedDate.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Açıklama Tarihi: $formattedDate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Back Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Geri Dön", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VerticalBarChart(
    items: List<com.fontakip.data.remote.model.FundDistributionItem>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = getPrimaryColor()

    // Parse weights and sort descending
    val parsedItems = remember(items) {
        items.mapNotNull { item ->
            val weight = item.agirlik.toDoubleOrNull() ?: 0.0
            if (weight > 0) {
                item.hisseKodu to weight
            } else null
        }.sortedByDescending { it.second }
    }

    if (parsedItems.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Veri bulunamadı", color = TextSecondary)
        }
        return
    }

    val maxWeight = parsedItems.maxOf { it.second }.toFloat()
    val scrollState = rememberScrollState()
    
    val barWidthDp = 40.dp
    val spacingDp = 16.dp
    val paddingLeftDp = 50.dp
    val paddingRightDp = 16.dp
    val paddingTopDp = 24.dp
    val paddingBottomDp = 60.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        // Y-axis
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .width(paddingLeftDp)
                .align(Alignment.CenterStart)
        ) {
            val height = size.height
            val chartHeightPx = height - paddingTopDp.toPx() - paddingBottomDp.toPx()
            val levels = 5
            for (i in 0..levels) {
                val value = (maxWeight * i / levels)
                val y = paddingTopDp.toPx() + chartHeightPx - (i.toFloat() / levels * chartHeightPx)
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = String.format(Locale.US, "%.1f%%", value),
                    style = textStyle.copy(color = labelColor.copy(alpha = 0.8f)),
                    topLeft = Offset(4f, y - 12f)
                )
            }
        }

        // Scrollable X-axis & Bars
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = paddingLeftDp)
                .horizontalScroll(scrollState)
        ) {
            val totalWidthDp = (parsedItems.size * (barWidthDp.value + spacingDp.value)).dp + paddingRightDp
            
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(totalWidthDp)
            ) {
                val height = size.height
                val chartHeightPx = height - paddingTopDp.toPx() - paddingBottomDp.toPx()
                val levels = 5
                
                // Draw horizontal grid lines
                for (i in 0..levels) {
                    val y = paddingTopDp.toPx() + chartHeightPx - (i.toFloat() / levels * chartHeightPx)
                    drawLine(
                        color = labelColor.copy(alpha = 0.08f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }

                // Draw bars
                parsedItems.forEachIndexed { index, (hisseKodu, weight) ->
                    val x = (index * (barWidthDp.toPx() + spacingDp.toPx())) + spacingDp.toPx()
                    val barHeightPx = (weight.toFloat() / maxWeight) * chartHeightPx
                    val y = paddingTopDp.toPx() + chartHeightPx - barHeightPx

                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidthDp.toPx(), barHeightPx),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Draw value on top of bar
                    val valLabel = String.format(Locale.US, "%.1f%%", weight)
                    val valLayout = textMeasurer.measure(valLabel, textStyle.copy(fontWeight = FontWeight.Bold))
                    drawText(
                        textLayoutResult = valLayout,
                        color = labelColor,
                        topLeft = Offset(x + (barWidthDp.toPx() - valLayout.size.width) / 2, y - 18f)
                    )

                    // Draw rotated label below bar
                    val codeLayout = textMeasurer.measure(hisseKodu, textStyle.copy(fontWeight = FontWeight.Medium))
                    rotate(degrees = 45f, pivot = Offset(x + barWidthDp.toPx() / 2, paddingTopDp.toPx() + chartHeightPx + 10f)) {
                        drawText(
                            textLayoutResult = codeLayout,
                            color = labelColor,
                            topLeft = Offset(x + barWidthDp.toPx() / 2, paddingTopDp.toPx() + chartHeightPx + 10f)
                        )
                    }
                }
            }
        }
    }
}
