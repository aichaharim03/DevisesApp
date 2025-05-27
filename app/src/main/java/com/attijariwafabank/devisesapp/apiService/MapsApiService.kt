package com.attijariwafabank.devisesapp.apiService

import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.data.AgencyResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApiService {
    @GET("geoserver/geoloc_getPOI.php?action=getAgencesNear")
    suspend fun getNearbyAgencies(
        @Query("latitude") lat: String,
        @Query("longitude") long: String
    ): Response<AgencyResponse>


}