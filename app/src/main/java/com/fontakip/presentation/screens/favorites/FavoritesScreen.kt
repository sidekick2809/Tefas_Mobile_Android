package com.fontakip.presentation.screens.favorites


import com.fontakip.presentation.theme.LocalAppTheme
import com.fontakip.presentation.theme.themeProfitGreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fontakip.presentation.theme.Background
import com.fontakip.presentation.theme.CardBackground
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.getThemeBackgroundColor
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.White
import com.fontakip.presentation.viewmodel.FavoriteWithAsset
import com.fontakip.presentation.viewmodel.FavoritesViewModel
import com.fontakip.domain.model.Asset
import com.fontakip.presentation.screens.portfolio.FundDetailScreen
import com.fontakip.presentation.theme.themeOnSurface
import com.fontakip.presentation.theme.themeSurface
import com.fontakip.presentation.viewmodel.PortfolioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    portfolioViewModel: PortfolioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFundForDetail by remember { mutableStateOf<Asset?>(null) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                color = MaterialTheme.colorScheme.themeSurface
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Title
                        Text(
                            text = "FAVORİLER",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.themeOnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(2f).padding(12.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getThemeBackgroundColor())
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            if (uiState.favorites.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Henüz favoriniz yok",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    Text(
                        text = "Fon Verileri sayfasından kalp ikonuna tıklayarak ekleyebilirsiniz",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            } else {

                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.favorites) { favoriteWithAsset ->
                        FavoriteFundItem(
                            favoriteWithAsset = favoriteWithAsset,
                            onFavoriteClick = { code, portfolioId ->
                                viewModel.toggleFavorite(code, portfolioId)
                            },
                            onCodeClick = { fundCode ->
                                // Find the asset from favorites and show detail
                                val asset = uiState.favorites
                                    .find { it.code == fundCode }
                                    ?.asset
                                selectedFundForDetail = asset
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Fund Detail Overlay
    selectedFundForDetail?.let { asset ->
        FundDetailScreen(
            asset = asset,
            viewModel = portfolioViewModel,
            onBackClick = { selectedFundForDetail = null }
        )
    }
}

@Composable
private fun FavoriteFundItem(
    favoriteWithAsset: FavoriteWithAsset,
    onFavoriteClick: (String, Long) -> Unit,
    onCodeClick: (String) -> Unit
) {
    val asset = favoriteWithAsset.asset
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(size = 12.dp))
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header: KOD/Ünvan and FİYAT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onCodeClick(favoriteWithAsset.code) }
                ) {
                    Text(
                        text = favoriteWithAsset.code,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (asset != null) {
                        Text(
                            text = asset.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (asset != null) {
                        Text(
                            text = "${String.format("%.4f", asset.currentPrice)} TL",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onFavoriteClick(favoriteWithAsset.code, favoriteWithAsset.portfolioId) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favori",
                            tint = LossRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (asset != null) {
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
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = "${if (asset.dailyChangePercent >= 0) "+" else ""}${String.format("%.2f", asset.dailyChangePercent)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (asset.dailyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 1H% (Haftalık)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "1H%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = "${if (asset.weeklyChangePercent >= 0) "+" else ""}${String.format("%.2f", asset.weeklyChangePercent)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (asset.weeklyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 1A% (Aylık)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "1A%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = "${if (asset.monthlyChangePercent >= 0) "+" else ""}${String.format("%.2f", asset.monthlyChangePercent)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (asset.monthlyChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // 3A% (3 Aylık)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "3A%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = "${if (asset.threeMonthChangePercent >= 0) "+" else ""}${String.format("%.2f", asset.threeMonthChangePercent)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (asset.threeMonthChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // 1Y% (1 Yıllık)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "1Y%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = "${if (asset.oneYearChangePercent >= 0) "+" else ""}${String.format("%.2f", asset.oneYearChangePercent)}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (asset.oneYearChangePercent >= 0) MaterialTheme.colorScheme.themeProfitGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
