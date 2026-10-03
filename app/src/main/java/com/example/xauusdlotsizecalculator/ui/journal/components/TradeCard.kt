package com.example.xauusdlotsizecalculator.ui.journal.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import com.example.xauusdlotsizecalculator.theme.TerminalDestructiveRed
import com.example.xauusdlotsizecalculator.theme.TerminalDestructiveText
import com.example.xauusdlotsizecalculator.theme.TvBreakeven
import com.example.xauusdlotsizecalculator.theme.TvBreakevenContainer
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvGoldAccent
import com.example.xauusdlotsizecalculator.theme.TvLightGrey
import com.example.xauusdlotsizecalculator.theme.TvLossColor
import com.example.xauusdlotsizecalculator.theme.TvLossContainer
import com.example.xauusdlotsizecalculator.theme.TvLossContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.theme.TvWinColor
import com.example.xauusdlotsizecalculator.theme.TvWinContainer
import com.example.xauusdlotsizecalculator.theme.TvWinContainerBorder
import com.example.xauusdlotsizecalculator.theme.tactileClickable
import com.example.xauusdlotsizecalculator.theme.terminalGlass
import java.text.DecimalFormat

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TradeCard(
    trade: Trade,
    accountName: String? = null,
    onClick: () -> Unit,
    onDeleteRequest: ((Trade) -> Unit)? = null,
    onDuplicateRequest: ((Trade) -> Unit)? = null,
    onEditRequest: ((Trade) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    val pnl = trade.profitLoss ?: 0.0
    val isWin = trade.status == TradeStatus.WIN || pnl > 0
    val isLoss = trade.status == TradeStatus.LOSS || pnl < 0
    val isBreakeven = trade.status == TradeStatus.BREAKEVEN || (trade.isClosed && pnl == 0.0)
    val isOpen = trade.status == TradeStatus.OPEN

    val (pnlColor, pnlContainer, pnlBorder) = when {
        isOpen -> Triple(TvPurpleGlow, TvPlumContainer, TvDarkSurfaceBorder)
        isWin -> Triple(TvWinColor, TvWinContainer, TvWinContainerBorder)
        isLoss -> Triple(TvLossColor, TvLossContainer, TvLossContainerBorder)
        else -> Triple(TvBreakeven, TvBreakevenContainer, TvDarkSurfaceBorder)
    }

    val directionColor = if (trade.direction == TradeDirection.BUY) TvBuyColor else TvSilver.copy(alpha = 0.85f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .terminalGlass(
                shape = RoundedCornerShape(18.dp),
                backgroundColor = Color(0xDC0C0A15),
                borderColor = Color(0xFF2E1C44),
                specularHighlight = if (trade.direction == TradeDirection.BUY) Color(0x50A855F7) else Color(0x30E5E7EB),
                elevation = 4.dp
            )
            .clickable(onClick = onClick)
    ) {
        // Left Direction Accent Indicator Strip
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            directionColor,
                            directionColor.copy(alpha = 0.2f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Direction & Symbol Tag, Account Pill, Date/Time, and Contextual Action Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    // Direction & Symbol Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (trade.direction == TradeDirection.BUY) TvBuyColor.copy(alpha = 0.85f) else TvSilver.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, if (trade.direction == TradeDirection.BUY) TvPurplePrimary else TvSilver.copy(alpha = 0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
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

                    // Account Tag (if available)
                    if (!accountName.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TvPlumContainer.copy(alpha = 0.8f),
                            border = BorderStroke(1.dp, TvDarkSurfaceBorder)
                        ) {
                            Text(
                                text = accountName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TvPurpleGlow,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Date & Time
                    Text(
                        text = trade.formattedDateTime,
                        fontSize = 11.sp,
                        color = TvSilver.copy(alpha = 0.8f)
                    )

                    // Contextual 3-dot Menu Button
                    if (onDeleteRequest != null || onDuplicateRequest != null || onEditRequest != null) {
                        Box {
                            IconButton(
                                onClick = { showOverflowMenu = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Trade Options",
                                    tint = TvSilver,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false }
                            ) {
                                if (onEditRequest != null) {
                                    DropdownMenuItem(
                                        text = { Text("Edit Trade") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = TvPurpleGlow, modifier = Modifier.size(16.dp))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            onEditRequest(trade)
                                        }
                                    )
                                }

                                if (onDuplicateRequest != null) {
                                    DropdownMenuItem(
                                        text = { Text("Duplicate Trade") },
                                        leadingIcon = {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = TvSilverBright, modifier = Modifier.size(16.dp))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            onDuplicateRequest(trade)
                                        }
                                    )
                                }

                                if (onDeleteRequest != null) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Delete Trade",
                                                color = TerminalDestructiveRed,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = TerminalDestructiveRed, modifier = Modifier.size(16.dp))
                                        },
                                        onClick = {
                                            showOverflowMenu = false
                                            onDeleteRequest(trade)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Row 2: Lot Size & Price levels (Entry -> SL -> TP) vs P/L and R Multiple
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TvPurpleGlow)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "OPEN",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TvPurpleGlow
                                )
                            }
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

            // Row 3: Setup Badge & Setup Quality
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Setup tag
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
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

                // Setup Quality Stars & Label
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
            }

            // Row 4: Mistakes List (Responsive wrapping with FlowRow)
            val actualMistakes = trade.mistakes.filter { !it.equals("No Mistake", ignoreCase = true) }
            if (actualMistakes.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    actualMistakes.forEach { mistake ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = TvLossContainer,
                            border = BorderStroke(1.dp, TvLossContainerBorder)
                        ) {
                            Text(
                                text = mistake,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TvLossColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            } else if (trade.isClosed) {
                Text(
                    text = "● Clean execution",
                    fontSize = 10.sp,
                    color = TvSilver.copy(alpha = 0.5f)
                )
            }
        }
    }
}
