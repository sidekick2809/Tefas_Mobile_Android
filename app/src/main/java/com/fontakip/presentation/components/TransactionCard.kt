package com.fontakip.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fontakip.data.local.entities.TransactionEntity
import com.fontakip.presentation.theme.CardWhite
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.ProfitGreen
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionCard(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier
) {
    val isBuy = transaction.transactionType == "BUY"
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("tr", "TR"))
    val formattedDate = dateFormat.format(Date(transaction.date))
    val totalAmount = transaction.price * transaction.quantity
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Type Indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isBuy) ProfitGreen.copy(alpha = 0.15f) else LossRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBuy) "AL" else "SAT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isBuy) ProfitGreen else LossRed
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Fund Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.fundCode,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = getPrimaryColor()
                )
                Text(
                    text = transaction.fundName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$formattedDate • ${String.format(Locale.US, "%.4f", transaction.price)} TL",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            // Amount and Quantity
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${String.format(Locale.US, "%.2f", transaction.quantity)} adet",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    text = "${String.format(Locale.US, "%.2f", totalAmount)} TL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isBuy) ProfitGreen else LossRed
                )
            }
        }
    }
}
