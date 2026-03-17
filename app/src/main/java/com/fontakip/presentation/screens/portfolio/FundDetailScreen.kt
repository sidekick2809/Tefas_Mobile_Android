package com.fontakip.presentation.screens.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.fontakip.presentation.viewmodel.PortfolioViewModel
import com.fontakip.presentation.screens.portfolio.TransactionHandler
import com.fontakip.presentation.theme.BinanceLossRed
import com.fontakip.presentation.theme.BinanceProfitGreen
import com.fontakip.presentation.theme.BinanceTextPrimary
import com.fontakip.presentation.theme.BinanceTextSecondary
import com.fontakip.presentation.theme.BinanceYellowDark
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
                        valueColor = BinanceProfitGreen
                    )
                    // Daily Change
                    GridBox(
                        label = "Günlük",
                        value = "${String.format(Locale.US, "%+.3f", asset.dailyChangePercent)}%",
                        valueColor = if (asset.dailyChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        modifier = Modifier.weight(1f)
                    )
                    // Weekly Change
                    GridBox(
                        label = "Haftalık",
                        value = "${String.format(Locale.US, "%+.3f", asset.weeklyChangePercent)}%",
                        valueColor = if (asset.weeklyChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
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
                        valueColor = if (asset.monthlyChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "3 Ay",
                        value = "${String.format(Locale.US, "%+.3f", asset.threeMonthChangePercent)}%",
                        valueColor = if (asset.threeMonthChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "6 Ay",
                        value = "${String.format(Locale.US, "%+.3f", asset.sixMonthChangePercent)}%",
                        valueColor = if (asset.sixMonthChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
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
                        valueColor = if (asset.yearToDateChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "1 Yıl",
                        value = "${String.format(Locale.US, "%+.3f", asset.oneYearChangePercent)}%",
                        valueColor = if (asset.oneYearChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        modifier = Modifier.weight(1f)
                    )
                    GridBox(
                        label = "3 Yıl",
                        value = "${String.format(Locale.US, "%+.3f", asset.threeYearChangePercent)}%",
                        valueColor = if (asset.threeYearChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
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
                        valueColor = if (asset.fiveYearChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
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
                                color = BinanceTextSecondary
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
                        valueColor = BinanceYellowDark
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
                    valueColor = BinanceYellowDark
                )
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
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun TransactionListSection(
    fundCode: String,
    viewModel: TransactionHandler
) {
    var transactions by remember { mutableStateOf<List<TransactionEntity>>(emptyList()) }
    val tlFormat = DecimalFormat("#,##0.00 TL")
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))

    LaunchedEffect(fundCode) {
        viewModel.getTransactionsByFundCode(fundCode) { result ->
            transactions = result
        }
    }

    if (transactions.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BinanceYellowDark)
        ) {
            Text(
                text = "Bu fon için işlem bulunmuyor",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = BinanceTextPrimary,
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
                //color = if (value.toDoubleOrNull() ?: 0.0 >= 0) BinanceProfitGreen else BinanceLossRed,
                textAlign = TextAlign.Center
            )
        }
    }
}
