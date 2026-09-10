package com.example.xauusdlotsizecalculator.ui.account

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xauusdlotsizecalculator.data.database.AccountCalculatedStats
import com.example.xauusdlotsizecalculator.domain.model.Account
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.DisciplineSettings
import com.example.xauusdlotsizecalculator.theme.FinancialNumericStyle
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvGoldAccent
import com.example.xauusdlotsizecalculator.theme.TvGreenProfit
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurpleNumber
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvRedLoss
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.theme.TvWarning
import com.example.xauusdlotsizecalculator.theme.TvWarningContainer
import com.example.xauusdlotsizecalculator.theme.TvWarningContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvWarningText
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // File Picker for JSON Import
    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val content = context.contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
                    if (!content.isNullOrBlank()) {
                        viewModel.importBackupJson(content)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.onSnackbarDismissed()
        }
    }

    AccountScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onSelectAccount = { viewModel.onSelectAccount(it) },
        onOpenAddAccount = { viewModel.setShowAddAccountDialog(true) },
        onOpenEditAccount = { viewModel.setShowEditAccountDialog(true) },
        onOpenDeleteAccount = { viewModel.setShowDeleteAccountDialog(true) },
        onOpenAdjustBalance = { viewModel.setShowAdjustBalanceDialog(true) },
        onOpenEditDiscipline = { viewModel.setShowEditDisciplineDialog(true) },
        onOpenEditCalculator = { viewModel.setShowEditCalculatorDialog(true) },
        onSaveAccount = { viewModel.onUpdateAccount(it) },
        onAddAccount = { viewModel.onAddAccount(it) },
        onDeleteAccount = { id, moveTradesToId -> viewModel.onDeleteAccount(id, moveTradesToId) },
        onAdjustBalance = { viewModel.onAdjustStartingBalance(it) },
        onSaveDiscipline = { viewModel.onSaveDisciplineSettings(it) },
        onSaveCalculator = { viewModel.onSaveCalculatorSettings(it) },
        onDismissDialogs = {
            viewModel.setShowAddAccountDialog(false)
            viewModel.setShowEditAccountDialog(false)
            viewModel.setShowDeleteAccountDialog(false)
            viewModel.setShowAdjustBalanceDialog(false)
            viewModel.setShowEditDisciplineDialog(false)
            viewModel.setShowEditCalculatorDialog(false)
        },
        onExportCsv = {
            scope.launch {
                val csv = viewModel.getExportCsv()
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, csv)
                    type = "text/csv"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Export Trades as CSV"))
            }
        },
        onExportJson = {
            scope.launch {
                val json = viewModel.getExportJson()
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, json)
                    type = "application/json"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Export TradeLog Backup (JSON)"))
            }
        },
        onImportJson = {
            importJsonLauncher.launch("application/json")
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenContent(
    uiState: AccountUiState,
    snackbarHostState: SnackbarHostState? = null,
    onSelectAccount: (Long) -> Unit = {},
    onOpenAddAccount: () -> Unit = {},
    onOpenEditAccount: () -> Unit = {},
    onOpenDeleteAccount: () -> Unit = {},
    onOpenAdjustBalance: () -> Unit = {},
    onOpenEditDiscipline: () -> Unit = {},
    onOpenEditCalculator: () -> Unit = {},
    onSaveAccount: (Account) -> Unit = {},
    onAddAccount: (Account) -> Unit = {},
    onDeleteAccount: (Long, Long?) -> Unit = { _, _ -> },
    onAdjustBalance: (Double) -> Unit = {},
    onSaveDiscipline: (DisciplineSettings) -> Unit = {},
    onSaveCalculator: (CalculatorSettings) -> Unit = {},
    onDismissDialogs: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    onExportJson: () -> Unit = {},
    onImportJson: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stats = uiState.currentAccountStats
    val activeAccount = stats?.account ?: uiState.availableAccounts.firstOrNull { it.id == uiState.selectedAccountId }
    var accountDropdownExpanded by remember { mutableStateOf(false) }

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
                                text = if (activeAccount?.isPropFirm == true) "PROP" else "LIVE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Trading Accounts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Active Account Header & Switcher
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ACCOUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TvSilver
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { accountDropdownExpanded = true },
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.2.dp, TvPurplePrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = TvPurpleGlow,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = activeAccount?.name ?: "Select Account",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TvSilverBright
                                        )
                                        Text(
                                            text = "${if (activeAccount?.isPropFirm == true) "Prop Account" else "Personal Account"} • ${activeAccount?.currency ?: "$"}${DecimalFormat("#,##0.00").format(stats?.currentBalance ?: activeAccount?.currentBalance ?: 5000.0)}",
                                            fontSize = 11.sp,
                                            color = TvSilver
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = TvSilver
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = accountDropdownExpanded,
                            onDismissRequest = { accountDropdownExpanded = false }
                        ) {
                            uiState.availableAccounts.forEach { acc ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = acc.name,
                                                fontWeight = if (acc.id == uiState.selectedAccountId) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                text = "Balance: ${acc.currency}${DecimalFormat("#,##0.00").format(acc.currentBalance)}",
                                                fontSize = 11.sp,
                                                color = TvSilver
                                            )
                                        }
                                    },
                                    onClick = {
                                        onSelectAccount(acc.id)
                                        accountDropdownExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = TvPurpleGlow, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("+ Add New Account", fontWeight = FontWeight.Bold, color = TvPurpleGlow)
                                    }
                                },
                                onClick = {
                                    accountDropdownExpanded = false
                                    onOpenAddAccount()
                                }
                            )
                        }
                    }

                    // Edit Account Button
                    IconButton(
                        onClick = onOpenEditAccount,
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Account", tint = TvSilver)
                    }

                    // Delete Account Button (only if more than 1 account)
                    if (uiState.availableAccounts.size > 1) {
                        IconButton(
                            onClick = onOpenDeleteAccount,
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Account", tint = TvRedLoss)
                        }
                    }
                }
            }

            // Section 2: Account Overview Dashboard Card
            if (stats != null && activeAccount != null) {
                AccountDashboardCard(
                    stats = stats,
                    onAdjustBalance = onOpenAdjustBalance,
                    onEditRules = onOpenEditAccount
                )
            }

            // Section 3: Personal Discipline Guard
            DisciplineCard(
                discipline = uiState.disciplineSettings,
                todayTrades = stats?.todayTradesCount ?: 0,
                todayPnl = stats?.todayPnl ?: 0.0,
                isSessionLimit = uiState.isSessionLimitExceeded,
                isLossStop = uiState.isDailyLossStopExceeded,
                isProfitStop = uiState.isDailyProfitStopReached,
                onEdit = onOpenEditDiscipline
            )

            // Section 4: Data Management & Calculator Preferences
            SettingsSection(
                calculatorSettings = uiState.calculatorSettings,
                onEditCalculator = onOpenEditCalculator,
                onExportCsv = onExportCsv,
                onExportJson = onExportJson,
                onImportJson = onImportJson
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Dialogs
        if (uiState.showAddAccountDialog) {
            AddEditAccountDialog(
                account = null,
                onSave = { onAddAccount(it) },
                onDismiss = onDismissDialogs
            )
        }

        if (uiState.showEditAccountDialog && activeAccount != null) {
            AddEditAccountDialog(
                account = activeAccount,
                onSave = { onSaveAccount(it) },
                onDismiss = onDismissDialogs
            )
        }

        if (uiState.showDeleteAccountDialog && activeAccount != null) {
            DeleteAccountDialog(
                accountToDelete = activeAccount,
                otherAccounts = uiState.availableAccounts.filter { it.id != activeAccount.id },
                onConfirmDelete = { moveTradesToId ->
                    onDeleteAccount(activeAccount.id, moveTradesToId)
                },
                onDismiss = onDismissDialogs
            )
        }

        if (uiState.showAdjustBalanceDialog && activeAccount != null) {
            AdjustBalanceDialog(
                currentStarting = activeAccount.startingBalance,
                currency = activeAccount.currency,
                onSave = { onAdjustBalance(it) },
                onDismiss = onDismissDialogs
            )
        }

        if (uiState.showEditDisciplineDialog) {
            EditDisciplineDialog(
                current = uiState.disciplineSettings,
                onSave = onSaveDiscipline,
                onDismiss = onDismissDialogs
            )
        }

        if (uiState.showEditCalculatorDialog) {
            EditCalculatorDialog(
                current = uiState.calculatorSettings,
                onSave = onSaveCalculator,
                onDismiss = onDismissDialogs
            )
        }
    }
}

