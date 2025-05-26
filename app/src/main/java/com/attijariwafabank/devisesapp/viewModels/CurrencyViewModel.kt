package com.attijariwafabank.devisesapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attijariwafabank.devisesapp.CurrencyRepository
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    private val repository = CurrencyRepository()
    val currenciesLiveData = MutableLiveData<List<String>>()
    val errorLiveData = MutableLiveData<String>()
    private val _errorLiveData = MutableLiveData<String>()

    private val _historicalRates = MutableLiveData<Map<String, Double>>()
    val historicalRates: LiveData<Map<String, Double>> get() = _historicalRates

    private val _conversionResult = MutableLiveData<Double>()
    val conversionResult: LiveData<Double> get() = _conversionResult
    private val _currencies = MutableLiveData<List<String>>()
    val currencies: LiveData<List<String>> get() = _currencies



    fun convertCurrency(accessKey: String, from: String, to: String, amount: Double, date: String? = null) {
        viewModelScope.launch {
            val response = repository.convertCurrency(accessKey, from, to, amount, date)
            response?.let {
                _conversionResult.value = it.result
            }
        }
    }
    fun fetchCurrencies(accessKey: String , source: String , currencies: String? = null) {
        viewModelScope.launch {
            try {
                val response = repository.getCurrencies(accessKey , source , currencies)
                if (response != null && response.success == true && response.quotes != null) {
                    val sourcePrefix = response.source ?: ""
                    val currencyList = response.quotes.entrySet()
                        .map { entry -> "${entry.key.removePrefix(sourcePrefix)}: ${entry.value}" }
                        .toList()
                    _currencies.postValue(currencyList)
                } else {
                    _errorLiveData.postValue("Error: Unable to fetch currencies")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }


    fun requestTimeFrame(
        accessKey: String,
        source: String,
        targetCurrency: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            val response = repository.getTimeFrame(accessKey, source, targetCurrency, startDate, endDate)
            if (response?.success == true && response.quotes != null) {
                val key = source + targetCurrency.trim()
                val rateMap = response.quotes.mapValues { it.value[key] ?: 0.0 }
                _historicalRates.postValue(rateMap)
                Log.d("API_STATUS", "TimeFrame API call successful: $response")
            } else {
                Log.d("API_STATUS", "TimeFrame API call unsuccessful: $response")
            }
        }
    }


}