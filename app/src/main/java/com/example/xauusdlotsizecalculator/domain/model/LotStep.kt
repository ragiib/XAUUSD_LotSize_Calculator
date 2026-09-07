package com.example.xauusdlotsizecalculator.domain.model

import java.math.BigDecimal

enum class LotStep(
    val step: BigDecimal,
    val displayName: String,
    val description: String
) {
    STEP_0_01(BigDecimal("0.01"), "0.01", "Micro (0.01 lot)"),
    STEP_0_1(BigDecimal("0.1"), "0.10", "Mini (0.10 lot)"),
    STEP_1_0(BigDecimal("1.0"), "1.00", "Standard (1.00 lot)");

    companion object {
        fun fromValue(value: BigDecimal): LotStep {
            return entries.firstOrNull { it.step.compareTo(value) == 0 } ?: STEP_0_01
        }

        fun fromString(str: String): LotStep {
            return entries.firstOrNull { it.step.stripTrailingZeros().toPlainString() == str.trim() || it.displayName == str.trim() }
                ?: STEP_0_01
        }
    }
}
