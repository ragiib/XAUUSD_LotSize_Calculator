package com.example.xauusdlotsizecalculator.ui.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    sheetState: SheetState,
    currentSettings: CalculatorSettings,
    onSave: (CalculatorSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var riskInput by remember { mutableStateOf(currentSettings.defaultRiskPercent.toString().removeSuffix(".0")) }
    var selectedLotStep by remember { mutableStateOf(currentSettings.defaultLotStep) }
    var contractSizeInput by remember { mutableStateOf(currentSettings.contractSize.toString().removeSuffix(".0")) }
    var selectedRoundingMode by remember { mutableStateOf(currentSettings.roundingMode) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Calculator Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Default Risk Percentage
            OutlinedTextField(
                value = riskInput,
                onValueChange = { riskInput = it },
                label = { Text("Default Risk %") },
                trailingIcon = { Text("%", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Default Lot Step
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Default Broker Lot Step",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LotStep.entries.forEach { step ->
                        val isSelected = selectedLotStep == step
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedLotStep = step },
                            label = { Text(step.displayName, fontSize = 13.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Rounding Mode
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Lot Rounding Mode",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LotRoundingMode.entries.forEach { mode ->
                        val isSelected = selectedRoundingMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRoundingMode = mode },
                            label = { Text(mode.displayName, fontSize = 12.sp) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Text(
                    text = selectedRoundingMode.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            // Contract Size
            OutlinedTextField(
                value = contractSizeInput,
                onValueChange = { contractSizeInput = it },
                label = { Text("Contract Size (oz per 1.00 lot)") },
                placeholder = { Text("100") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        val riskVal = riskInput.toDoubleOrNull() ?: 1.0
                        val contractSizeVal = contractSizeInput.toDoubleOrNull() ?: 100.0
                        val newSettings = CalculatorSettings(
                            defaultRiskPercent = riskVal,
                            defaultLotStep = selectedLotStep,
                            contractSize = contractSizeVal,
                            roundingMode = selectedRoundingMode
                        )
                        onSave(newSettings)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Settings")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
