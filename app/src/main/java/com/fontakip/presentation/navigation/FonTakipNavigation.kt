package com.fontakip.presentation.navigation


import com.fontakip.presentation.theme.LocalAppTheme
import com.fontakip.presentation.theme.getThemeColors
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fontakip.presentation.screens.backup.BackupScreen
import com.fontakip.presentation.screens.analytics.AnalyticsScreen
import com.fontakip.presentation.screens.favorites.FavoritesScreen
import com.fontakip.presentation.screens.fonverileri.FonVerileriScreen
import com.fontakip.presentation.screens.portfolio.MainPortfolioScreen
import com.fontakip.presentation.screens.portfolio.AddFundScreen
import com.fontakip.presentation.screens.portfolio.PortfolioAnalyticsScreen
import com.fontakip.presentation.screens.search.SearchScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Portfolio.route,
        label = "Portföy",
        selectedIcon = Icons.Filled.BusinessCenter,
        unselectedIcon = Icons.Outlined.BusinessCenter
    ),
    BottomNavItem(
        route = Screen.FonVerileri.route,
        label = "Fon Verileri",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    ),
    BottomNavItem(
        route = Screen.PortfolioAnalytics.route,
        label = "Grafik",
        selectedIcon = Icons.Filled.DonutLarge,
        unselectedIcon = Icons.Outlined.DonutLarge
    ),
    BottomNavItem(
        route = Screen.Favorites.route,
        label = "Favoriler",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    BottomNavItem(
        route = Screen.Profile.route,
        label = "Yedekle",
        selectedIcon = Icons.Filled.Backup,
        unselectedIcon = Icons.Outlined.Backup
    )
)

@Composable
fun FonTakipNavigation() {
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { paddingValues ->
            SwipeableNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun SwipeableNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Alt sayfalar için swipe'ı devre dışı bırak
    val isSubPage = currentRoute?.startsWith("add_fund") == true || 
                    currentRoute?.startsWith("edit_asset") == true ||
                    currentRoute?.startsWith("fund_detail") == true

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (!isSubPage) {
                    Modifier.pointerInput(currentRoute) {
                        var totalDragX = 0f
                        val swipeThreshold = 100f
                        
                        detectHorizontalDragGestures(
                            onDragStart = { totalDragX = 0f },
                            onDragEnd = {
                                val currentIndex = bottomNavItems.indexOfFirst { it.route == currentRoute }
                                if (currentIndex >= 0) {
                                    when {
                                        // Sola swipe (parmak sağa hareket ediyor) -> sonraki sayfa
                                        totalDragX < -swipeThreshold && currentIndex < bottomNavItems.size - 1 -> {
                                            val nextRoute = bottomNavItems[currentIndex + 1].route
                                            navController.navigate(nextRoute) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                        // Sağa swipe (parmak sola hareket ediyor) -> önceki sayfa  
                                        totalDragX > swipeThreshold && currentIndex > 0 -> {
                                            val prevRoute = bottomNavItems[currentIndex - 1].route
                                            navController.navigate(prevRoute) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                }
                                totalDragX = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                totalDragX += dragAmount
                            }
                        )
                    }
                } else {
                    Modifier
                }
            )
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Portfolio.route
        ) {
            composable(Screen.Portfolio.route) {
                MainPortfolioScreen(
                    onNavigateToAddAsset = { portfolioId, portfolioName ->
                        navController.navigate(Screen.AddFund.createRoute(portfolioId, portfolioName))
                    }
                )
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
            composable(Screen.Search.route) {
                SearchScreen()
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen()
            }
            composable(Screen.FonVerileri.route) {
                FonVerileriScreen()
            }
            composable(Screen.Profile.route) {
                BackupScreen()
            }
            composable(Screen.PortfolioAnalytics.route) { backStackEntry ->
                val portfolioIndex = backStackEntry.arguments?.getString("portfolioIndex")?.toIntOrNull() ?: 0
                PortfolioAnalyticsScreen(initialPortfolioIndex = portfolioIndex)
            }
            composable(Screen.MainPortfolio.route) {
                MainPortfolioScreen()
            }
            composable(Screen.AddFund.route) { backStackEntry ->
                val portfolioId = backStackEntry.arguments?.getString("portfolioId")?.toLongOrNull() ?: 0L
                val portfolioName = backStackEntry.arguments?.getString("portfolioName") ?: "Portföyüm"
                AddFundScreen(
                    portfolioId = portfolioId,
                    portfolioName = portfolioName,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Yazıların sistem barı veya ekran altı kısımlarında kaybolmaması için eklendi
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Alt taban çubuğu (Base Bar Background)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                .background(getThemeColors(LocalAppTheme.current).navBarBackground, RoundedCornerShape(24.dp))
        )

        // İtemlerin yer aldığı Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp), // Kutularla hizalamayı düzeltmek için yüksekliği ayarladık
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.25f else 1f,
                    animationSpec = tween(durationMillis = 300),
                    label = "icon_scale"
                )
                
                // Bütün elemanı yukarı çekiyoruz (gap'in açılmasını önlemek için)
                val columnOffsetY by animateDpAsState(
                    targetValue = if (selected) (-10).dp else 0.dp,
                    animationSpec = tween(durationMillis = 300),
                    label = "column_offset"
                )

                val boxSize by animateDpAsState(
                    targetValue = if (selected) 52.dp else 42.dp,
                    animationSpec = tween(durationMillis = 300),
                    label = "box_size"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .offset(y = columnOffsetY) // Tıklanınca hem ikon hem yazı beraber taşacak!
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom // En alta yığılım
                ) {
                    Box(
                        modifier = Modifier
                            .size(boxSize)
                            .then(
                                if (selected) {
                                    Modifier
                                        // Neon sarı hale (halo) efekti
                                        .shadow(
                                            elevation = 16.dp,
                                            shape = CircleShape,
                                            spotColor = MaterialTheme.colorScheme.primary,
                                            ambientColor = MaterialTheme.colorScheme.primary
                                        )
                                        .background(getThemeColors(LocalAppTheme.current).navBarBackground, CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                                    Color.Transparent
                                                )
                                            ), 
                                            CircleShape
                                        )
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label,
                            modifier = Modifier
                                .scale(scale)
                                .size(24.dp),
                            tint = if (selected) MaterialTheme.colorScheme.primary else getThemeColors(LocalAppTheme.current).navBarUnselectedIcon
                        )
                    }

                    // İkonla yazı arasındaki devasa boşluğu kaldırdık!
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.primary else getThemeColors(LocalAppTheme.current).navBarUnselectedIcon,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        maxLines = 1,
                        // Yazıları yukarı kaydırdık
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}
