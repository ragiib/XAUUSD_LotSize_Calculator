package com.example.xauusdlotsizecalculator.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.DisciplineSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.PropFirmSettings

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // --- Calculator Settings ---
    fun getSettings(): CalculatorSettings {
        val riskPercent = prefs.getFloat(KEY_DEFAULT_RISK, 1.0f).toDouble()
        val lotStepStr = prefs.getString(KEY_DEFAULT_LOT_STEP, LotStep.STEP_0_01.name) ?: LotStep.STEP_0_01.name
        val contractSize = prefs.getFloat(KEY_CONTRACT_SIZE, 100.0f).toDouble()
        val roundingModeStr = prefs.getString(KEY_ROUNDING_MODE, LotRoundingMode.ROUND_DOWN.name) ?: LotRoundingMode.ROUND_DOWN.name

        val lotStep = try {
            LotStep.valueOf(lotStepStr)
        } catch (_: Exception) {
            LotStep.STEP_0_01
        }

        val roundingMode = try {
            LotRoundingMode.valueOf(roundingModeStr)
        } catch (_: Exception) {
            LotRoundingMode.ROUND_DOWN
        }

        return CalculatorSettings(
            defaultRiskPercent = riskPercent,
            defaultLotStep = lotStep,
            contractSize = contractSize,
            roundingMode = roundingMode
        )
    }

    fun saveSettings(settings: CalculatorSettings) {
        prefs.edit()
            .putFloat(KEY_DEFAULT_RISK, settings.defaultRiskPercent.toFloat())
            .putString(KEY_DEFAULT_LOT_STEP, settings.defaultLotStep.name)
            .putFloat(KEY_CONTRACT_SIZE, settings.contractSize.toFloat())
            .putString(KEY_ROUNDING_MODE, settings.roundingMode.name)
            .apply()
    }

    // --- Prop Firm Settings ---
    fun getPropFirmSettings(): PropFirmSettings {
        return PropFirmSettings(
            enabled = prefs.getBoolean(KEY_PROP_ENABLED, true),
            name = prefs.getString(KEY_PROP_NAME, "PropScholar Freedom 5K") ?: "PropScholar Freedom 5K",
            startingBalance = prefs.getFloat(KEY_PROP_STARTING_BALANCE, 5000.0f).toDouble(),
            profitTargetPercent = prefs.getFloat(KEY_PROP_TARGET_PERCENT, 10.0f).toDouble(),
            maxLossPercent = prefs.getFloat(KEY_PROP_MAX_LOSS_PERCENT, 6.0f).toDouble(),
            dailyLossPercent = prefs.getFloat(KEY_PROP_DAILY_LOSS_PERCENT, 3.0f).toDouble(),
            maxGoldVolumeLots = prefs.getFloat(KEY_PROP_MAX_GOLD_LOTS, 0.20f).toDouble(),
            leverage = prefs.getInt(KEY_PROP_LEVERAGE, 50)
        )
    }

    fun savePropFirmSettings(settings: PropFirmSettings) {
        prefs.edit()
            .putBoolean(KEY_PROP_ENABLED, settings.enabled)
            .putString(KEY_PROP_NAME, settings.name)
            .putFloat(KEY_PROP_STARTING_BALANCE, settings.startingBalance.toFloat())
            .putFloat(KEY_PROP_TARGET_PERCENT, settings.profitTargetPercent.toFloat())
            .putFloat(KEY_PROP_MAX_LOSS_PERCENT, settings.maxLossPercent.toFloat())
            .putFloat(KEY_PROP_DAILY_LOSS_PERCENT, settings.dailyLossPercent.toFloat())
            .putFloat(KEY_PROP_MAX_GOLD_LOTS, settings.maxGoldVolumeLots.toFloat())
            .putInt(KEY_PROP_LEVERAGE, settings.leverage)
            .apply()
    }

    // --- Discipline Settings ---
    fun getDisciplineSettings(): DisciplineSettings {
        return DisciplineSettings(
            maxTradesPerSession = prefs.getInt(KEY_DISC_MAX_TRADES, 3),
            dailyProfitStop = prefs.getFloat(KEY_DISC_PROFIT_STOP, 100.0f).toDouble(),
            dailyLossStop = prefs.getFloat(KEY_DISC_LOSS_STOP, 50.0f).toDouble(),
            autoSyncBalance = prefs.getBoolean(KEY_DISC_AUTO_SYNC, true)
        )
    }

    fun saveDisciplineSettings(settings: DisciplineSettings) {
        prefs.edit()
            .putInt(KEY_DISC_MAX_TRADES, settings.maxTradesPerSession)
            .putFloat(KEY_DISC_PROFIT_STOP, settings.dailyProfitStop.toFloat())
            .putFloat(KEY_DISC_LOSS_STOP, settings.dailyLossStop.toFloat())
            .putBoolean(KEY_DISC_AUTO_SYNC, settings.autoSyncBalance)
            .apply()
    }

    // --- Account Balance & Currency ---
    fun getCurrentBalance(): Double {
        return prefs.getFloat(KEY_CURRENT_BALANCE, 5000.0f).toDouble()
    }

    fun saveCurrentBalance(balance: Double) {
        prefs.edit().putFloat(KEY_CURRENT_BALANCE, balance.toFloat()).apply()
    }

    fun getCurrency(): String {
        return prefs.getString(KEY_CURRENCY, "$") ?: "$"
    }

    fun saveCurrency(currency: String) {
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }

    // --- Appearance Theme ---
    fun getThemeMode(): String {
        return prefs.getString(KEY_THEME_MODE, "DARK") ?: "DARK"
    }

    fun saveThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    companion object {
        private const val PREFS_NAME = "xauusd_calculator_settings"
        private const val KEY_DEFAULT_RISK = "default_risk_percent"
        private const val KEY_DEFAULT_LOT_STEP = "default_lot_step"
        private const val KEY_CONTRACT_SIZE = "contract_size"
        private const val KEY_ROUNDING_MODE = "rounding_mode"

        // Prop Firm
        private const val KEY_PROP_ENABLED = "prop_enabled"
        private const val KEY_PROP_NAME = "prop_name"
        private const val KEY_PROP_STARTING_BALANCE = "prop_starting_balance"
        private const val KEY_PROP_TARGET_PERCENT = "prop_target_percent"
        private const val KEY_PROP_MAX_LOSS_PERCENT = "prop_max_loss_percent"
        private const val KEY_PROP_DAILY_LOSS_PERCENT = "prop_daily_loss_percent"
        private const val KEY_PROP_MAX_GOLD_LOTS = "prop_max_gold_lots"
        private const val KEY_PROP_LEVERAGE = "prop_leverage"

        // Discipline
        private const val KEY_DISC_MAX_TRADES = "disc_max_trades"
        private const val KEY_DISC_PROFIT_STOP = "disc_profit_stop"
        private const val KEY_DISC_LOSS_STOP = "disc_loss_stop"
        private const val KEY_DISC_AUTO_SYNC = "disc_auto_sync"

        // Balance & Preferences
        private const val KEY_CURRENT_BALANCE = "current_balance"
        private const val KEY_CURRENCY = "app_currency"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
