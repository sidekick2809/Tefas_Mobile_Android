package com.fontakip.presentation.screens.fonverileri

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.Portfolio
import com.fontakip.presentation.components.CustomDatePickerDialog
import com.fontakip.presentation.screens.portfolio.FundDetailScreen
import com.fontakip.presentation.screens.portfolio.FundTransactionScreen
import com.fontakip.presentation.theme.Babyblue
import com.fontakip.presentation.theme.Background
import com.fontakip.presentation.theme.BinanceDarkSurface
import com.fontakip.presentation.theme.BinanceLossRed
import com.fontakip.presentation.theme.BinanceProfitGreen
import com.fontakip.presentation.theme.BinanceTextPrimary
import com.fontakip.presentation.theme.BinanceTextSecondary
import com.fontakip.presentation.theme.BinanceYellow
import com.fontakip.presentation.theme.BinanceYellowDark
import com.fontakip.presentation.theme.CardBackground
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.PrimaryBlueLight
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.getPrimaryContainerColor
import com.fontakip.presentation.theme.getThemeBackgroundColor
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.White
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.viewmodel.FonVerileriViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Main screen for viewing and managing investment fund data (TEFAS).
 *
 * This screen provides a comprehensive interface for:
 * - Filtering funds by type (Investment, Pension, or All).
 * - Searching for specific funds by their code or name.
 * - Refreshing fund data and managing date ranges for performance calculations.
 * - Displaying a list of funds with current prices and performance percentages (Daily, Weekly, Monthly).
 * - Facilitating fund transactions (buy/sell) and viewing detailed fund information.
 * - Managing favorites within specific portfolios.
 *
 * @param viewModel The [FonVerileriViewModel] that manages the UI state and business logic for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FonVerileriScreen(
    viewModel: FonVerileriViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var selectedFundForTransaction by remember { mutableStateOf<Asset?>(null) }
    var selectedFundForDetail by remember { mutableStateOf<Asset?>(null) }

    // Pull-to-refresh state
    val pullToRefreshState = rememberPullToRefreshState()

    // Handle pull-to-refresh
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            viewModel.fetchTefasData()
        }
    }

    // Update refresh state when loading completes
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = BinanceDarkSurface
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Title - centered
                    Text(
                        text = "FON VERİLERİ",
                        fontWeight = FontWeight.Bold,
                        color = BinanceYellow,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Center)

                    )

                    // Settings button on the right
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Ayarlar",
                            tint = BinanceYellow
                        )
                    }
                }
            }
        }
    ) {
        paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                    .background(BinanceDarkSurface, RoundedCornerShape(16.dp))
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Filter Section - will scroll with the list
            item {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
            // Tür Selection (YAT / EMK) - boxed and compact
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BinanceDarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TÜR:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BinanceYellowDark,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = uiState.selectedFontip == "YAT",
                        onClick = { viewModel.setFontip("YAT") },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = BinanceTextPrimary
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YAT",
                        style = MaterialTheme.typography.bodySmall,
                        color = BinanceYellowDark
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = uiState.selectedFontip == "EMK",
                        onClick = { viewModel.setFontip("EMK") },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = BinanceTextPrimary
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EMK",
                        style = MaterialTheme.typography.bodySmall,
                        color = BinanceYellowDark
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = uiState.selectedFontip == "ALL",
                        onClick = { viewModel.setFontip("ALL") },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = BinanceTextPrimary
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HEPSİ",
                        style = MaterialTheme.typography.bodySmall,
                        color = BinanceYellowDark
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { viewModel.deleteAllAssets() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Sil",
                            tint = BinanceLossRed
                        )
                    }
                    IconButton(
                        onClick = { viewModel.fetchTefasData() },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = BinanceYellow
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Downloading,
                                contentDescription = "Güncelle",
                                tint = BinanceYellow
                            )
                        }
                    }
                }
            }

            // Search Field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                placeholder = { Text("FON KODU ara...", color = BinanceTextPrimary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BinanceYellow
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Temizle",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = BinanceTextSecondary,
                    unfocusedBorderColor = BinanceTextSecondary,
                    cursorColor = BinanceTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date Info Card - compact
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BinanceYellowDark)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tarih Bilgileri",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BinanceTextPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DateInfoRowCompact(label = "Bugün", value = uiState.todayDate)
                        DateInfoRowCompact(label = "Dün", value = uiState.yesterdayDate)
                        DateInfoRowCompact(label = "7 Gün", value = uiState.sevenDaysAgoDate)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status Message
            if (uiState.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.message.contains("başarı") || uiState.message.contains("alındı") || uiState.message.contains("satıldı"))
                            BinanceProfitGreen.copy(alpha = 0.1f)
                        else
                            TextSecondary.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = uiState.message,
                        modifier = Modifier.padding(16.dp),
                        color = if (uiState.message.contains("başarı") || uiState.message.contains("alındı") || uiState.message.contains("satıldı"))
                            BinanceProfitGreen
                        else
                            TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Fund Count
            if (uiState.fundCount > 0 && uiState.message.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Kaydedilen fon sayısı: ${uiState.fundCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BinanceTextSecondary
                )
            }
                } // End Filter Section Column
            }

            // Fund List
            if (uiState.funds.isNotEmpty()) {
                items(uiState.funds.take(100)) { fund ->
                    FundListItem(
                        fund = fund,
                        portfolios = uiState.portfolios,
                        onFavoriteClick = { code, portfolioId ->
                            viewModel.toggleFavorite(code, portfolioId)
                        },
                        onBuyClick = { selectedFundForTransaction = fund },
                        onFundClick = { selectedFundForDetail = fund }
                    )
                }
                if (uiState.funds.size > 100) {
                    item {
                        Text(
                            text = "... ve ${uiState.funds.size - 100} fon daha",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // Pull-to-refresh indicator
        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = BinanceDarkSurface,
            contentColor = BinanceYellow
        )
    }
}

    // Settings Dialog
    if (showSettingsDialog) {
        DateSettingsDialog(
            todayDate = uiState.todayDate,
            yesterdayDate = uiState.yesterdayDate,
            sevenDaysAgoDate = uiState.sevenDaysAgoDate,
            onDismiss = { showSettingsDialog = false },
            onSave = { today, yesterday, sevenDays ->
                viewModel.updateDate("today", today)
                viewModel.updateDate("yesterday", yesterday)
                viewModel.updateDate("sevenDaysAgo", sevenDays)
                showSettingsDialog = false
            }
        )
    }

    // Fund Transaction Screen Dialog
    selectedFundForTransaction?.let {
        FundTransactionScreen(
            fund = it,
            portfolios = viewModel.uiState.value.portfolios,
            onBackClick = { selectedFundForTransaction = null },
            onSaveBuy = { quantity, price, date, portfolioId ->
                viewModel.buyFund(it, quantity, price, date, portfolioId)
                selectedFundForTransaction = null
            },
            onSaveSell = { quantity, price, date, portfolioId ->
                viewModel.sellFund(it, quantity, price, date, portfolioId)
                selectedFundForTransaction = null
            }
        )
    }

    // Fund Detail Screen
    selectedFundForDetail?.let {
        FundDetailScreen(
            asset = it,
            viewModel = viewModel,
            onBackClick = { selectedFundForDetail = null }
        )
    }
}

