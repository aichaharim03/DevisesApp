package com.attijariwafabank.devisesapp.retrofitInstance

import com.attijariwafabank.devisesapp.apiService.CurrencyApiService
import com.attijariwafabank.devisesapp.apiService.MapsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitMapsInstance {
    private const val BASE_URL = "http://gsrv.itafrica.mobi"

    val api: MapsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapsApiService::class.java)
    }
}