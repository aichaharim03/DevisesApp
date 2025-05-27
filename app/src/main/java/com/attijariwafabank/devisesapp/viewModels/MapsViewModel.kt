package com.attijariwafabank.devisesapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.attijariwafabank.devisesapp.data.Agency
import com.attijariwafabank.devisesapp.repo.MapsRepository
import kotlinx.coroutines.launch

class MapsViewModel : ViewModel() {

    private val repository = MapsRepository()

    private val _nearbyAgencies = MutableLiveData<List<Agency>>()
    val nearbyAgencies: LiveData<List<Agency>> = _nearbyAgencies

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchNearbyAgencies(latitude: String, longitude: String) {
        viewModelScope.launch {
            try {
                val agencies = repository.getNearbyAgencies(latitude, longitude)
                _nearbyAgencies.postValue(agencies)
            } catch (e: Exception) {
                _error.postValue("Erreur : ${e.message}")
            }
        }
    }
}
