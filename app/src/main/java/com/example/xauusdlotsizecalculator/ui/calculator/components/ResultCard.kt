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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
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
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvLightGrey
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPlumContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurpleNumber
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright

@Composable
fun ResultCard(
    result: CalculationResult?,
    onCopyFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
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

                // Minimum Lot Notice if applicable (matching chart aesthetic, not red)
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

                // Divider line (TradingView subtle separator)
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

                    // Calculated SL Price: Purple for BUY, Light Grey for SELL (Zero red!)
                    MetricBox(
                        label = "Calculated SL (${result.direction.label})",
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
                        secondaryValue = "Points / Dollar drop",
                        modifier = Modifier.weight(1f)
                    )

                    MetricBox(
                        label = "Contract Specification",
                        primaryValue = result.formattedContractSize,
                        secondaryValue = "1 lot = 100 oz gold",
                        modifier = Modifier.weight(1f)
                    )
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
