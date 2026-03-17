package com.fontakip.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fontakip.domain.model.Asset
import com.fontakip.domain.repository.AssetRepository
import com.fontakip.domain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val selectedFontip: String = "YAT", // YAT or EMK
    val favorites: List<FavoriteWithAsset> = emptyList()
)

data class FavoriteWithAsset(
    val code: String,
    val portfolioId: Long,
    val asset: Asset?
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val assetRepository: AssetRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun setFontip(fontip: String) {
        _uiState.value = _uiState.value.copy(selectedFontip = fontip)
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            favoriteRepository.getAllFavorites().collect { favorites ->
                val favoritesWithAssets = favorites.map { favorite ->
                    val assets = assetRepository.searchAllFunds(favorite.code)
                    val asset = assets.firstOrNull()
                    FavoriteWithAsset(
                        code = favorite.code,
                        portfolioId = favorite.portfolioId,
                        asset = asset
                    )
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    favorites = favoritesWithAssets
                )
            }
        }
    }

    fun toggleFavorite(code: String, portfolioId: Long) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(code, portfolioId)
        }
    }

    fun removeFavorite(code: String, portfolioId: Long) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(code, portfolioId)
        }
    }
}
