package com.example.xauusdlotsizecalculator.domain.model

enum class LotRoundingMode(
    val displayName: String,
    val description: String
) {
    ROUND_DOWN("Round Down", "Conservative (Risk-safe, never exceeds risk budget)"),
    ROUND_NEAREST("Round Nearest", "Standard nearest valid broker lot");

    val isDown: Boolean get() = this == ROUND_DOWN
}
