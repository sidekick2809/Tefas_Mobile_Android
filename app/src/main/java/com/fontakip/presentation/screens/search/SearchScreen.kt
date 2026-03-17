package com.fontakip.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fontakip.presentation.theme.TextSecondary
import com.fontakip.presentation.theme.getThemeBackgroundColor

@Composable
fun SearchScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getThemeBackgroundColor()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Keşfet Ekranı",
            style = MaterialTheme.typography.titleLarge,
            color = TextSecondary
        )
    }
}
