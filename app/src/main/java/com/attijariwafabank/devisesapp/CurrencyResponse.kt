package com.attijariwafabank.devisesapp

import android.icu.text.IDNA
import retrofit2.http.Query

data class CurrencyResponse(
    val success: Boolean,
    val query: Query,
    val info: Info,
    val result: Double
) {
    data class Query(
        val from: String,
        val to: String,
        val amount: Double
    )

    data class Info(
        val rate: Double
    )
}
