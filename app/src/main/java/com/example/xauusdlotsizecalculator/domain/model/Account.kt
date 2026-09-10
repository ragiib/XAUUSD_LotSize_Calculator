package com.example.xauusdlotsizecalculator.domain.model

data class Account(
    val id: Long = 0,
    val name: String,
    val currency: String = "$",
    val startingBalance: Double = 5000.0,
    val currentBalance: Double = 5000.0,
    val profitTargetPercent: Double = 10.0,
    val maxLossPercent: Double = 6.0,
    val dailyLossPercent: Double = 3.0,
    val maxGoldLots: Double = 0.20,
    val leverage: Int = 50,
    val isPropFirm: Boolean = true,
    val createdAtEpochMs: Long = System.currentTimeMillis()
) {
    val profitTargetAmount: Double
        get() = startingBalance * (profitTargetPercent / 100.0)

    val maxLossAmount: Double
        get() = startingBalance * (maxLossPercent / 100.0)

    val dailyLossAmount: Double
        get() = startingBalance * (dailyLossPercent / 100.0)
}
