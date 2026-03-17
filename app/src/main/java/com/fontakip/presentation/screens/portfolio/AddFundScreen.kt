package com.fontakip.presentation.screens.portfolio

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.fontakip.domain.model.Asset
import com.fontakip.presentation.components.CustomDatePickerDialog
import com.fontakip.presentation.theme.Background
import com.fontakip.presentation.theme.BlueGrey700
import com.fontakip.presentation.theme.CardWhite
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.getThemeBackgroundColor
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.White
import com.fontakip.presentation.viewmodel.AddFundViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundScreen(
    portfolioId: Long,
    portfolioName: String = "Portföyüm",
    onBackClick: () -> Unit,
    viewModel: AddFundViewModel = hiltViewModel()
) {
    // Set the portfolio ID and name in the ViewModel
    LaunchedEffect(portfolioId, portfolioName) {
        viewModel.setPortfolioId(portfolioId)
        viewModel.setPortfolioName(portfolioName)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var searchQuery by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTransactionScreen by remember { mutableStateOf(false) }
    var selectedFundForTransaction by remember { mutableStateOf<Asset?>(null) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
    
    // Debounced search - auto search when typing
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            delay(500) // Wait 500ms after user stops typing
            viewModel.searchFunds(searchQuery)
        }
    }
    
    // Show transaction screen if a fund is selected
    if (showTransactionScreen && selectedFundForTransaction != null) {
        FundTransactionScreen(
            fund = selectedFundForTransaction!!,
            portfolios = viewModel.uiState.value.portfolios,
            onBackClick = { 
                showTransactionScreen = false 
                selectedFundForTransaction = null
            },
            onSaveBuy = { quantity, price, date, selectedPortfolioId ->
                viewModel.buyFund(quantity, price, date, selectedPortfolioId)
                showTransactionScreen = false
                selectedFundForTransaction = null
                // Navigate back after successful buy
                onBackClick()
            },
            onSaveSell = { quantity, price, date, selectedPortfolioId ->
                viewModel.sellFund(quantity, price, date, selectedPortfolioId)
                showTransactionScreen = false
                selectedFundForTransaction = null
                // Navigate back after successful sell
                onBackClick()
            }
        )
        return
    }
    
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
                        verticalAlignment = Alignment.Top
                    ) {
                        // Back Button
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = White
                            )
                        }

                        // Title
                        Text(
                            text = "Fon Ekle",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
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
                .padding(16.dp)
        ) {
            // Search Section - Auto search when typing
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Fon Adı veya Kodu") },
                placeholder = { Text("Fon ara... (en az 2 karakter)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = getPrimaryColor())
                }
            }
            
            // Search Results
            if (uiState.searchResults.isNotEmpty() && uiState.selectedFund == null) {
                Text(
                    text = "Sonuçlar (${uiState.searchResults.size} fon bulundu)",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.searchResults) { fund ->
                        FundSearchResultCard(
                            fund = fund,
                            onEditClick = {
                                selectedFundForTransaction = fund
                                showTransactionScreen = true
                            },
                            onClick = { 
                                viewModel.selectFund(fund)
                                priceText = if (fund.currentPrice > 0) {
                                    String.format(Locale.US, "%.4f", fund.currentPrice)
                                } else ""
                            }
                        )
                    }
                }
            }
            
            // Selected Fund Details
            uiState.selectedFund?.let { fund ->
                // Fund Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fund.code,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = getPrimaryColor()
                            )
                            IconButton(
                                onClick = {
                                    selectedFundForTransaction = fund
                                    showTransactionScreen = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Düzenle",
                                    tint = getPrimaryColor()
                                )
                            }
                        }
                        Text(
                            text = fund.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        if (fund.currentPrice > 0) {
                            Text(
                                text = "Fiyat: ${String.format(Locale.US, "%.4f", fund.currentPrice)} TL",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Quantity Input
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Adet") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price Input
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Fiyat (TL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Date Picker
                OutlinedTextField(
                    value = if (uiState.purchaseDate > 0) dateFormat.format(Date(uiState.purchaseDate)) else "",
                    onValueChange = { },
                    label = { Text("Tarih") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Buy/Sell Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            val quantity = quantityText.toDoubleOrNull()
                            val price = priceText.toDoubleOrNull()
                            
                            if (quantity == null || quantity <= 0) {
                                Toast.makeText(context, "Geçerli bir adet girin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (price == null || price <= 0) {
                                Toast.makeText(context, "Geçerli bir fiyat girin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (uiState.purchaseDate <= 0) {
                                Toast.makeText(context, "Bir tarih seçin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            viewModel.buyFund(quantity, price, uiState.purchaseDate, portfolioId)
                            Toast.makeText(context, "Alım işlemi yapıldı", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Al"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AL")
                    }
                    
                    Button(
                        onClick = {
                            val quantity = quantityText.toDoubleOrNull()
                            val price = priceText.toDoubleOrNull()
                            
                            if (quantity == null || quantity <= 0) {
                                Toast.makeText(context, "Geçerli bir adet girin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (price == null || price <= 0) {
                                Toast.makeText(context, "Geçerli bir fiyat girin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (uiState.purchaseDate <= 0) {
                                Toast.makeText(context, "Bir tarih seçin", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            viewModel.sellFund(quantity, price, uiState.purchaseDate, portfolioId)
                            Toast.makeText(context, "Satım işlemi yapıldı", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = LossRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Sat"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SAT")
                    }
                }
            }
            
            // No results message
            if (uiState.searchResults.isEmpty() && !uiState.isLoading && searchQuery.length >= 2 && uiState.selectedFund == null) {
                Text(
                    text = "Sonuç bulunamadı",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            
            // Hint message
            if (searchQuery.isEmpty()) {
                Text(
                    text = "Fon adı veya kodu girerek arama yapın",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        
        // Custom Date Picker Dialog
        if (showDatePicker) {
            CustomDatePickerDialog(
                initialDate = uiState.purchaseDate,
                onDateSelected = { selectedDate ->
                    viewModel.setPurchaseDate(selectedDate)
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}

@Composable
private fun FundSearchResultCard(
    fund: Asset,
    onEditClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fund.code,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = getPrimaryColor()
                )
                Text(
                    text = fund.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    maxLines = 2
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (fund.currentPrice > 0) {
                    Text(
                        text = String.format(Locale.US, "%.4f TL", fund.currentPrice),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                // Edit/Pencil button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "İşlem Ekle",
                        tint = getPrimaryColor()
                    )
                }
            }
        }
    }
}
