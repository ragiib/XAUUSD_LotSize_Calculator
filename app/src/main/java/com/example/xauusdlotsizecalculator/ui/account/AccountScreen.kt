package com.example.xauusdlotsizecalculator.ui.account

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.DisciplineSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.PropFirmSettings
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
        onEditProp = { viewModel.setShowEditPropDialog(true) },
        onEditDiscipline = { viewModel.setShowEditDisciplineDialog(true) },
        onAdjustBalance = { viewModel.setShowEditBalanceDialog(true) },
        onEditCalculator = { viewModel.setShowEditCalculatorDialog(true) },
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
        onSaveBalance = { viewModel.onUpdateBalance(it) },
        onDismissBalanceDialog = { viewModel.setShowEditBalanceDialog(false) },
        onSaveProp = { viewModel.onSavePropFirmSettings(it) },
        onDismissPropDialog = { viewModel.setShowEditPropDialog(false) },
        onSaveDiscipline = { viewModel.onSaveDisciplineSettings(it) },
        onDismissDisciplineDialog = { viewModel.setShowEditDisciplineDialog(false) },
        onSaveCalculator = { viewModel.onSaveCalculatorSettings(it) },
        onDismissCalculatorDialog = { viewModel.setShowEditCalculatorDialog(false) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenContent(
    uiState: AccountUiState,
    snackbarHostState: SnackbarHostState? = null,
    onEditProp: () -> Unit = {},
    onEditDiscipline: () -> Unit = {},
    onAdjustBalance: () -> Unit = {},
    onEditCalculator: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    onExportJson: () -> Unit = {},
    onImportJson: () -> Unit = {},
    onSaveBalance: (Double) -> Unit = {},
    onDismissBalanceDialog: () -> Unit = {},
    onSaveProp: (PropFirmSettings) -> Unit = {},
    onDismissPropDialog: () -> Unit = {},
    onSaveDiscipline: (DisciplineSettings) -> Unit = {},
    onDismissDisciplineDialog: () -> Unit = {},
    onSaveCalculator: (CalculatorSettings) -> Unit = {},
    onDismissCalculatorDialog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                                text = "PROP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Account & Rules",
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
            // Section 1: PropScholar Dashboard Card
            PropFirmCard(
                propSettings = uiState.propFirmSettings,
                currentBalance = uiState.currentBalance,
                onEdit = onEditProp
            )

            // Section 2: Personal Trading Limits & Discipline Guard
            DisciplineCard(
                discipline = uiState.disciplineSettings,
                todayTrades = uiState.todayTradesCount,
                todayPnl = uiState.todayPnl,
                isSessionLimit = uiState.isSessionLimitExceeded,
                isLossStop = uiState.isDailyLossStopExceeded,
                isProfitStop = uiState.isDailyProfitStopReached,
                onEdit = onEditDiscipline
            )

            // Section 3: Balance Reconcile & Tracking
            BalanceCard(
                currentBalance = uiState.currentBalance,
                autoSync = uiState.disciplineSettings.autoSyncBalance,
                onAdjustBalance = onAdjustBalance
            )

            // Section 4: Settings & Data Management
            SettingsSection(
                calculatorSettings = uiState.calculatorSettings,
                onEditCalculator = onEditCalculator,
                onExportCsv = onExportCsv,
                onExportJson = onExportJson,
                onImportJson = onImportJson
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Dialogs
        if (uiState.showEditBalanceDialog) {
            EditBalanceDialog(
                currentBalance = uiState.currentBalance,
                onSave = onSaveBalance,
                onDismiss = onDismissBalanceDialog
            )
        }

        if (uiState.showEditPropDialog) {
            EditPropFirmDialog(
                current = uiState.propFirmSettings,
                onSave = onSaveProp,
                onDismiss = onDismissPropDialog
            )
        }

        if (uiState.showEditDisciplineDialog) {
            EditDisciplineDialog(
                current = uiState.disciplineSettings,
                onSave = onSaveDiscipline,
                onDismiss = onDismissDisciplineDialog
            )
        }

        if (uiState.showEditCalculatorDialog) {
            EditCalculatorDialog(
                current = uiState.calculatorSettings,
                onSave = onSaveCalculator,
                onDismiss = onDismissCalculatorDialog
            )
        }
    }
}

@Composable
private fun PropFirmCard(
    propSettings: PropFirmSettings,
    currentBalance: Double,
    onEdit: () -> Unit
) {
    val starting = propSettings.startingBalance
    val profit = currentBalance - starting
    val targetAmount = propSettings.profitTargetAmount
    val progressPercent = if (targetAmount > 0) ((profit / targetAmount) * 100.0).coerceIn(0.0, 100.0) else 0.0
    val remaining = (targetAmount - profit).coerceAtLeast(0.0)

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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = TvPurpleGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = propSettings.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvSilverBright
                    )
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TvSilver, modifier = Modifier.size(18.dp))
                }
            }

            // Balances & Target Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "$${DecimalFormat("#,##0.00").format(currentBalance)}",
                        style = FinancialNumericStyle.copy(fontSize = 32.sp, color = TvPurpleNumber)
                    )
                    Text(
                        text = "Starting: $${DecimalFormat("#,##0").format(starting)}",
                        fontSize = 12.sp,
                        color = TvSilver
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    val pSign = if (profit >= 0) "+" else "-"
                    val pColor = if (profit >= 0) TvGreenProfit else TvRedLoss
                    Text(
                        text = "$pSign$${DecimalFormat("#,##0.00").format(Math.abs(profit))}",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = pColor
                    )
                    Text(
                        text = "Target: +$${DecimalFormat("#,##0").format(targetAmount)} (${propSettings.profitTargetPercent}%)",
                        fontSize = 12.sp,
                        color = TvSilver
                    )
                }
            }

            // Progress Bar towards Target
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Phase 1 Target Progress: ${DecimalFormat("0.0").format(progressPercent)}%",
                        fontSize = 12.sp,
                        color = TvSilverBright
                    )
                    Text(
                        text = "$${DecimalFormat("#,##0.00").format(remaining)} to target",
                        fontSize = 12.sp,
                        color = TvPurpleGlow
                    )
                }

                LinearProgressIndicator(
                    progress = { (progressPercent / 100.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = TvGreenProfit,
                    trackColor = TvPlumContainer
                )
            }

            // Risk Limits Strip
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LimitItem("Max Loss (6%)", "$${DecimalFormat("#,##0").format(propSettings.maxLossAmount)}")
                    LimitItem("Daily Loss (3%)", "$${DecimalFormat("#,##0").format(propSettings.dailyLossAmount)}")
                    LimitItem("XAUUSD Open Limit", "${DecimalFormat("0.00").format(propSettings.maxGoldVolumeLots)} lots")
                }
            }

            Text(
                text = "Manual tracking tool • Not affiliated with or connected to PropScholar API",
                fontSize = 10.sp,
                color = TvSilver.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
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
                        text = "PERSONAL DISCIPLINE GUARD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TvSilver
                    )
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TvSilver, modifier = Modifier.size(18.dp))
                }
            }

            // Warning Banners if Limits Reached
            if (isSessionLimit) {
                DisciplineWarningBanner(
                    icon = Icons.Default.WarningAmber,
                    title = "Session Limit Reached",
                    message = "You have taken $todayTrades of ${discipline.maxTradesPerSession} planned trades today. Protect your edge and stop overtrading."
                )
            }

            if (isLossStop) {
                DisciplineWarningBanner(
                    icon = Icons.Default.WarningAmber,
                    title = "Daily Loss Stop Hit",
                    message = "Daily loss of $${DecimalFormat("#,##0.00").format(Math.abs(todayPnl))} exceeds your -$${discipline.dailyLossStop} stop. Step away from charts."
                )
            }

            if (isProfitStop) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TvGreenProfit.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, TvGreenProfit.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎯", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily profit target (+$${discipline.dailyProfitStop}) hit! Lock in gains and protect capital.",
                            fontSize = 12.sp,
                            color = TvGreenProfit
                        )
                    }
                }
            }

            if (!isSessionLimit && !isLossStop && !isProfitStop) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🟢 Trading discipline in good standing: $todayTrades/${discipline.maxTradesPerSession} trades taken today.",
                        fontSize = 12.sp,
                        color = TvSilverBright,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LimitItem("Max Trades/Session", "${discipline.maxTradesPerSession}")
                LimitItem("Daily Profit Stop", "+$${DecimalFormat("#,##0").format(discipline.dailyProfitStop)}")
                LimitItem("Daily Loss Stop", "-$${DecimalFormat("#,##0").format(discipline.dailyLossStop)}")
            }
        }
    }
}