@Composable
private fun AccountDashboardCard(
    stats: AccountCalculatedStats,
    onAdjustBalance: () -> Unit,
    onEditRules: () -> Unit
) {
    val acc = stats.account
    val pnl = stats.totalPnl
    val pnlColor = if (pnl > 0) TvGreenProfit else if (pnl < 0) TvRedLoss else TvSilver
    val pnlSign = if (pnl > 0) "+" else if (pnl < 0) "-" else ""
    val pnlPct = if (acc.startingBalance > 0) (pnl / acc.startingBalance) * 100.0 else 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, TvPurplePrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Shield + Name + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (acc.isPropFirm) Icons.Default.Shield else Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = TvPurpleGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = acc.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvSilverBright
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TvPlumContainer,
                    border = BorderStroke(1.dp, TvDarkSurfaceBorder)
                ) {
                    Text(
                        text = if (acc.isPropFirm) "PROP RULES" else "PERSONAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvPurpleGlow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // 4-Grid Balance & P/L Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricBox(
                    label = "Starting Balance",
                    value = "${acc.currency}${DecimalFormat("#,##0.00").format(acc.startingBalance)}",
                    valueColor = TvSilverBright,
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    label = "Current Balance",
                    value = "${acc.currency}${DecimalFormat("#,##0.00").format(stats.currentBalance)}",
                    valueColor = TvSilverBright,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricBox(
                    label = "Total Realized P/L",
                    value = "$pnlSign${acc.currency}${DecimalFormat("#,##0.00").format(Math.abs(pnl))}",
                    subText = "${DecimalFormat("+0.00;-0.00").format(pnlPct)}%",
                    valueColor = pnlColor,
                    modifier = Modifier.weight(1f)
                )

                val todayPnl = stats.todayPnl
                val todayColor = if (todayPnl > 0) TvGreenProfit else if (todayPnl < 0) TvRedLoss else TvSilver
                val todaySign = if (todayPnl > 0) "+" else if (todayPnl < 0) "-" else ""
                MetricBox(
                    label = "Today's P/L",
                    value = "$todaySign${acc.currency}${DecimalFormat("#,##0.00").format(Math.abs(todayPnl))}",
                    subText = "${stats.todayTradesCount} trades today",
                    valueColor = todayColor,
                    modifier = Modifier.weight(1f)
                )
            }

            // Prop Firm Rules / Loss Limits Section
            if (acc.isPropFirm) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profit Target
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Profit Target (${acc.profitTargetPercent}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TvSilver
                            )
                            Text(
                                text = if (stats.isTargetReached) "Target Achieved! ✓" else "${acc.currency}${DecimalFormat("#,##0.00").format(stats.profitTargetRemainingAmount)} remaining",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stats.isTargetReached) TvGreenProfit else TvSilverBright
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (stats.profitTargetProgressPercent / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = TvGreenProfit,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Max Loss & Daily Loss Remaining
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Max Loss Limit (${acc.maxLossPercent}%)",
                                fontSize = 11.sp,
                                color = TvSilver
                            )
                            Text(
                                text = "${acc.currency}${DecimalFormat("#,##0.00").format(stats.maxLossRemainingAmount)} left",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stats.isMaxLossLimitExceeded) TvRedLoss else TvSilverBright
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Daily Loss Limit (${acc.dailyLossPercent}%)",
                                fontSize = 11.sp,
                                color = TvSilver
                            )
                            Text(
                                text = "${acc.currency}${DecimalFormat("#,##0.00").format(stats.dailyLossRemainingAmount)} left",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stats.isDailyLossLimitExceeded) TvRedLoss else TvSilverBright
                            )
                        }
                    }

                    // Account Specific Trading Specs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Max Gold: ${DecimalFormat("0.00").format(acc.maxGoldLots)} lots",
                            fontSize = 11.sp,
                            color = TvSilver
                        )
                        Text(
                            text = "Leverage: 1:${acc.leverage}",
                            fontSize = 11.sp,
                            color = TvSilver
                        )
                    }
                }
            }

            // Quick Actions: Adjust Balance & Edit Rules
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onAdjustBalance,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Adjust Balance", fontSize = 12.sp)
                }

                Button(
                    onClick = onEditRules,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TvPurplePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit Rules", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    valueColor: Color,
    subText: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TvSilver
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            if (subText != null) {
                Text(
                    text = subText,
                    fontSize = 10.sp,
                    color = TvSilver.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun DisciplineCard(
    discipline: DisciplineSettings,
    todayTrades: Int,
    todayPnl: Double,
    isSessionLimit: Boolean,
    isLossStop: Boolean,
    isProfitStop: Boolean,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = TvPurpleGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Discipline & Psychology Guard",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvSilverBright
                    )
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Rules", tint = TvSilver, modifier = Modifier.size(16.dp))
                }
            }

            // Status alerts
            if (isSessionLimit) {
                WarningBanner("Session limit reached ($todayTrades / ${discipline.maxTradesPerSession} trades). Step away from the charts.")
            }
            if (isLossStop) {
                WarningBanner("Daily loss stop hit ($${DecimalFormat("#,##0.00").format(Math.abs(todayPnl))} / $${DecimalFormat("#,##0.00").format(discipline.dailyLossStop)}). Stop trading for today.")
            }
            if (isProfitStop) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TvPlumContainer,
                    border = BorderStroke(1.dp, TvPurplePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Daily profit target hit (+$${DecimalFormat("#,##0.00").format(todayPnl)})! Protect your capital.",
                        color = TvPurpleGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Max Trades / Day: ${discipline.maxTradesPerSession}", fontSize = 12.sp, color = TvSilver)
                Text(text = "Daily Loss Stop: $${discipline.dailyLossStop.toInt()}", fontSize = 12.sp, color = TvSilver)
                Text(text = "Profit Stop: $${discipline.dailyProfitStop.toInt()}", fontSize = 12.sp, color = TvSilver)
            }
        }
    }
}

