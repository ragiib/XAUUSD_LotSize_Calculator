package com.example.xauusdlotsizecalculator.ui.calculator

import com.example.xauusdlotsizecalculator.domain.model.CalculationResult
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.PropFirmSettings
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.ValidationResult
import java.math.BigDecimal

enum class SlInputMode(val displayName: String) {
    PRICE("SL Price"),
    PERCENT("SL %")
}

data class CalculatorUiState(
    val balanceInput: String = "2500",
    val riskPercentInput: String = "1",
    val entryPriceInput: String = "4411.537",
    val slPercentInput: String = "0.131",
    val slPriceInput: String = "4405.758",
    val tpPriceInput: String = "",
    val slMode: SlInputMode = SlInputMode.PRICE,
    val direction: TradeDirection = TradeDirection.BUY,
    val lotStep: LotStep = LotStep.STEP_0_01,
    val roundingMode: LotRoundingMode = LotRoundingMode.ROUND_DOWN,
    val contractSize: BigDecimal = BigDecimal("100"),
    val result: CalculationResult? = null,
    val validation: ValidationResult = ValidationResult(),
    val isDetailsExpanded: Boolean = false,
    val showSettingsSheet: Boolean = false,
    val snackbarMessage: String? = null,
    val settings: CalculatorSettings = CalculatorSettings(),
    val propFirmSettings: PropFirmSettings = PropFirmSettings(),
    val isPropFirmLimitExceeded: Boolean = false,
    val propFirmWarningMessage: String? = null,
    val propFirmRiskAtLimitText: String? = null
)
