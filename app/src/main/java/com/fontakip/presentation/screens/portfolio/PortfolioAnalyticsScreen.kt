package com.fontakip.presentation.screens.portfolio

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fontakip.presentation.components.HorizontalBarChart
import com.fontakip.presentation.components.PieChart
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.viewmodel.PortfolioAnalyticsUiState
import com.fontakip.presentation.viewmodel.PortfolioAnalyticsViewModel
import kotlinx.coroutines.delay
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioAnalyticsScreen(
    viewModel: PortfolioAnalyticsViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
    initialPortfolioIndex: Int = 0
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    var showPortfolioDropdown by remember { mutableStateOf(false) }
    
    val activityContext = androidx.compose.ui.platform.LocalContext.current
    val savedPortfolioIndex = remember {
        activityContext.getSharedPreferences("portfolio_prefs", android.content.Context.MODE_PRIVATE)
            .getInt("current_portfolio_index", 0)
    }
    val actualInitialIndex = if (initialPortfolioIndex > 0) initialPortfolioIndex else savedPortfolioIndex
    var hasSetInitialPortfolio by remember { mutableStateOf(false) }

    // İlk yüklemede ViewModel'e kayıtlı veya aktarılan portföy indeksini aktar ve sadece bir kere yap
    LaunchedEffect(uiState.portfolios) {
        if (!hasSetInitialPortfolio && uiState.portfolios.isNotEmpty()) {
            val validIndex = if (actualInitialIndex >= 0 && actualInitialIndex < uiState.portfolios.size) actualInitialIndex else 0
            // ViewModel aslen default seciyor init kisminda ama biz override ediyoruz
            viewModel.selectPortfolio(validIndex)
            hasSetInitialPortfolio = true
        }
    }
    
    // Kullanıcı grafik sayfasında portföy değiştirirse prefs güncellensin
    LaunchedEffect(uiState.currentPortfolioIndex) {
        if (hasSetInitialPortfolio) {
            val sharedPrefs = activityContext.getSharedPreferences("portfolio_prefs", android.content.Context.MODE_PRIVATE)
            sharedPrefs.edit().putInt("current_portfolio_index", uiState.currentPortfolioIndex).apply()
        }
    }
    
    val currentPortfolio = uiState.portfolios.getOrNull(uiState.currentPortfolioIndex)
    val portfolioName = currentPortfolio?.name ?: "Portföyüm"
    
    // Animation effect on load
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Portföy seçici kartı
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    animationSpec = tween(300),
                                    initialOffsetY = { -it }
                                )
                    ) {
                        PortfolioSelectorCard(
                            portfolioName = portfolioName,
                            showDropdown = showPortfolioDropdown,
                            onDropdownToggle = { showPortfolioDropdown = it },
                            onPreviousPortfolio = {
                                val prevIndex = if (uiState.currentPortfolioIndex == 0) {
                                    uiState.portfolios.size - 1
                                } else {
                                    uiState.currentPortfolioIndex - 1
                                }
                                viewModel.selectPortfolio(prevIndex)
                            },
                            onNextPortfolio = {
                                val nextIndex = (uiState.currentPortfolioIndex + 1) % uiState.portfolios.size
                                viewModel.selectPortfolio(nextIndex)
                            },
                            onPortfolioSelected = { index ->
                                viewModel.selectPortfolio(index)
                                showPortfolioDropdown = false
                            },
                            portfolios = uiState.portfolios.map { it.name },
                            currentIndex = uiState.currentPortfolioIndex
                        )
                    }
                }
                
                // Özet kartı
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    animationSpec = tween(300),
                                    initialOffsetY = { it }
                                )
                    ) {
                        PortfolioSummaryCard(uiState = uiState)
                    }
                }
                
                // Pie Chart - Portföy Dağılımı
                /* item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = tween(400),
                                    initialOffsetY = { it }
                                )
                    ) {
                        PieChartCard(
                            title = "Portföy Dağılımı",
                            data = uiState.pieChartData
                        )
                    }
                } */
                
                // 1. Ağırlık grafiği - portfolioPercentage değeri
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(500)) +
                                slideInVertically(
                                    animationSpec = tween(500),
                                    initialOffsetY = { it }
                                )
                    ) {
                        HorizontalBarChart(
                            title = "Ağırlık Dağılımı (%)",
                            data = uiState.weightChartData,
                            showNegativeValues = false
                        )
                    }
                }
                
                // 2. Kar grafiği - profitLossPercent değeri
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(600)) +
                                slideInVertically(
                                    animationSpec = tween(600),
                                    initialOffsetY = { it }
                                )
                    ) {
                        HorizontalBarChart(
                            title = "Kar/Zarar (%)",
                            data = uiState.profitLossChartData,
                            showNegativeValues = true
                        )
                    }
                }
                
                // 3. Günlük % değişim grafiği
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(700)) +
                                slideInVertically(
                                    animationSpec = tween(700),
                                    initialOffsetY = { it }
                                )
                    ) {
                        HorizontalBarChart(
                            title = "Günlük Değişim (%)",
                            data = uiState.dailyChangeChartData,
                            showNegativeValues = true
                        )
                    }
                }
                
                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PortfolioSummaryCard(uiState: PortfolioAnalyticsUiState) {
    val decimalFormat = DecimalFormat("#,##0.00")
    val percentFormat = DecimalFormat("#.##")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                spotColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            )
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Portföy Özeti",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Toplam Değer
                SummaryItem(
                    label = "Toplam Değer",
                    value = "₺${decimalFormat.format(uiState.summary.totalValue)}"
                )
                
                // Toplam Maliyet
                SummaryItem(
                    label = "Maliyet",
                    value = "₺${decimalFormat.format(uiState.summary.totalCost)}"
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Kar/Zarar TL
                val profitLossColor = if (uiState.summary.profitLossTL >= 0) Color(0xFF061612) else LossRed
                SummaryItem(
                    label = "Kar/Zarar",
                    value = "${if (uiState.summary.profitLossTL >= 0) "+" else ""}₺${decimalFormat.format(uiState.summary.profitLossTL)}",
                    valueColor = MaterialTheme.colorScheme.error
                )
                
                // Kar/Zarar %
                SummaryItem(
                    label = "Kar/Zarar %",
                    value = "${if (uiState.summary.profitLossPercent >= 0) "+" else ""}${percentFormat.format(uiState.summary.profitLossPercent)}%",
                    valueColor = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Günlük değişim
            val dailyChangeColor = if (uiState.summary.dailyChangePercent >= 0) Color(0xFF061612) else LossRed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = "Günlük Değişim",
                    value = "${if (uiState.summary.dailyChangeTL >= 0) "+" else ""}₺${decimalFormat.format(uiState.summary.dailyChangeTL)}",
                    valueColor = dailyChangeColor
                )
                
                SummaryItem(
                    label = "Günlük %",
                    value = "${if (uiState.summary.dailyChangePercent >= 0) "+" else ""}${percentFormat.format(uiState.summary.dailyChangePercent)}%",
                    valueColor = dailyChangeColor
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/* @Composable
private fun PieChartCard(
    title: String,
    data: List<com.fontakip.presentation.viewmodel.AssetChartData>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PieChart(
                data = data,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
*/

@Composable
private fun PortfolioSelectorCard(
    portfolioName: String,
    showDropdown: Boolean,
    onDropdownToggle: (Boolean) -> Unit,
    onPreviousPortfolio: () -> Unit,
    onNextPortfolio: () -> Unit,
    onPortfolioSelected: (Int) -> Unit,
    portfolios: List<String>,
    currentIndex: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol ok - önceki portföy
            IconButton(onClick = onPreviousPortfolio) {
                Icon(
                    imageVector = Icons.Default.ArrowLeft,
                    contentDescription = "Önceki Portföy",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            // Portföy seçici
            Box {
                Row(
                    modifier = Modifier
                        .clickable { onDropdownToggle(true) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = portfolioName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Açılır menü",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { onDropdownToggle(false) }
                ) {
                    portfolios.forEachIndexed { index, name ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = name,
                                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = { onPortfolioSelected(index) }
                        )
                    }
                }
            }
            
            // Sağ ok - sonraki portföy
            IconButton(onClick = onNextPortfolio) {
                Icon(
                    imageVector = Icons.Default.ArrowRight,
                    contentDescription = "Sonraki Portföy",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}