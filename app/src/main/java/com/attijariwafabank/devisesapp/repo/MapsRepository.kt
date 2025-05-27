package com.attijariwafabank.devisesapp.repo

import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.retrofitInstance.RetrofitMapsInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapsRepository {

    suspend fun getNearbyAgencies(latitude: String, longitude: String): List<Agency> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitMapsInstance.api.getNearbyAgencies(latitude, longitude)
                if (response.isSuccessful) {
                    response.body()?.agency ?: emptyList()
                } else {
                    throw Exception("Échec de récupération : ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception("Erreur lors de la récupération : ${e.message}", e)
            }
        }
    }
}
