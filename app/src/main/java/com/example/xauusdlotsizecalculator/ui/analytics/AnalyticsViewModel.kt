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
    val selectedAccountId: StateFlow<Long?> = repository.selectedAccountId

    init {
        if (repository.selectedAccountId.value == null) {
            val first = repository.accounts.value.firstOrNull()
            if (first != null) {
                repository.selectAccount(first.id)
            }
        }
    }

    val analyticsSummary: StateFlow<AnalyticsSummary> = combine(
        repository.trades,
        repository.selectedAccountId,
        repository.accounts
    ) { allTrades, accountId, accList ->
        val activeId = accountId ?: accList.firstOrNull()?.id
        val filtered = if (activeId != null) {
            allTrades.filter { it.accountId == activeId }
        } else {
            allTrades
        }
        repository.computeAnalytics(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsSummary())

    fun onSelectAccountFilter(accountId: Long) {
        repository.selectAccount(accountId)
    }
}
