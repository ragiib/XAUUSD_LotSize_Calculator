package com.example.xauusdlotsizecalculator.domain.model

enum class DayPerformanceStatus(val label: String, val emoji: String) {
    POSITIVE("Positive Day", "▲"),
    NEGATIVE("Negative Day", "▼"),
    NO_TRADES("No Trades", "—")
}

data class DailyPerformance(
    val netProfitLoss: Double = 0.0,
    val percentChange: Double = 0.0,
    val tradeCount: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val breakevens: Int = 0,
    val averageR: Double = 0.0,
    val bestTrade: Double? = null,
    val worstTrade: Double? = null,
    val status: DayPerformanceStatus = DayPerformanceStatus.NO_TRADES
)

data class SetupPerformance(
    val setupName: String,
    val tradeCount: Int,
    val winCount: Int,
    val winRate: Double,
    val totalR: Double,
    val netProfitLoss: Double
)

data class MistakeImpact(
    val mistakeName: String,
    val occurrenceCount: Int,
    val financialLossImpact: Double
)

data class AnalyticsSummary(
    val totalTrades: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val breakevens: Int = 0,
    val openTrades: Int = 0,
    val winRate: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalLoss: Double = 0.0,
    val netProfitLoss: Double = 0.0,
    val averageWin: Double = 0.0,
    val averageLoss: Double = 0.0,
    val largestWin: Double = 0.0,
    val largestLoss: Double = 0.0,
    val averageR: Double = 0.0,
    val profitFactor: Double = 0.0,
    val bestSetup: String? = null,
    val worstSetup: String? = null,
    val mostCommonMistake: String? = null,
    val dailyPerformance: DailyPerformance = DailyPerformance(),
    val setupPerformances: List<SetupPerformance> = emptyList(),
    val mistakeImpacts: List<MistakeImpact> = emptyList(),
    val mistakeCapitalDrain: Double = 0.0
)
