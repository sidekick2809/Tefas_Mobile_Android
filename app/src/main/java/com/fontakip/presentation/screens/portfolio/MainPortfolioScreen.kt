package com.fontakip.presentation.screens.portfolio


import com.fontakip.presentation.theme.LocalAppTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fontakip.R
import com.fontakip.data.local.ThemePreferences
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.Portfolio
import com.fontakip.domain.model.PortfolioSummary
import com.fontakip.presentation.theme.AppTheme
import com.fontakip.presentation.theme.themeProfitGreen
import com.fontakip.presentation.viewmodel.PortfolioViewModel
import com.fontakip.presentation.navigation.Screen
import com.fontakip.presentation.screens.portfolio.FundDetailScreen
import com.fontakip.presentation.screens.portfolio.FundTransactionScreen
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.fontakip.data.local.entities.TransactionEntity
import com.fontakip.presentation.theme.Babyblue
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.PrimaryBlueLight
import com.fontakip.presentation.theme.PrimaryRed
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.theme.ThemeColors
import androidx.compose.material3.Card as Card1
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Payments
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.fontakip.presentation.theme.themeBigBox
import com.fontakip.presentation.theme.themeBorder
import com.fontakip.presentation.theme.themeIconics
import com.fontakip.presentation.theme.themeSmallBox
import com.fontakip.presentation.theme.themekututext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPortfolioScreen(
    viewModel: PortfolioViewModel = hiltViewModel(),
    onNavigateToAssetDetail: (Long) -> Unit = {},
    onNavigateToAddAsset: (Long, String) -> Unit = { _, _ -> },
    onNavigateToBuySell: (Long) -> Unit = {},
    onNavigateToFundDetail: (Asset) -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showNewPortfolioDialog by remember { mutableStateOf(false) }
    var newPortfolioName by remember { mutableStateOf("") }
    var selectedAssetForDetail by remember { mutableStateOf<Asset?>(null) }
    var selectedFundForTransaction by remember { mutableStateOf<Asset?>(null) }
    var showThemeDialog by remember { mutableStateOf(false) }
    
    // Activity context for theme changes
    val activityContext = androidx.compose.ui.platform.LocalContext.current

    val currentPortfolio = uiState.portfolios.getOrNull(uiState.currentPortfolioIndex)
    val portfolioName = currentPortfolio?.name ?: "Ana Portföy"

    // Save selected portfolio index to SharedPreferences for PortfolioAnalyticsScreen to use
    LaunchedEffect(uiState.currentPortfolioIndex) {
        val sharedPrefs = activityContext.getSharedPreferences("portfolio_prefs", android.content.Context.MODE_PRIVATE)
        sharedPrefs.edit().putInt("current_portfolio_index", uiState.currentPortfolioIndex).apply()
    }

    // Animation effect on load
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Arrow
                        IconButton(
                            onClick = { 
                                val prevIndex = if (uiState.currentPortfolioIndex == 0) {
                                    uiState.portfolios.size - 1
                                } else {
                                    uiState.currentPortfolioIndex - 1
                                }
                                viewModel.selectPortfolio(prevIndex)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowLeft,
                                contentDescription = "Önceki Portföy",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Title
                        Text(
                            text = portfolioName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        // Right Arrow
                        IconButton(
                            onClick = { 
                                val nextIndex = (uiState.currentPortfolioIndex + 1) % uiState.portfolios.size
                                viewModel.selectPortfolio(nextIndex)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                                contentDescription = "Sonraki Portföy",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Action buttons included in the Row for vertical alignment
                        val selectedPortfolio = uiState.portfolios.getOrNull(uiState.currentPortfolioIndex)
                        val isDefaultPortfolio = selectedPortfolio?.id == 1L

                        if (!isDefaultPortfolio) {
                            // Rename Icon (only for user-created portfolios)
                            IconButton(onClick = {
                                newPortfolioName = portfolioName
                                showRenameDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Portföyü Yeniden Adlandır",
                                    tint = MaterialTheme.colorScheme.themeIconics
                                )
                            }
                            // Delete Icon (only for user-created portfolios)
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Portföyü Sil",
                                    tint = MaterialTheme.colorScheme.themeIconics
                                )
                            }
                        }
                        // Add New Portfolio Icon
                        IconButton(onClick = {
                            newPortfolioName = ""
                            showNewPortfolioDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Yeni Portföy Ekle",
                                tint = MaterialTheme.colorScheme.themeIconics
                            )
                        }
                        // Settings Icon
                        IconButton(onClick = { showThemeDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Ayarlar",
                                tint = MaterialTheme.colorScheme.themeIconics
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                // Portfolio Summary Card as first item
                item {
                        PortfolioSummarySection(
                            summary = uiState.summary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    // Asset List
                    items(uiState.assets) { asset ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(animationSpec = tween(300)) +
                                    slideInVertically(
                                        animationSpec = tween(300),
                                        initialOffsetY = { it }
                                    )
                        ) {
                            AssetCard(
                                asset = asset,
                                totalPortfolioValue = uiState.summary.totalValue,
                                onInfoClick = { selectedAssetForDetail = asset },
                                onBuyClick = { selectedFundForTransaction = asset },
                                onCardClick = { onNavigateToFundDetail(asset) }
                            )
                        }
                    }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Rename Dialog
        if (showRenameDialog) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text("Portföyü Yeniden Adlandır") },
                text = {
                    OutlinedTextField(
                        value = newPortfolioName,
                        onValueChange = { newPortfolioName = it },
                        label = { Text("Portföy Adı") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.updatePortfolioName(currentPortfolio?.id ?: 0, newPortfolioName)
                            showRenameDialog = false
                        }
                    ) {
                        Text("Kaydet")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }

        // Delete Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Portföyü Sil") },
                text = { Text("Bu portföyü silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteCurrentPortfolio()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Sil", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }

        // New Portfolio Dialog
        if (showNewPortfolioDialog) {
            AlertDialog(
                onDismissRequest = { showNewPortfolioDialog = false },
                title = { Text("Yeni Portföy Oluştur") },
                text = {
                    Column {
                        Text(
                            text = "Yeni portföy için bir isim girin",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPortfolioName,
                            onValueChange = { newPortfolioName = it },
                            label = { Text("Portföy Adı") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newPortfolioName.isNotBlank()) {
                                viewModel.createNewPortfolio(newPortfolioName)
                                newPortfolioName = ""
                                showNewPortfolioDialog = false
                            }
                        }
                    ) {
                        Text("Oluştur")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNewPortfolioDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }

        // Tema Seçim Dialogu
        if (showThemeDialog) {
            // Mevcut kayıtlı temayı oku
            val savedTheme = remember { 
                ThemePreferences.getInstance(activityContext).getTheme() 
            }
            var selectedTheme by remember { mutableStateOf(savedTheme) }
            
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { 
                    Text(
                        text = "Tema Seç",
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                text = {
                    Column {
                        // Binance Dark Theme Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTheme = AppTheme.BINANCE_DARK }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == AppTheme.BINANCE_DARK,
                                onClick = { selectedTheme = AppTheme.BINANCE_DARK }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Binance Dark",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Koyu arka plan, parlak sarı vurgular",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // Binance Light Theme Option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTheme = AppTheme.BINANCE_LIGHT }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTheme == AppTheme.BINANCE_LIGHT,
                                onClick = { selectedTheme = AppTheme.BINANCE_LIGHT }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Binance Light",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Açık arka plan, koyu metin",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Temayı kaydet
                            ThemePreferences.getInstance(activityContext).saveTheme(selectedTheme)
                            showThemeDialog = false
                        }
                    ) {
                        Text(
                            text = "Uygula",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showThemeDialog = false
                        }
                    ) {
                        Text(
                            text = "İptal",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        // Fund Detail Dialog
        selectedAssetForDetail?.let { asset ->
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { selectedAssetForDetail = null },
                properties = androidx.compose.ui.window.DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                FundDetailScreen(
                    asset = asset,
                    viewModel = viewModel,
                    onBackClick = { selectedAssetForDetail = null }
                )
            }
        }
    } // End of Scaffold

    // Fund Transaction Screen (AL/SAT) - Full Screen (Scaffold dışında)
    selectedFundForTransaction?.let { asset ->
        val selectedPortfolio = uiState.portfolios.getOrNull(uiState.currentPortfolioIndex)
        FundTransactionScreen(
            fund = asset,
            portfolios = uiState.portfolios,
            currentPortfolioId = selectedPortfolio?.id,
            onBackClick = { selectedFundForTransaction = null },
            onSaveBuy = { quantity, price, date, portfolioId ->
                viewModel.buyFund(asset, quantity, price, date, portfolioId)
                selectedFundForTransaction = null
            },
            onSaveSell = { quantity, price, date, portfolioId ->
                viewModel.sellFund(asset, quantity, price, date, portfolioId)
                selectedFundForTransaction = null
            }
        )
    }
}

@Composable
private fun PortfolioSummarySection(
    summary: PortfolioSummary,
    modifier: Modifier = Modifier
) {
    val tlFormat = DecimalFormat("#,##0.00 TL")
    val percentFormat = DecimalFormat("+#,##0.###%;-#,##0.###%")
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale("tr", "TR"))

    Card1(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                spotColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.themeBorder.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.themeBigBox)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
  


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
            Text(
                text = "Portföy Değeri",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Text(
                text = tlFormat.format(summary.totalValue),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Alış Maliyeti",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(
                        text = tlFormat.format(summary.totalCost),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column {
                    Text(
                        text = "Kar/Zarar (TL)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(
                        text = "${if (summary.profitLossTL >= 0) "+" else ""}${tlFormat.format(summary.profitLossTL)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (summary.profitLossTL >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error
                    )
                }
                Column {
                    Text(
                        text = "Kar/Zarar (%)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    Text(
                        text = "${if (summary.profitLossPercent >= 0) "+" else ""}${String.format(Locale.US, "%.3f", summary.profitLossPercent)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (summary.profitLossPercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                Column {
                Text(
                    text = "Portföy (Günlük)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primaryContainer
                )

            Text(
                text = "${String.format(Locale.US, "%.3f", summary.dailyChangePercent)}% / ${tlFormat.format(summary.dailyChangeTL)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (summary.dailyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error
            )
                }
                Column {
                    Text(
                        text = "Portföy (Haftalık)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )

                    Text(
                        text = "${String.format(Locale.US, "%.3f", summary.weeklyChangePercent)}% / ${tlFormat.format(summary.weeklyChangeTL)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (summary.weeklyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error
                    )
                }
                }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Son Güncelleme Kontrolü: ${dateFormat.format(Date(summary.lastUpdateTime))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        }
    }
}

@Composable
private fun AssetCard(
    asset: Asset,
    totalPortfolioValue: Double,
    onInfoClick: () -> Unit,
    onBuyClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val portfolioPercentage = if (totalPortfolioValue > 0) (asset.totalValue / totalPortfolioValue * 100) else 0.0
    val tlFormat = DecimalFormat("#,##0.00 TL")
    val percentFormat = DecimalFormat("+#,##0.###%;-#,##0.###%")

    Card1(
        modifier = Modifier
            .fillMaxWidth()
           // .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onCardClick() }
            .shadow(
            elevation = 12.dp,
             shape = RoundedCornerShape(16.dp),
            ambientColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
             spotColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
    )
            .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.themeBorder.copy(alpha = 0.9f),
             shape = RoundedCornerShape(16.dp)
    ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.themeBigBox)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = asset.code,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "- ${tlFormat.format(asset.totalValue)} (%${String.format(Locale.US, "%.1f", portfolioPercentage)})",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = asset.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle= FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row {
                    IconButton(onClick = onInfoClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Bilgi",
                            tint = MaterialTheme.colorScheme.themeIconics
                        )
                    }
                    IconButton(onClick = onBuyClick) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "AL SAT",
                            tint = MaterialTheme.colorScheme.themeIconics
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // First row: Yatırım Miktarı, Adet, Ort. Alış - 3 columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Yatırım Miktarı (Investment Amount)
                Card1(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.themeSmallBox)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Yatırım Miktarı",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tlFormat.format(asset.totalCost),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Adet (Units) - Integer only
                Card1(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.themeSmallBox)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Adet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.0f", asset.units),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Ortalama Alış (Average Purchase Price)
                Card1(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.themeSmallBox)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Br Maliyet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tlFormat.format(asset.purchasePrice),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Second row: 1G Kar, 1H Kar, KAR - 3 columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1G Kar
                Card1(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (asset.dailyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "1G Kar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = percentFormat.format(asset.dailyChangePercent / 100),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        //val dailyChangeTL = asset.units * asset.dailyChangePercent / 100 * asset.purchasePrice
                        val dailyChangeTL = (asset.units * asset.currentPrice) - (asset.units * asset.priceYesterday)
                        Text(
                            text = tlFormat.format(dailyChangeTL),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                    }
                }

                // 1H Kar
                Card1(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (asset.weeklyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "1H Kar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = percentFormat.format(asset.weeklyChangePercent / 100),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        val weeklyChangeTL = (asset.units * asset.currentPrice) - (asset.units * asset.priceSevenDaysAgo)
                        Text(
                            text = tlFormat.format(weeklyChangeTL),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                    }
                }

                // KAR
                Card1(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (asset.currentPrice > 0 && (asset.units * asset.currentPrice) > (asset.units * asset.purchasePrice)) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "KAR",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Önce % olarak kar/zarar
                        val profitLossPercent = if (asset.purchasePrice > 0) {
                            ((asset.currentPrice - asset.purchasePrice) / asset.purchasePrice) * 100
                        } else 0.0
                        Text(
                            text = if (asset.currentPrice > 0) "${if (profitLossPercent >= 0) "+" else ""}${String.format(Locale.US, "%.2f", profitLossPercent)}%" else "Veri Yok",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Sonra TL olarak kar/zarar
                        Text(
                            text = if (asset.currentPrice > 0) tlFormat.format((asset.units * asset.currentPrice) - (asset.units * asset.purchasePrice)) else "Veri Yok",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.themekututext
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale("tr", "TR"))
    val isBuy = transaction.transactionType == "BUY"

    Card1(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.fundCode,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBuy) "AL" else "SAT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .background(
                                if (isBuy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.fundName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.US, "%.0f Adet", transaction.quantity),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = tlFormat.format(transaction.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateFormat.format(Date(transaction.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private val tlFormat = DecimalFormat("#,##0.00 TL")