package com.example.xauusdlotsizecalculator.ui.calculator

import com.example.xauusdlotsizecalculator.domain.model.CalculationResult
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.ValidationResult
import java.math.BigDecimal

data class CalculatorUiState(
    val balanceInput: String = "2500",
    val riskPercentInput: String = "1",
    val entryPriceInput: String = "4411.537",
    val slPercentInput: String = "0.131",
    val direction: TradeDirection = TradeDirection.BUY,
    val lotStep: LotStep = LotStep.STEP_0_01,
    val roundingMode: LotRoundingMode = LotRoundingMode.ROUND_DOWN,
    val contractSize: BigDecimal = BigDecimal("100"),
    val result: CalculationResult? = null,
    val validation: ValidationResult = ValidationResult(),
    val isDetailsExpanded: Boolean = false,
    val showSettingsSheet: Boolean = false,
    val snackbarMessage: String? = null,
    val settings: CalculatorSettings = CalculatorSettings()
)
