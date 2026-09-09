package com.example.xauusdlotsizecalculator.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xauusdlotsizecalculator.data.database.TradeRepository
import com.example.xauusdlotsizecalculator.domain.model.AnalyticsSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TradeRepository(application.applicationContext)

    val analyticsSummary: StateFlow<AnalyticsSummary> = repository.trades
        .map { list -> repository.computeAnalytics(list) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsSummary())
}
