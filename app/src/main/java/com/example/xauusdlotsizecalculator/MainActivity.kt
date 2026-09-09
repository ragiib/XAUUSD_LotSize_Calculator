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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

                TradeLogApp(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    calculatorContent = {
                        CalculatorScreen(
                            viewModel = calculatorViewModel,
                            onSaveAsTrade = { draftTrade ->
                                journalViewModel.onOpenAddSheet(draftTrade)
                                selectedTab = TradeLogTab.JOURNAL
                            }
                        )
                    },
                    journalContent = {
                        JournalScreen(viewModel = journalViewModel)
                    },
                    analyticsContent = {
                        AnalyticsScreen(viewModel = analyticsViewModel)
                    },
                    accountContent = {
                        AccountScreen(viewModel = accountViewModel)
                    }
                )
            }
        }
    }
}

@Composable
fun TradeLogApp(
    selectedTab: TradeLogTab,
    onTabSelected: (TradeLogTab) -> Unit,
    calculatorContent: @Composable () -> Unit,
    journalContent: @Composable () -> Unit,
    analyticsContent: @Composable () -> Unit,
    accountContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            TradeLogBottomNav(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                TradeLogTab.CALCULATOR -> calculatorContent()
                TradeLogTab.JOURNAL -> journalContent()
                TradeLogTab.ANALYTICS -> analyticsContent()
                TradeLogTab.ACCOUNT -> accountContent()
            }
        }
    }
}

@Preview(name = "TradeLog App Shell (Dashboard)", showBackground = true)
@Composable
fun TradeLogAppPreview() {
    XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        var tab by rememberSaveable { mutableStateOf(TradeLogTab.CALCULATOR) }
        TradeLogApp(
            selectedTab = tab,
            onTabSelected = { tab = it },
            calculatorContent = {
                com.example.xauusdlotsizecalculator.ui.calculator.CalculatorScreenContent(
                    uiState = com.example.xauusdlotsizecalculator.ui.calculator.CalculatorUiState(
                        balanceInput = "5000",
                        riskPercentInput = "1",
                        entryPriceInput = "2650.00",
                        slPriceInput = "2645.00",
                        slPercentInput = "0.189",
                        tpPriceInput = "2662.50",
                        direction = com.example.xauusdlotsizecalculator.domain.model.TradeDirection.BUY
                    ),
                    onBalanceChange = {},
                    onRiskPercentChange = {},
                    onPresetRiskSelected = {},
                    onEntryPriceChange = {},
                    onSlPercentChange = {},
                    onSlPriceChange = {},
                    onTpPriceChange = {},
                    onSlModeChange = {},
                    onDirectionChange = {},
                    onLotStepChange = {},
                    onRoundingModeChange = {},
                    onCalculateClick = {},
                    onResetClick = {},
                    onToggleDetails = {},
                    onOpenSettings = {},
                    onCloseSettings = {},
                    onSaveSettings = {},
                    onSaveAsTrade = {},
                    onCopyFeedback = {},
                    onSnackbarDismissed = {}
                )
            },
            journalContent = {},
            analyticsContent = {},
            accountContent = {}
        )
    }
}