@Composable
private fun DateInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun DateInfoRowCompact(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.inverseSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.inverseSurface
        )
    }
}

@Composable
private fun DateSettingsDialog(
    todayDate: String,
    yesterdayDate: String,
    sevenDaysAgoDate: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var today by remember { mutableStateOf(todayDate) }
    var yesterday by remember { mutableStateOf(yesterdayDate) }
    var sevenDays by remember { mutableStateOf(sevenDaysAgoDate) }

    var showTodayPicker by remember { mutableStateOf(false) }
    var showYesterdayPicker by remember { mutableStateOf(false) }
    var showSevenDaysPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tarih Ayarları",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Tarihleri seçin veya dd.mm.yyyy formatında girin",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bugün
                DateFieldWithPicker(
                    label = "Bugün",
                    value = today,
                    onValueChange = { today = it },
                    onClick = { showTodayPicker = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Dün
                DateFieldWithPicker(
                    label = "Dün",
                    value = yesterday,
                    onValueChange = { yesterday = it },
                    onClick = { showYesterdayPicker = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // 7 Gün Önce
                DateFieldWithPicker(
                    label = "7 Gün Önce",
                    value = sevenDays,
                    onValueChange = { sevenDays = it },
                    onClick = { showSevenDaysPicker = true }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(today, yesterday, sevenDays) }) {
                Text("Kaydet", color = getPrimaryColor())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = TextSecondary)
            }
        }
    )

    // Date Pickers
    if (showTodayPicker) {
        DatePickerDialogComponent(
            currentDate = today,
            onDateSelected = { today = it },
            onDismiss = { showTodayPicker = false }
        )
    }
    if (showYesterdayPicker) {
        DatePickerDialogComponent(
            currentDate = yesterday,
            onDateSelected = { yesterday = it },
            onDismiss = { showYesterdayPicker = false }
        )
    }
    if (showSevenDaysPicker) {
        DatePickerDialogComponent(
            currentDate = sevenDays,
            onDateSelected = { sevenDays = it },
            onDismiss = { showSevenDaysPicker = false }
        )
    }
}

