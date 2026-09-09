package com.example.xauusdlotsizecalculator.domain.calculator

import com.example.xauusdlotsizecalculator.domain.model.CalculationInput
import com.example.xauusdlotsizecalculator.domain.model.CalculationResult
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import com.example.xauusdlotsizecalculator.domain.model.ValidationResult
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

object XauusdLotCalculator {

    private val MATH_CONTEXT = MathContext(16, RoundingMode.HALF_UP)
    private val ONE_HUNDRED = BigDecimal("100")
    val DEFAULT_CONTRACT_SIZE: BigDecimal = BigDecimal("100")

    /**
     * Step 1: Calculate Maximum Dollar Risk Amount
     * Risk Amount = Balance * (Risk Percentage / 100)
     */
    fun calculateRiskAmount(balance: BigDecimal, riskPercent: BigDecimal): BigDecimal {
        require(balance > BigDecimal.ZERO) { "Balance must be positive" }
        require(riskPercent > BigDecimal.ZERO) { "Risk percent must be positive" }
        return balance.multiply(riskPercent, MATH_CONTEXT).divide(ONE_HUNDRED, MATH_CONTEXT)
    }

    /**
     * Step 2: Calculate SL Price Distance
     * SL Distance = Entry Price * (SL Percentage / 100)
     */
    fun calculateSlDistance(entryPrice: BigDecimal, slPercent: BigDecimal): BigDecimal {
        require(entryPrice > BigDecimal.ZERO) { "Entry price must be positive" }
        require(slPercent > BigDecimal.ZERO) { "SL percent must be positive" }
        return entryPrice.multiply(slPercent, MATH_CONTEXT).divide(ONE_HUNDRED, MATH_CONTEXT)
    }

    /**
     * Calculate Target Stop Loss Price based on Trade Direction
     * BUY: Entry Price - SL Distance
     * SELL: Entry Price + SL Distance
     */
    fun calculateSlPrice(
        entryPrice: BigDecimal,
        slDistance: BigDecimal,
        direction: TradeDirection
    ): BigDecimal {
        require(entryPrice > BigDecimal.ZERO) { "Entry price must be positive" }
        require(slDistance >= BigDecimal.ZERO) { "SL distance cannot be negative" }
        return when (direction) {
            TradeDirection.BUY -> entryPrice.subtract(slDistance, MATH_CONTEXT)
            TradeDirection.SELL -> entryPrice.add(slDistance, MATH_CONTEXT)
        }
    }

    /**
     * Step 3a: Dollar Risk per 1.00 Lot
     * Dollar Risk per 1 lot = SL Distance * Contract Size
     */
    fun calculateDollarRiskPerLot(slDistance: BigDecimal, contractSize: BigDecimal): BigDecimal {
        require(slDistance > BigDecimal.ZERO) { "SL distance must be positive" }
        require(contractSize > BigDecimal.ZERO) { "Contract size must be positive" }
        return slDistance.multiply(contractSize, MATH_CONTEXT)
    }

    /**
     * Step 3b: Calculate Exact Required Lot Size
     * Exact Lot Size = Risk Amount / Dollar Risk per 1 Lot
     */
    fun calculateExactLotSize(riskAmount: BigDecimal, dollarRiskPerLot: BigDecimal): BigDecimal {
        require(dollarRiskPerLot > BigDecimal.ZERO) { "Dollar risk per lot must be positive" }
        return riskAmount.divide(dollarRiskPerLot, MATH_CONTEXT)
    }

    /**
     * Step 4: Round to Broker Lot Step
     * Supports 0.01, 0.1, 1.0 lot steps with:
     * - ROUND_DOWN (Conservative: never exceeds intended risk)
     * - ROUND_NEAREST (Standard mathematical rounding)
     */
    fun roundToLotStep(
        exactLot: BigDecimal,
        lotStep: BigDecimal,
        mode: LotRoundingMode
    ): BigDecimal {
        require(lotStep > BigDecimal.ZERO) { "Lot step must be positive" }
        if (exactLot <= BigDecimal.ZERO) return BigDecimal.ZERO

        // Number of steps: exactLot / lotStep
        val stepsRatio = exactLot.divide(lotStep, 10, RoundingMode.HALF_UP)
        val roundedSteps = when (mode) {
            LotRoundingMode.ROUND_DOWN -> stepsRatio.setScale(0, RoundingMode.DOWN)
            LotRoundingMode.ROUND_NEAREST -> stepsRatio.setScale(0, RoundingMode.HALF_UP)
        }
        return roundedSteps.multiply(lotStep)
    }

