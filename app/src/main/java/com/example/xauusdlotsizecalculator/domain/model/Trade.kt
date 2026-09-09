package com.example.xauusdlotsizecalculator.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TradeStatus(val displayName: String) {
    OPEN("Open"),
    WIN("Win"),
    LOSS("Loss"),
    BREAKEVEN("Breakeven")
}

enum class SetupQuality(val stars: String, val displayName: String) {
    A_PLUS("⭐⭐⭐", "A+"),
    GOOD("⭐⭐", "Good"),
    AVERAGE("⭐", "Average"),
    POOR("❌", "Poor");

    val fullLabel: String get() = "$stars $displayName"
}

data class Trade(
    val id: Long = 0,
    val dateEpochMs: Long = System.currentTimeMillis(),
    val symbol: String = "XAUUSD",
    val direction: TradeDirection = TradeDirection.BUY,
    val entryPrice: Double = 0.0,
    val exitPrice: Double? = null,
    val stopLossPrice: Double = 0.0,
    val takeProfitPrice: Double? = null,
    val lotSize: Double = 0.0,
    val plannedRiskAmount: Double = 0.0,
    val plannedRiskPercent: Double = 1.0,
    val slDistance: Double = 0.0,
    val plannedRrRatio: Double? = null,
    val status: TradeStatus = TradeStatus.OPEN,
    val profitLoss: Double? = null,
    val profitLossPercent: Double? = null,
    val rMultiple: Double? = null,
    val setup: String = "Liquidity Sweep",
    val setupQuality: SetupQuality = SetupQuality.A_PLUS,
    val mistakes: List<String> = listOf("No mistake"),
    val emotionBefore: String = "Calm",
    val emotionAfter: String = "Calm",
    val thinkingNotes: String = "",
    val wouldTakeAgain: Boolean = true,
    val lesson: String = "",
    val notes: String = "",
    val screenshotPath: String? = null
) {
    val formattedDate: String
        get() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dateEpochMs))

    val formattedTime: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(dateEpochMs))

    val formattedDateTime: String
        get() = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(dateEpochMs))

    val isClosed: Boolean
        get() = status != TradeStatus.OPEN

    val hasMistake: Boolean
        get() = mistakes.any { it != "No mistake" }
}

val DEFAULT_SETUPS = listOf(
    "Liquidity Sweep",
    "Break of Structure",
    "Change of Character",
    "Fair Value Gap",
    "Break & Retest",
    "Support/Resistance",
    "Trend Continuation",
    "Reversal",
    "Other"
)

val DEFAULT_MISTAKES = listOf(
    "No mistake",
    "Wrong lot size",
    "FOMO",
    "Revenge trading",
    "Overtrading",
    "Entered too early",
    "Entered too late",
    "No confirmation",
    "Ignored setup",
    "Moved Stop Loss",
    "Moved Take Profit",
    "Closed too early",
    "Held too long",
    "Averaged losing position",
    "Martingale",
    "Traded after planned stop",
    "Emotional decision",
    "Broke prop-firm rule",
    "Other"
)

val DEFAULT_EMOTIONS = listOf(
    "Calm",
    "Confident",
    "Fearful",
    "Greedy",
    "FOMO",
    "Angry",
    "Impatient",
    "Revenge",
    "Other"
)
