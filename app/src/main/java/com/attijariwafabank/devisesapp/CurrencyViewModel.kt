package com.attijariwafabank.devisesapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.attijariwafabank.devisesapp.Currencies
import retrofit2.Response

class CurrencyViewModel : ViewModel() {
    private val repository = CurrencyRepository()
    val currenciesLiveData = MutableLiveData<List<String>>()
    val errorLiveData = MutableLiveData<String>()

    private val _conversionResult = MutableLiveData<Double>()
    val conversionResult: LiveData<Double> get() = _conversionResult

    fun convertCurrency(accessKey: String, from: String, to: String, amount: Double, date: String? = null) {
        viewModelScope.launch {
            val response = repository.convertCurrency(accessKey, from, to, amount, date)
            response?.let {
                _conversionResult.value = it.result
            }
        }
    }
    fun fetchCurrencies(accessKey: String) {
        viewModelScope.launch {
            try {
                // Make API request to get list of currencies
                val response = RetrofitInstance.api.getCurrencies(accessKey)

                // Check if the response is successful
                if (response.isSuccessful) {
                    // Extract currency codes (keys of the 'currencies' map) and post to LiveData
                    currenciesLiveData.postValue(response.body()?.currencies?.keys?.toList())
                } else {
                    errorLiveData.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                errorLiveData.postValue("Error: ${e.message}")
            }
        }
    }


}