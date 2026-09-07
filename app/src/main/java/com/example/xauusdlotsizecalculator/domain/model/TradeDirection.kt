package com.example.xauusdlotsizecalculator.domain.model

enum class TradeDirection(val label: String) {
    BUY("BUY"),
    SELL("SELL");

    val isBuy: Boolean get() = this == BUY
    val isSell: Boolean get() = this == SELL
}
