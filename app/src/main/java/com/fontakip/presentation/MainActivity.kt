package com.fontakip.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.fontakip.data.local.ThemePreferences
import com.fontakip.presentation.navigation.FonTakipNavigation
import com.fontakip.presentation.theme.AppTheme
import com.fontakip.presentation.theme.FonTakipTheme
import com.fontakip.presentation.theme.getBackgroundColor
import com.fontakip.presentation.theme.getBackgroundGradient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private lateinit var themePreferences: ThemePreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        themePreferences = ThemePreferences.getInstance(this)
        
        setContent {
            val selectedTheme by themePreferences.themeFlow.collectAsState(initial = themePreferences.getTheme())
            
            // Tema: Kullanıcının seçtiği temayı kullan
            val appTheme = selectedTheme
            
            FonTakipTheme(appTheme = appTheme) {
                // Arka plan gradyanı veya düz renk
                val backgroundGradient = getBackgroundGradient(selectedTheme)
                val backgroundColor = getBackgroundColor(selectedTheme)
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (backgroundGradient != null) {
                                Modifier.background(backgroundGradient)
                            } else {
                                Modifier.background(backgroundColor)
                            }
                        )
                ) {
                    FonTakipNavigation()
                }
            }
        }
    }
}
