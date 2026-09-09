package com.example.xauusdlotsizecalculator.ui.journal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Sort
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xauusdlotsizecalculator.domain.model.DEFAULT_MISTAKES
import com.example.xauusdlotsizecalculator.domain.model.DEFAULT_SETUPS
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.ui.journal.components.AddEditTradeSheet
import com.example.xauusdlotsizecalculator.ui.journal.components.TradeCard
import com.example.xauusdlotsizecalculator.ui.journal.components.TradeDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val trades by viewModel.filteredTrades.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showSortMenu by remember { mutableStateOf(false) }
    var showSetupMenu by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.onSnackbarDismissed()
        }
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
                                        viewModel.onSortOptionSelected(opt)
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
                                    viewModel.onSetupFilterSelected(null)
                                    showSetupMenu = false
                                }
                            )
                            DEFAULT_SETUPS.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        viewModel.onSetupFilterSelected(s)
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
                onClick = { viewModel.onOpenAddSheet() },
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
            // Horizontal Filter Chips: All, Wins, Losses, Breakeven, Open
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TradeResultFilter.entries.forEach { f ->
                    val isSelected = uiState.resultFilter == f
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onResultFilterSelected(f) },
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
                        onClick = { viewModel.onSetupFilterSelected(null) },
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

            // Trade Cards List
            if (trades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
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
                        TradeCard(
                            trade = trade,
                            onClick = { viewModel.onTradeClicked(trade) }
                        )
                    }
                }
            }
        }

        // Details Bottom Sheet
        if (uiState.selectedTradeForDetail != null) {
            TradeDetailBottomSheet(
                trade = uiState.selectedTradeForDetail!!,
                sheetState = detailSheetState,
                onDismiss = { viewModel.onDismissDetailSheet() },
                onEdit = { viewModel.onEditTradeClicked(it) },
                onDuplicate = { viewModel.onDuplicateTrade(it) },
                onDelete = { viewModel.onDeleteTrade(it) }
            )
        }

        // Add / Edit Trade Bottom Sheet
        if (uiState.isAddSheetOpen) {
            AddEditTradeSheet(
                trade = uiState.selectedTradeForEdit,
                sheetState = addSheetState,
                onSave = { viewModel.onSaveTrade(it) },
                onDismiss = { viewModel.onDismissAddEditSheet() }
            )
        }
    }
}
