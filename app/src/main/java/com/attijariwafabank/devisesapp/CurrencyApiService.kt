package com.attijariwafabank.devisesapp
import com.attijariwafabank.devisesapp.data.Currency
import com.attijariwafabank.devisesapp.data.CurrencyResponse
import com.attijariwafabank.devisesapp.data.TimeFrameResponse
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
    suspend fun getCurrencies(
        @Query("access_key") accessKey: String
        ,@Query("source") source: String
        ,@Query("currencies") currencies: String?
    ): Response<Currency>
    @GET("timeframe")
    suspend fun getTimeFrame(
        @Query("access_key") accessKey: String,
        @Query("source") source: String ,
        @Query("currencies") currencies: String?,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<TimeFrameResponse>
}