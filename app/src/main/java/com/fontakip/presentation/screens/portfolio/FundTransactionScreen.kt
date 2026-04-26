package com.fontakip.presentation.screens.portfolio

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.Portfolio
import com.fontakip.presentation.components.CustomDatePickerDialog
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
import com.fontakip.presentation.theme.themeBackground
import com.fontakip.presentation.theme.themeBorder
import com.fontakip.presentation.theme.themeCardBorder
import com.fontakip.presentation.theme.themeSmallBox
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundTransactionScreen(
    fund: Asset,
    portfolios: List<Portfolio>,
    currentPortfolioId: Long? = null,
    onBackClick: () -> Unit,
    onSaveBuy: (quantity: Double, price: Double, date: Long, portfolioId: Long) -> Unit,
    onSaveSell: (quantity: Double, price: Double, date: Long, portfolioId: Long) -> Unit
) {
    val context = LocalContext.current
    
    val defaultPrice = fund.currentPrice
    
    var quantityText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf(defaultPrice.takeIf { it > 0 }?.let { String.format(Locale.US, "%.4f", it) } ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var purchaseDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedPortfolioId by remember { mutableStateOf(currentPortfolioId ?: portfolios.firstOrNull()?.id ?: 0L) }
    var expandedPortfolio by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
    
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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
                            text = "İşlem Ekle - ${fund.code}",
                            color = MaterialTheme.colorScheme.background,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        
                        // Close Button
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = White
                            )
                        }
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
                .offset { IntOffset(offsetX.roundToInt(), 0) }
        ) {
            // Fund Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = fund.code,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = fund.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    if (fund.currentPrice > 0) {
                        Text(
                            text = "Güncel Fiyat: ${String.format(Locale.US, "%.4f", fund.currentPrice)} TL",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (fund.purchasePrice > 0 && fund.units > 0) {
                        Text(
                            text = "Ort. Alış: ${String.format(Locale.US, "%.4f", fund.purchasePrice)} TL",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Portfolio Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedPortfolio = true },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Portföy Seçin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = portfolios.find { it.id == selectedPortfolioId }?.name ?: "Portföy seçin",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "▼",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Portfolio Dropdown
            if (expandedPortfolio) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        portfolios.forEach { portfolio ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedPortfolioId = portfolio.id
                                        expandedPortfolio = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = portfolio.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (portfolio.id == selectedPortfolioId) getPrimaryColor() else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (portfolio.id == selectedPortfolioId) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Quantity Input
            OutlinedTextField(
                value = quantityText,
                onValueChange = { quantityText = it },
                label = { Text("Miktar (Adet)",color = MaterialTheme.colorScheme.onSurfaceVariant) },
                placeholder = { Text("Örn: 10",color = MaterialTheme.colorScheme.onSurfaceVariant) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = getPrimaryColor(),
                    unfocusedBorderColor = getPrimaryColor(),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Price Input
            OutlinedTextField(
                value =  priceText,
                onValueChange = { priceText = it },
                label = { Text("Fiyat (TL)", color = MaterialTheme.colorScheme.onSurfaceVariant) },//Kutu rengi
                placeholder = { Text("Örn: 105.50", color = MaterialTheme.colorScheme.onSurfaceVariant) },//Kutu bos ornek veri rengi
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = getPrimaryColor(),
                    unfocusedBorderColor = getPrimaryColor(),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            val totalValue = (quantityText.toDoubleOrNull() ?: 0.0) * (priceText.toDoubleOrNull() ?: 0.0)
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Total Value Display (Read-only)
            OutlinedTextField(
                value = String.format(Locale.US, "%.2f", totalValue),
                onValueChange = { },
                label = { Text("İşlem Tutarı (TL)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.themeBorder,
                    unfocusedBorderColor = MaterialTheme.colorScheme.themeBorder,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.themeBorder,
                    disabledContainerColor = MaterialTheme.colorScheme.themeBackground,
                    disabledLabelColor = MaterialTheme.colorScheme.themeCardBorder
                )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Date Picker
            OutlinedTextField(
                value = dateFormat.format(Date(purchaseDate)),
                onValueChange = { },
                label = { Text("İşlem Tarihi",color = MaterialTheme.colorScheme.onSurfaceVariant) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
                    .padding(16.dp),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.themeBorder,
                    unfocusedBorderColor = MaterialTheme.colorScheme.themeBorder,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.themeBorder,
                    disabledContainerColor = MaterialTheme.colorScheme.themeBackground,
                    disabledLabelColor = MaterialTheme.colorScheme.themeCardBorder
                )
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Buy/Sell Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val quantity = quantityText.toDoubleOrNull()
                        val price = priceText.toDoubleOrNull()
                        
                        if (selectedPortfolioId == 0L) {
                            Toast.makeText(context, "Lütfen bir portföy seçin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (quantity == null || quantity <= 0) {
                            Toast.makeText(context, "Geçerli bir miktar girin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (price == null || price <= 0) {
                            Toast.makeText(context, "Geçerli bir fiyat girin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        onSaveBuy(quantity, price, purchaseDate, selectedPortfolioId)
                        Toast.makeText(context, "Alım işlemi yapıldı", Toast.LENGTH_SHORT).show()
                        quantityText = ""
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
                        
                        if (selectedPortfolioId == 0L) {
                            Toast.makeText(context, "Lütfen bir portföy seçin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (quantity == null || quantity <= 0) {
                            Toast.makeText(context, "Geçerli bir miktar girin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (price == null || price <= 0) {
                            Toast.makeText(context, "Geçerli bir fiyat girin", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        onSaveSell(quantity, price, purchaseDate, selectedPortfolioId)
                        Toast.makeText(context, "Satım işlemi yapıldı", Toast.LENGTH_SHORT).show()
                        quantityText = ""
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
        
        // Custom Date Picker Dialog
        if (showDatePicker) {
            CustomDatePickerDialog(
                initialDate = purchaseDate,
                onDateSelected = { selectedDate ->
                    purchaseDate = selectedDate
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}
