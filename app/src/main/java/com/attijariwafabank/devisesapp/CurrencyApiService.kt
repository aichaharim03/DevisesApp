package com.attijariwafabank.devisesapp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET("convert")
    suspend fun convertCurrency(
        @Query("access_key") accessKey: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double,    
        @Query("date") date: String? = null
    ): Response<CurrencyResponse>
    @GET("live")
    suspend fun getCurrencies( @Query("access_key") accessKey: String): Response<Currency>
}