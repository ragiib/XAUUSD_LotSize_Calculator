package com.example.xauusdlotsizecalculator.ui.journal.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import com.example.xauusdlotsizecalculator.theme.TvBreakeven
import com.example.xauusdlotsizecalculator.theme.TvBreakevenContainer
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvGoldAccent
import com.example.xauusdlotsizecalculator.theme.TvGreenContainer
import com.example.xauusdlotsizecalculator.theme.TvGreenContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvGreenProfit
import com.example.xauusdlotsizecalculator.theme.TvLightGrey
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvRedContainer
import com.example.xauusdlotsizecalculator.theme.TvRedContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvRedLoss
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import java.text.DecimalFormat

@Composable
fun TradeCard(
    trade: Trade,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pnl = trade.profitLoss ?: 0.0
    val isWin = trade.status == TradeStatus.WIN || pnl > 0
    val isLoss = trade.status == TradeStatus.LOSS || pnl < 0
    val isBreakeven = trade.status == TradeStatus.BREAKEVEN || (trade.isClosed && pnl == 0.0)
    val isOpen = trade.status == TradeStatus.OPEN

    val (pnlColor, pnlContainer, pnlBorder) = when {
        isOpen -> Triple(TvPurpleGlow, TvPlumContainer, TvDarkSurfaceBorder)
        isWin -> Triple(TvGreenProfit, TvGreenContainer, TvGreenContainerBorder)
        isLoss -> Triple(TvRedLoss, TvRedContainer, TvRedContainerBorder)
        else -> Triple(TvBreakeven, TvBreakevenContainer, TvDarkSurfaceBorder)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Direction & Symbol Tag, Setup Tag, and Date/Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Direction Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (trade.direction == TradeDirection.BUY) TvBuyColor else TvSilver.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (trade.direction == TradeDirection.BUY) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (trade.direction == TradeDirection.BUY) Color.White else TvLightGrey,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${trade.symbol} • ${trade.direction.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (trade.direction == TradeDirection.BUY) Color.White else TvLightGrey
                            )
                        }
                    }

                    // Setup tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
                    ) {
                        Text(
                            text = trade.setup,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TvSilverBright,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Date & Time
                Text(
                    text = trade.formattedDateTime,
                    fontSize = 11.sp,
                    color = TvSilver.copy(alpha = 0.8f)
                )
            }

            // Row 2: Lot Size & Price levels (Entry -> SL) vs P/L and R Multiple
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${DecimalFormat("#,##0.00").format(trade.lotSize)} lots",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TvSilverBright
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Risk: $${DecimalFormat("#,##0.00").format(trade.plannedRiskAmount)}",
                            fontSize = 12.sp,
                            color = TvSilver
                        )
                    }

                    Text(
                        text = "Entry ${trade.entryPrice} • SL ${trade.stopLossPrice}${if (trade.takeProfitPrice != null) " • TP ${trade.takeProfitPrice}" else ""}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TvSilver.copy(alpha = 0.7f)
                    )
                }

                // Financial Result Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = pnlContainer,
                    border = BorderStroke(1.dp, pnlBorder)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        if (isOpen) {
                            Text(
                                text = "OPEN",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TvPurpleGlow
                            )
                            if (trade.plannedRrRatio != null) {
                                Text(
                                    text = "Plan: 1:${DecimalFormat("#0.0").format(trade.plannedRrRatio)}",
                                    fontSize = 10.sp,
                                    color = TvSilver
                                )
                            }
                        } else {
                            val sign = if (pnl > 0) "+" else if (pnl < 0) "-" else ""
                            Text(
                                text = "$sign$${DecimalFormat("#,##0.00").format(Math.abs(pnl))}",
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = pnlColor
                            )
                            trade.rMultiple?.let { r ->
                                val rSign = if (r > 0) "+" else if (r < 0) "-" else ""
                                Text(
                                    text = "$rSign${DecimalFormat("#0.00").format(Math.abs(r))}R",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = pnlColor.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Row 3: Quality Stars & Mistakes Tag (if any)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = trade.setupQuality.stars,
                        fontSize = 12.sp,
                        color = TvGoldAccent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trade.setupQuality.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvSilverBright
                    )
                }

                if (trade.hasMistake) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TvRedContainer,
                        border = BorderStroke(1.dp, TvRedContainerBorder)
                    ) {
                        Text(
                            text = trade.mistakes.filter { it != "No mistake" }.joinToString(", "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TvRedLoss,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (trade.isClosed) {
                    Text(
                        text = "Clean execution",
                        fontSize = 10.sp,
                        color = TvSilver.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Trade Card - Winner", showBackground = true)
@Composable
fun TradeCardWinnerPreview() {
    com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        TradeCard(
            trade = Trade(
                id = 1,
                dateEpochMs = System.currentTimeMillis(),
                symbol = "XAUUSD",
                direction = TradeDirection.BUY,
                lotSize = 0.20,
                entryPrice = 2650.00,
                stopLossPrice = 2645.00,
                takeProfitPrice = 2662.50,
                exitPrice = 2662.50,
                status = TradeStatus.WIN,
                setup = "London Breakout",
                setupQuality = com.example.xauusdlotsizecalculator.domain.model.SetupQuality.A_PLUS,
                plannedRiskAmount = 100.0,
                profitLoss = 250.0,
                rMultiple = 2.50,
                mistakes = emptyList(),
                emotionBefore = "Calm",
                emotionAfter = "Confident"
            ),
            onClick = {}
        )
    }
}
