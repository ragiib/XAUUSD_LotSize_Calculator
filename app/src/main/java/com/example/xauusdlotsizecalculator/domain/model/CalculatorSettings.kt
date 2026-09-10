package com.example.xauusdlotsizecalculator.domain.model

data class CalculatorSettings(
    val defaultRiskPercent: Double = 1.0,
    val defaultLotStep: LotStep = LotStep.STEP_0_01,
    val contractSize: Double = 100.0,
    val roundingMode: LotRoundingMode = LotRoundingMode.ROUND_DOWN,
    val defaultAccountSize: Double = 5000.0
)
