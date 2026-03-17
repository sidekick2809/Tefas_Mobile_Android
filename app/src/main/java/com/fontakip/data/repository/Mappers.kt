package com.fontakip.data.repository

import com.fontakip.data.local.entities.AssetEntity
import com.fontakip.data.local.entities.PortfolioEntity
import com.fontakip.domain.model.Asset
import com.fontakip.domain.model.AssetType
import com.fontakip.domain.model.Portfolio

// Portfolio mappers
fun PortfolioEntity.toDomain(): Portfolio = Portfolio(
    id = id,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Portfolio.toEntity(): PortfolioEntity = PortfolioEntity(
    id = id,
    name = name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// Asset mappers
fun AssetEntity.toDomain(): Asset = Asset(
    id = id,
    portfolioId = portfolioId,
    code = code,
    name = name,
    type = AssetType.valueOf(type),
    units = units,
    purchasePrice = purchasePrice,
    purchaseDate = purchaseDate,
    currentPrice = currentPrice,
    lastUpdateDate = lastUpdateDate,
    fontip = fontip,
    dailyChangePercent = dailyChangePercent,
    weeklyChangePercent = weeklyChangePercent,
    monthlyChangePercent = monthlyChangePercent,
    threeMonthChangePercent = threeMonthChangePercent,
    sixMonthChangePercent = sixMonthChangePercent,
    yearToDateChangePercent = yearToDateChangePercent,
    oneYearChangePercent = oneYearChangePercent,
    threeYearChangePercent = threeYearChangePercent,
    fiveYearChangePercent = fiveYearChangePercent,
    fundType = fundType,
    turC = turC,
    company = company,
    tefasStatus = tefasStatus,
    priceYesterday = priceYesterday,
    priceSevenDaysAgo = priceSevenDaysAgo,
    isFavorite = isFavorite
)

fun Asset.toEntity(): AssetEntity = AssetEntity(
    id = id,
    portfolioId = portfolioId,
    code = code,
    name = name,
    type = type.name,
    units = units,
    purchasePrice = purchasePrice,
    purchaseDate = purchaseDate,
    currentPrice = currentPrice,
    lastUpdateDate = lastUpdateDate,
    fontip = fontip,
    dailyChangePercent = dailyChangePercent,
    weeklyChangePercent = weeklyChangePercent,
    monthlyChangePercent = monthlyChangePercent,
    threeMonthChangePercent = threeMonthChangePercent,
    sixMonthChangePercent = sixMonthChangePercent,
    yearToDateChangePercent = yearToDateChangePercent,
    oneYearChangePercent = oneYearChangePercent,
    threeYearChangePercent = threeYearChangePercent,
    fiveYearChangePercent = fiveYearChangePercent,
    fundType = fundType,
    turC = turC,
    company = company,
    tefasStatus = tefasStatus,
    priceYesterday = priceYesterday,
    priceSevenDaysAgo = priceSevenDaysAgo,
    isFavorite = isFavorite
)