@Composable
private fun DisciplineWarningBanner(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = TvWarningContainer,
        border = BorderStroke(1.2.dp, TvWarningContainerBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = TvWarning, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TvWarningText)
                Text(text = message, fontSize = 11.sp, color = TvSilverBright)
            }
        }
    }
}

@Composable
private fun BalanceCard(
    currentBalance: Double,
    autoSync: Boolean,
    onAdjustBalance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "CURRENT ACCOUNT BALANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TvSilver)
                Text(
                    text = "$${DecimalFormat("#,##0.00").format(currentBalance)}",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TvSilverBright
                )
                Text(
                    text = if (autoSync) "Auto-updates with logged trades" else "Manual sync only",
                    fontSize = 11.sp,
                    color = TvSilver
                )
            }

            Button(
                onClick = onAdjustBalance,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TvPlumContainer, contentColor = TvPurpleGlow),
                border = BorderStroke(1.dp, TvDarkSurfaceBorder)
            ) {
                Text("Adjust")
            }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "CONFIG & DATA MANAGEMENT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TvSilver
            )

            // Calculator Parameters Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, TvDarkSurfaceBorder),
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
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = TvPurpleGlow, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Calculator Specifications", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TvSilverBright)
                            Text(
                                text = "${calculatorSettings.contractSize.toInt()} oz/lot • Step ${calculatorSettings.defaultLotStep.displayName} • ${calculatorSettings.roundingMode.displayName}",
                                fontSize = 11.sp,
                                color = TvSilver
                            )
                        }
                    }
                    Icon(Icons.Default.Edit, contentDescription = null, tint = TvSilver, modifier = Modifier.size(16.dp))
                }
            }

            // Export Trades CSV
            OutlinedButton(
                onClick = onExportCsv,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Trades (CSV Spreadsheet)")
            }

            // Export Full JSON Backup
            OutlinedButton(
                onClick = onExportJson,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Complete Backup (JSON)")
            }

            // Import Backup JSON
            OutlinedButton(
                onClick = onImportJson,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Import Backup (JSON)")
            }
        }
    }
}

