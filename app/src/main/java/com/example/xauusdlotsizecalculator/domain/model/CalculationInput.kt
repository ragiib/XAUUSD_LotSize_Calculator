package com.example.xauusdlotsizecalculator.domain.model

import java.math.BigDecimal

data class CalculationInput(
    val balance: BigDecimal,
    val riskPercent: BigDecimal,
    val entryPrice: BigDecimal,
    val slPercent: BigDecimal,
    val direction: TradeDirection = TradeDirection.BUY,
    val contractSize: BigDecimal = DEFAULT_CONTRACT_SIZE,
    val lotStep: LotStep = LotStep.STEP_0_01,
    val roundingMode: LotRoundingMode = LotRoundingMode.ROUND_DOWN,
    val slPrice: BigDecimal? = null,
    val takeProfitPrice: BigDecimal? = null
) {
    companion object {
        val DEFAULT_CONTRACT_SIZE: BigDecimal = BigDecimal("100")
    }
}