    /**
     * Calculate SL Distance from Entry Price and SL Price
     */
    fun calculateSlDistanceFromPrice(entryPrice: BigDecimal, slPrice: BigDecimal): BigDecimal {
        require(entryPrice > BigDecimal.ZERO) { "Entry price must be positive" }
        require(slPrice > BigDecimal.ZERO) { "SL price must be positive" }
        return entryPrice.subtract(slPrice, MATH_CONTEXT).abs()
    }

    /**
     * Calculate SL Percentage from SL Distance
     */
    fun calculateSlPercentFromDistance(entryPrice: BigDecimal, slDistance: BigDecimal): BigDecimal {
        require(entryPrice > BigDecimal.ZERO) { "Entry price must be positive" }
        require(slDistance >= BigDecimal.ZERO) { "SL distance cannot be negative" }
        return slDistance.multiply(ONE_HUNDRED, MATH_CONTEXT).divide(entryPrice, MATH_CONTEXT)
    }

    /**
     * Calculate Planned Reward to Risk ratio
     */
    fun calculatePlannedRr(
        entryPrice: BigDecimal,
        slPrice: BigDecimal,
        tpPrice: BigDecimal
    ): BigDecimal? {
        val slDist = entryPrice.subtract(slPrice, MATH_CONTEXT).abs()
        val tpDist = tpPrice.subtract(entryPrice, MATH_CONTEXT).abs()
        if (slDist.compareTo(BigDecimal.ZERO) == 0) return null
        return tpDist.divide(slDist, MATH_CONTEXT)
    }

    /**
     * Calculate financial trade performance metrics
     * Returns Triple(profitLossAmount, profitLossPercent, rMultiple)
     */
    fun calculateTradeMetrics(
        entryPrice: Double,
        exitPrice: Double,
        slPrice: Double,
        lotSize: Double,
        contractSize: Double,
        direction: TradeDirection,
        plannedRiskAmount: Double
    ): Triple<Double, Double, Double> {
        val priceDiff = if (direction == TradeDirection.BUY) {
            exitPrice - entryPrice
        } else {
            entryPrice - exitPrice
        }
        val profitLossAmount = priceDiff * lotSize * contractSize
        val slDist = Math.abs(entryPrice - slPrice)
        val actualRisk = if (plannedRiskAmount > 0.0) plannedRiskAmount else (slDist * lotSize * contractSize)
        val profitLossPercent = if (actualRisk > 0.0) (profitLossAmount / actualRisk) * 100.0 else 0.0
        val rMultiple = if (actualRisk > 0.0) profitLossAmount / actualRisk else 0.0
        return Triple(profitLossAmount, profitLossPercent, rMultiple)
    }

