package com.naeem.thermalprinter.domain.model

sealed class PrintResult {
    object Success : PrintResult()
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : PrintResult()

    fun toResult(): Result<Unit> = when (this) {
        Success -> Result.success(Unit)
        is Error -> Result.failure(cause ?: Exception(message))
    }
}
