package com.example.xauusdlotsizecalculator.ui.calculator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xauusdlotsizecalculator.domain.calculator.XauusdLotCalculator
import com.example.xauusdlotsizecalculator.domain.model.CalculationInput
import com.example.xauusdlotsizecalculator.domain.model.CalculationResult
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.PropFirmSettings
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.theme.TerminalBackground
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme
import com.example.xauusdlotsizecalculator.ui.calculator.components.CalculationDetailsCard
import com.example.xauusdlotsizecalculator.ui.calculator.components.CalculatorInputs
import com.example.xauusdlotsizecalculator.ui.calculator.components.DirectionSelector
import com.example.xauusdlotsizecalculator.ui.calculator.components.ResultCard
import com.example.xauusdlotsizecalculator.ui.calculator.components.SettingsBottomSheet
import java.math.BigDecimal

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier,
    onSaveAsTrade: (Trade) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CalculatorScreenContent(
        uiState = uiState,
        onPairChange = { viewModel.onPairChange(it) },
        onSelectAccount = { viewModel.onSelectAccount(it) },
        onBalanceChange = { viewModel.onBalanceChange(it) },
        onRiskPercentChange = { viewModel.onRiskPercentChange(it) },
        onPresetRiskSelected = { viewModel.onPresetRiskSelected(it) },
        onEntryPriceChange = { viewModel.onEntryPriceChange(it) },
        onSlPercentChange = { viewModel.onSlPercentChange(it) },
        onSlPriceChange = { viewModel.onSlPriceChange(it) },
        onTpPriceChange = { viewModel.onTpPriceChange(it) },
        onTpPercentChange = { viewModel.onTpPercentChange(it) },
        onSlModeChange = { viewModel.onSlModeChange(it) },
        onTpModeChange = { viewModel.onTpModeChange(it) },
        onDirectionChange = { viewModel.onDirectionChange(it) },
        onLotStepChange = { viewModel.onLotStepChange(it) },
        onRoundingModeChange = { viewModel.onRoundingModeChange(it) },
        onCalculateClick = { viewModel.onCalculateClick() },
        onResetClick = { viewModel.onResetClick() },
        onToggleDetails = { viewModel.onToggleDetails() },
        onOpenSettings = { viewModel.onOpenSettings() },
        onCloseSettings = { viewModel.onCloseSettings() },
        onSaveSettings = { viewModel.onSaveSettings(it) },
        onSaveAsTrade = {
            viewModel.createTradeFromCalculation()?.let { trade ->
                onSaveAsTrade(trade)
            }
        },
        onCopyFeedback = { viewModel.onCopyMessage(it) },
        onSnackbarDismissed = { viewModel.onSnackbarDismissed() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreenContent(
    uiState: CalculatorUiState,
    onPairChange: (String) -> Unit = {},
    onSelectAccount: (Long) -> Unit = {},
    onBalanceChange: (String) -> Unit,
    onRiskPercentChange: (String) -> Unit,
    onPresetRiskSelected: (Double) -> Unit,
    onEntryPriceChange: (String) -> Unit,
    onSlPercentChange: (String) -> Unit,
    onSlPriceChange: (String) -> Unit,
    onTpPriceChange: (String) -> Unit,
    onTpPercentChange: (String) -> Unit = {},
    onSlModeChange: (SlInputMode) -> Unit,
    onTpModeChange: (SlInputMode) -> Unit = {},
    onDirectionChange: (TradeDirection) -> Unit,
    onLotStepChange: (LotStep) -> Unit,
    onRoundingModeChange: (LotRoundingMode) -> Unit,
    onCalculateClick: () -> Unit,
    onResetClick: () -> Unit,
    onToggleDetails: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onSaveSettings: (CalculatorSettings) -> Unit,
    onSaveAsTrade: () -> Unit,
    onCopyFeedback: (String) -> Unit,
    onSnackbarDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onSnackbarDismissed()
        }
    }

    TerminalBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TvPlumContainer,
                                border = BorderStroke(1.dp, TvPurplePrimary.copy(alpha = 0.5f)),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = "TERMINAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp,
                                    color = TvPurpleGlow,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Position Sizing Engine",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onResetClick) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = TvSilverBright
                            )
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TvSilverBright
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Direction Selector: BUY / SELL
                DirectionSelector(
                    selectedDirection = uiState.direction,
                    onDirectionSelected = onDirectionChange
                )

                // Primary Inputs Card
                CalculatorInputs(
                    pair = uiState.pairInput,
                    onPairChange = onPairChange,
                    availableAccounts = uiState.availableAccounts,
                    selectedAccountId = uiState.selectedAccountId,
                    onSelectAccount = onSelectAccount,
                    balance = uiState.balanceInput,
                    onBalanceChange = onBalanceChange,
                    riskPercent = uiState.riskPercentInput,
                    onRiskPercentChange = onRiskPercentChange,
                    onPresetRiskSelected = onPresetRiskSelected,
                    entryPrice = uiState.entryPriceInput,
                    onEntryPriceChange = onEntryPriceChange,
                    slPercent = uiState.slPercentInput,
                    onSlPercentChange = onSlPercentChange,
                    slPrice = uiState.slPriceInput,
                    onSlPriceChange = onSlPriceChange,
                    tpPrice = uiState.tpPriceInput,
                    onTpPriceChange = onTpPriceChange,
                    tpPercent = uiState.tpPercentInput,
                    onTpPercentChange = onTpPercentChange,
                    slMode = uiState.slMode,
                    onSlModeChange = onSlModeChange,
                    tpMode = uiState.tpMode,
                    onTpModeChange = onTpModeChange,
                    selectedLotStep = uiState.lotStep,
                    onLotStepChange = onLotStepChange,
                    validation = uiState.validation
                )

                // Action Buttons (Calculate & Reset)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetClick,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TvSilverBright
                        ),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset")
                    }

                    Button(
                        onClick = onCalculateClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TvPurplePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .drawBehind {
                                drawLine(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.35f), Color.Transparent)
                                    ),
                                    start = androidx.compose.ui.geometry.Offset(16.dp.toPx(), 1f),
                                    end = androidx.compose.ui.geometry.Offset(size.width - 16.dp.toPx(), 1f),
                                    strokeWidth = 1.2f
                                )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calculate Lot Size",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Results Card
                ResultCard(
                    result = uiState.result,
                    onCopyFeedback = onCopyFeedback,
                    isPropFirmLimitExceeded = uiState.isPropFirmLimitExceeded,
                    propFirmWarningMessage = uiState.propFirmWarningMessage,
                    propFirmRiskAtLimitText = uiState.propFirmRiskAtLimitText,
                    onSaveAsTrade = onSaveAsTrade
                )

                // Transparent Calculation Details (Expandable)
                CalculationDetailsCard(
                    result = uiState.result,
                    isExpanded = uiState.isDetailsExpanded,
                    onToggleExpanded = onToggleDetails
                )

                // Offline Guarantee & Footer Note
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TradeLog • Precision Sizing • Zero Network Tracking",
                        fontSize = 11.sp,
                        color = TvSilver.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Settings Modal Bottom Sheet
            if (uiState.showSettingsSheet) {
                SettingsBottomSheet(
                    sheetState = sheetState,
                    currentSettings = uiState.settings,
                    onSave = onSaveSettings,
                    onDismiss = onCloseSettings
                )
            }
        }
    }
}

@Preview(name = "Calculator Screen - Normal Result", showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    val sampleInput = CalculationInput(
        balance = BigDecimal("5000"),
        riskPercent = BigDecimal("1"),
        entryPrice = BigDecimal("2650.00"),
        slPercent = BigDecimal("0.18868"),
        direction = TradeDirection.BUY,
        contractSize = BigDecimal("100"),
        lotStep = LotStep.STEP_0_01,
        roundingMode = LotRoundingMode.ROUND_DOWN,
        slPrice = BigDecimal("2645.00"),
        takeProfitPrice = BigDecimal("2662.50")
    )
    val sampleResult = XauusdLotCalculator.calculate(sampleInput)

    XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        CalculatorScreenContent(
            uiState = CalculatorUiState(
                balanceInput = "5000",
                riskPercentInput = "1",
                entryPriceInput = "2650.00",
                slPriceInput = "2645.00",
                slPercentInput = "0.189",
                tpPriceInput = "2662.50",
                direction = TradeDirection.BUY,
                result = sampleResult,
                propFirmSettings = PropFirmSettings()
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
    }
}
