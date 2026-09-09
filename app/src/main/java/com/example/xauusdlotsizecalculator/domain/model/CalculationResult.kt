package com.example.xauusdlotsizecalculator.domain.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

data class CalculationResult(
    val riskAmount: BigDecimal,
    val slDistance: BigDecimal,
    val slPrice: BigDecimal,
    val dollarRiskPerLot: BigDecimal,
    val exactLotSize: BigDecimal,
    val brokerLotSize: BigDecimal,
    val actualRiskWithBrokerLot: BigDecimal,
    val actualRiskPercentWithBrokerLot: BigDecimal,
    val contractSize: BigDecimal,
    val lotStep: LotStep,
    val roundingMode: LotRoundingMode,
    val direction: TradeDirection,
    val isBelowMinimumLot: Boolean = false,
    val takeProfitPrice: BigDecimal? = null,
    val plannedRrRatio: BigDecimal? = null
) {
    val formattedBrokerLot: String
        get() = DecimalFormat("#,##0.00").apply {
            minimumFractionDigits = if (lotStep == LotStep.STEP_1_0) 0 else if (lotStep == LotStep.STEP_0_1) 1 else 2
            maximumFractionDigits = 2
        }.format(brokerLotSize)

    val formattedExactLot: String
        get() = DecimalFormat("#,##0.0000").format(exactLotSize.setScale(4, RoundingMode.HALF_UP))

    val formattedRiskAmount: String
        get() = DecimalFormat("$#,##0.00").format(riskAmount.setScale(2, RoundingMode.HALF_UP))

    val formattedActualRisk: String
        get() = DecimalFormat("$#,##0.00").format(actualRiskWithBrokerLot.setScale(2, RoundingMode.HALF_UP))

    val formattedActualRiskPercent: String
        get() = DecimalFormat("0.00'%'").format(actualRiskPercentWithBrokerLot.setScale(2, RoundingMode.HALF_UP))

    val formattedSlDistance: String
        get() = DecimalFormat("#,##0.000").format(slDistance.setScale(3, RoundingMode.HALF_UP))

    val formattedSlPrice: String
        get() = DecimalFormat("#,##0.000").format(slPrice.setScale(3, RoundingMode.HALF_UP))

    val formattedContractSize: String
        get() = "${contractSize.stripTrailingZeros().toPlainString()} oz / lot"

    val formattedTpPrice: String
        get() = takeProfitPrice?.let { DecimalFormat("#,##0.000").format(it.setScale(3, RoundingMode.HALF_UP)) } ?: "—"

    val formattedPlannedRr: String
        get() = plannedRrRatio?.let { "1 : ${DecimalFormat("#0.00").format(it)}" } ?: "—"
}
