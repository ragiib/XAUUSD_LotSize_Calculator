package com.example.xauusdlotsizecalculator.ui.journal.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import com.example.xauusdlotsizecalculator.theme.TerminalDestructiveBorder
import com.example.xauusdlotsizecalculator.theme.TerminalDestructiveContainer
import com.example.xauusdlotsizecalculator.theme.TerminalDestructiveRed
import com.example.xauusdlotsizecalculator.theme.TerminalDestructiveText
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvLightGrey
import com.example.xauusdlotsizecalculator.theme.TvLossColor
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import com.example.xauusdlotsizecalculator.theme.TvWinColor
import com.example.xauusdlotsizecalculator.theme.tactileClickable
import com.example.xauusdlotsizecalculator.theme.terminalGlass
import java.text.DecimalFormat

@Composable
fun DeleteTradeDialog(
    trade: Trade,
    accountName: String? = null,
    onConfirmDelete: (Trade) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .terminalGlass(
                        shape = RoundedCornerShape(24.dp),
                        backgroundColor = Color(0xF00F0A14),
                        borderColor = TerminalDestructiveBorder.copy(alpha = 0.5f),
                        specularHighlight = TerminalDestructiveRed.copy(alpha = 0.4f),
                        elevation = 12.dp
                    ),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Warning Icon in Glowing Terminal Ring
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TerminalDestructiveContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = Color(0x33EF4444),
                            border = BorderStroke(1.2.dp, TerminalDestructiveBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = TerminalDestructiveRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Title & Subtitle
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DELETE TRADE LOG",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Permanent Ledger Removal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp,
                            color = TerminalDestructiveText
                        )
                    }

                    // Trade Specification Card (so the user is 100% sure what they're deleting)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x60181124),
                        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (trade.direction == TradeDirection.BUY) TvBuyColor else TvSilver.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "${trade.symbol} • ${trade.direction.name}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (trade.direction == TradeDirection.BUY) Color.White else TvLightGrey,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "#${trade.id}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = TvSilver
                                    )
                                }

                                Text(
                                    text = trade.formattedDate,
                                    fontSize = 11.sp,
                                    color = TvSilver
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${DecimalFormat("#,##0.00").format(trade.lotSize)} lots • Entry ${trade.entryPrice}",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = TvSilverBright
                                    )
                                    Text(
                                        text = "Setup: ${trade.setup}",
                                        fontSize = 11.sp,
                                        color = TvSilver
                                    )
                                }

                                val pnl = trade.profitLoss ?: 0.0
                                val pnlColor = when {
                                    trade.status == TradeStatus.OPEN -> TvPurpleGlow
                                    pnl > 0 -> TvWinColor
                                    pnl < 0 -> TvLossColor
                                    else -> TvSilver
                                }
                                val pnlSign = if (pnl > 0) "+" else if (pnl < 0) "-" else ""

                                Text(
                                    text = if (trade.status == TradeStatus.OPEN) "OPEN" else "$pnlSign$${DecimalFormat("#,##0.00").format(Math.abs(pnl))}",
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = pnlColor
                                )
                            }
                        }
                    }

                    // Warning Explanation
                    Text(
                        text = "This action will permanently wipe this record from your Journal. Any account balance, win-rate metrics, and streak analytics derived from this trade will be recalculated immediately.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TvSilver,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    // Action Buttons: Cancel vs Permanent Delete
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TvSilverBright
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                onConfirmDelete(trade)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TerminalDestructiveRed,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Delete Trade",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