@Composable
private fun LimitItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = TvSilver)
        Text(text = value, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = TvSilverBright)
    }
}

@Composable
private fun EditBalanceDialog(
    currentBalance: Double,
    onSave: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var balanceInput by remember { mutableStateOf(currentBalance.toString().removeSuffix(".0")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adjust Current Balance") },
        text = {
            OutlinedTextField(
                value = balanceInput,
                onValueChange = { balanceInput = it },
                label = { Text("Account Balance ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                val bal = balanceInput.toDoubleOrNull() ?: currentBalance
                onSave(bal)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditPropFirmDialog(
    current: PropFirmSettings,
    onSave: (PropFirmSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(current.name) }
    var startingBalance by remember { mutableStateOf(current.startingBalance.toString().removeSuffix(".0")) }
    var targetPct by remember { mutableStateOf(current.profitTargetPercent.toString().removeSuffix(".0")) }
    var maxLossPct by remember { mutableStateOf(current.maxLossPercent.toString().removeSuffix(".0")) }
    var dailyLossPct by remember { mutableStateOf(current.dailyLossPercent.toString().removeSuffix(".0")) }
    var maxGoldLot by remember { mutableStateOf(current.maxGoldVolumeLots.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Prop Firm Rules") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Prop Firm Program") }, singleLine = true)
                OutlinedTextField(value = startingBalance, onValueChange = { startingBalance = it }, label = { Text("Starting Balance ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = targetPct, onValueChange = { targetPct = it }, label = { Text("Profit Target (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = maxLossPct, onValueChange = { maxLossPct = it }, label = { Text("Max Overall Loss (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = dailyLossPct, onValueChange = { dailyLossPct = it }, label = { Text("Daily Loss Limit (%)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = maxGoldLot, onValueChange = { maxGoldLot = it }, label = { Text("XAUUSD Max Open Volume (lots)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    current.copy(
                        name = name,
                        startingBalance = startingBalance.toDoubleOrNull() ?: current.startingBalance,
                        profitTargetPercent = targetPct.toDoubleOrNull() ?: current.profitTargetPercent,
                        maxLossPercent = maxLossPct.toDoubleOrNull() ?: current.maxLossPercent,
                        dailyLossPercent = dailyLossPct.toDoubleOrNull() ?: current.dailyLossPercent,
                        maxGoldVolumeLots = maxGoldLot.toDoubleOrNull() ?: current.maxGoldVolumeLots
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditDisciplineDialog(
    current: DisciplineSettings,
    onSave: (DisciplineSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var maxTrades by remember { mutableStateOf(current.maxTradesPerSession.toString()) }
    var profitStop by remember { mutableStateOf(current.dailyProfitStop.toString().removeSuffix(".0")) }
    var lossStop by remember { mutableStateOf(current.dailyLossStop.toString().removeSuffix(".0")) }
    var autoSync by remember { mutableStateOf(current.autoSyncBalance) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Personal Discipline Rules") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = maxTrades, onValueChange = { maxTrades = it }, label = { Text("Max Trades per Session") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = profitStop, onValueChange = { profitStop = it }, label = { Text("Daily Profit Stop ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = lossStop, onValueChange = { lossStop = it }, label = { Text("Daily Loss Stop ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-update balance with trades", fontSize = 13.sp)
                    Switch(checked = autoSync, onCheckedChange = { autoSync = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    DisciplineSettings(
                        maxTradesPerSession = maxTrades.toIntOrNull() ?: current.maxTradesPerSession,
                        dailyProfitStop = profitStop.toDoubleOrNull() ?: current.dailyProfitStop,
                        dailyLossStop = lossStop.toDoubleOrNull() ?: current.dailyLossStop,
                        autoSyncBalance = autoSync
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditCalculatorDialog(
    current: CalculatorSettings,
    onSave: (CalculatorSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var risk by remember { mutableStateOf(current.defaultRiskPercent.toString().removeSuffix(".0")) }
    var contractSize by remember { mutableStateOf(current.contractSize.toString().removeSuffix(".0")) }
    var lotStep by remember { mutableStateOf(current.defaultLotStep) }
    var roundingMode by remember { mutableStateOf(current.roundingMode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Calculator Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = risk, onValueChange = { risk = it }, label = { Text("Default Risk %") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
                OutlinedTextField(value = contractSize, onValueChange = { contractSize = it }, label = { Text("Contract Size (oz / 1.0 lot)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)

                Text("Broker Lot Step:", fontSize = 12.sp, color = TvSilver)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LotStep.entries.forEach { s ->
                        FilterChip(
                            selected = lotStep == s,
                            onClick = { lotStep = s },
                            label = { Text(s.displayName) }
                        )
                    }
                }

                Text("Rounding Mode:", fontSize = 12.sp, color = TvSilver)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    LotRoundingMode.entries.forEach { m ->
                        FilterChip(
                            selected = roundingMode == m,
                            onClick = { roundingMode = m },
                            label = { Text(m.displayName) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    CalculatorSettings(
                        defaultRiskPercent = risk.toDoubleOrNull() ?: current.defaultRiskPercent,
                        defaultLotStep = lotStep,
                        contractSize = contractSize.toDoubleOrNull() ?: current.contractSize,
                        roundingMode = roundingMode
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(name = "PropScholar Account & Discipline Guard", showBackground = true)
@Composable
fun AccountScreenPreview() {
    com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        AccountScreenContent(
            uiState = AccountUiState(
                propFirmSettings = PropFirmSettings(
                    name = "PropScholar Freedom 5K",
                    startingBalance = 5000.0,
                    profitTargetPercent = 8.0,
                    maxLossPercent = 6.0,
                    dailyLossPercent = 3.0,
                    maxGoldVolumeLots = 0.20
                ),
                disciplineSettings = DisciplineSettings(
                    maxTradesPerSession = 4,
                    dailyProfitStop = 150.0,
                    dailyLossStop = 100.0,
                    autoSyncBalance = true
                ),
                calculatorSettings = CalculatorSettings(
                    defaultRiskPercent = 1.0,
                    defaultLotStep = LotStep.STEP_0_01,
                    contractSize = 100.0,
                    roundingMode = LotRoundingMode.ROUND_DOWN
                ),
                currentBalance = 5285.50,
                todayTradesCount = 2,
                todayPnl = 85.50,
                isSessionLimitExceeded = false,
                isDailyLossStopExceeded = false,
                isDailyProfitStopReached = false
            )
        )
    }
}
