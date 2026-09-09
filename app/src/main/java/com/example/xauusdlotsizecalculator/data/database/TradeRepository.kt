package com.example.xauusdlotsizecalculator.data.database

import android.content.Context
import com.example.xauusdlotsizecalculator.domain.model.AnalyticsSummary
import com.example.xauusdlotsizecalculator.domain.model.DailyPerformance
import com.example.xauusdlotsizecalculator.domain.model.DayPerformanceStatus
import com.example.xauusdlotsizecalculator.domain.model.MistakeImpact
import com.example.xauusdlotsizecalculator.domain.model.SetupPerformance
import com.example.xauusdlotsizecalculator.domain.model.SetupQuality
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class TradeRepository(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val dbHelper = TradeLogDbHelper(context.applicationContext)
    private val _trades = MutableStateFlow<List<Trade>>(emptyList())
    val trades: StateFlow<List<Trade>> = _trades.asStateFlow()

    init {
        refreshTrades()
    }

    fun refreshTrades() {
        CoroutineScope(ioDispatcher).launch {
            val list = dbHelper.getAllTrades()
            _trades.value = list
        }
    }

    suspend fun saveTrade(trade: Trade): Long = withContext(ioDispatcher) {
        val id = dbHelper.insertTrade(trade)
        _trades.value = dbHelper.getAllTrades()
        id
    }

    suspend fun updateTrade(trade: Trade): Boolean = withContext(ioDispatcher) {
        val success = dbHelper.updateTrade(trade)
        if (success) {
            _trades.value = dbHelper.getAllTrades()
        }
        success
    }

    suspend fun deleteTrade(id: Long): Boolean = withContext(ioDispatcher) {
        val success = dbHelper.deleteTrade(id)
        if (success) {
            _trades.value = dbHelper.getAllTrades()
        }
        success
    }

    suspend fun duplicateTrade(id: Long): Trade? = withContext(ioDispatcher) {
        val original = dbHelper.getTradeById(id) ?: return@withContext null
        val copy = original.copy(
            id = 0,
            dateEpochMs = System.currentTimeMillis(),
            notes = if (original.notes.isNotBlank()) "Copy of: ${original.notes}" else "Copy"
        )
        val newId = dbHelper.insertTrade(copy)
        _trades.value = dbHelper.getAllTrades()
        copy.copy(id = newId)
    }

    suspend fun getTrade(id: Long): Trade? = withContext(ioDispatcher) {
        dbHelper.getTradeById(id)
    }

    suspend fun exportToCsv(): String = withContext(ioDispatcher) {
        val allTrades = dbHelper.getAllTrades()
        val sb = StringBuilder()
        // Header
        sb.append("ID,Date,Time,Symbol,Direction,Entry,Exit,StopLoss,TakeProfit,LotSize,PlannedRisk$,PlannedRisk%,SLDistance,PlannedRR,Status,ProfitLoss$,ProfitLoss%,RMultiple,Setup,Quality,Mistakes,EmotionBefore,EmotionAfter,ThinkingNotes,TakeAgain,Lesson,Notes\n")
        for (t in allTrades) {
            sb.append("${t.id},")
            sb.append("\"${t.formattedDate}\",")
            sb.append("\"${t.formattedTime}\",")
            sb.append("\"${t.symbol}\",")
            sb.append("\"${t.direction.name}\",")
            sb.append("${t.entryPrice},")
            sb.append("${t.exitPrice ?: ""},")
            sb.append("${t.stopLossPrice},")
            sb.append("${t.takeProfitPrice ?: ""},")
            sb.append("${t.lotSize},")
            sb.append("${t.plannedRiskAmount},")
            sb.append("${t.plannedRiskPercent},")
            sb.append("${t.slDistance},")
            sb.append("${t.plannedRrRatio ?: ""},")
            sb.append("\"${t.status.name}\",")
            sb.append("${t.profitLoss ?: ""},")
            sb.append("${t.profitLossPercent ?: ""},")
            sb.append("${t.rMultiple ?: ""},")
            sb.append("\"${t.setup.replace("\"", "\"\"")}\",")
            sb.append("\"${t.setupQuality.name}\",")
            sb.append("\"${t.mistakes.joinToString("; ").replace("\"", "\"\"")}\",")
            sb.append("\"${t.emotionBefore.replace("\"", "\"\"")}\",")
            sb.append("\"${t.emotionAfter.replace("\"", "\"\"")}\",")
            sb.append("\"${t.thinkingNotes.replace("\"", "\"\"")}\",")
            sb.append("${if (t.wouldTakeAgain) "YES" else "NO"},")
            sb.append("\"${t.lesson.replace("\"", "\"\"")}\",")
            sb.append("\"${t.notes.replace("\"", "\"\"")}\"\n")
        }
        sb.toString()
    }

    suspend fun exportToJson(): String = withContext(ioDispatcher) {
        val allTrades = dbHelper.getAllTrades()
        val jsonArray = JSONArray()
        for (t in allTrades) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("dateEpochMs", t.dateEpochMs)
                put("symbol", t.symbol)
                put("direction", t.direction.name)
                put("entryPrice", t.entryPrice)
                if (t.exitPrice != null) put("exitPrice", t.exitPrice)
                put("stopLossPrice", t.stopLossPrice)
                if (t.takeProfitPrice != null) put("takeProfitPrice", t.takeProfitPrice)
                put("lotSize", t.lotSize)
                put("plannedRiskAmount", t.plannedRiskAmount)
                put("plannedRiskPercent", t.plannedRiskPercent)
                put("slDistance", t.slDistance)
                if (t.plannedRrRatio != null) put("plannedRrRatio", t.plannedRrRatio)
                put("status", t.status.name)
                if (t.profitLoss != null) put("profitLoss", t.profitLoss)
                if (t.profitLossPercent != null) put("profitLossPercent", t.profitLossPercent)
                if (t.rMultiple != null) put("rMultiple", t.rMultiple)
                put("setup", t.setup)
                put("setupQuality", t.setupQuality.name)
                put("mistakes", JSONArray(t.mistakes))
                put("emotionBefore", t.emotionBefore)
                put("emotionAfter", t.emotionAfter)
                put("thinkingNotes", t.thinkingNotes)
                put("wouldTakeAgain", t.wouldTakeAgain)
                put("lesson", t.lesson)
                put("notes", t.notes)
                if (t.screenshotPath != null) put("screenshotPath", t.screenshotPath)
            }
            jsonArray.put(obj)
        }
        jsonArray.toString(2)
    }

    suspend fun importFromJson(jsonStr: String): Int = withContext(ioDispatcher) {
        var count = 0
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val mistakesList = mutableListOf<String>()
                if (obj.has("mistakes")) {
                    val mArr = obj.getJSONArray("mistakes")
                    for (j in 0 until mArr.length()) {
                        mistakesList.add(mArr.getString(j))
                    }
                }
                if (mistakesList.isEmpty()) mistakesList.add("No mistake")

                val trade = Trade(
                    id = 0, // Generate new ID
                    dateEpochMs = obj.optLong("dateEpochMs", System.currentTimeMillis()),
                    symbol = obj.optString("symbol", "XAUUSD"),
                    direction = try { TradeDirection.valueOf(obj.optString("direction", "BUY")) } catch (_: Exception) { TradeDirection.BUY },
                    entryPrice = obj.optDouble("entryPrice", 0.0),
                    exitPrice = if (obj.has("exitPrice")) obj.optDouble("exitPrice") else null,
                    stopLossPrice = obj.optDouble("stopLossPrice", 0.0),
                    takeProfitPrice = if (obj.has("takeProfitPrice")) obj.optDouble("takeProfitPrice") else null,
                    lotSize = obj.optDouble("lotSize", 0.0),
                    plannedRiskAmount = obj.optDouble("plannedRiskAmount", 0.0),
                    plannedRiskPercent = obj.optDouble("plannedRiskPercent", 1.0),
                    slDistance = obj.optDouble("slDistance", 0.0),
                    plannedRrRatio = if (obj.has("plannedRrRatio")) obj.optDouble("plannedRrRatio") else null,
                    status = try { TradeStatus.valueOf(obj.optString("status", "OPEN")) } catch (_: Exception) { TradeStatus.OPEN },
                    profitLoss = if (obj.has("profitLoss")) obj.optDouble("profitLoss") else null,
                    profitLossPercent = if (obj.has("profitLossPercent")) obj.optDouble("profitLossPercent") else null,
                    rMultiple = if (obj.has("rMultiple")) obj.optDouble("rMultiple") else null,
                    setup = obj.optString("setup", "Liquidity Sweep"),
                    setupQuality = try { SetupQuality.valueOf(obj.optString("setupQuality", "A_PLUS")) } catch (_: Exception) { SetupQuality.A_PLUS },
                    mistakes = mistakesList,
                    emotionBefore = obj.optString("emotionBefore", "Calm"),
                    emotionAfter = obj.optString("emotionAfter", "Calm"),
                    thinkingNotes = obj.optString("thinkingNotes", ""),
                    wouldTakeAgain = obj.optBoolean("wouldTakeAgain", true),
                    lesson = obj.optString("lesson", ""),
                    notes = obj.optString("notes", ""),
                    screenshotPath = if (obj.has("screenshotPath")) obj.optString("screenshotPath") else null
                )
                dbHelper.insertTrade(trade)
                count++
            }
            _trades.value = dbHelper.getAllTrades()
        } catch (_: Exception) {
            // Return count of successfully imported trades
        }
        count
    }

    fun computeAnalytics(tradeList: List<Trade>): AnalyticsSummary {
        if (tradeList.isEmpty()) {
            return AnalyticsSummary()
        }

        val closedTrades = tradeList.filter { it.isClosed }
        val wins = closedTrades.filter { it.status == TradeStatus.WIN }
        val losses = closedTrades.filter { it.status == TradeStatus.LOSS }
        val breakevens = closedTrades.filter { it.status == TradeStatus.BREAKEVEN }
        val openTrades = tradeList.count { it.status == TradeStatus.OPEN }

        val totalProfit = wins.sumOf { it.profitLoss ?: 0.0 }
        val totalLossAbs = Math.abs(losses.sumOf { it.profitLoss ?: 0.0 })
        val netPnl = closedTrades.sumOf { it.profitLoss ?: 0.0 }

        val winRate = if (closedTrades.isNotEmpty()) {
            (wins.size.toDouble() / closedTrades.size.toDouble()) * 100.0
        } else 0.0

        val averageWin = if (wins.isNotEmpty()) totalProfit / wins.size else 0.0
        val averageLoss = if (losses.isNotEmpty()) totalLossAbs / losses.size else 0.0
        val largestWin = wins.maxOfOrNull { it.profitLoss ?: 0.0 } ?: 0.0
        val largestLoss = losses.minOfOrNull { it.profitLoss ?: 0.0 }?.let { Math.abs(it) } ?: 0.0

        val totalR = closedTrades.mapNotNull { it.rMultiple }.sum()
        val averageR = if (closedTrades.isNotEmpty()) totalR / closedTrades.size else 0.0

        val profitFactor = if (totalLossAbs > 0.0) totalProfit / totalLossAbs else (if (totalProfit > 0.0) 99.9 else 0.0)

        // Setup performance
        val setupGroups = closedTrades.groupBy { it.setup }
        val setupPerformances = setupGroups.map { (setupName, trades) ->
            val setupWins = trades.count { it.status == TradeStatus.WIN }
            val rate = if (trades.isNotEmpty()) (setupWins.toDouble() / trades.size) * 100.0 else 0.0
            val rSum = trades.mapNotNull { it.rMultiple }.sum()
            val pnlSum = trades.sumOf { it.profitLoss ?: 0.0 }
            SetupPerformance(
                setupName = setupName,
                tradeCount = trades.size,
                winCount = setupWins,
                winRate = rate,
                totalR = rSum,
                netProfitLoss = pnlSum
            )
        }.sortedByDescending { it.netProfitLoss }

        val bestSetup = setupPerformances.firstOrNull()?.setupName
        val worstSetup = setupPerformances.lastOrNull()?.setupName

        // Mistake Impact
        val mistakeMap = mutableMapOf<String, Pair<Int, Double>>() // Name -> (Count, Loss Impact)
        for (t in closedTrades) {
            for (m in t.mistakes) {
                if (m != "No mistake") {
                    val current = mistakeMap[m] ?: Pair(0, 0.0)
                    val lossContribution = if ((t.profitLoss ?: 0.0) < 0) Math.abs(t.profitLoss ?: 0.0) else 0.0
                    mistakeMap[m] = Pair(current.first + 1, current.second + lossContribution)
                }
            }
        }
        val mistakeImpacts = mistakeMap.map { (name, pair) ->
            MistakeImpact(
                mistakeName = name,
                occurrenceCount = pair.first,
                financialLossImpact = pair.second
            )
        }.sortedByDescending { it.financialLossImpact }

        val mostCommonMistake = mistakeImpacts.maxByOrNull { it.occurrenceCount }?.mistakeName

        // Daily performance (Today)
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = todayCalendar.timeInMillis

        val todayTrades = closedTrades.filter { it.dateEpochMs >= startOfToday }
        val todayPnl = todayTrades.sumOf { it.profitLoss ?: 0.0 }
        val todayWins = todayTrades.count { it.status == TradeStatus.WIN }
        val todayLosses = todayTrades.count { it.status == TradeStatus.LOSS }
        val todayBreakevens = todayTrades.count { it.status == TradeStatus.BREAKEVEN }
        val todayTotalR = todayTrades.mapNotNull { it.rMultiple }.sum()
        val todayAvgR = if (todayTrades.isNotEmpty()) todayTotalR / todayTrades.size else 0.0

        val dayStatus = when {
            todayTrades.isEmpty() -> DayPerformanceStatus.NO_TRADES
            todayPnl > 0 -> DayPerformanceStatus.POSITIVE
            else -> DayPerformanceStatus.NEGATIVE
        }

        val dailyPerformance = DailyPerformance(
            netProfitLoss = todayPnl,
            tradeCount = todayTrades.size,
            wins = todayWins,
            losses = todayLosses,
            breakevens = todayBreakevens,
            averageR = todayAvgR,
            status = dayStatus
        )

        return AnalyticsSummary(
            totalTrades = closedTrades.size,
            wins = wins.size,
            losses = losses.size,
            breakevens = breakevens.size,
            openTrades = openTrades,
            winRate = winRate,
            totalProfit = totalProfit,
            totalLoss = totalLossAbs,
            netProfitLoss = netPnl,
            averageWin = averageWin,
            averageLoss = averageLoss,
            largestWin = largestWin,
            largestLoss = largestLoss,
            averageR = averageR,
            profitFactor = profitFactor,
            bestSetup = bestSetup,
            worstSetup = worstSetup,
            mostCommonMistake = mostCommonMistake,
            dailyPerformance = dailyPerformance,
            setupPerformances = setupPerformances,
            mistakeImpacts = mistakeImpacts
        )
    }
}
