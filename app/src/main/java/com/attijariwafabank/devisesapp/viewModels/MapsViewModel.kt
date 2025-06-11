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
    val isLoading = MutableLiveData<Boolean>()
    private val nearbyAgenciesVM = MutableLiveData<List<Agency>>()
    val nearbyAgencies: LiveData<List<Agency>> = nearbyAgenciesVM

    private val errorVM = MutableLiveData<String>()
    val error: LiveData<String> = errorVM

    fun fetchNearbyAgencies(latitude: String, longitude: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val agencies = repository.getNearbyAgencies(latitude, longitude)
                nearbyAgenciesVM.postValue(agencies)
                isLoading.postValue(false)
            } catch (e: Exception) {
                errorVM.postValue("Erreur : ${e.message}")
            }
        }
    }
}
