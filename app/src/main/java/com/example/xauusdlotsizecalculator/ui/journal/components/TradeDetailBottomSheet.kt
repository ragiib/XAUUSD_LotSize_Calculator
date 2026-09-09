package com.example.xauusdlotsizecalculator.ui.journal.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import com.example.xauusdlotsizecalculator.theme.TvBuyColor
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvGoldAccent
import com.example.xauusdlotsizecalculator.theme.TvGreenProfit
import com.example.xauusdlotsizecalculator.theme.TvLightGrey
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvRedContainer
import com.example.xauusdlotsizecalculator.theme.TvRedContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvRedLoss
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import java.io.File
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailBottomSheet(
    trade: Trade,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onEdit: (Trade) -> Unit,
    onDuplicate: (Trade) -> Unit,
    onDelete: (Trade) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar: Symbol & Direction, Date, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (trade.direction == TradeDirection.BUY) TvBuyColor else TvSilver.copy(alpha = 0.2f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "${trade.symbol} • ${trade.direction.name}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trade.direction == TradeDirection.BUY) Color.White else TvLightGrey,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
                    ) {
                        Text(
                            text = trade.setup,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TvPurpleGlow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = trade.formattedDateTime,
                    fontSize = 12.sp,
                    color = TvSilver
                )
            }

            // Outcome / Financial Results Banner
            val pnl = trade.profitLoss ?: 0.0
            val pnlColor = when {
                trade.status == TradeStatus.OPEN -> TvPurpleGlow
                pnl > 0 -> TvGreenProfit
                pnl < 0 -> TvRedLoss
                else -> TvSilver
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRADE RESULT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TvSilver
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (trade.status == TradeStatus.WIN) TvGreenProfit.copy(alpha = 0.2f) else if (trade.status == TradeStatus.LOSS) TvRedLoss.copy(alpha = 0.2f) else TvPlumContainer
                        ) {
                            Text(
                                text = trade.status.displayName.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = pnlColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            val sign = if (pnl > 0) "+" else if (pnl < 0) "-" else ""
                            Text(
                                text = if (trade.status == TradeStatus.OPEN) "Position Open" else "$sign$${DecimalFormat("#,##0.00").format(Math.abs(pnl))}",
                                fontSize = 30.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = pnlColor
                            )

                            if (trade.profitLossPercent != null) {
                                Text(
                                    text = "Return on Risk: ${DecimalFormat("0.00").format(trade.profitLossPercent)}%",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TvSilver
                                )
                            }
                        }

                        if (trade.rMultiple != null) {
                            val rSign = if (trade.rMultiple > 0) "+" else if (trade.rMultiple < 0) "-" else ""
                            Text(
                                text = "$rSign${DecimalFormat("#0.00").format(Math.abs(trade.rMultiple))}R",
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = pnlColor
                            )
                        }
                    }
                }
            }

            // Execution Prices & Specifications Grid
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PRICE LEVELS & SIZING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TvSilver
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailItem("Entry Price", trade.entryPrice.toString())
                        DetailItem("Exit Price", trade.exitPrice?.toString() ?: "—")
                        DetailItem("Stop Loss", trade.stopLossPrice.toString())
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailItem("Take Profit", trade.takeProfitPrice?.toString() ?: "—")
                        DetailItem("Lot Size", "${DecimalFormat("#,##0.00").format(trade.lotSize)} lots")
                        DetailItem("Planned Risk", "$${DecimalFormat("#,##0.00").format(trade.plannedRiskAmount)} (${trade.plannedRiskPercent}%)")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailItem("SL Distance", "${DecimalFormat("#,##0.000").format(trade.slDistance)} pts")
                        DetailItem("Planned R:R", trade.plannedRrRatio?.let { "1 : ${DecimalFormat("#0.00").format(it)}" } ?: "—")
                        DetailItem("Setup Quality", trade.setupQuality.fullLabel)
                    }
                }
            }

            // Mistakes Section
            if (trade.hasMistake) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = TvRedContainer,
                    border = BorderStroke(1.dp, TvRedContainerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = TvRedLoss,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mistakes Identified",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TvRedLoss
                            )
                        }
                        Text(
                            text = trade.mistakes.filter { it != "No mistake" }.joinToString(", "),
                            fontSize = 13.sp,
                            color = TvSilverBright
                        )
                    }
                }
            }

            // Psychology & Emotions
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = TvPurpleGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Psychology & Mindset",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TvPurpleGlow
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Before: ${trade.emotionBefore}",
                            fontSize = 13.sp,
                            color = TvSilverBright
                        )
                        Text(
                            text = "After: ${trade.emotionAfter}",
                            fontSize = 13.sp,
                            color = TvSilverBright
                        )
                    }

                    if (trade.thinkingNotes.isNotBlank()) {
                        Text(
                            text = "Thought Process: \"${trade.thinkingNotes}\"",
                            fontSize = 12.sp,
                            color = TvSilver,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }

            // Trade Review (Take again? & Lesson)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = TvPlumContainer,
                border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Would take again?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TvSilverBright
                        )
                        Text(
                            text = if (trade.wouldTakeAgain) "✅ YES" else "❌ NO",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trade.wouldTakeAgain) TvGreenProfit else TvRedLoss
                        )
                    }

                    if (trade.lesson.isNotBlank()) {
                        Text(
                            text = "Lesson to remember:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TvPurpleGlow
                        )
                        Text(
                            text = trade.lesson,
                            fontSize = 13.sp,
                            color = TvSilverBright,
                            lineHeight = 18.sp
                        )
                    }

                    if (trade.notes.isNotBlank()) {
                        Text(
                            text = "Notes: ${trade.notes}",
                            fontSize = 12.sp,
                            color = TvSilver
                        )
                    }
                }
            }

            // Attached Screenshot Preview if exists
            trade.screenshotPath?.let { path ->
                val imgFile = File(path)
                if (imgFile.exists()) {
                    val bitmap = remember(path) { BitmapFactory.decodeFile(path)?.asImageBitmap() }
                    if (bitmap != null) {
                        Text(
                            text = "Trade Chart Screenshot",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TvSilver
                        )
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Chart Screenshot",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Action Buttons: Edit, Duplicate, Delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TvRedLoss),
                    border = BorderStroke(1.dp, TvRedLoss.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }

                OutlinedButton(
                    onClick = { onDuplicate(trade) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Duplicate")
                }

                Button(
                    onClick = { onEdit(trade) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TvPurplePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Trade Entry?") },
                text = { Text("Are you sure you want to delete this trade record from your journal? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirm = false
                            onDelete(trade)
                        }
                    ) {
                        Text("Delete", color = TvRedLoss, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, color = TvSilver)
        Text(
            text = value,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            color = TvSilverBright
        )
    }
}
