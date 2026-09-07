package com.example.xauusdlotsizecalculator.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.xauusdlotsizecalculator.domain.model.CalculatorSettings
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

    companion object {
        private const val PREFS_NAME = "xauusd_calculator_settings"
        private const val KEY_DEFAULT_RISK = "default_risk_percent"
        private const val KEY_DEFAULT_LOT_STEP = "default_lot_step"
        private const val KEY_CONTRACT_SIZE = "contract_size"
        private const val KEY_ROUNDING_MODE = "rounding_mode"
    }
}
