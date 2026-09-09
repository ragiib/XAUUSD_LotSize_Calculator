package com.example.xauusdlotsizecalculator.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xauusdlotsizecalculator.data.database.TradeRepository
import com.example.xauusdlotsizecalculator.data.preferences.SettingsRepository
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.DisciplineSettings
import com.example.xauusdlotsizecalculator.domain.model.PropFirmSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class AccountUiState(
    val propFirmSettings: PropFirmSettings = PropFirmSettings(),
    val disciplineSettings: DisciplineSettings = DisciplineSettings(),
    val calculatorSettings: CalculatorSettings = CalculatorSettings(),
    val currentBalance: Double = 5000.0,
    val currency: String = "$",
    val themeMode: String = "DARK",
    val todayTradesCount: Int = 0,
    val todayPnl: Double = 0.0,
    val isSessionLimitExceeded: Boolean = false,
    val isDailyLossStopExceeded: Boolean = false,
    val isDailyProfitStopReached: Boolean = false,
    val showEditBalanceDialog: Boolean = false,
    val showEditPropDialog: Boolean = false,
    val showEditDisciplineDialog: Boolean = false,
    val showEditCalculatorDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradeRepository(application.applicationContext)
    private val settingsRepo = SettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    init {
        loadSettings()
        observeTradesForDiscipline()
    }

    private fun loadSettings() {
        val prop = settingsRepo.getPropFirmSettings()
        val disc = settingsRepo.getDisciplineSettings()
        val calc = settingsRepo.getSettings()
        val bal = settingsRepo.getCurrentBalance()
        val curr = settingsRepo.getCurrency()
        val theme = settingsRepo.getThemeMode()

        _uiState.update {
            it.copy(
                propFirmSettings = prop,
                disciplineSettings = disc,
                calculatorSettings = calc,
                currentBalance = bal,
                currency = curr,
                themeMode = theme
            )
        }
    }

    private fun observeTradesForDiscipline() {
        viewModelScope.launch {
            repository.trades.collect { allTrades ->
                val todayCal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val startOfToday = todayCal.timeInMillis
                val todayTrades = allTrades.filter { it.dateEpochMs >= startOfToday }
                val todayClosed = todayTrades.filter { it.isClosed }
                val todayPnl = todayClosed.sumOf { it.profitLoss ?: 0.0 }

                val discipline = _uiState.value.disciplineSettings
                val isSessionLimit = todayTrades.size >= discipline.maxTradesPerSession
                val isLossStop = todayPnl <= -discipline.dailyLossStop
                val isProfitStop = todayPnl >= discipline.dailyProfitStop

                _uiState.update {
                    it.copy(
                        todayTradesCount = todayTrades.size,
                        todayPnl = todayPnl,
                        isSessionLimitExceeded = isSessionLimit,
                        isDailyLossStopExceeded = isLossStop,
                        isDailyProfitStopReached = isProfitStop
                    )
                }
            }
        }
    }

    fun onUpdateBalance(newBalance: Double) {
        settingsRepo.saveCurrentBalance(newBalance)
        _uiState.update { it.copy(currentBalance = newBalance, showEditBalanceDialog = false, snackbarMessage = "Balance updated") }
    }

    fun onSavePropFirmSettings(settings: PropFirmSettings) {
        settingsRepo.savePropFirmSettings(settings)
        _uiState.update { it.copy(propFirmSettings = settings, showEditPropDialog = false, snackbarMessage = "Prop firm settings saved") }
    }

    fun onSaveDisciplineSettings(settings: DisciplineSettings) {
        settingsRepo.saveDisciplineSettings(settings)
        _uiState.update { it.copy(disciplineSettings = settings, showEditDisciplineDialog = false, snackbarMessage = "Discipline rules saved") }
        observeTradesForDiscipline()
    }

    fun onSaveCalculatorSettings(settings: CalculatorSettings) {
        settingsRepo.saveSettings(settings)
        _uiState.update { it.copy(calculatorSettings = settings, showEditCalculatorDialog = false, snackbarMessage = "Calculator settings saved") }
    }

    fun onSaveThemeMode(mode: String) {
        settingsRepo.saveThemeMode(mode)
        _uiState.update { it.copy(themeMode = mode, snackbarMessage = "Theme updated") }
    }

    fun setShowEditBalanceDialog(show: Boolean) {
        _uiState.update { it.copy(showEditBalanceDialog = show) }
    }

    fun setShowEditPropDialog(show: Boolean) {
        _uiState.update { it.copy(showEditPropDialog = show) }
    }

    fun setShowEditDisciplineDialog(show: Boolean) {
        _uiState.update { it.copy(showEditDisciplineDialog = show) }
    }

    fun setShowEditCalculatorDialog(show: Boolean) {
        _uiState.update { it.copy(showEditCalculatorDialog = show) }
    }

    suspend fun getExportCsv(): String = repository.exportToCsv()

    suspend fun getExportJson(): String = repository.exportToJson()

    suspend fun importBackupJson(jsonStr: String): Int {
        val imported = repository.importFromJson(jsonStr)
        _uiState.update { it.copy(snackbarMessage = "Imported $imported trades successfully") }
        return imported
    }

    fun onSnackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
