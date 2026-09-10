package com.example.xauusdlotsizecalculator.ui.calculator

import com.example.xauusdlotsizecalculator.domain.model.Account
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
    val pairInput: String = "XAUUSD",
    val selectedAccountId: Long = 1L,
    val availableAccounts: List<Account> = emptyList(),
    val balanceInput: String = "5000",
    val riskPercentInput: String = "1",
    val entryPriceInput: String = "2650.00",
    val slPercentInput: String = "0.189",
    val slPriceInput: String = "2645.00",
    val tpPriceInput: String = "2662.50",
    val tpPercentInput: String = "0.472",
    val slMode: SlInputMode = SlInputMode.PRICE,
    val tpMode: SlInputMode = SlInputMode.PRICE,
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
