package com.example.xauusdlotsizecalculator.ui.journal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xauusdlotsizecalculator.data.database.TradeRepository
import com.example.xauusdlotsizecalculator.data.preferences.SettingsRepository
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TradeResultFilter(val displayName: String) {
    ALL("All"),
    WINS("Wins"),
    LOSSES("Losses"),
    BREAKEVEN("Breakeven"),
    OPEN("Open")
}

enum class TradeSortOption(val displayName: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    HIGHEST_PROFIT("Highest Profit"),
    LARGEST_LOSS("Largest Loss")
}

data class JournalUiState(
    val resultFilter: TradeResultFilter = TradeResultFilter.ALL,
    val selectedSetupFilter: String? = null,
    val selectedMistakeFilter: String? = null,
    val sortOption: TradeSortOption = TradeSortOption.NEWEST,
    val selectedTradeForDetail: Trade? = null,
    val selectedTradeForEdit: Trade? = null,
    val isAddSheetOpen: Boolean = false,
    val snackbarMessage: String? = null
)

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradeRepository(application.applicationContext)
    private val settingsRepo = SettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState

    val filteredTrades: StateFlow<List<Trade>> = combine(
        repository.trades,
        _uiState
    ) { allTrades, state ->
        var list = allTrades

        // Result Filter
        list = when (state.resultFilter) {
            TradeResultFilter.ALL -> list
            TradeResultFilter.WINS -> list.filter { it.status == TradeStatus.WIN || (it.profitLoss ?: 0.0) > 0 }
            TradeResultFilter.LOSSES -> list.filter { it.status == TradeStatus.LOSS || (it.profitLoss ?: 0.0) < 0 }
            TradeResultFilter.BREAKEVEN -> list.filter { it.status == TradeStatus.BREAKEVEN || (it.isClosed && (it.profitLoss ?: 0.0) == 0.0) }
            TradeResultFilter.OPEN -> list.filter { it.status == TradeStatus.OPEN }
        }

        // Setup Filter
        if (state.selectedSetupFilter != null) {
            list = list.filter { it.setup.equals(state.selectedSetupFilter, ignoreCase = true) }
        }

        // Mistake Filter
        if (state.selectedMistakeFilter != null) {
            list = list.filter { it.mistakes.contains(state.selectedMistakeFilter) }
        }

        // Sort
        when (state.sortOption) {
            TradeSortOption.NEWEST -> list.sortedByDescending { it.dateEpochMs }
            TradeSortOption.OLDEST -> list.sortedBy { it.dateEpochMs }
            TradeSortOption.HIGHEST_PROFIT -> list.sortedByDescending { it.profitLoss ?: Double.NEGATIVE_INFINITY }
            TradeSortOption.LARGEST_LOSS -> list.sortedBy { it.profitLoss ?: Double.POSITIVE_INFINITY }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onResultFilterSelected(filter: TradeResultFilter) {
        _uiState.update { it.copy(resultFilter = filter) }
    }

    fun onSetupFilterSelected(setup: String?) {
        _uiState.update { it.copy(selectedSetupFilter = setup) }
    }

    fun onMistakeFilterSelected(mistake: String?) {
        _uiState.update { it.copy(selectedMistakeFilter = mistake) }
    }

    fun onSortOptionSelected(sort: TradeSortOption) {
        _uiState.update { it.copy(sortOption = sort) }
    }

    fun onTradeClicked(trade: Trade) {
        _uiState.update { it.copy(selectedTradeForDetail = trade) }
    }

    fun onDismissDetailSheet() {
        _uiState.update { it.copy(selectedTradeForDetail = null) }
    }

    fun onOpenAddSheet(draftTrade: Trade? = null) {
        _uiState.update { it.copy(isAddSheetOpen = true, selectedTradeForEdit = draftTrade) }
    }

    fun onDismissAddEditSheet() {
        _uiState.update { it.copy(isAddSheetOpen = false, selectedTradeForEdit = null) }
    }

    fun onEditTradeClicked(trade: Trade) {
        _uiState.update { it.copy(selectedTradeForDetail = null, isAddSheetOpen = true, selectedTradeForEdit = trade) }
    }

    fun onSaveTrade(trade: Trade) {
        viewModelScope.launch {
            if (trade.id == 0L) {
                repository.saveTrade(trade)
                _uiState.update { it.copy(snackbarMessage = "Trade logged successfully") }

                // Auto-sync balance if closed trade with P/L
                if (trade.isClosed && trade.profitLoss != null) {
                    val discipline = settingsRepo.getDisciplineSettings()
                    if (discipline.autoSyncBalance) {
                        val currentBalance = settingsRepo.getCurrentBalance()
                        settingsRepo.saveCurrentBalance(currentBalance + trade.profitLoss)
                    }
                }
            } else {
                repository.updateTrade(trade)
                _uiState.update { it.copy(snackbarMessage = "Trade updated") }
            }
            onDismissAddEditSheet()
        }
    }

    fun onDeleteTrade(trade: Trade) {
        viewModelScope.launch {
            repository.deleteTrade(trade.id)
            _uiState.update { it.copy(selectedTradeForDetail = null, snackbarMessage = "Trade deleted") }
        }
    }

    fun onDuplicateTrade(trade: Trade) {
        viewModelScope.launch {
            repository.duplicateTrade(trade.id)
            _uiState.update { it.copy(selectedTradeForDetail = null, snackbarMessage = "Trade duplicated") }
        }
    }

    fun onSnackbarDismissed() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
