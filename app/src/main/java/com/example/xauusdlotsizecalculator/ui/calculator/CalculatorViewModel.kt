package com.example.xauusdlotsizecalculator.ui.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.xauusdlotsizecalculator.data.preferences.SettingsRepository
import com.example.xauusdlotsizecalculator.domain.calculator.XauusdLotCalculator
import com.example.xauusdlotsizecalculator.domain.model.CalculationInput
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        calculateIfValid()
    }

    private fun loadSettings() {
        val savedSettings = settingsRepo.getSettings()
        _uiState.update { current ->
            current.copy(
                riskPercentInput = savedSettings.defaultRiskPercent.toString().removeSuffix(".0"),
                lotStep = savedSettings.defaultLotStep,
                roundingMode = savedSettings.roundingMode,
                contractSize = BigDecimal(savedSettings.contractSize.toString()),
                settings = savedSettings
            )
        }
    }

    fun onBalanceChange(value: String) {
        _uiState.update { it.copy(balanceInput = value) }
        calculateIfValid()
    }

    fun onRiskPercentChange(value: String) {
        _uiState.update { it.copy(riskPercentInput = value) }
        calculateIfValid()
    }

    fun onEntryPriceChange(value: String) {
        _uiState.update { it.copy(entryPriceInput = value) }
        calculateIfValid()
    }

    fun onSlPercentChange(value: String) {
        _uiState.update { it.copy(slPercentInput = value) }
        calculateIfValid()
    }

    fun onDirectionChange(direction: TradeDirection) {
        _uiState.update { it.copy(direction = direction) }
        calculateIfValid()
    }

    fun onLotStepChange(lotStep: LotStep) {
        _uiState.update { it.copy(lotStep = lotStep) }
        calculateIfValid()
    }

    fun onRoundingModeChange(mode: LotRoundingMode) {
        _uiState.update { it.copy(roundingMode = mode) }
        calculateIfValid()
    }

    fun onPresetRiskSelected(percent: Double) {
        val strVal = if (percent % 1.0 == 0.0) percent.toInt().toString() else percent.toString()
        _uiState.update { it.copy(riskPercentInput = strVal) }
        calculateIfValid()
    }

    fun onCalculateClick() {
        val state = _uiState.value
        val validation = XauusdLotCalculator.validate(
            balanceStr = state.balanceInput,
            riskPercentStr = state.riskPercentInput,
            entryPriceStr = state.entryPriceInput,
            slPercentStr = state.slPercentInput
        )

        _uiState.update { it.copy(validation = validation) }

        if (validation.isValid) {
            val balance = XauusdLotCalculator.parseDecimal(state.balanceInput) ?: return
            val riskPercent = XauusdLotCalculator.parseDecimal(state.riskPercentInput) ?: return
            val entryPrice = XauusdLotCalculator.parseDecimal(state.entryPriceInput) ?: return
            val slPercent = XauusdLotCalculator.parseDecimal(state.slPercentInput) ?: return

            val input = CalculationInput(
                balance = balance,
                riskPercent = riskPercent,
                entryPrice = entryPrice,
                slPercent = slPercent,
                direction = state.direction,
                contractSize = state.contractSize,
                lotStep = state.lotStep,
                roundingMode = state.roundingMode
            )

            val result = XauusdLotCalculator.calculate(input)
            _uiState.update { it.copy(result = result) }
        }
    }

    private fun calculateIfValid() {
        val state = _uiState.value
        val balance = XauusdLotCalculator.parseDecimal(state.balanceInput)
        val riskPercent = XauusdLotCalculator.parseDecimal(state.riskPercentInput)
        val entryPrice = XauusdLotCalculator.parseDecimal(state.entryPriceInput)
        val slPercent = XauusdLotCalculator.parseDecimal(state.slPercentInput)

        if (balance != null && balance > BigDecimal.ZERO &&
            riskPercent != null && riskPercent > BigDecimal.ZERO && riskPercent <= BigDecimal("100") &&
            entryPrice != null && entryPrice > BigDecimal.ZERO &&
            slPercent != null && slPercent > BigDecimal.ZERO && slPercent < BigDecimal("100")
        ) {
            val input = CalculationInput(
                balance = balance,
                riskPercent = riskPercent,
                entryPrice = entryPrice,
                slPercent = slPercent,
                direction = state.direction,
                contractSize = state.contractSize,
                lotStep = state.lotStep,
                roundingMode = state.roundingMode
            )
            val result = XauusdLotCalculator.calculate(input)
            _uiState.update {
                it.copy(
                    result = result,
                    validation = XauusdLotCalculator.validate(
                        state.balanceInput,
                        state.riskPercentInput,
                        state.entryPriceInput,
                        state.slPercentInput
                    )
                )
            }
        }
    }

    fun onResetClick() {
        val currentSettings = _uiState.value.settings
        _uiState.update {
            CalculatorUiState(
                balanceInput = "2500",
                riskPercentInput = currentSettings.defaultRiskPercent.toString().removeSuffix(".0"),
                entryPriceInput = "4411.537",
                slPercentInput = "0.131",
                direction = TradeDirection.BUY,
                lotStep = currentSettings.defaultLotStep,
                roundingMode = currentSettings.roundingMode,
                contractSize = BigDecimal(currentSettings.contractSize.toString()),
                settings = currentSettings
            )
        }
        calculateIfValid()
    }

    fun onToggleDetails() {
        _uiState.update { it.copy(isDetailsExpanded = !it.isDetailsExpanded) }
    }

    fun onOpenSettings() {
        _uiState.update { it.copy(showSettingsSheet = true) }
    }

    fun onCloseSettings() {
        _uiState.update { it.copy(showSettingsSheet = false) }
    }

    fun onSaveSettings(settings: CalculatorSettings) {
        settingsRepo.saveSettings(settings)
        _uiState.update { current ->
            current.copy(
                settings = settings,
                lotStep = settings.defaultLotStep,
                roundingMode = settings.roundingMode,
                contractSize = BigDecimal(settings.contractSize.toString()),
                showSettingsSheet = false,
                snackbarMessage = "Settings saved"
            )
        }
        calculateIfValid()
    }

    fun onCopyMessage(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun onSnackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
