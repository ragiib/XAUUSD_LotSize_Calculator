package com.example.xauusdlotsizecalculator.ui.calculator.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.CalculationResult
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPlumContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright

@Composable
fun CalculationDetailsCard(
    result: CalculationResult?,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
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
                .padding(16.dp)
        ) {
            // Header Row (Always clickable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpanded),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = TvPurpleGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Calculation Details & Formulas",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TvSilverBright
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = TvSilver
                )
            }

            // Expanded Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FormulaStepItem(
                        stepNumber = "1",
                        title = "Maximum Dollar Risk Amount",
                        formula = "Balance × Risk % ÷ 100",
                        calculation = if (result != null) {
                            "${result.riskAmount} = Risk Amount"
                        } else null
                    )

                    FormulaStepItem(
                        stepNumber = "2",
                        title = "Stop Loss Price Distance",
                        formula = "Entry Price × SL % ÷ 100",
                        calculation = if (result != null) {
                            "${result.formattedSlDistance} pts = Price Distance"
                        } else null
                    )

                    FormulaStepItem(
                        stepNumber = "3",
                        title = "Target Stop Loss Price (${result?.direction?.label ?: "BUY/SELL"})",
                        formula = if (result?.direction?.isBuy == true) "Entry Price − SL Distance" else "Entry Price + SL Distance",
                        calculation = if (result != null) {
                            "Target SL = ${result.formattedSlPrice}"
                        } else null
                    )

                    FormulaStepItem(
                        stepNumber = "4",
                        title = "Dollar Risk per 1.00 Lot",
                        formula = "SL Distance × Contract Size (100 oz)",
                        calculation = if (result != null) {
                            "${result.formattedSlDistance} × 100 = $${result.dollarRiskPerLot.setScale(2, java.math.RoundingMode.HALF_UP)} / lot"
                        } else null
                    )

                    FormulaStepItem(
                        stepNumber = "5",
                        title = "Exact Required Lot Size",
                        formula = "Risk Amount ÷ (SL Distance × Contract Size)",
                        calculation = if (result != null) {
                            "${result.formattedRiskAmount} ÷ $${result.dollarRiskPerLot.setScale(2, java.math.RoundingMode.HALF_UP)} = ${result.formattedExactLot} lots"
                        } else null
                    )

                    FormulaStepItem(
                        stepNumber = "6",
                        title = "Broker Lot Size Rounding",
                        formula = if (result?.roundingMode == LotRoundingMode.ROUND_DOWN) {
                            "Floor(Exact Lot ÷ Lot Step) × Lot Step (Risk-Safe)"
                        } else {
                            "RoundNearest(Exact Lot ÷ Lot Step) × Lot Step"
                        },
                        calculation = if (result != null) {
                            "Step ${result.lotStep.displayName} → ${result.formattedBrokerLot} lots (Risk: ${result.formattedActualRisk})"
                        } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun FormulaStepItem(
    stepNumber: String,
    title: String,
    formula: String,
    calculation: String?
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TvPlumContainer,
                    border = BorderStroke(1.dp, TvPlumContainerBorder),
                    modifier = Modifier.size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stepNumber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TvPurpleGlow
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TvSilverBright
                )
            }

            Text(
                text = formula,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = TvPurpleGlow,
                modifier = Modifier.padding(start = 28.dp)
            )

            if (calculation != null) {
                Text(
                    text = calculation,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TvSilver,
                    modifier = Modifier.padding(start = 28.dp)
                )
            }
        }
    }
}
