package com.example.xauusdlotsizecalculator.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xauusdlotsizecalculator.data.database.AccountCalculatedStats
import com.example.xauusdlotsizecalculator.data.database.TradeRepository
import com.example.xauusdlotsizecalculator.data.preferences.SettingsRepository
import com.example.xauusdlotsizecalculator.domain.model.Account
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

data class AccountUiState(
    val availableAccounts: List<Account> = emptyList(),
    val selectedAccountId: Long = 1L,
    val currentAccountStats: AccountCalculatedStats? = null,
    val disciplineSettings: DisciplineSettings = DisciplineSettings(),
    val calculatorSettings: CalculatorSettings = CalculatorSettings(),
    val themeMode: String = "DARK",
    val isSessionLimitExceeded: Boolean = false,
    val isDailyLossStopExceeded: Boolean = false,
    val isDailyProfitStopReached: Boolean = false,
    val showAddAccountDialog: Boolean = false,
    val showEditAccountDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val showAdjustBalanceDialog: Boolean = false,
    val showEditDisciplineDialog: Boolean = false,
    val showEditCalculatorDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradeRepository.getInstance(application)
    private val settingsRepo = SettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    init {
        loadSettings()
        observeData()
    }

    private fun loadSettings() {
        val disc = settingsRepo.getDisciplineSettings()
        val calc = settingsRepo.getSettings()
        val theme = settingsRepo.getThemeMode()

        _uiState.update {
            it.copy(
                disciplineSettings = disc,
                calculatorSettings = calc,
                themeMode = theme
            )
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.accounts,
                repository.selectedAccountId,
                repository.trades
            ) { accounts, selectedId, trades ->
                Triple(accounts, selectedId, trades)
            }.collect { (accounts, selectedId, trades) ->
                if (accounts.isNotEmpty()) {
                    val activeAcc = accounts.firstOrNull { it.id == selectedId } ?: accounts.first()
                    val stats = repository.calculateAccountStats(activeAcc, trades)

                    val discipline = _uiState.value.disciplineSettings
                    val isSessionLimit = stats.todayTradesCount >= discipline.maxTradesPerSession
                    val isLossStop = stats.todayPnl <= -discipline.dailyLossStop
                    val isProfitStop = stats.todayPnl >= discipline.dailyProfitStop

                    _uiState.update { current ->
                        current.copy(
                            availableAccounts = accounts,
                            selectedAccountId = activeAcc.id,
                            currentAccountStats = stats,
                            isSessionLimitExceeded = isSessionLimit,
                            isDailyLossStopExceeded = isLossStop,
                            isDailyProfitStopReached = isProfitStop
                        )
                    }
                }
            }
        }
    }

    fun onSelectAccount(accountId: Long) {
        repository.selectAccount(accountId)
    }

    fun onAddAccount(account: Account) {
        viewModelScope.launch {
            val newId = repository.createAccount(account)
            repository.selectAccount(newId)
            _uiState.update { it.copy(showAddAccountDialog = false, snackbarMessage = "Account '${account.name}' created") }
        }
    }

    fun onUpdateAccount(account: Account) {
        viewModelScope.launch {
            repository.updateAccount(account)
            _uiState.update { it.copy(showEditAccountDialog = false, snackbarMessage = "Account updated") }
        }
    }

    fun onDeleteAccount(accountId: Long, moveTradesToId: Long?) {
        viewModelScope.launch {
            repository.deleteAccount(accountId, moveTradesToId)
            _uiState.update { it.copy(showDeleteAccountDialog = false, snackbarMessage = "Account deleted") }
        }
    }

    fun onAdjustStartingBalance(newStartingBalance: Double) {
        val currentAccountId = _uiState.value.selectedAccountId
        viewModelScope.launch {
            repository.updateStartingBalance(currentAccountId, newStartingBalance)
            _uiState.update { it.copy(showAdjustBalanceDialog = false, snackbarMessage = "Starting balance updated") }
        }
    }

    fun onSaveDisciplineSettings(settings: DisciplineSettings) {
        settingsRepo.saveDisciplineSettings(settings)
        _uiState.update { it.copy(disciplineSettings = settings, showEditDisciplineDialog = false, snackbarMessage = "Discipline rules saved") }
    }

    fun onSaveCalculatorSettings(settings: CalculatorSettings) {
        settingsRepo.saveSettings(settings)
        _uiState.update { it.copy(calculatorSettings = settings, showEditCalculatorDialog = false, snackbarMessage = "Calculator settings saved") }
    }

    fun onSaveThemeMode(mode: String) {
        settingsRepo.saveThemeMode(mode)
        _uiState.update { it.copy(themeMode = mode, snackbarMessage = "Theme updated") }
    }

    fun setShowAddAccountDialog(show: Boolean) {
        _uiState.update { it.copy(showAddAccountDialog = show) }
    }

    fun setShowEditAccountDialog(show: Boolean) {
        _uiState.update { it.copy(showEditAccountDialog = show) }
    }

    fun setShowDeleteAccountDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteAccountDialog = show) }
    }

    fun setShowAdjustBalanceDialog(show: Boolean) {
        _uiState.update { it.copy(showAdjustBalanceDialog = show) }
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
        _uiState.update { it.copy(snackbarMessage = "Imported $imported items successfully") }
        return imported
    }

    fun onSnackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
