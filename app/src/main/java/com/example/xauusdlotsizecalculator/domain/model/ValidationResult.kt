package com.example.xauusdlotsizecalculator.domain.model

data class ValidationResult(
    val balanceError: String? = null,
    val riskPercentError: String? = null,
    val entryPriceError: String? = null,
    val slPercentError: String? = null
) {
    val isValid: Boolean
        get() = balanceError == null &&
                riskPercentError == null &&
                entryPriceError == null &&
                slPercentError == null
}
