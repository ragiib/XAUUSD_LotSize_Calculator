package com.example.xauusdlotsizecalculator.ui.analytics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xauusdlotsizecalculator.domain.model.DayPerformanceStatus
import com.example.xauusdlotsizecalculator.domain.model.MistakeImpact
import com.example.xauusdlotsizecalculator.domain.model.SetupPerformance
import com.example.xauusdlotsizecalculator.theme.FinancialNumericStyle
import com.example.xauusdlotsizecalculator.theme.TvBreakeven
import com.example.xauusdlotsizecalculator.theme.TvDarkSurfaceBorder
import com.example.xauusdlotsizecalculator.theme.TvGoldAccent
import com.example.xauusdlotsizecalculator.theme.TvGreenContainer
import com.example.xauusdlotsizecalculator.theme.TvGreenContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvGreenProfit
import com.example.xauusdlotsizecalculator.theme.TvPlumContainer
import com.example.xauusdlotsizecalculator.theme.TvPurpleGlow
import com.example.xauusdlotsizecalculator.theme.TvPurpleNumber
import com.example.xauusdlotsizecalculator.theme.TvPurplePrimary
import com.example.xauusdlotsizecalculator.theme.TvRedContainer
import com.example.xauusdlotsizecalculator.theme.TvRedContainerBorder
import com.example.xauusdlotsizecalculator.theme.TvRedLoss
import com.example.xauusdlotsizecalculator.theme.TvSilver
import com.example.xauusdlotsizecalculator.theme.TvSilverBright
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.analyticsSummary.collectAsStateWithLifecycle()

    AnalyticsScreenContent(
        summary = summary,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreenContent(
    summary: com.example.xauusdlotsizecalculator.domain.model.AnalyticsSummary,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "EDGE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Analytics",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Daily Performance (Today)
            DailyPerformanceCard(summary = summary)

            // Section 2: Overall KPI Summary Cards
            OverallKpiCard(summary = summary)

            // Section 3: Setup Performance Breakdown
            SetupPerformanceSection(setups = summary.setupPerformances)

            // Section 4: "My Mistakes" Leak Tracker
            MistakeImpactSection(mistakes = summary.mistakeImpacts)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DailyPerformanceCard(summary: com.example.xauusdlotsizecalculator.domain.model.AnalyticsSummary) {
    val daily = summary.dailyPerformance
    val (statusColor, containerColor, borderColor) = when (daily.status) {
        DayPerformanceStatus.POSITIVE -> Triple(TvGreenProfit, TvGreenContainer, TvGreenContainerBorder)
        DayPerformanceStatus.NEGATIVE -> Triple(TvRedLoss, TvRedContainer, TvRedContainerBorder)
        DayPerformanceStatus.NO_TRADES -> Triple(TvSilver, MaterialTheme.colorScheme.surfaceVariant, TvDarkSurfaceBorder)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Today's Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoGraph,
                        contentDescription = null,
                        tint = TvPurpleGlow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TODAY'S PERFORMANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TvSilver
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = containerColor,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Text(
                        text = "${daily.status.emoji} ${daily.status.label}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // P/L & Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    val sign = if (daily.netProfitLoss > 0) "+" else if (daily.netProfitLoss < 0) "-" else ""
                    Text(
                        text = if (daily.tradeCount == 0) "$0.00" else "$sign$${DecimalFormat("#,##0.00").format(Math.abs(daily.netProfitLoss))}",
                        style = FinancialNumericStyle.copy(fontSize = 32.sp, color = statusColor)
                    )

                    Text(
                        text = "Today's Net P/L",
                        fontSize = 12.sp,
                        color = TvSilver
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${daily.tradeCount} Trades",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TvSilverBright
                    )
                    Text(
                        text = "${daily.wins}W • ${daily.losses}L • ${daily.breakevens}BE",
                        fontSize = 12.sp,
                        color = TvSilver
                    )
                    if (daily.tradeCount > 0) {
                        val rSign = if (daily.averageR > 0) "+" else if (daily.averageR < 0) "-" else ""
                        Text(
                            text = "Avg: $rSign${DecimalFormat("#0.00").format(Math.abs(daily.averageR))}R",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverallKpiCard(summary: com.example.xauusdlotsizecalculator.domain.model.AnalyticsSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QueryStats,
                    contentDescription = null,
                    tint = TvPurpleGlow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LIFETIME OVERVIEW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TvSilver
                )
            }

            // Net P/L & Win Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val netPnl = summary.netProfitLoss
                val netSign = if (netPnl > 0) "+" else if (netPnl < 0) "-" else ""
                val netColor = if (netPnl > 0) TvGreenProfit else if (netPnl < 0) TvRedLoss else TvSilver

                KpiMetricItem(
                    label = "Net Profit / Loss",
                    value = "$netSign$${DecimalFormat("#,##0.00").format(Math.abs(netPnl))}",
                    subValue = "${summary.totalTrades} closed trades",
                    valueColor = netColor,
                    modifier = Modifier.weight(1f)
                )

                KpiMetricItem(
                    label = "Win Rate",
                    value = "${DecimalFormat("0.0").format(summary.winRate)}%",
                    subValue = "${summary.wins}W • ${summary.losses}L • ${summary.breakevens}BE",
                    valueColor = TvPurpleNumber,
                    modifier = Modifier.weight(1f)
                )
            }

            // Profit Factor & Average R
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiMetricItem(
                    label = "Profit Factor",
                    value = DecimalFormat("#0.00").format(summary.profitFactor),
                    subValue = "Gains vs Losses ratio",
                    modifier = Modifier.weight(1f)
                )

                val avgR = summary.averageR
                val avgRSign = if (avgR > 0) "+" else if (avgR < 0) "-" else ""
                KpiMetricItem(
                    label = "Average R",
                    value = "$avgRSign${DecimalFormat("#0.00").format(Math.abs(avgR))}R",
                    subValue = "Expectancy per trade",
                    valueColor = if (avgR > 0) TvGreenProfit else if (avgR < 0) TvRedLoss else TvSilver,
                    modifier = Modifier.weight(1f)
                )
            }

            // Avg Win / Avg Loss
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiMetricItem(
                    label = "Avg Win",
                    value = "+$${DecimalFormat("#,##0.00").format(summary.averageWin)}",
                    subValue = "Largest: +$${DecimalFormat("#,##0.00").format(summary.largestWin)}",
                    valueColor = TvGreenProfit,
                    modifier = Modifier.weight(1f)
                )

                KpiMetricItem(
                    label = "Avg Loss",
                    value = "-$${DecimalFormat("#,##0.00").format(summary.averageLoss)}",
                    subValue = "Largest: -$${DecimalFormat("#,##0.00").format(summary.largestLoss)}",
                    valueColor = TvRedLoss,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SetupPerformanceSection(setups: List<SetupPerformance>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SETUP PERFORMANCE (EDGE)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TvSilver
                )

                Text(
                    text = "${setups.size} setups",
                    fontSize = 11.sp,
                    color = TvSilver
                )
            }

            if (setups.isEmpty()) {
                Text(
                    text = "No completed trades to analyze setups yet.",
                    fontSize = 13.sp,
                    color = TvSilver,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                setups.forEachIndexed { index, s ->
                    val pnlSign = if (s.netProfitLoss > 0) "+" else if (s.netProfitLoss < 0) "-" else ""
                    val pnlColor = if (s.netProfitLoss > 0) TvGreenProfit else if (s.netProfitLoss < 0) TvRedLoss else TvSilver

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = s.setupName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TvSilverBright
                                    )
                                    if (index == 0 && s.netProfitLoss > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = TvGoldAccent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "TOP EDGE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TvGoldAccent,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "${s.tradeCount} trades • ${DecimalFormat("0.0").format(s.winRate)}% win rate",
                                    fontSize = 11.sp,
                                    color = TvSilver
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$pnlSign$${DecimalFormat("#,##0.00").format(Math.abs(s.netProfitLoss))}",
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = pnlColor
                                )

                                val rSign = if (s.totalR > 0) "+" else if (s.totalR < 0) "-" else ""
                                Text(
                                    text = "$rSign${DecimalFormat("#0.00").format(Math.abs(s.totalR))}R",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TvSilver
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MistakeImpactSection(mistakes: List<MistakeImpact>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = TvRedLoss,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MY MISTAKES (LEAK TRACKER)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TvSilver
                )
            }

            Text(
                text = "Track and eliminate costly repeated behaviors to protect your trading capital.",
                fontSize = 12.sp,
                color = TvSilver
            )

            if (mistakes.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TvGreenContainer,
                    border = BorderStroke(1.dp, TvGreenContainerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎉 Zero repeated mistakes recorded! Keep up the disciplined execution.",
                        fontSize = 12.sp,
                        color = TvGreenProfit,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            } else {
                val maxLoss = mistakes.maxOfOrNull { it.financialLossImpact } ?: 1.0

                mistakes.forEach { m ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = m.mistakeName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TvSilverBright
                            )

                            Text(
                                text = "${m.occurrenceCount} times  •  -$${DecimalFormat("#,##0.00").format(m.financialLossImpact)} impact",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                color = TvRedLoss
                            )
                        }

                        // Relative visual impact bar
                        val progress = if (maxLoss > 0) (m.financialLossImpact / maxLoss).toFloat().coerceIn(0.05f, 1f) else 0.05f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TvRedLoss,
                            trackColor = TvRedContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiMetricItem(
    label: String,
    value: String,
    subValue: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TvSilverBright
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, TvDarkSurfaceBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = label, fontSize = 11.sp, color = TvSilver)
            Text(
                text = value,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(text = subValue, fontSize = 10.sp, color = TvSilver.copy(alpha = 0.7f))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Analytics Dashboard", showBackground = true)
@Composable
fun AnalyticsDashboardPreview() {
    com.example.xauusdlotsizecalculator.theme.XAUUSDLotSizeCalculatorTheme(darkTheme = true) {
        AnalyticsScreenContent(
            summary = com.example.xauusdlotsizecalculator.domain.model.AnalyticsSummary(
                totalTrades = 18,
                wins = 12,
                losses = 5,
                breakevens = 1,
                winRate = 66.7,
                totalProfit = 1420.50,
                totalLoss = 640.00,
                netProfitLoss = 780.50,
                profitFactor = 2.22,
                averageWin = 118.38,
                averageLoss = 128.00,
                largestWin = 310.00,
                largestLoss = 150.00,
                averageR = 1.35,
                dailyPerformance = com.example.xauusdlotsizecalculator.domain.model.DailyPerformance(
                    netProfitLoss = 245.00,
                    percentChange = 4.9,
                    tradeCount = 3,
                    wins = 2,
                    losses = 1,
                    breakevens = 0,
                    averageR = 1.63,
                    status = com.example.xauusdlotsizecalculator.domain.model.DayPerformanceStatus.POSITIVE
                ),
                setupPerformances = listOf(
                    com.example.xauusdlotsizecalculator.domain.model.SetupPerformance(
                        setupName = "London Breakout",
                        tradeCount = 8,
                        winCount = 6,
                        winRate = 75.0,
                        totalR = 4.8,
                        netProfitLoss = 580.00
                    ),
                    com.example.xauusdlotsizecalculator.domain.model.SetupPerformance(
                        setupName = "Order Block",
                        tradeCount = 6,
                        winCount = 4,
                        winRate = 66.7,
                        totalR = 2.4,
                        netProfitLoss = 280.00
                    ),
                    com.example.xauusdlotsizecalculator.domain.model.SetupPerformance(
                        setupName = "Asia Sweep",
                        tradeCount = 4,
                        winCount = 2,
                        winRate = 50.0,
                        totalR = -0.6,
                        netProfitLoss = -79.50
                    )
                ),
                mistakeImpacts = listOf(
                    com.example.xauusdlotsizecalculator.domain.model.MistakeImpact(
                        mistakeName = "Moved Stop Loss Early",
                        occurrenceCount = 3,
                        financialLossImpact = 210.00
                    ),
                    com.example.xauusdlotsizecalculator.domain.model.MistakeImpact(
                        mistakeName = "FOMO / Chased Entry",
                        occurrenceCount = 2,
                        financialLossImpact = 185.00
                    )
                )
            )
        )
    }
}
