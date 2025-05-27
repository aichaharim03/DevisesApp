package com.attijariwafabank.devisesapp.retrofitInstance


import com.attijariwafabank.devisesapp.apiService.CurrencyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitCurrencyInstance {
    private const val BASE_URL = "https://api.currencylayer.com/"

    val api: CurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }

}