package com.fontakip.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.fontakip.presentation.theme.BinanceDarkSurface
import com.fontakip.presentation.theme.CardBackground
import com.fontakip.presentation.theme.White
import com.fontakip.presentation.theme.getPrimaryColor
import com.fontakip.presentation.theme.getNavBarBackgroundColor
import com.fontakip.presentation.theme.getNavBarIndicatorColor
import com.fontakip.presentation.theme.getNavBarSelectedIconColor
import com.fontakip.presentation.theme.getNavBarSelectedTextColor
import com.fontakip.presentation.theme.getNavBarUnselectedIconColor
import com.fontakip.presentation.theme.getNavBarUnselectedTextColor
import com.fontakip.presentation.theme.getBackgroundGradientBrush
import com.fontakip.presentation.theme.FintechPrimary
import com.fontakip.presentation.theme.FintechPrimaryGradientStart
import com.fontakip.presentation.theme.FintechPrimaryGradientEnd
import com.fontakip.presentation.theme.BinanceYellow
import com.fontakip.presentation.theme.BinanceNavBarBackground
import com.fontakip.presentation.theme.BinanceNavBarActive
import com.fontakip.presentation.theme.BinanceNavBarInactive
import com.fontakip.presentation.theme.BinanceProfitGreen
import kotlinx.coroutines.selects.select

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
            .background(BinanceDarkSurface)
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
    var totalDragX by remember { mutableStateOf(0f) }
    val swipeThreshold = 100f

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
                    Modifier.pointerInput(Unit) {
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

    // Binance Style Bottom Bar - Clean and minimal
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = BinanceYellow.copy(alpha = 0.1f),
                spotColor = BinanceYellow.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = BinanceNavBarBackground
    ) {
        NavigationBar(
            modifier = Modifier.height(72.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                // Seçili icon için animasyon değerleri
                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.15f else 1f,
                    animationSpec = tween(durationMillis = 200),
                    label = "icon_scale"
                )
                
                val iconColor by animateColorAsState(
                    targetValue = if (selected) BinanceYellow else BinanceNavBarInactive,
                    animationSpec = tween(durationMillis = 200),
                    label = "icon_color"
                )

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .then(
                                    if (selected) {
                                        // Binance yellow indicator ring
                                        Modifier
                                            .clip(CircleShape)
                                            .background(BinanceYellow.copy(alpha = 0.15f))
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
                                tint = if (selected) BinanceYellow else BinanceNavBarInactive
                            )
                        }
                    },
                    label = {
                        // Sadece aktif label göster - slide animation ile
                        AnimatedVisibility(
                            visible = selected,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(200)
                            ) + fadeIn(animationSpec = tween(200)),
                            exit = slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(200)
                            ) + fadeOut(animationSpec = tween(200))
                        ) {
                            Text(
                                text = item.label,
                                fontWeight = FontWeight.SemiBold,
                                color = BinanceYellow,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BinanceYellow,
                        selectedTextColor = BinanceYellow,
                        unselectedIconColor = BinanceProfitGreen,
                        unselectedTextColor = BinanceProfitGreen,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
