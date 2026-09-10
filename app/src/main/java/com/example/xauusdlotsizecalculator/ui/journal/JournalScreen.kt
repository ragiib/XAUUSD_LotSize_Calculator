package com.example.xauusdlotsizecalculator.ui.journal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xauusdlotsizecalculator.domain.model.Account
import com.example.xauusdlotsizecalculator.domain.model.DEFAULT_SETUPS
import com.example.xauusdlotsizecalculator.domain.model.SetupQuality
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme
import com.example.xauusdlotsizecalculator.ui.journal.components.AddEditTradeSheet
import com.example.xauusdlotsizecalculator.ui.journal.components.TradeCard
import com.example.xauusdlotsizecalculator.ui.journal.components.TradeDetailBottomSheet

@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val trades by viewModel.filteredTrades.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()

    JournalScreenContent(
        uiState = uiState,
        trades = trades,
        accounts = accounts,
        onAccountFilterSelected = { viewModel.onAccountFilterSelected(it) },
        onResultFilterSelected = { viewModel.onResultFilterSelected(it) },
        onSetupFilterSelected = { viewModel.onSetupFilterSelected(it) },
        onSortOptionSelected = { viewModel.onSortOptionSelected(it) },
        onTradeClicked = { viewModel.onTradeClicked(it) },
        onOpenAddSheet = { viewModel.onOpenAddSheet() },
        onDismissDetailSheet = { viewModel.onDismissDetailSheet() },
        onDismissAddEditSheet = { viewModel.onDismissAddEditSheet() },
        onSaveTrade = { viewModel.onSaveTrade(it) },
        onEditTradeClicked = { viewModel.onEditTradeClicked(it) },
        onDuplicateTrade = { viewModel.onDuplicateTrade(it) },
        onDeleteTrade = { viewModel.onDeleteTrade(it) },
        onSnackbarDismissed = { viewModel.onSnackbarDismissed() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreenContent(
    uiState: JournalUiState,
    trades: List<Trade>,
    accounts: List<Account> = emptyList(),
    onAccountFilterSelected: (Long?) -> Unit = {},
    onResultFilterSelected: (TradeResultFilter) -> Unit,
    onSetupFilterSelected: (String?) -> Unit,
    onSortOptionSelected: (TradeSortOption) -> Unit,
    onTradeClicked: (Trade) -> Unit,
    onOpenAddSheet: () -> Unit,
    onDismissDetailSheet: () -> Unit,
    onDismissAddEditSheet: () -> Unit,
    onSaveTrade: (Trade) -> Unit,
    onEditTradeClicked: (Trade) -> Unit,
    onDuplicateTrade: (Trade) -> Unit,
    onDeleteTrade: (Trade) -> Unit,
    onSnackbarDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showSortMenu by remember { mutableStateOf(false) }
    var showSetupMenu by remember { mutableStateOf(false) }
    var showAccountMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onSnackbarDismissed()
        }
    }

    val selectedAccountName = if (uiState.selectedAccountFilterId != null) {
        accounts.firstOrNull { it.id == uiState.selectedAccountFilterId }?.name ?: "Selected Account"
    } else {
        "All Accounts"
    }

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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "${trades.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Trade Journal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Account Filter Menu Button
                    if (accounts.size > 1) {
                        Box {
                            IconButton(onClick = { showAccountMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "Filter by Account",
                                    tint = if (uiState.selectedAccountFilterId != null) TvPurpleGlow else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            DropdownMenu(
                                expanded = showAccountMenu,
                                onDismissRequest = { showAccountMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "All Accounts",
                                            fontWeight = if (uiState.selectedAccountFilterId == null) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onAccountFilterSelected(null)
                                        showAccountMenu = false
                                    }
                                )
                                accounts.forEach { acc ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = acc.name,
                                                fontWeight = if (uiState.selectedAccountFilterId == acc.id) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            onAccountFilterSelected(acc.id)
                                            showAccountMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Sort Menu Button
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Sort,
                                contentDescription = "Sort Trades",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            TradeSortOption.entries.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.displayName) },
                                    onClick = {
                                        onSortOptionSelected(opt)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Setup Filter Menu Button
                    Box {
                        IconButton(onClick = { showSetupMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Setup",
                                tint = if (uiState.selectedSetupFilter != null) TvPurpleGlow else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showSetupMenu,
                            onDismissRequest = { showSetupMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Setups") },
                                onClick = {
                                    onSetupFilterSelected(null)
                                    showSetupMenu = false
                                }
                            )
                            DEFAULT_SETUPS.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        onSetupFilterSelected(s)
                                        showSetupMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddSheet,
                containerColor = TvPurplePrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Trade")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Filter Chips: All, Wins, Losses, Breakeven, Open, plus active Account filter tag
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.selectedAccountFilterId != null) {
                    FilterChip(
                        selected = true,
                        onClick = { onAccountFilterSelected(null) },
                        label = { Text("$selectedAccountName ✕", fontSize = 12.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvPlumContainer,
                            selectedLabelColor = TvPurpleGlow
                        ),
                        border = BorderStroke(1.dp, TvPurplePrimary),
                        modifier = Modifier.height(32.dp)
                    )
                }

                TradeResultFilter.entries.forEach { f ->
                    val isSelected = uiState.resultFilter == f
                    FilterChip(
                        selected = isSelected,
                        onClick = { onResultFilterSelected(f) },
                        label = { Text(f.displayName, fontSize = 12.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvPurplePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = TvSilver
                        ),
                        border = if (isSelected) null else BorderStroke(1.dp, TvDarkSurfaceBorder),
                        modifier = Modifier.height(32.dp)
                    )
                }

                if (uiState.selectedSetupFilter != null) {
                    FilterChip(
                        selected = true,
                        onClick = { onSetupFilterSelected(null) },
                        label = { Text("Setup: ${uiState.selectedSetupFilter} ✕", fontSize = 12.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvPlumContainer,
                            selectedLabelColor = TvPurpleGlow
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }

            // Trades List
            if (trades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = TvPlumContainer,
                            border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                    contentDescription = null,
                                    tint = TvPurpleGlow,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Text(
                            text = "No Trades Recorded",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TvSilverBright
                        )

                        Text(
                            text = "Calculate a position and tap 'Save as Trade', or tap the + button to log an entry manually.",
                            fontSize = 13.sp,
                            color = TvSilver,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trades, key = { it.id }) { trade ->
                        val accountName = accounts.firstOrNull { it.id == trade.accountId }?.name
                        TradeCard(
                            trade = trade,
                            accountName = accountName,
                            onClick = { onTradeClicked(trade) }
                        )
                    }
                }
            }
        }

        // Details Bottom Sheet
        if (uiState.selectedTradeForDetail != null) {
            val detailAccountName = accounts.firstOrNull { it.id == uiState.selectedTradeForDetail?.accountId }?.name
            TradeDetailBottomSheet(
                trade = uiState.selectedTradeForDetail!!,
                accountName = detailAccountName,
                sheetState = detailSheetState,
                onDismiss = onDismissDetailSheet,
                onEdit = onEditTradeClicked,
                onDuplicate = onDuplicateTrade,
                onDelete = onDeleteTrade
            )
        }

        // Add / Edit Trade Bottom Sheet
        if (uiState.isAddSheetOpen) {
            AddEditTradeSheet(
                trade = uiState.selectedTradeForEdit,
                availableAccounts = accounts,
                sheetState = addSheetState,
                onSave = onSaveTrade,
                onDismiss = onDismissAddEditSheet
            )
        }
    }
}

@Preview(name = "Trade Journal - Active Trades", showBackground = true)
@Composable
fun JournalScreenPreview() {
    val sampleTrades = listOf(
        Trade(
            id = 1,
            accountId = 1,
            symbol = "XAUUSD",
            direction = TradeDirection.BUY,
            entryPrice = 2650.00,
            stopLossPrice = 2642.50,
            takeProfitPrice = 2670.00,
            exitPrice = 2665.00,
            lotSize = 0.15,
            plannedRiskAmount = 112.50,
            plannedRiskPercent = 2.0,
            slDistance = 7.50,
            plannedRrRatio = 2.67,
            status = TradeStatus.WIN,
            profitLoss = 225.00,
            profitLossPercent = 200.0,
            rMultiple = 2.00,
            setup = "Liquidity Sweep",
            setupQuality = SetupQuality.A_PLUS,
            mistakes = listOf("No Mistake")
        ),
        Trade(
            id = 2,
            accountId = 1,
            symbol = "XAUUSD",
            direction = TradeDirection.SELL,
            entryPrice = 2660.00,
            exitPrice = 2666.00,
            stopLossPrice = 2666.00,
            lotSize = 0.10,
            plannedRiskAmount = 60.00,
            plannedRiskPercent = 1.0,
            slDistance = 6.00,
            status = TradeStatus.LOSS,
            profitLoss = -60.00,
            profitLossPercent = -100.0,
            rMultiple = -1.00,
            setup = "Fair Value Gap",
            setupQuality = SetupQuality.GOOD,
            mistakes = listOf("FOMO", "Entered Too Early", "Tight SL")
        )
    )

    XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        JournalScreenContent(
            uiState = JournalUiState(),
            trades = sampleTrades,
            onResultFilterSelected = {},
            onSetupFilterSelected = {},
            onSortOptionSelected = {},
            onTradeClicked = {},
            onOpenAddSheet = {},
            onDismissDetailSheet = {},
            onDismissAddEditSheet = {},
            onSaveTrade = {},
            onEditTradeClicked = {},
            onDuplicateTrade = {},
            onDeleteTrade = {},
            onSnackbarDismissed = {}
        )
    }
}
