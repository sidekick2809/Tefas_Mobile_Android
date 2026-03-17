package com.fontakip.domain.model

data class Favorite(
    val id: Long = 0,
    val code: String,
    val portfolioId: Long,
    val createdAt: Long = System.currentTimeMillis()
)
