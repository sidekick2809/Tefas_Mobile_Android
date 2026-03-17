package com.fontakip.presentation.navigation

sealed class Screen(val route: String) {
    object Portfolio : Screen("portfolio")
    object Analytics : Screen("analytics")
    object Search : Screen("search")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object FonVerileri : Screen("fon_verileri")
    object AddAsset : Screen("add_asset")
    object EditAsset : Screen("edit_asset/{assetId}") {
        fun createRoute(assetId: Long) = "edit_asset/$assetId"
    }
    // Dashboard detail routes
    object PortfolioDetail : Screen("portfolio_detail")
    object CostDetail : Screen("cost_detail")
    object ProfitLossDetail : Screen("profit_loss_detail")
    object AddFund : Screen("add_fund/{portfolioId}/{portfolioName}") {
        fun createRoute(portfolioId: Long, portfolioName: String) = "add_fund/$portfolioId/$portfolioName"
    }
    object MainPortfolio : Screen("main_portfolio")
    object PortfolioAnalytics : Screen("portfolio_analytics/{portfolioIndex}") {
        fun createRoute(portfolioIndex: Int) = "portfolio_analytics/$portfolioIndex"
    }
    object FundDetail : Screen("fund_detail/{fundCode}") {
        fun createRoute(fundCode: String) = "fund_detail/$fundCode"
    }
}
