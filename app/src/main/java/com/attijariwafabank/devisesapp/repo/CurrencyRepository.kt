package com.attijariwafabank.devisesapp.repo

import com.attijariwafabank.devisesapp.data.Currency
import com.attijariwafabank.devisesapp.data.CurrencyResponse
import com.attijariwafabank.devisesapp.data.TimeFrameResponse
import com.attijariwafabank.devisesapp.retrofitInstance.RetrofitCurrencyInstance
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
                val response = RetrofitCurrencyInstance.api.convertCurrency(accessKey, from, to, amount, date)
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getCurrencies(accessKey: String , source : String , currencies :String?): Currency? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitCurrencyInstance.api.getCurrencies(accessKey,source,currencies)
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getTimeFrame(accessKey: String, source: String, currencies: String?, startDate: String, endDate: String
    ): TimeFrameResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitCurrencyInstance.api.getTimeFrame(accessKey, source, currencies, startDate, endDate)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }



}