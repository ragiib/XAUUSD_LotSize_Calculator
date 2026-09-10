package com.example.xauusdlotsizecalculator.ui.journal.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.calculator.XauusdLotCalculator
import com.example.xauusdlotsizecalculator.domain.model.Account
import com.example.xauusdlotsizecalculator.domain.model.DEFAULT_EMOTIONS
import com.example.xauusdlotsizecalculator.domain.model.DEFAULT_MISTAKES
import com.example.xauusdlotsizecalculator.domain.model.DEFAULT_SETUPS
import com.example.xauusdlotsizecalculator.domain.model.SetupQuality
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvGreenProfit
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvRedLoss
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTradeSheet(
    trade: Trade?,
    availableAccounts: List<Account> = emptyList(),
    sheetState: SheetState,
    onSave: (Trade) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        AddEditTradeFormContent(
            trade = trade,
            availableAccounts = availableAccounts,
            onSave = onSave,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTradeFormContent(
    trade: Trade?,
    availableAccounts: List<Account> = emptyList(),
    onSave: (Trade) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isEdit = trade != null && trade.id != 0L

    var selectedAccountId by remember {
        mutableStateOf(trade?.accountId ?: availableAccounts.firstOrNull()?.id ?: 1L)
    }
    var accountDropdownExpanded by remember { mutableStateOf(false) }

    var symbol by remember { mutableStateOf(trade?.symbol ?: "XAUUSD") }
    var direction by remember { mutableStateOf(trade?.direction ?: TradeDirection.BUY) }
    var entryPriceStr by remember { mutableStateOf(if (trade != null && trade.entryPrice > 0) trade.entryPrice.toString() else "") }
    var exitPriceStr by remember { mutableStateOf(trade?.exitPrice?.toString() ?: "") }
    var slPriceStr by remember { mutableStateOf(if (trade != null && trade.stopLossPrice > 0) trade.stopLossPrice.toString() else "") }
    var tpPriceStr by remember { mutableStateOf(trade?.takeProfitPrice?.toString() ?: "") }
    var lotSizeStr by remember { mutableStateOf(if (trade != null && trade.lotSize > 0) trade.lotSize.toString() else "") }
    var riskAmountStr by remember { mutableStateOf(if (trade != null && trade.plannedRiskAmount > 0) trade.plannedRiskAmount.toString() else "") }
    var riskPercentStr by remember { mutableStateOf(trade?.plannedRiskPercent?.toString() ?: "1.0") }

    var status by remember { mutableStateOf(trade?.status ?: TradeStatus.OPEN) }
    var pnlStr by remember { mutableStateOf(trade?.profitLoss?.toString() ?: "") }
    var setup by remember { mutableStateOf(trade?.setup ?: "Liquidity Sweep") }
    var customSetup by remember { mutableStateOf("") }
    var quality by remember { mutableStateOf(trade?.setupQuality ?: SetupQuality.A_PLUS) }

    var selectedMistakes by remember {
        mutableStateOf(
            if (trade?.mistakes.isNullOrEmpty()) setOf("No Mistake")
            else trade!!.mistakes.toSet()
        )
    }
    var emotionBefore by remember { mutableStateOf(trade?.emotionBefore ?: "Calm") }
    var emotionAfter by remember { mutableStateOf(trade?.emotionAfter ?: "Calm") }
    var thinkingNotes by remember { mutableStateOf(trade?.thinkingNotes ?: "") }
    var wouldTakeAgain by remember { mutableStateOf(trade?.wouldTakeAgain ?: true) }
    var lesson by remember { mutableStateOf(trade?.lesson ?: "") }
    var notes by remember { mutableStateOf(trade?.notes ?: "") }
    var screenshotPath by remember { mutableStateOf(trade?.screenshotPath) }

    // Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val screenshotsDir = File(context.filesDir, "screenshots").apply { mkdirs() }
                val targetFile = File(screenshotsDir, "trade_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                screenshotPath = targetFile.absolutePath
            } catch (_: Exception) {}
        }
    }

    // Auto-calculate P/L and R when Exit Price is entered
    fun tryAutoCalculatePnl() {
        val entry = entryPriceStr.toDoubleOrNull() ?: return
        val exit = exitPriceStr.toDoubleOrNull() ?: return
        val sl = slPriceStr.toDoubleOrNull() ?: return
        val lot = lotSizeStr.toDoubleOrNull() ?: return
        val risk = riskAmountStr.toDoubleOrNull() ?: (Math.abs(entry - sl) * lot * 100.0)

        val metrics = XauusdLotCalculator.calculateTradeMetrics(
            entryPrice = entry,
            exitPrice = exit,
            slPrice = sl,
            lotSize = lot,
            contractSize = 100.0,
            direction = direction,
            plannedRiskAmount = risk
        )
        pnlStr = DecimalFormat("#0.00").format(metrics.first)
        status = when {
            metrics.first > 0.01 -> TradeStatus.WIN
            metrics.first < -0.01 -> TradeStatus.LOSS
            else -> TradeStatus.BREAKEVEN
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visible Back Button in Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isEdit) "Edit Journal Trade" else "Log New Trade",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Account Selector
        if (availableAccounts.isNotEmpty()) {
            val currentAccount = availableAccounts.firstOrNull { it.id == selectedAccountId } ?: availableAccounts.first()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ACCOUNT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TvSilver
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { accountDropdownExpanded = true },
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, TvPurplePrimary.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp)
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
                                Text(
                                    text = currentAccount.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TvSilverBright
                                )
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
                        availableAccounts.forEach { acc ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = acc.name,
                                            fontWeight = if (acc.id == selectedAccountId) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = "Balance: ${acc.currency}${DecimalFormat("#,##0.00").format(acc.currentBalance)}",
                                            fontSize = 11.sp,
                                            color = TvSilver
                                        )
                                    }
                                },
                                onClick = {
                                    selectedAccountId = acc.id
                                    accountDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Pair / Symbol Input with Quick Suggestion Chips
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = symbol,
                onValueChange = { symbol = it.uppercase() },
                label = { Text("Pair / Symbol") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("XAUUSD", "EURUSD", "GBPUSD", "USDJPY", "BTCUSD").forEach { p ->
                    val isSelected = symbol.equals(p, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { symbol = p },
                        label = { Text(p, fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvPlumContainer,
                            selectedLabelColor = TvPurpleGlow
                        ),
                        border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else null
                    )
                }
            }
        }

        // Direction Selector: BUY / SELL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(TradeDirection.BUY, TradeDirection.SELL).forEach { dir ->
                val isSelected = direction == dir
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) (if (dir == TradeDirection.BUY) TvBuyColor else TvSilver.copy(alpha = 0.3f)) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { direction = dir }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = dir.name,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TvSilver
                        )
                    }
                }
            }
        }

        // Entry & Exit Price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = entryPriceStr,
                onValueChange = { entryPriceStr = it },
                label = { Text("Entry Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = exitPriceStr,
                onValueChange = {
                    exitPriceStr = it
                    tryAutoCalculatePnl()
                },
                label = { Text("Exit Price") },
                placeholder = { Text("Optional") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        // Stop Loss & Take Profit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = slPriceStr,
                onValueChange = { slPriceStr = it },
                label = { Text("Stop Loss") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = tpPriceStr,
                onValueChange = { tpPriceStr = it },
                label = { Text("Take Profit") },
                placeholder = { Text("Optional") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        // Lot Size & Risk $
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = lotSizeStr,
                onValueChange = { lotSizeStr = it },
                label = { Text("Lot Size") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = riskAmountStr,
                onValueChange = { riskAmountStr = it },
                label = { Text("Risk Amount ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        // Trade Status Selector (OPEN, WIN, LOSS, BREAKEVEN)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Trade Result Status:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TvSilver)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TradeStatus.entries.forEach { st ->
                    val isSelected = status == st
                    FilterChip(
                        selected = isSelected,
                        onClick = { status = st },
                        label = { Text(st.displayName, fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (st) {
                                TradeStatus.WIN -> TvGreenProfit
                                TradeStatus.LOSS -> TvRedLoss
                                TradeStatus.BREAKEVEN -> TvSilver.copy(alpha = 0.4f)
                                TradeStatus.OPEN -> TvPurplePrimary
                            },
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = TvSilver
                        ),
                        border = null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Profit / Loss ($) if Closed
        if (status != TradeStatus.OPEN) {
            OutlinedTextField(
                value = pnlStr,
                onValueChange = { pnlStr = it },
                label = { Text("Realized Profit / Loss ($)") },
                placeholder = { Text(if (status == TradeStatus.WIN) "+150.00" else if (status == TradeStatus.LOSS) "-100.00" else "0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Setup Selection
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Setup / Strategy:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TvSilver)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DEFAULT_SETUPS.forEach { s ->
                    val isSelected = setup == s
                    FilterChip(
                        selected = isSelected,
                        onClick = { setup = s },
                        label = { Text(s, fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvPlumContainer,
                            selectedLabelColor = TvPurpleGlow,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = TvSilver
                        ),
                        border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else null
                    )
                }
            }

            if (setup == "Other") {
                OutlinedTextField(
                    value = customSetup,
                    onValueChange = { customSetup = it },
                    label = { Text("Enter Custom Setup Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Setup Quality
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Execution Quality Grade:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TvSilver)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SetupQuality.entries.forEach { q ->
                    val isSelected = quality == q
                    FilterChip(
                        selected = isSelected,
                        onClick = { quality = q },
                        label = { Text(q.fullLabel, fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TvPlumContainer,
                            selectedLabelColor = TvPurpleGlow,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = TvSilver
                        ),
                        border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Mistakes Tracker (Including "Tight SL")
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Mistakes (Select all that apply):", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TvSilver)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DEFAULT_MISTAKES.forEach { m ->
                    val isNoMistakeOption = m.equals("No Mistake", ignoreCase = true)
                    val isSelected = selectedMistakes.any { it.equals(m, ignoreCase = true) }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedMistakes = if (isNoMistakeOption) {
                                setOf("No Mistake")
                            } else {
                                val updated = selectedMistakes.filter { !it.equals("No Mistake", ignoreCase = true) }.toMutableSet()
                                if (isSelected) {
                                    updated.removeAll { it.equals(m, ignoreCase = true) }
                                } else {
                                    updated.add(m)
                                }
                                if (updated.isEmpty()) setOf("No Mistake") else updated
                            }
                        },
                        label = { Text(m, fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isNoMistakeOption) TvPlumContainer else TvRedLoss.copy(alpha = 0.2f),
                            selectedLabelColor = if (isNoMistakeOption) TvPurpleGlow else TvRedLoss,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = TvSilver
                        ),
                        border = if (isSelected) BorderStroke(1.dp, if (isNoMistakeOption) TvPurplePrimary else TvRedLoss) else null
                    )
                }
            }
        }

        // Psychology & Emotions
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Emotion Before Trade:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TvSilver)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DEFAULT_EMOTIONS.forEach { emo ->
                    val isSelected = emotionBefore == emo
                    FilterChip(
                        selected = isSelected,
                        onClick = { emotionBefore = emo },
                        label = { Text(emo, fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else null
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "Emotion After Trade:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TvSilver)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DEFAULT_EMOTIONS.forEach { emo ->
                    val isSelected = emotionAfter == emo
                    FilterChip(
                        selected = isSelected,
                        onClick = { emotionAfter = emo },
                        label = { Text(emo, fontSize = 11.sp) },
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else null
                    )
                }
            }
        }

        OutlinedTextField(
            value = thinkingNotes,
            onValueChange = { thinkingNotes = it },
            label = { Text("What was I thinking? (Mental state notes)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Review Questions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Would you take this trade again?", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TvSilver)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = wouldTakeAgain,
                    onClick = { wouldTakeAgain = true },
                    label = { Text("YES") },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TvGreenProfit,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = !wouldTakeAgain,
                    onClick = { wouldTakeAgain = false },
                    label = { Text("NO") },
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TvRedLoss,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        OutlinedTextField(
            value = lesson,
            onValueChange = { lesson = it },
            label = { Text("Key Lesson / Takeaway") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("General Notes") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Screenshot Attachment Button
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, TvDarkSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { imagePickerLauncher.launch("image/*") }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = TvPurpleGlow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (screenshotPath != null) "Chart Screenshot Attached ✓" else "Attach Chart Screenshot",
                    fontWeight = FontWeight.Medium,
                    color = if (screenshotPath != null) TvGreenProfit else TvSilverBright
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val entry = entryPriceStr.toDoubleOrNull() ?: 0.0
                    val exit = exitPriceStr.toDoubleOrNull()
                    val sl = slPriceStr.toDoubleOrNull() ?: 0.0
                    val tp = tpPriceStr.toDoubleOrNull()
                    val lot = lotSizeStr.toDoubleOrNull() ?: 0.0
                    val plannedRisk = riskAmountStr.toDoubleOrNull() ?: 0.0
                    val riskPct = riskPercentStr.toDoubleOrNull() ?: 1.0

                    val slDist = Math.abs(entry - sl)
                    val plannedRr = if (tp != null && slDist > 0) Math.abs(tp - entry) / slDist else null

                    val finalSetup = if (setup == "Other" && customSetup.isNotBlank()) customSetup.trim() else setup

                    val pnl = pnlStr.toDoubleOrNull()
                    val actualRisk = if (plannedRisk > 0.0) plannedRisk else (slDist * lot * 100.0)
                    val rMult = if (pnl != null && actualRisk > 0.0) pnl / actualRisk else null
                    val pnlPct = if (pnl != null && actualRisk > 0.0) (pnl / actualRisk) * 100.0 else null

                    val updatedTrade = Trade(
                        id = trade?.id ?: 0L,
                        accountId = selectedAccountId,
                        dateEpochMs = trade?.dateEpochMs ?: System.currentTimeMillis(),
                        symbol = symbol.trim().ifBlank { "XAUUSD" }.uppercase(),
                        direction = direction,
                        entryPrice = entry,
                        exitPrice = exit,
                        stopLossPrice = sl,
                        takeProfitPrice = tp,
                        lotSize = lot,
                        plannedRiskAmount = plannedRisk,
                        plannedRiskPercent = riskPct,
                        slDistance = slDist,
                        plannedRrRatio = plannedRr,
                        status = status,
                        profitLoss = pnl,
                        profitLossPercent = pnlPct,
                        rMultiple = rMult,
                        setup = finalSetup,
                        setupQuality = quality,
                        mistakes = selectedMistakes.toList(),
                        emotionBefore = emotionBefore,
                        emotionAfter = emotionAfter,
                        thinkingNotes = thinkingNotes,
                        wouldTakeAgain = wouldTakeAgain,
                        lesson = lesson,
                        notes = notes,
                        screenshotPath = screenshotPath
                    )
                    onSave(updatedTrade)
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TvPurplePrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isEdit) "Save Changes" else "Log Trade", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(name = "Add / Edit Trade Sheet", showBackground = true)
@Composable
fun AddEditTradeSheetPreview() {
    val sampleTrade = Trade(
        id = 1,
        accountId = 1,
        symbol = "XAUUSD",
        direction = TradeDirection.BUY,
        entryPrice = 2650.00,
        stopLossPrice = 2645.00,
        takeProfitPrice = 2662.50,
        lotSize = 0.10,
        plannedRiskAmount = 50.00,
        plannedRiskPercent = 1.0,
        setup = "Liquidity Sweep",
        setupQuality = SetupQuality.A_PLUS
    )

    XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            AddEditTradeFormContent(
                trade = sampleTrade,
                onSave = {},
                onDismiss = {}
            )
        }
    }
}
