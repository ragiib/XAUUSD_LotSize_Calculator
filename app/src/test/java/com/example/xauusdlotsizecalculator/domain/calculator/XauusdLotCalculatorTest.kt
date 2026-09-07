package com.example.xauusdlotsizecalculator.domain.calculator

import com.example.xauusdlotsizecalculator.domain.model.CalculationInput
import com.example.xauusdlotsizecalculator.domain.model.LotRoundingMode
import com.example.xauusdlotsizecalculator.domain.model.LotStep
import com.example.xauusdlotsizecalculator.domain.model.TradeDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class XauusdLotCalculatorTest {

    /**
     * Primary Reference Test Case from Requirements:
     * Balance: 2500
     * Risk: 1%
     * Entry: 4411.537
     * SL: 0.131%
     * Contract Size: 100
     */
    @Test
    fun testReferenceCase_Buy_RoundDown() {
        val input = CalculationInput(
            balance = BigDecimal("2500"),
            riskPercent = BigDecimal("1"),
            entryPrice = BigDecimal("4411.537"),
            slPercent = BigDecimal("0.131"),
            direction = TradeDirection.BUY,
            contractSize = BigDecimal("100"),
            lotStep = LotStep.STEP_0_01,
            roundingMode = LotRoundingMode.ROUND_DOWN
        )

        val result = XauusdLotCalculator.calculate(input)

        // Step 1: Risk Amount = 2500 * 1 / 100 = 25
        assertEquals(25.0, result.riskAmount.toDouble(), 0.0001)

        // Step 2: SL Distance = 4411.537 * 0.131 / 100 = 5.77911347
        val expectedSlDistance = 4411.537 * 0.131 / 100.0
        assertEquals(expectedSlDistance, result.slDistance.toDouble(), 0.00001)

        // BUY SL Price = 4411.537 - 5.77911347 = 4405.75788653
        val expectedBuySlPrice = 4411.537 - expectedSlDistance
        assertEquals(expectedBuySlPrice, result.slPrice.toDouble(), 0.00001)

        // Dollar Risk per 1 lot = 5.77911347 * 100 = 577.911347
        val expectedDollarRiskPerLot = expectedSlDistance * 100.0
        assertEquals(expectedDollarRiskPerLot, result.dollarRiskPerLot.toDouble(), 0.0001)

        // Exact Lot Size = 25 / 577.911347 = ~0.043259...
        val expectedExactLot = 25.0 / expectedDollarRiskPerLot
        assertEquals(expectedExactLot, result.exactLotSize.toDouble(), 0.0001)

        // Broker Lot with 0.01 step and ROUND_DOWN: 0.04
        assertEquals(0.04, result.brokerLotSize.toDouble(), 0.0001)

        // Risk safety guarantee: actual risk must NEVER exceed max risk amount ($25.00)
        assertTrue(result.actualRiskWithBrokerLot <= result.riskAmount)
        // 0.04 * 577.911347 = ~23.116...
        assertEquals(23.12, result.actualRiskWithBrokerLot.setScale(2, RoundingMode.HALF_UP).toDouble(), 0.01)

        // Actual risk percent <= 1.00%
        assertTrue(result.actualRiskPercentWithBrokerLot <= BigDecimal("1.0"))
        assertFalse(result.isBelowMinimumLot)
    }

    @Test
    fun testReferenceCase_Sell_CalculatesSlAboveEntry() {
        val input = CalculationInput(
            balance = BigDecimal("2500"),
            riskPercent = BigDecimal("1"),
            entryPrice = BigDecimal("4411.537"),
            slPercent = BigDecimal("0.131"),
            direction = TradeDirection.SELL,
            contractSize = BigDecimal("100"),
            lotStep = LotStep.STEP_0_01,
            roundingMode = LotRoundingMode.ROUND_DOWN
        )

        val result = XauusdLotCalculator.calculate(input)

        // SELL SL Price = 4411.537 + 5.77911347 = 4417.31611347
        val expectedSellSlPrice = 4411.537 + (4411.537 * 0.131 / 100.0)
        assertEquals(expectedSellSlPrice, result.slPrice.toDouble(), 0.00001)
        assertTrue(result.slPrice > input.entryPrice)
    }

    @Test
    fun testRoundingModes_RoundDownVsRoundNearest() {
        val exactLot = BigDecimal("0.047")
        val lotStep = BigDecimal("0.01")

        val roundedDown = XauusdLotCalculator.roundToLotStep(exactLot, lotStep, LotRoundingMode.ROUND_DOWN)
        val roundedNearest = XauusdLotCalculator.roundToLotStep(exactLot, lotStep, LotRoundingMode.ROUND_NEAREST)

        assertEquals(0.04, roundedDown.toDouble(), 0.0001)
        assertEquals(0.05, roundedNearest.toDouble(), 0.0001)

        // When exact lot is 0.042
        val exactLotLow = BigDecimal("0.042")
        val roundedDownLow = XauusdLotCalculator.roundToLotStep(exactLotLow, lotStep, LotRoundingMode.ROUND_DOWN)
        val roundedNearestLow = XauusdLotCalculator.roundToLotStep(exactLotLow, lotStep, LotRoundingMode.ROUND_NEAREST)

        assertEquals(0.04, roundedDownLow.toDouble(), 0.0001)
        assertEquals(0.04, roundedNearestLow.toDouble(), 0.0001)
    }

    @Test
    fun testLotSteps_MiniAndStandard() {
        val exactLot = BigDecimal("2.45")

        // Step 0.1 (Mini lot)
        val miniDown = XauusdLotCalculator.roundToLotStep(exactLot, BigDecimal("0.1"), LotRoundingMode.ROUND_DOWN)
        val miniNearest = XauusdLotCalculator.roundToLotStep(exactLot, BigDecimal("0.1"), LotRoundingMode.ROUND_NEAREST)
        assertEquals(2.4, miniDown.toDouble(), 0.0001)
        assertEquals(2.5, miniNearest.toDouble(), 0.0001)

        // Step 1.0 (Standard lot)
        val standardDown = XauusdLotCalculator.roundToLotStep(exactLot, BigDecimal("1.0"), LotRoundingMode.ROUND_DOWN)
        val standardNearest = XauusdLotCalculator.roundToLotStep(exactLot, BigDecimal("1.0"), LotRoundingMode.ROUND_NEAREST)
        assertEquals(2.0, standardDown.toDouble(), 0.0001)
        assertEquals(2.0, standardNearest.toDouble(), 0.0001)
    }

    @Test
    fun testSmallAccount_BelowMinimumLotFlag() {
        // Account balance $100, risk 0.5% ($0.50 risk budget), SL 1% at $2600 ($26 SL distance -> $2600 risk per 1 lot)
        // Exact lot = 0.50 / 2600 = 0.000192 lots (< 0.01)
        val input = CalculationInput(
            balance = BigDecimal("100"),
            riskPercent = BigDecimal("0.5"),
            entryPrice = BigDecimal("2600"),
            slPercent = BigDecimal("1.0"),
            lotStep = LotStep.STEP_0_01,
            roundingMode = LotRoundingMode.ROUND_DOWN
        )

        val result = XauusdLotCalculator.calculate(input)
        assertEquals(0.0, result.brokerLotSize.toDouble(), 0.0001)
        assertTrue(result.isBelowMinimumLot)
    }

    @Test
    fun testCustomContractSize() {
        val input = CalculationInput(
            balance = BigDecimal("5000"),
            riskPercent = BigDecimal("2"), // $100 risk
            entryPrice = BigDecimal("2500"),
            slPercent = BigDecimal("1"), // $25 SL distance
            contractSize = BigDecimal("50"), // Custom 50 oz contract
            lotStep = LotStep.STEP_0_01,
            roundingMode = LotRoundingMode.ROUND_DOWN
        )

        val result = XauusdLotCalculator.calculate(input)
        // Dollar risk per lot = 25 * 50 = $1250
        assertEquals(1250.0, result.dollarRiskPerLot.toDouble(), 0.0001)
        // Exact lot = 100 / 1250 = 0.08
        assertEquals(0.08, result.exactLotSize.toDouble(), 0.0001)
        assertEquals(0.08, result.brokerLotSize.toDouble(), 0.0001)
    }

    @Test
    fun testInputValidation_ValidInputs() {
        val validation = XauusdLotCalculator.validate(
            balanceStr = "2500",
            riskPercentStr = "1",
            entryPriceStr = "4411.537",
            slPercentStr = "0.131"
        )
        assertTrue(validation.isValid)
        assertNull(validation.balanceError)
        assertNull(validation.riskPercentError)
        assertNull(validation.entryPriceError)
        assertNull(validation.slPercentError)
    }

    @Test
    fun testInputValidation_CommaDecimals() {
        val validation = XauusdLotCalculator.validate(
            balanceStr = "2500,50",
            riskPercentStr = "1,5",
            entryPriceStr = "4411,537",
            slPercentStr = "0,131"
        )
        assertTrue(validation.isValid)
    }

    @Test
    fun testInputValidation_InvalidValues() {
        val validation = XauusdLotCalculator.validate(
            balanceStr = "-100",
            riskPercentStr = "150",
            entryPriceStr = "0",
            slPercentStr = "105"
        )
        assertFalse(validation.isValid)
        assertNotNull(validation.balanceError)
        assertNotNull(validation.riskPercentError)
        assertNotNull(validation.entryPriceError)
        assertNotNull(validation.slPercentError)
    }

    @Test
    fun testInputValidation_EmptyAndMalformed() {
        val validation = XauusdLotCalculator.validate(
            balanceStr = "",
            riskPercentStr = "abc",
            entryPriceStr = " ",
            slPercentStr = "0.0.1"
        )
        assertFalse(validation.isValid)
    }
}
