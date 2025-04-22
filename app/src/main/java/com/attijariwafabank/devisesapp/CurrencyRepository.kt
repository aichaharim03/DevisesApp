package com.attijariwafabank.devisesapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepository {
    suspend fun convertCurrency(
        accessKey: String,
        from: String,
        to: String,
        amount: Double,
        date: String? = null
    ): CurrencyResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.convertCurrency(accessKey, from, to, amount, date)
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}