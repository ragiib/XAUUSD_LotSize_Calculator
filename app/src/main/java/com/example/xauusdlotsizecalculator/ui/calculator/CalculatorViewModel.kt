package com.example.xauusdlotsizecalculator.ui.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xauusdlotsizecalculator.data.database.TradeRepository
import com.example.xauusdlotsizecalculator.data.preferences.SettingsRepository
import com.example.xauusdlotsizecalculator.domain.calculator.XauusdLotCalculator
import com.example.xauusdlotsizecalculator.domain.model.Account
import com.example.xauusdlotsizecalculator.domain.model.CalculationInput
import com.example.xauusdlotsizecalculator.domain.model.CalculationResult
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.PropFirmSettings
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import com.example.xauusdlotsizecalculator.domain.model.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradeRepository.getInstance(application)
    private val settingsRepo = SettingsRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        observeAccounts()
        calculateIfValid()
    }

    private fun loadSettings() {
        val savedSettings = settingsRepo.getSettings()
        _uiState.update { current ->
            current.copy(
                balanceInput = savedSettings.defaultAccountSize.toInt().toString(),
                riskPercentInput = savedSettings.defaultRiskPercent.toString().removeSuffix(".0"),
                lotStep = savedSettings.defaultLotStep,
                roundingMode = savedSettings.roundingMode,
                contractSize = BigDecimal(savedSettings.contractSize.toString()),
                settings = savedSettings
            )
        }
    }

    private fun observeAccounts() {
        viewModelScope.launch {
            combine(
                repository.accounts,
                repository.selectedAccountId
            ) { accounts, selectedId ->
                Pair(accounts, selectedId)
            }.collect { (accounts, selectedId) ->
                if (accounts.isNotEmpty()) {
                    val activeId = selectedId ?: accounts.first().id
                    val selectedAcc = accounts.firstOrNull { it.id == activeId } ?: accounts.first()
                    
                    val prop = PropFirmSettings(
                        enabled = selectedAcc.isPropFirm,
                        name = selectedAcc.name,
                        startingBalance = selectedAcc.startingBalance,
                        profitTargetPercent = selectedAcc.profitTargetPercent,
                        maxLossPercent = selectedAcc.maxLossPercent,
                        dailyLossPercent = selectedAcc.dailyLossPercent,
                        maxGoldVolumeLots = selectedAcc.maxGoldLots,
                        leverage = selectedAcc.leverage
                    )

                    val balFormatted = if (selectedAcc.currentBalance % 1.0 == 0.0) {
                        selectedAcc.currentBalance.toInt().toString()
                    } else {
                        DecimalFormat("#.##").format(selectedAcc.currentBalance)
                    }

                    _uiState.update { it.copy(
                        availableAccounts = accounts,
                        selectedAccountId = selectedAcc.id,
                        balanceInput = balFormatted,
                        propFirmSettings = prop
                    )}
                    calculateIfValid()
                }
            }
        }
    }

    fun onSelectAccount(accountId: Long) {
        repository.selectAccount(accountId)
    }

    fun onPairChange(pair: String) {
        _uiState.update { it.copy(pairInput = pair) }
        calculateIfValid()
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
        syncSlInputsOnEntryChange(value)
        calculateIfValid()
    }

    fun onSlPercentChange(value: String) {
        _uiState.update { it.copy(slPercentInput = value) }
        val entry = XauusdLotCalculator.parseDecimal(_uiState.value.entryPriceInput)
        val slPct = XauusdLotCalculator.parseDecimal(value)
        if (entry != null && slPct != null && entry > BigDecimal.ZERO && slPct > BigDecimal.ZERO) {
            val dist = XauusdLotCalculator.calculateSlDistance(entry, slPct)
            val slPr = XauusdLotCalculator.calculateSlPrice(entry, dist, _uiState.value.direction)
            _uiState.update { it.copy(slPriceInput = DecimalFormat("#,##0.000").format(slPr.setScale(3, RoundingMode.HALF_UP)).replace(",", "")) }
        }
        calculateIfValid()
    }

    fun onSlPriceChange(value: String) {
        _uiState.update { it.copy(slPriceInput = value) }
        val entry = XauusdLotCalculator.parseDecimal(_uiState.value.entryPriceInput)
        val slPr = XauusdLotCalculator.parseDecimal(value)
        if (entry != null && slPr != null && entry > BigDecimal.ZERO && slPr > BigDecimal.ZERO) {
            val dist = XauusdLotCalculator.calculateSlDistanceFromPrice(entry, slPr)
            val slPct = XauusdLotCalculator.calculateSlPercentFromDistance(entry, dist)
            _uiState.update { it.copy(slPercentInput = DecimalFormat("0.000").format(slPct.setScale(3, RoundingMode.HALF_UP))) }
        }
        calculateIfValid()
    }

    fun onTpPriceChange(value: String) {
        _uiState.update { it.copy(tpPriceInput = value) }
        val entry = XauusdLotCalculator.parseDecimal(_uiState.value.entryPriceInput)
        val tpPr = XauusdLotCalculator.parseDecimal(value)
        if (entry != null && tpPr != null && entry > BigDecimal.ZERO && tpPr > BigDecimal.ZERO) {
            val tpPct = XauusdLotCalculator.calculateTpPercentFromPrice(entry, tpPr)
            _uiState.update { it.copy(tpPercentInput = DecimalFormat("0.000").format(tpPct.setScale(3, RoundingMode.HALF_UP))) }
        }
        calculateIfValid()
    }

    fun onTpPercentChange(value: String) {
        _uiState.update { it.copy(tpPercentInput = value) }
        val entry = XauusdLotCalculator.parseDecimal(_uiState.value.entryPriceInput)
        val tpPct = XauusdLotCalculator.parseDecimal(value)
        if (entry != null && tpPct != null && entry > BigDecimal.ZERO && tpPct >= BigDecimal.ZERO) {
            val tpPr = XauusdLotCalculator.calculateTpPrice(entry, tpPct, _uiState.value.direction)
            _uiState.update { it.copy(tpPriceInput = DecimalFormat("#,##0.000").format(tpPr.setScale(3, RoundingMode.HALF_UP)).replace(",", "")) }
        }
        calculateIfValid()
    }

    fun onSlModeChange(mode: SlInputMode) {
        _uiState.update { it.copy(slMode = mode) }
        calculateIfValid()
    }

    fun onTpModeChange(mode: SlInputMode) {
        _uiState.update { it.copy(tpMode = mode) }
        calculateIfValid()
    }

    fun onDirectionChange(direction: TradeDirection) {
        _uiState.update { it.copy(direction = direction) }
        val entry = XauusdLotCalculator.parseDecimal(_uiState.value.entryPriceInput)
        val slPct = XauusdLotCalculator.parseDecimal(_uiState.value.slPercentInput)
        if (entry != null && slPct != null && entry > BigDecimal.ZERO) {
            val dist = XauusdLotCalculator.calculateSlDistance(entry, slPct)
            val slPr = XauusdLotCalculator.calculateSlPrice(entry, dist, direction)
            _uiState.update { it.copy(slPriceInput = DecimalFormat("#,##0.000").format(slPr.setScale(3, RoundingMode.HALF_UP)).replace(",", "")) }
        }
        val tpPct = XauusdLotCalculator.parseDecimal(_uiState.value.tpPercentInput)
        if (entry != null && tpPct != null && entry > BigDecimal.ZERO) {
            val tpPr = XauusdLotCalculator.calculateTpPrice(entry, tpPct, direction)
            _uiState.update { it.copy(tpPriceInput = DecimalFormat("#,##0.000").format(tpPr.setScale(3, RoundingMode.HALF_UP)).replace(",", "")) }
        }
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

    private fun syncSlInputsOnEntryChange(entryStr: String) {
        val entry = XauusdLotCalculator.parseDecimal(entryStr) ?: return
        if (entry <= BigDecimal.ZERO) return
        val state = _uiState.value
        if (state.slMode == SlInputMode.PERCENT) {
            val slPct = XauusdLotCalculator.parseDecimal(state.slPercentInput)
            if (slPct != null && slPct > BigDecimal.ZERO) {
                val dist = XauusdLotCalculator.calculateSlDistance(entry, slPct)
                val slPr = XauusdLotCalculator.calculateSlPrice(entry, dist, state.direction)
                _uiState.update { it.copy(slPriceInput = DecimalFormat("#,##0.000").format(slPr.setScale(3, RoundingMode.HALF_UP)).replace(",", "")) }
            }
        } else {
            val slPr = XauusdLotCalculator.parseDecimal(state.slPriceInput)
            if (slPr != null && slPr > BigDecimal.ZERO) {
                val dist = XauusdLotCalculator.calculateSlDistanceFromPrice(entry, slPr)
                val slPct = XauusdLotCalculator.calculateSlPercentFromDistance(entry, dist)
                _uiState.update { it.copy(slPercentInput = DecimalFormat("0.000").format(slPct.setScale(3, RoundingMode.HALF_UP))) }
            }
        }

        if (state.tpMode == SlInputMode.PERCENT) {
            val tpPct = XauusdLotCalculator.parseDecimal(state.tpPercentInput)
            if (tpPct != null && tpPct >= BigDecimal.ZERO) {
                val tpPr = XauusdLotCalculator.calculateTpPrice(entry, tpPct, state.direction)
                _uiState.update { it.copy(tpPriceInput = DecimalFormat("#,##0.000").format(tpPr.setScale(3, RoundingMode.HALF_UP)).replace(",", "")) }
            }
        } else {
            val tpPr = XauusdLotCalculator.parseDecimal(state.tpPriceInput)
            if (tpPr != null && tpPr > BigDecimal.ZERO) {
                val tpPct = XauusdLotCalculator.calculateTpPercentFromPrice(entry, tpPr)
                _uiState.update { it.copy(tpPercentInput = DecimalFormat("0.000").format(tpPct.setScale(3, RoundingMode.HALF_UP))) }
            }
        }
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
            calculateIfValid()
        }
    }

    private fun calculateIfValid() {
        val state = _uiState.value
        val balance = XauusdLotCalculator.parseDecimal(state.balanceInput)
        val riskPercent = XauusdLotCalculator.parseDecimal(state.riskPercentInput)
        val entryPrice = XauusdLotCalculator.parseDecimal(state.entryPriceInput)
        val slPercent = XauusdLotCalculator.parseDecimal(state.slPercentInput)
        val slPrice = if (state.slMode == SlInputMode.PRICE) XauusdLotCalculator.parseDecimal(state.slPriceInput) else null
        val tpPrice = XauusdLotCalculator.parseDecimal(state.tpPriceInput)

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
                roundingMode = state.roundingMode,
                slPrice = slPrice,
                takeProfitPrice = tpPrice
            )
            val result = XauusdLotCalculator.calculate(input)

            // Evaluate Prop Firm Risk Guard for the currently selected account
            val prop = state.propFirmSettings
            val isLimitExceeded = prop.enabled && result.brokerLotSize.toDouble() > prop.maxGoldVolumeLots
            val warningMsg = if (isLimitExceeded) {
                "${prop.name} Max Gold Volume:\n${DecimalFormat("0.00").format(prop.maxGoldVolumeLots)} lots"
            } else null

            val riskAtLimitText = if (isLimitExceeded) {
                val dollarRiskAtLimit = BigDecimal(prop.maxGoldVolumeLots.toString())
                    .multiply(result.dollarRiskPerLot)
                val percentAtLimit = if (balance > BigDecimal.ZERO) {
                    dollarRiskAtLimit.multiply(BigDecimal("100")).divide(balance, 2, RoundingMode.HALF_UP)
                } else BigDecimal.ZERO
                "At max ${DecimalFormat("0.00").format(prop.maxGoldVolumeLots)} lots, actual risk is $${DecimalFormat("#,##0.00").format(dollarRiskAtLimit)} ($percentAtLimit%)"
            } else null

            _uiState.update {
                it.copy(
                    result = result,
                    validation = XauusdLotCalculator.validate(
                        state.balanceInput,
                        state.riskPercentInput,
                        state.entryPriceInput,
                        state.slPercentInput
                    ),
                    isPropFirmLimitExceeded = isLimitExceeded,
                    propFirmWarningMessage = warningMsg,
                    propFirmRiskAtLimitText = riskAtLimitText
                )
            }
        }
    }

    fun createTradeFromCalculation(): Trade? {
        val state = _uiState.value
        val result = state.result ?: return null
        val entry = XauusdLotCalculator.parseDecimal(state.entryPriceInput)?.toDouble() ?: return null
        val riskPct = XauusdLotCalculator.parseDecimal(state.riskPercentInput)?.toDouble() ?: 1.0
        val pair = state.pairInput.trim().ifBlank { "XAUUSD" }.uppercase()

        return Trade(
            id = 0,
            accountId = state.selectedAccountId,
            dateEpochMs = System.currentTimeMillis(),
            symbol = pair,
            direction = state.direction,
            entryPrice = entry,
            stopLossPrice = result.slPrice.toDouble(),
            takeProfitPrice = result.takeProfitPrice?.toDouble(),
            lotSize = result.brokerLotSize.toDouble(),
            plannedRiskAmount = result.riskAmount.toDouble(),
            plannedRiskPercent = riskPct,
            slDistance = result.slDistance.toDouble(),
            plannedRrRatio = result.plannedRrRatio?.toDouble(),
            status = TradeStatus.OPEN
        )
    }

    fun onResetClick() {
        val currentSettings = _uiState.value.settings
        val prop = _uiState.value.propFirmSettings
        val accounts = _uiState.value.availableAccounts
        val selectedId = _uiState.value.selectedAccountId

        _uiState.update {
            CalculatorUiState(
                pairInput = "XAUUSD",
                selectedAccountId = selectedId,
                availableAccounts = accounts,
                balanceInput = currentSettings.defaultAccountSize.toInt().toString(),
                riskPercentInput = currentSettings.defaultRiskPercent.toString().removeSuffix(".0"),
                entryPriceInput = "2650.00",
                slPercentInput = "0.189",
                slPriceInput = "2645.00",
                direction = TradeDirection.BUY,
                lotStep = currentSettings.defaultLotStep,
                roundingMode = currentSettings.roundingMode,
                contractSize = BigDecimal(currentSettings.contractSize.toString()),
                settings = currentSettings,
                propFirmSettings = prop
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
                snackbarMessage = "Calculator settings saved"
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
