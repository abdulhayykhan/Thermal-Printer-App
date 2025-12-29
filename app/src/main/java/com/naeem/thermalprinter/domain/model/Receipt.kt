package com.naeem.thermalprinter.domain.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Receipt(
    val businessName: String = "NAEEM DOCUMENTATION",
    val amount: Double,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val currency: String = "SR"
) {
    fun validate(): Result<Unit> {
        return when {
            amount <= 0.0 -> Result.failure(IllegalArgumentException("Amount must be positive"))
            amount > 999999.99 -> Result.failure(IllegalArgumentException("Amount too large"))
            businessName.isBlank() -> Result.failure(IllegalArgumentException("Business name required"))
            else -> Result.success(Unit)
        }
    }

    fun formattedAmount(): String = String.format("%.2f", amount)

    fun formattedTimestamp(): String =
        timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}