@Composable
private fun WarningBanner(message: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = TvWarningContainer,
        border = BorderStroke(1.dp, TvWarningContainerBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = TvWarning, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = message, color = TvWarningText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SettingsSection(
    calculatorSettings: CalculatorSettings,
    onEditCalculator: () -> Unit,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit,
    onImportJson: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Preferences & Data Backup",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TvSilverBright
            )

            // Calculator settings row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onEditCalculator)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = TvPurpleGlow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Calculator Defaults", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TvSilverBright)
                            Text(
                                "Default Size: $${calculatorSettings.defaultAccountSize.toInt()} • Risk: ${calculatorSettings.defaultRiskPercent}%",
                                fontSize = 11.sp,
                                color = TvSilver
                            )
                        }
                    }
                    Icon(Icons.Default.Edit, contentDescription = null, tint = TvSilver, modifier = Modifier.size(16.dp))
                }
            }

            // Export / Import Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExportCsv,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export CSV", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onExportJson,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Backup JSON", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onImportJson,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restore", fontSize = 11.sp)
                }
            }
        }
    }
}

// ==========================================
// DIALOGS WITH VISIBLE BACK BUTTONS
// ==========================================

@Composable
fun AddEditAccountDialog(
    account: Account?,
    onSave: (Account) -> Unit,
    onDismiss: () -> Unit
) {
    val isEdit = account != null
    var name by remember { mutableStateOf(account?.name ?: "") }
    var startingBalanceStr by remember { mutableStateOf(account?.startingBalance?.toInt()?.toString() ?: "5000") }
    var currency by remember { mutableStateOf(account?.currency ?: "$") }
    var isPropFirm by remember { mutableStateOf(account?.isPropFirm ?: true) }
    var profitTargetStr by remember { mutableStateOf(account?.profitTargetPercent?.toString() ?: "10.0") }
    var maxLossStr by remember { mutableStateOf(account?.maxLossPercent?.toString() ?: "6.0") }
    var dailyLossStr by remember { mutableStateOf(account?.dailyLossPercent?.toString() ?: "3.0") }
    var maxGoldStr by remember { mutableStateOf(account?.maxGoldLots?.toString() ?: "0.20") }
    var leverageStr by remember { mutableStateOf(account?.leverage?.toString() ?: "50") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isEdit) "Edit Account & Rules" else "Add Trading Account")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Account Name") },
                    placeholder = { Text("e.g. PropScholar Freedom 5K") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startingBalanceStr,
                        onValueChange = { startingBalanceStr = it },
                        label = { Text("Starting Balance") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1.5f)
                    )

                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Currency") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Account Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isPropFirm,
                        onClick = { isPropFirm = true },
                        label = { Text("Prop Firm") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isPropFirm,
                        onClick = { isPropFirm = false },
                        label = { Text("Personal") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isPropFirm) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = profitTargetStr,
                            onValueChange = { profitTargetStr = it },
                            label = { Text("Target %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxLossStr,
                            onValueChange = { maxLossStr = it },
                            label = { Text("Max Loss %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = dailyLossStr,
                            onValueChange = { dailyLossStr = it },
                            label = { Text("Daily Loss %") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = maxGoldStr,
                            onValueChange = { maxGoldStr = it },
                            label = { Text("Max Gold Lots") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = leverageStr,
                            onValueChange = { leverageStr = it },
                            label = { Text("Leverage (1:X)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = startingBalanceStr.toDoubleOrNull() ?: 5000.0
                    val acc = Account(
                        id = account?.id ?: 0L,
                        name = name.trim().ifBlank { "Account" },
                        currency = currency.trim().ifBlank { "$" },
                        startingBalance = bal,
                        currentBalance = account?.currentBalance ?: bal,
                        profitTargetPercent = profitTargetStr.toDoubleOrNull() ?: 10.0,
                        maxLossPercent = maxLossStr.toDoubleOrNull() ?: 6.0,
                        dailyLossPercent = dailyLossStr.toDoubleOrNull() ?: 3.0,
                        maxGoldLots = maxGoldStr.toDoubleOrNull() ?: 0.20,
                        leverage = leverageStr.toIntOrNull() ?: 50,
                        isPropFirm = isPropFirm
                    )
                    onSave(acc)
                }
            ) {
                Text(if (isEdit) "Save Changes" else "Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DeleteAccountDialog(
    accountToDelete: Account,
    otherAccounts: List<Account>,
    onConfirmDelete: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedChoice by remember { mutableStateOf(if (otherAccounts.isNotEmpty()) "MOVE" else "DELETE") }
    var targetAccountId by remember { mutableStateOf(otherAccounts.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete '${accountToDelete.name}'?")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Do not lose your trading history accidentally. Choose how to handle trades belonging to this account:",
                    fontSize = 13.sp,
                    color = TvSilver
                )

                if (otherAccounts.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedChoice = "MOVE" }
                    ) {
                        RadioButton(
                            selected = selectedChoice == "MOVE",
                            onClick = { selectedChoice = "MOVE" }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Move trades to another account", fontSize = 13.sp)
                    }

                    if (selectedChoice == "MOVE") {
                        Column(modifier = Modifier.padding(start = 32.dp)) {
                            otherAccounts.forEach { acc ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { targetAccountId = acc.id }
                                ) {
                                    RadioButton(
                                        selected = targetAccountId == acc.id,
                                        onClick = { targetAccountId = acc.id }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(acc.name, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedChoice = "DELETE" }
                ) {
                    RadioButton(
                        selected = selectedChoice == "DELETE",
                        onClick = { selectedChoice = "DELETE" }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete associated trades permanently", fontSize = 13.sp, color = TvRedLoss)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val moveTo = if (selectedChoice == "MOVE") targetAccountId else null
                    onConfirmDelete(moveTo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TvRedLoss)
            ) {
                Text("Delete Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdjustBalanceDialog(
    currentStarting: Double,
    currency: String,
    onSave: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var balStr by remember { mutableStateOf(currentStarting.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adjust Starting Balance")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Current balance will recalculate automatically as Starting Balance + Realized P/L.",
                    fontSize = 12.sp,
                    color = TvSilver
                )
                OutlinedTextField(
                    value = balStr,
                    onValueChange = { balStr = it },
                    label = { Text("Starting Balance") },
                    leadingIcon = { Text(currency, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newBal = balStr.toDoubleOrNull() ?: currentStarting
                    onSave(newBal)
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditDisciplineDialog(
    current: DisciplineSettings,
    onSave: (DisciplineSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var maxTrades by remember { mutableStateOf(current.maxTradesPerSession.toString()) }
    var lossStop by remember { mutableStateOf(current.dailyLossStop.toInt().toString()) }
    var profitStop by remember { mutableStateOf(current.dailyProfitStop.toInt().toString()) }
    var autoSync by remember { mutableStateOf(current.autoSyncBalance) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Discipline Rules")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = maxTrades,
                    onValueChange = { maxTrades = it },
                    label = { Text("Max Trades Per Day") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lossStop,
                    onValueChange = { lossStop = it },
                    label = { Text("Daily Loss Stop ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = profitStop,
                    onValueChange = { profitStop = it },
                    label = { Text("Daily Profit Stop ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newSettings = DisciplineSettings(
                        maxTradesPerSession = maxTrades.toIntOrNull() ?: 3,
                        dailyLossStop = lossStop.toDoubleOrNull() ?: 50.0,
                        dailyProfitStop = profitStop.toDoubleOrNull() ?: 100.0,
                        autoSyncBalance = autoSync
                    )
                    onSave(newSettings)
                }
            ) {
                Text("Save Rules")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditCalculatorDialog(
    current: CalculatorSettings,
    onSave: (CalculatorSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var defaultRisk by remember { mutableStateOf(current.defaultRiskPercent.toString()) }
    var defaultSize by remember { mutableStateOf(current.defaultAccountSize.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Calculator Defaults")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = defaultSize,
                    onValueChange = { defaultSize = it },
                    label = { Text("Default Calculator Balance ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = defaultRisk,
                    onValueChange = { defaultRisk = it },
                    label = { Text("Default Risk %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = current.copy(
                        defaultAccountSize = defaultSize.toDoubleOrNull() ?: 5000.0,
                        defaultRiskPercent = defaultRisk.toDoubleOrNull() ?: 1.0
                    )
                    onSave(updated)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
