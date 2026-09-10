package com.example.xauusdlotsizecalculator.ui.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.Account
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.ValidationResult
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.ui.calculator.SlInputMode
import java.text.DecimalFormat

@Composable
fun CalculatorInputs(
    pair: String = "XAUUSD",
    onPairChange: (String) -> Unit = {},
    availableAccounts: List<Account> = emptyList(),
    selectedAccountId: Long = 1L,
    onSelectAccount: (Long) -> Unit = {},
    balance: String,
    onBalanceChange: (String) -> Unit,
    riskPercent: String,
    onRiskPercentChange: (String) -> Unit,
    onPresetRiskSelected: (Double) -> Unit,
    entryPrice: String,
    onEntryPriceChange: (String) -> Unit,
    slPercent: String,
    onSlPercentChange: (String) -> Unit,
    slPrice: String,
    onSlPriceChange: (String) -> Unit,
    tpPrice: String,
    onTpPriceChange: (String) -> Unit,
    slMode: SlInputMode,
    onSlModeChange: (SlInputMode) -> Unit,
    selectedLotStep: LotStep,
    onLotStepChange: (LotStep) -> Unit,
    validation: ValidationResult,
    modifier: Modifier = Modifier
) {
    var accountDropdownExpanded by remember { mutableStateOf(false) }
    val selectedAccount = availableAccounts.firstOrNull { it.id == selectedAccountId }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Account Selector (if multiple accounts available)
            if (availableAccounts.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Trading Account",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TvSilver
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { accountDropdownExpanded = true },
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = TvPurpleGlow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedAccount?.name ?: "Select Account",
                                        fontWeight = FontWeight.SemiBold,
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
                                                text = "Bal: ${acc.currency}${DecimalFormat("#,##0.00").format(acc.currentBalance)}",
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
                        }
                    }
                }
            }

            // Pair / Symbol Input Field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TradingInputField(
                    value = pair,
                    onValueChange = onPairChange,
                    label = "Pair / Symbol",
                    placeholder = "XAUUSD",
                    onClear = { onPairChange("") }
                )

                // Quick Symbol Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val commonPairs = listOf("XAUUSD", "EURUSD", "GBPUSD", "USDJPY", "BTCUSD")
                    commonPairs.forEach { p ->
                        val isSelected = pair.equals(p, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onPairChange(p) },
                            label = { Text(p, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TvPlumContainer,
                                selectedLabelColor = TvPurpleGlow,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = TvSilver
                            ),
                            border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else BorderStroke(1.dp, TvDarkSurfaceBorder)
                        )
                    }
                }
            }

            // Section 1: Account Balance
            TradingInputField(
                value = balance,
                onValueChange = onBalanceChange,
                label = "Account Balance",
                leadingText = "$",
                placeholder = "5000",
                errorMessage = validation.balanceError,
                onClear = { onBalanceChange("") }
            )

            // Section 2: Risk Percentage
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TradingInputField(
                    value = riskPercent,
                    onValueChange = onRiskPercentChange,
                    label = "Risk Percentage",
                    trailingText = "%",
                    placeholder = "1.0",
                    errorMessage = validation.riskPercentError,
                    onClear = { onRiskPercentChange("") }
                )

                // Quick Risk Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Presets:",
                        fontSize = 12.sp,
                        color = TvSilver,
                        fontWeight = FontWeight.Medium
                    )

                    val presets = listOf(0.5, 1.0, 2.0, 3.0)
                    presets.forEach { preset ->
                        val presetStr = if (preset % 1.0 == 0.0) preset.toInt().toString() else preset.toString()
                        val isSelected = riskPercent.trim() == presetStr
                        FilterChip(
                            selected = isSelected,
                            onClick = { onPresetRiskSelected(preset) },
                            label = { Text("$presetStr%", fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TvPlumContainer,
                                selectedLabelColor = TvPurpleGlow,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = TvSilver
                            ),
                            border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else BorderStroke(1.dp, TvDarkSurfaceBorder)
                        )
                    }
                }
            }

            // Section 3: Entry Price
            TradingInputField(
                value = entryPrice,
                onValueChange = onEntryPriceChange,
                label = "Entry Price",
                placeholder = "2650.00",
                errorMessage = validation.entryPriceError,
                onClear = { onEntryPriceChange("") }
            )

            // Section 4: Stop Loss Input (Toggle between % and Price)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stop Loss",
                        fontSize = 12.sp,
                        color = TvSilver,
                        fontWeight = FontWeight.Medium
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = slMode == SlInputMode.PRICE,
                            onClick = { onSlModeChange(SlInputMode.PRICE) },
                            label = { Text("By Price", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TvPlumContainer,
                                selectedLabelColor = TvPurpleGlow,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = TvSilver
                            ),
                            border = if (slMode == SlInputMode.PRICE) BorderStroke(1.dp, TvPurplePrimary) else BorderStroke(1.dp, TvDarkSurfaceBorder)
                        )
                        FilterChip(
                            selected = slMode == SlInputMode.PERCENT,
                            onClick = { onSlModeChange(SlInputMode.PERCENT) },
                            label = { Text("By %", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TvPlumContainer,
                                selectedLabelColor = TvPurpleGlow,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = TvSilver
                            ),
                            border = if (slMode == SlInputMode.PERCENT) BorderStroke(1.dp, TvPurplePrimary) else BorderStroke(1.dp, TvDarkSurfaceBorder)
                        )
                    }
                }

                if (slMode == SlInputMode.PRICE) {
                    TradingInputField(
                        value = slPrice,
                        onValueChange = onSlPriceChange,
                        label = "SL Price Level",
                        placeholder = "2645.00",
                        onClear = { onSlPriceChange("") }
                    )
                } else {
                    TradingInputField(
                        value = slPercent,
                        onValueChange = onSlPercentChange,
                        label = "SL Distance Percent",
                        trailingText = "%",
                        placeholder = "0.189",
                        errorMessage = validation.slPercentError,
                        onClear = { onSlPercentChange("") }
                    )
                }
            }

            // Section 5: Take Profit Price (Optional)
            TradingInputField(
                value = tpPrice,
                onValueChange = onTpPriceChange,
                label = "Take Profit Price (Optional)",
                placeholder = "2662.50",
                onClear = { onTpPriceChange("") }
            )

            // Section 6: Broker Lot Step Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Broker Lot Step",
                    fontSize = 12.sp,
                    color = TvSilver,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LotStep.entries.forEach { step ->
                        val isSelected = selectedLotStep == step
                        FilterChip(
                            selected = isSelected,
                            onClick = { onLotStepChange(step) },
                            label = {
                                Text(
                                    text = step.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TvPlumContainer,
                                selectedLabelColor = TvPurpleGlow,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = TvSilver
                            ),
                            border = if (isSelected) BorderStroke(1.dp, TvPurplePrimary) else BorderStroke(1.dp, TvDarkSurfaceBorder),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TradingInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingText: String? = null,
    trailingText: String? = null,
    placeholder: String = "",
    errorMessage: String? = null,
    onClear: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 12.sp, color = TvSilver) },
            placeholder = { Text(placeholder, color = TvSilver.copy(alpha = 0.4f), fontSize = 13.sp) },
            leadingIcon = if (leadingText != null) {
                {
                    Text(
                        text = leadingText,
                        color = TvPurpleGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            } else null,
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    if (trailingText != null) {
                        Text(
                            text = trailingText,
                            color = TvPurpleGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    if (value.isNotEmpty() && onClear != null) {
                        IconButton(
                            onClick = onClear,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TvSilver,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            },
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TvSilverBright
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TvPurplePrimary,
                unfocusedBorderColor = TvDarkSurfaceBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                errorBorderColor = TvSilver,
                errorContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}
