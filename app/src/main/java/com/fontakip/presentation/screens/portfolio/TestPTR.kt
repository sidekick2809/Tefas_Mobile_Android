package com.fontakip.presentation.screens.portfolio
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Test() {
    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            state.endRefresh()
        }
    }
}
