package com.fontakip.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fontakip.presentation.theme.LossRed
import com.fontakip.presentation.theme.PrimaryBlue
import com.fontakip.presentation.theme.TextPrimary
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CustomDatePickerDialog(
    initialDate: Long = System.currentTimeMillis(),
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { 
        mutableStateOf(Calendar.getInstance().apply { timeInMillis = initialDate }) 
    }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().apply { timeInMillis = initialDate }) }
    
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("tr", "TR"))
    val daysOfWeek = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF455A64),
        title = {
            Text(
                text = "Tarih Seç",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Month Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, -1)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Önceki ay",
                            tint = getPrimaryColor()
                        )
                    }
                    
                    Text(
                        text = monthYearFormat.format(currentMonth.time),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    IconButton(onClick = {
                        currentMonth = (currentMonth.clone() as Calendar).apply {
                            add(Calendar.MONTH, 1)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Sonraki ay",
                            tint = getPrimaryColor()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Days of Week Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (day == "Cmt" || day == "Paz") FontWeight.Bold else FontWeight.Normal,
                            color = if (day == "Cmt" || day == "Paz") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Calendar Grid
                val calendar = currentMonth.clone() as Calendar
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                
                val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                // Convert from Sunday=1 to Monday=1 format
                val adjustedFirstDay = if (firstDayOfWeek == Calendar.SUNDAY) 7 else firstDayOfWeek - 1
                
                val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                
                val weeks = (adjustedFirstDay - 1 + daysInMonth + 6) / 7
                
                for (week in 0 until weeks) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (dayOfWeek in 1..7) {
                            val dayIndex = week * 7 + dayOfWeek - adjustedFirstDay + 1
                            
                            if (dayIndex in 1..daysInMonth) {
                                val dayCalendar = Calendar.getInstance().apply {
                                    time = currentMonth.time
                                    set(Calendar.DAY_OF_MONTH, dayIndex)
                                }
                                val dayOfWeekValue = dayCalendar.get(Calendar.DAY_OF_WEEK)
                                val isWeekend = dayOfWeekValue == Calendar.SATURDAY || dayOfWeekValue == Calendar.SUNDAY
                                val isSelected = dayCalendar.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                        dayCalendar.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR)
                                val isToday = dayCalendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                                        dayCalendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> getPrimaryColor()
                                                isToday -> getPrimaryColor().copy(alpha = 0.2f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            selectedDate = dayCalendar
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayIndex.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.onBackground
                                            isWeekend -> LossRed
                                            else -> TextPrimary
                                        }
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(selectedDate.timeInMillis)
                onDismiss()
            }) {
                Text("Tamam", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}
