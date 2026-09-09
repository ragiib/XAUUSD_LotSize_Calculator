package com.example.xauusdlotsizecalculator.ui.calculator.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.CalculationResult
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.theme.FinancialNumericStyle
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvLightGrey
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPlumContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurpleNumber
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.theme.TvWarning
import com.example.xauusdlotsizecalculator.theme.TvWarningContainer
import com.example.xauusdlotsizecalculator.theme.TvWarningContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvWarningText

@Composable
fun ResultCard(
    result: CalculationResult?,
    onCopyFeedback: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPropFirmLimitExceeded: Boolean = false,
    propFirmWarningMessage: String? = null,
    propFirmRiskAtLimitText: String? = null,
    onSaveAsTrade: (() -> Unit)? = null
) {
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    TvPurplePrimary,
                    TvPlumContainerBorder,
                    TvDarkSurfaceBorder
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        if (result == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Enter valid trade parameters above to calculate lot size",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TvSilver
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECOMMENDED LOT SIZE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TvPurpleGlow
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TvPlumContainer,
                        border = BorderStroke(1.dp, TvPlumContainerBorder)
                    ) {
                        Text(
                            text = if (result.roundingMode == LotRoundingMode.ROUND_DOWN) "Round Down (Safe)" else "Nearest Step",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TvPurpleGlow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Primary Hero Lot Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = result.formattedBrokerLot,
                                style = FinancialNumericStyle.copy(
                                    fontSize = 42.sp,
                                    color = TvPurpleNumber
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "lots",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TvSilver,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Text(
                            text = "Exact Calculated: ${result.formattedExactLot} lots",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TvSilver
                        )
                    }

                    // Copy Lot Size Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TvPlumContainer)
                            .border(BorderStroke(1.dp, TvPlumContainerBorder), RoundedCornerShape(12.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(result.formattedBrokerLot))
                                onCopyFeedback("Copied ${result.formattedBrokerLot} lots to clipboard")
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Lot Size",
                                tint = TvPurpleGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Copy",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TvPurpleGlow
                            )
                        }
                    }
                }

                // PropScholar Risk Guard Warning (Prominent warning when limit exceeded)
                if (isPropFirmLimitExceeded && propFirmWarningMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = TvWarningContainer,
                        border = BorderStroke(1.2.dp, TvWarningContainerBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = "Warning",
                                    tint = TvWarning,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "⚠️ XAUUSD LIMIT EXCEEDED",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TvWarningText
                                )
                            }

                            Text(
                                text = propFirmWarningMessage,
                                fontSize = 12.sp,
                                color = TvSilverBright,
                                lineHeight = 16.sp
                            )

                            if (propFirmRiskAtLimitText != null) {
                                Text(
                                    text = propFirmRiskAtLimitText,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TvWarning
                                )
                            }
                        }
                    }
                }

                // Minimum Lot Notice if applicable
                if (result.isBelowMinimumLot) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = TvSilverBright,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Risk budget is too small for ${result.lotStep.displayName} min broker lot. Required lot is below ${result.lotStep.displayName}.",
                                fontSize = 12.sp,
                                color = TvSilverBright,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Divider line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(TvDarkSurfaceBorder)
                )

                // 2x2 Financial Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        label = "Risk Amount",
                        primaryValue = result.formattedRiskAmount,
                        secondaryValue = "Actual: ${result.formattedActualRisk} (${result.formattedActualRiskPercent})",
                        modifier = Modifier.weight(1f)
                    )

                    MetricBox(
                        label = "Stop Loss (${result.direction.label})",
                        primaryValue = result.formattedSlPrice,
                        primaryColor = if (result.direction.isBuy) TvPurpleGlow else TvLightGrey,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(result.formattedSlPrice))
                            onCopyFeedback("Copied SL price ${result.formattedSlPrice} to clipboard")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBox(
                        label = "SL Price Distance",
                        primaryValue = result.formattedSlDistance,
                        secondaryValue = "Points / Price drop",
                        modifier = Modifier.weight(1f)
                    )

                    if (result.plannedRrRatio != null) {
                        MetricBox(
                            label = "Planned R:R Ratio",
                            primaryValue = result.formattedPlannedRr,
                            secondaryValue = "TP: ${result.formattedTpPrice}",
                            primaryColor = TvPurpleGlow,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        MetricBox(
                            label = "Contract Specification",
                            primaryValue = result.formattedContractSize,
                            secondaryValue = "1 lot = 100 oz gold",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Quick "Save as Trade" Action Button
                if (onSaveAsTrade != null) {
                    Button(
                        onClick = onSaveAsTrade,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TvPurplePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save as Trade",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    primaryValue: String,
    modifier: Modifier = Modifier,
    primaryColor: Color = TvSilverBright,
    secondaryValue: String? = null,
    onCopy: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TvSilver
                )

                if (onCopy != null) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = TvPurpleGlow,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable(onClick = onCopy)
                    )
                }
            }

            Text(
                text = primaryValue,
                fontSize = 17.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            if (secondaryValue != null) {
                Text(
                    text = secondaryValue,
                    fontSize = 10.sp,
                    color = TvSilver.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Result Card", showBackground = true)
@Composable
fun ResultCardPreview() {
    val sampleInput = com.example.xauusdlotsizecalculator.domain.model.CalculationInput(
        balance = java.math.BigDecimal("5000"),
        riskPercent = java.math.BigDecimal("1"),
        entryPrice = java.math.BigDecimal("2650.00"),
        slPercent = java.math.BigDecimal("0.18868"),
        direction = com.example.xauusdlotsizecalculator.domain.model.TradeDirection.BUY,
        contractSize = java.math.BigDecimal("100"),
        lotStep = com.example.xauusdlotsizecalculator.domain.model.LotStep.STEP_0_01,
        roundingMode = com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode.ROUND_DOWN,
        slPrice = java.math.BigDecimal("2645.00"),
        takeProfitPrice = java.math.BigDecimal("2662.50")
    )
    val sampleResult = com.example.xauusdlotsizecalculator.domain.calculator.XauusdLotCalculator.calculate(sampleInput)

    com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        ResultCard(
            result = sampleResult,
            onCopyFeedback = {},
            onSaveAsTrade = {}
        )
    }
}

