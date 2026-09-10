package com.example.xauusdlotsizecalculator.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xauusdlotsizecalculator.data.database.TradeRepository
import com.example.xauusdlotsizecalculator.domain.model.Account
import com.example.xauusdlotsizecalculator.domain.model.AnalyticsSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradeRepository.getInstance(application)

    val accounts: StateFlow<List<Account>> = repository.accounts

    private val _selectedAccountId = MutableStateFlow<Long?>(null) // null = All Accounts
    val selectedAccountId: StateFlow<Long?> = _selectedAccountId.asStateFlow()

    val analyticsSummary: StateFlow<AnalyticsSummary> = combine(
        repository.trades,
        _selectedAccountId
    ) { allTrades, accountId ->
        val filtered = if (accountId != null) {
            allTrades.filter { it.accountId == accountId }
        } else {
            allTrades
        }
        repository.computeAnalytics(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsSummary())

    fun onSelectAccountFilter(accountId: Long?) {
        _selectedAccountId.value = accountId
    }
}
