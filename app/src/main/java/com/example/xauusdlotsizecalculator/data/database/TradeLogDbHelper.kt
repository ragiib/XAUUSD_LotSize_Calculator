package com.example.xauusdlotsizecalculator.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.xauusdlotsizecalculator.domain.model.SetupQuality
import com.example.xauusdlotsizecalculator.domain.model.Trade
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.TradeStatus

class TradeLogDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "tradelog.db"
        const val DATABASE_VERSION = 1

        const val TABLE_TRADES = "trades"
        const val COL_ID = "id"
        const val COL_DATE_EPOCH_MS = "date_epoch_ms"
        const val COL_SYMBOL = "symbol"
        const val COL_DIRECTION = "direction"
        const val COL_ENTRY_PRICE = "entry_price"
        const val COL_EXIT_PRICE = "exit_price"
        const val COL_STOP_LOSS_PRICE = "stop_loss_price"
        const val COL_TAKE_PROFIT_PRICE = "take_profit_price"
        const val COL_LOT_SIZE = "lot_size"
        const val COL_PLANNED_RISK_AMOUNT = "planned_risk_amount"
        const val COL_PLANNED_RISK_PERCENT = "planned_risk_percent"
        const val COL_SL_DISTANCE = "sl_distance"
        const val COL_PLANNED_RR_RATIO = "planned_rr_ratio"
        const val COL_STATUS = "status"
        const val COL_PROFIT_LOSS = "profit_loss"
        const val COL_PROFIT_LOSS_PERCENT = "profit_loss_percent"
        const val COL_R_MULTIPLE = "r_multiple"
        const val COL_SETUP = "setup"
        const val COL_SETUP_QUALITY = "setup_quality"
        const val COL_MISTAKES = "mistakes"
        const val COL_EMOTION_BEFORE = "emotion_before"
        const val COL_EMOTION_AFTER = "emotion_after"
        const val COL_THINKING_NOTES = "thinking_notes"
        const val COL_WOULD_TAKE_AGAIN = "would_take_again"
        const val COL_LESSON = "lesson"
        const val COL_NOTES = "notes"
        const val COL_SCREENSHOT_PATH = "screenshot_path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $TABLE_TRADES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DATE_EPOCH_MS INTEGER NOT NULL,
                $COL_SYMBOL TEXT NOT NULL,
                $COL_DIRECTION TEXT NOT NULL,
                $COL_ENTRY_PRICE REAL NOT NULL,
                $COL_EXIT_PRICE REAL,
                $COL_STOP_LOSS_PRICE REAL NOT NULL,
                $COL_TAKE_PROFIT_PRICE REAL,
                $COL_LOT_SIZE REAL NOT NULL,
                $COL_PLANNED_RISK_AMOUNT REAL NOT NULL,
                $COL_PLANNED_RISK_PERCENT REAL NOT NULL,
                $COL_SL_DISTANCE REAL NOT NULL,
                $COL_PLANNED_RR_RATIO REAL,
                $COL_STATUS TEXT NOT NULL,
                $COL_PROFIT_LOSS REAL,
                $COL_PROFIT_LOSS_PERCENT REAL,
                $COL_R_MULTIPLE REAL,
                $COL_SETUP TEXT NOT NULL,
                $COL_SETUP_QUALITY TEXT NOT NULL,
                $COL_MISTAKES TEXT NOT NULL,
                $COL_EMOTION_BEFORE TEXT NOT NULL,
                $COL_EMOTION_AFTER TEXT NOT NULL,
                $COL_THINKING_NOTES TEXT NOT NULL,
                $COL_WOULD_TAKE_AGAIN INTEGER NOT NULL,
                $COL_LESSON TEXT NOT NULL,
                $COL_NOTES TEXT NOT NULL,
                $COL_SCREENSHOT_PATH TEXT
            );
        """.trimIndent()
        db.execSQL(createTableSql)
        db.execSQL("CREATE INDEX idx_trades_date ON $TABLE_TRADES ($COL_DATE_EPOCH_MS DESC);")
        db.execSQL("CREATE INDEX idx_trades_status ON $TABLE_TRADES ($COL_STATUS);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Migration strategy for future versions
    }

    fun insertTrade(trade: Trade): Long {
        val db = writableDatabase
        val cv = createContentValues(trade)
        return db.insert(TABLE_TRADES, null, cv)
    }

    fun updateTrade(trade: Trade): Boolean {
        val db = writableDatabase
        val cv = createContentValues(trade)
        val affected = db.update(TABLE_TRADES, cv, "$COL_ID = ?", arrayOf(trade.id.toString()))
        return affected > 0
    }

    fun deleteTrade(id: Long): Boolean {
        val db = writableDatabase
        val affected = db.delete(TABLE_TRADES, "$COL_ID = ?", arrayOf(id.toString()))
        return affected > 0
    }

    fun getTradeById(id: Long): Trade? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRADES,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) cursorToTrade(it) else null
        }
    }

    fun getAllTrades(): List<Trade> {
        val db = readableDatabase
        val list = mutableListOf<Trade>()
        val cursor = db.query(
            TABLE_TRADES,
            null,
            null,
            null,
            null,
            null,
            "$COL_DATE_EPOCH_MS DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToTrade(it))
            }
        }
        return list
    }

    private fun createContentValues(trade: Trade): ContentValues {
        return ContentValues().apply {
            put(COL_DATE_EPOCH_MS, trade.dateEpochMs)
            put(COL_SYMBOL, trade.symbol)
            put(COL_DIRECTION, trade.direction.name)
            put(COL_ENTRY_PRICE, trade.entryPrice)
            if (trade.exitPrice != null) put(COL_EXIT_PRICE, trade.exitPrice) else putNull(COL_EXIT_PRICE)
            put(COL_STOP_LOSS_PRICE, trade.stopLossPrice)
            if (trade.takeProfitPrice != null) put(COL_TAKE_PROFIT_PRICE, trade.takeProfitPrice) else putNull(COL_TAKE_PROFIT_PRICE)
            put(COL_LOT_SIZE, trade.lotSize)
            put(COL_PLANNED_RISK_AMOUNT, trade.plannedRiskAmount)
            put(COL_PLANNED_RISK_PERCENT, trade.plannedRiskPercent)
            put(COL_SL_DISTANCE, trade.slDistance)
            if (trade.plannedRrRatio != null) put(COL_PLANNED_RR_RATIO, trade.plannedRrRatio) else putNull(COL_PLANNED_RR_RATIO)
            put(COL_STATUS, trade.status.name)
            if (trade.profitLoss != null) put(COL_PROFIT_LOSS, trade.profitLoss) else putNull(COL_PROFIT_LOSS)
            if (trade.profitLossPercent != null) put(COL_PROFIT_LOSS_PERCENT, trade.profitLossPercent) else putNull(COL_PROFIT_LOSS_PERCENT)
            if (trade.rMultiple != null) put(COL_R_MULTIPLE, trade.rMultiple) else putNull(COL_R_MULTIPLE)
            put(COL_SETUP, trade.setup)
            put(COL_SETUP_QUALITY, trade.setupQuality.name)
            put(COL_MISTAKES, trade.mistakes.joinToString("|||"))
            put(COL_EMOTION_BEFORE, trade.emotionBefore)
            put(COL_EMOTION_AFTER, trade.emotionAfter)
            put(COL_THINKING_NOTES, trade.thinkingNotes)
            put(COL_WOULD_TAKE_AGAIN, if (trade.wouldTakeAgain) 1 else 0)
            put(COL_LESSON, trade.lesson)
            put(COL_NOTES, trade.notes)
            if (trade.screenshotPath != null) put(COL_SCREENSHOT_PATH, trade.screenshotPath) else putNull(COL_SCREENSHOT_PATH)
        }
    }

    private fun cursorToTrade(c: Cursor): Trade {
        val directionStr = c.getString(c.getColumnIndexOrThrow(COL_DIRECTION))
        val direction = try { TradeDirection.valueOf(directionStr) } catch (_: Exception) { TradeDirection.BUY }

        val statusStr = c.getString(c.getColumnIndexOrThrow(COL_STATUS))
        val status = try { TradeStatus.valueOf(statusStr) } catch (_: Exception) { TradeStatus.OPEN }

        val qualityStr = c.getString(c.getColumnIndexOrThrow(COL_SETUP_QUALITY))
        val quality = try { SetupQuality.valueOf(qualityStr) } catch (_: Exception) { SetupQuality.A_PLUS }

        val mistakesRaw = c.getString(c.getColumnIndexOrThrow(COL_MISTAKES)) ?: "No mistake"
        val mistakes = if (mistakesRaw.isBlank()) listOf("No mistake") else mistakesRaw.split("|||")

        val exitPrice = if (c.isNull(c.getColumnIndexOrThrow(COL_EXIT_PRICE))) null else c.getDouble(c.getColumnIndexOrThrow(COL_EXIT_PRICE))
        val tpPrice = if (c.isNull(c.getColumnIndexOrThrow(COL_TAKE_PROFIT_PRICE))) null else c.getDouble(c.getColumnIndexOrThrow(COL_TAKE_PROFIT_PRICE))
        val plannedRr = if (c.isNull(c.getColumnIndexOrThrow(COL_PLANNED_RR_RATIO))) null else c.getDouble(c.getColumnIndexOrThrow(COL_PLANNED_RR_RATIO))
        val pnl = if (c.isNull(c.getColumnIndexOrThrow(COL_PROFIT_LOSS))) null else c.getDouble(c.getColumnIndexOrThrow(COL_PROFIT_LOSS))
        val pnlPercent = if (c.isNull(c.getColumnIndexOrThrow(COL_PROFIT_LOSS_PERCENT))) null else c.getDouble(c.getColumnIndexOrThrow(COL_PROFIT_LOSS_PERCENT))
        val rMult = if (c.isNull(c.getColumnIndexOrThrow(COL_R_MULTIPLE))) null else c.getDouble(c.getColumnIndexOrThrow(COL_R_MULTIPLE))
        val screenshot = if (c.isNull(c.getColumnIndexOrThrow(COL_SCREENSHOT_PATH))) null else c.getString(c.getColumnIndexOrThrow(COL_SCREENSHOT_PATH))

        return Trade(
            id = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
            dateEpochMs = c.getLong(c.getColumnIndexOrThrow(COL_DATE_EPOCH_MS)),
            symbol = c.getString(c.getColumnIndexOrThrow(COL_SYMBOL)),
            direction = direction,
            entryPrice = c.getDouble(c.getColumnIndexOrThrow(COL_ENTRY_PRICE)),
            exitPrice = exitPrice,
            stopLossPrice = c.getDouble(c.getColumnIndexOrThrow(COL_STOP_LOSS_PRICE)),
            takeProfitPrice = tpPrice,
            lotSize = c.getDouble(c.getColumnIndexOrThrow(COL_LOT_SIZE)),
            plannedRiskAmount = c.getDouble(c.getColumnIndexOrThrow(COL_PLANNED_RISK_AMOUNT)),
            plannedRiskPercent = c.getDouble(c.getColumnIndexOrThrow(COL_PLANNED_RISK_PERCENT)),
            slDistance = c.getDouble(c.getColumnIndexOrThrow(COL_SL_DISTANCE)),
            plannedRrRatio = plannedRr,
            status = status,
            profitLoss = pnl,
            profitLossPercent = pnlPercent,
            rMultiple = rMult,
            setup = c.getString(c.getColumnIndexOrThrow(COL_SETUP)),
            setupQuality = quality,
            mistakes = mistakes,
            emotionBefore = c.getString(c.getColumnIndexOrThrow(COL_EMOTION_BEFORE)),
            emotionAfter = c.getString(c.getColumnIndexOrThrow(COL_EMOTION_AFTER)),
            thinkingNotes = c.getString(c.getColumnIndexOrThrow(COL_THINKING_NOTES)),
            wouldTakeAgain = c.getInt(c.getColumnIndexOrThrow(COL_WOULD_TAKE_AGAIN)) == 1,
            lesson = c.getString(c.getColumnIndexOrThrow(COL_LESSON)),
            notes = c.getString(c.getColumnIndexOrThrow(COL_NOTES)),
            screenshotPath = screenshot
        )
    }
}