    /**
     * Perform the complete calculation workflow
     */
    fun calculate(input: CalculationInput): CalculationResult {
        val riskAmount = calculateRiskAmount(input.balance, input.riskPercent)
        val slDistance = if (input.slPrice != null && input.slPrice > BigDecimal.ZERO) {
            calculateSlDistanceFromPrice(input.entryPrice, input.slPrice)
        } else {
            calculateSlDistance(input.entryPrice, input.slPercent)
        }
        val slPrice = if (input.slPrice != null && input.slPrice > BigDecimal.ZERO) {
            input.slPrice
        } else {
            calculateSlPrice(input.entryPrice, slDistance, input.direction)
        }
        val dollarRiskPerLot = calculateDollarRiskPerLot(slDistance, input.contractSize)
        val exactLotSize = calculateExactLotSize(riskAmount, dollarRiskPerLot)
        val brokerLotSize = roundToLotStep(exactLotSize, input.lotStep.step, input.roundingMode)

        val actualRiskWithBrokerLot = brokerLotSize.multiply(dollarRiskPerLot, MATH_CONTEXT)
        val actualRiskPercentWithBrokerLot = if (input.balance > BigDecimal.ZERO) {
            actualRiskWithBrokerLot.multiply(ONE_HUNDRED, MATH_CONTEXT).divide(input.balance, MATH_CONTEXT)
        } else {
            BigDecimal.ZERO
        }

        val isBelowMinimumLot = exactLotSize < input.lotStep.step && brokerLotSize.compareTo(BigDecimal.ZERO) == 0

        val plannedRrRatio = if (input.takeProfitPrice != null && input.takeProfitPrice > BigDecimal.ZERO) {
            calculatePlannedRr(input.entryPrice, slPrice, input.takeProfitPrice)
        } else null

        return CalculationResult(
            riskAmount = riskAmount,
            slDistance = slDistance,
            slPrice = slPrice,
            dollarRiskPerLot = dollarRiskPerLot,
            exactLotSize = exactLotSize,
            brokerLotSize = brokerLotSize,
            actualRiskWithBrokerLot = actualRiskWithBrokerLot,
            actualRiskPercentWithBrokerLot = actualRiskPercentWithBrokerLot,
            contractSize = input.contractSize,
            lotStep = input.lotStep,
            roundingMode = input.roundingMode,
            direction = input.direction,
            isBelowMinimumLot = isBelowMinimumLot,
            takeProfitPrice = input.takeProfitPrice,
            plannedRrRatio = plannedRrRatio
        )
    }

    /**
     * Validates input strings and returns a ValidationResult
     */
    fun validate(
        balanceStr: String,
        riskPercentStr: String,
        entryPriceStr: String,
        slPercentStr: String
    ): ValidationResult {
        var balanceError: String? = null
        var riskPercentError: String? = null
        var entryPriceError: String? = null
        var slPercentError: String? = null

        val balanceVal = parseDecimal(balanceStr)
        if (balanceStr.isBlank()) {
            balanceError = "Account balance is required"
        } else if (balanceVal == null) {
            balanceError = "Invalid balance amount"
        } else if (balanceVal <= BigDecimal.ZERO) {
            balanceError = "Balance must be greater than 0"
        }

        val riskVal = parseDecimal(riskPercentStr)
        if (riskPercentStr.isBlank()) {
            riskPercentError = "Risk percentage is required"
        } else if (riskVal == null) {
            riskPercentError = "Invalid risk percentage"
        } else if (riskVal <= BigDecimal.ZERO) {
            riskPercentError = "Risk must be greater than 0%"
        } else if (riskVal > ONE_HUNDRED) {
            riskPercentError = "Risk cannot exceed 100%"
        }

        val entryVal = parseDecimal(entryPriceStr)
        if (entryPriceStr.isBlank()) {
            entryPriceError = "Entry price is required"
        } else if (entryVal == null) {
            entryPriceError = "Invalid entry price"
        } else if (entryVal <= BigDecimal.ZERO) {
            entryPriceError = "Entry price must be greater than 0"
        }

        val slVal = parseDecimal(slPercentStr)
        if (slPercentStr.isBlank()) {
            slPercentError = "SL percentage is required"
        } else if (slVal == null) {
            slPercentError = "Invalid SL percentage"
        } else if (slVal <= BigDecimal.ZERO) {
            slPercentError = "SL % must be greater than 0%"
        } else if (slVal >= ONE_HUNDRED) {
            slPercentError = "SL % must be less than 100%"
        }

        return ValidationResult(
            balanceError = balanceError,
            riskPercentError = riskPercentError,
            entryPriceError = entryPriceError,
            slPercentError = slPercentError
        )
    }

    /**
     * Safely parse a localized decimal string (supports '.' and ',')
     */
    fun parseDecimal(raw: String): BigDecimal? {
        val sanitized = raw.trim().replace(',', '.')
        if (sanitized.isEmpty()) return null
        return try {
            BigDecimal(sanitized)
        } catch (_: Exception) {
            null
        }
    }
}
