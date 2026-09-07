package com.example.xauusdlotsizecalculator.ui.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.ValidationResult
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright

@Composable
fun CalculatorInputs(
    balance: String,
    onBalanceChange: (String) -> Unit,
    riskPercent: String,
    onRiskPercentChange: (String) -> Unit,
    onPresetRiskSelected: (Double) -> Unit,
    entryPrice: String,
    onEntryPriceChange: (String) -> Unit,
    slPercent: String,
    onSlPercentChange: (String) -> Unit,
    selectedLotStep: LotStep,
    onLotStepChange: (LotStep) -> Unit,
    validation: ValidationResult,
    modifier: Modifier = Modifier
) {
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
            // Section 1: Account Balance
            TradingInputField(
                value = balance,
                onValueChange = onBalanceChange,
                label = "Account Balance",
                leadingText = "$",
                placeholder = "2500",
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
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TvPurplePrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = TvSilver
                            ),
                            border = if (isSelected) null else BorderStroke(1.dp, TvDarkSurfaceBorder),
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }

            // Section 3: Entry Price and SL Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TradingInputField(
                    value = entryPrice,
                    onValueChange = onEntryPriceChange,
                    label = "Entry Price",
                    placeholder = "4411.537",
                    errorMessage = validation.entryPriceError,
                    onClear = { onEntryPriceChange("") },
                    modifier = Modifier.weight(1f)
                )

                TradingInputField(
                    value = slPercent,
                    onValueChange = onSlPercentChange,
                    label = "SL Distance %",
                    trailingText = "%",
                    placeholder = "0.131",
                    errorMessage = validation.slPercentError,
                    onClear = { onSlPercentChange("") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Section 4: Lot Step Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Broker Lot Step:",
                    fontSize = 13.sp,
                    color = TvSilver,
                    fontWeight = FontWeight.SemiBold
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
            label = { Text(label, fontSize = 13.sp, color = TvSilver) },
            placeholder = { Text(placeholder, color = TvSilver.copy(alpha = 0.4f)) },
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
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
