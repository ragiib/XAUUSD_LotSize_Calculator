package com.example.xauusdlotsizecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme
import com.example.xauusdlotsizecalculator.ui.account.AccountScreen
import com.example.xauusdlotsizecalculator.ui.account.AccountViewModel
import com.example.xauusdlotsizecalculator.ui.analytics.AnalyticsScreen
import com.example.xauusdlotsizecalculator.ui.analytics.AnalyticsViewModel
import com.example.xauusdlotsizecalculator.ui.calculator.CalculatorScreen
import com.example.xauusdlotsizecalculator.ui.calculator.CalculatorViewModel
import com.example.xauusdlotsizecalculator.ui.journal.JournalScreen
import com.example.xauusdlotsizecalculator.ui.journal.JournalViewModel
import com.example.xauusdlotsizecalculator.ui.navigation.TradeLogBottomNav
import com.example.xauusdlotsizecalculator.ui.navigation.TradeLogTab

class MainActivity : ComponentActivity() {

    private val calculatorViewModel: CalculatorViewModel by viewModels()
    private val journalViewModel: JournalViewModel by viewModels()
    private val analyticsViewModel: AnalyticsViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XAUUSDLotSizeCalculatorTheme {
                var selectedTab by rememberSaveable { mutableStateOf(TradeLogTab.CALCULATOR) }

                Scaffold(
                    bottomBar = {
                        TradeLogBottomNav(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (selectedTab) {
                            TradeLogTab.CALCULATOR -> {
                                CalculatorScreen(
                                    viewModel = calculatorViewModel,
                                    onSaveAsTrade = { draftTrade ->
                                        journalViewModel.onOpenAddSheet(draftTrade)
                                        selectedTab = TradeLogTab.JOURNAL
                                    }
                                )
                            }
                            TradeLogTab.JOURNAL -> {
                                JournalScreen(
                                    viewModel = journalViewModel
                                )
                            }
                            TradeLogTab.ANALYTICS -> {
                                AnalyticsScreen(
                                    viewModel = analyticsViewModel
                                )
                            }
                            TradeLogTab.ACCOUNT -> {
                                AccountScreen(
                                    viewModel = accountViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
