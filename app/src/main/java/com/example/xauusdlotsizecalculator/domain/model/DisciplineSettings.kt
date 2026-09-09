package com.example.xauusdlotsizecalculator.domain.model

data class DisciplineSettings(
    val maxTradesPerSession: Int = 3,
    val dailyProfitStop: Double = 100.0,
    val dailyLossStop: Double = 50.0,
    val autoSyncBalance: Boolean = true
)