@Composable
private fun DateFieldWithPicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        readOnly = false,
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Takvim",
                    tint = BinanceYellow
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun DatePickerDialogComponent(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))
    val initialDate = try {
        dateFormat.parse(currentDate)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
    
    CustomDatePickerDialog(
        initialDate = initialDate,
        onDateSelected = { millis ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            onDateSelected(dateFormat.format(calendar.time))
        },
        onDismiss = onDismiss
    )
}

@Composable
private fun FundListItem(
    fund: Asset,
    portfolios: List<Portfolio>, // Add this parameter
    onFavoriteClick: (String, Long) -> Unit,
    onBuyClick: () -> Unit,
    onFundClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = BinanceDarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: KOD/Ünvan and FİYAT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).clickable(onClick = onFundClick)) {
                    Text(
                        text = fund.code,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BinanceYellow
                    )
                    Text(
                        text = fund.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = BinanceYellowDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${String.format("%.4f", fund.currentPrice)} TL",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BinanceTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onBuyClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "AL SAT",
                            tint = BinanceYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val portfolioId = portfolios.firstOrNull()?.id ?: 0L
                            onFavoriteClick(fund.code, portfolioId)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (fund.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (fund.isFavorite) LossRed else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Background)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Change percentages: 1G%, 1H%, 1A%
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 1G% (Günlük)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "1G%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BinanceTextSecondary
                    )
                    Text(
                        text = String.format("%+.2f%%", fund.dailyChangePercent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (fund.dailyChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 1H% (Haftalık)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "1H%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BinanceTextSecondary
                    )
                    Text(
                        text = String.format("%+.2f%%", fund.weeklyChangePercent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (fund.weeklyChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 1A% (Aylık)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "1A%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BinanceTextSecondary
                    )
                    Text(
                        text = String.format("%+.2f%%", fund.monthlyChangePercent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (fund.monthlyChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 3A% (3 Aylık)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "3A%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BinanceTextSecondary
                    )
                    Text(
                        text = String.format("%+.2f%%", fund.threeMonthChangePercent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (fund.threeMonthChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // 1Y% (1 Yıllık)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "1Y%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BinanceTextSecondary
                    )
                    Text(
                        text = String.format("%+.2f%%", fund.oneYearChangePercent),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (fund.oneYearChangePercent >= 0) BinanceProfitGreen else BinanceLossRed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
