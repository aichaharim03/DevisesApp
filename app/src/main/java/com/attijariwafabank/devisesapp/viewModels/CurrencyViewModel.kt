package com.attijariwafabank.devisesapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attijariwafabank.devisesapp.repo.CurrencyRepository
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    private val repository = CurrencyRepository()
    val errorLiveData = MutableLiveData<String>()
    private val errorLiveDataVM = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    private val historicalRatesVM = MutableLiveData<Map<String, Double>>()
    val historicalRates: LiveData<Map<String, Double>> get() = historicalRatesVM

    private val conversionResultVM = MutableLiveData<Double>()
    val conversionResult: LiveData<Double> get() = conversionResultVM
    private val currenciesVM = MutableLiveData<List<String>>()
    val currencies: LiveData<List<String>> get() = currenciesVM



    fun convertCurrency(accessKey: String, from: String, to: String, amount: Double, date: String? = null) {
        isLoading.value = true
        viewModelScope.launch {
            val response = repository.convertCurrency(accessKey, from, to, amount, date)
            response?.let {
                conversionResultVM.value = it.result
                isLoading.postValue(false)

            }
        }
    }
    fun fetchCurrencies(accessKey: String , source: String , currencies: String? = null) {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.getCurrencies(accessKey , source , currencies)
                if (response != null && response.success == true && response.quotes != null) {
                    val sourcePrefix = response.source ?: ""
                    val currencyList = response.quotes.entrySet()
                        .map { entry -> "${entry.key.removePrefix(sourcePrefix)}: ${entry.value}" }
                        .toList()
                    currenciesVM.postValue(currencyList)
                    isLoading.postValue(false)
                } else {
                    errorLiveDataVM.postValue("Error: Unable to fetch currencies")
                }
            } catch (e: Exception) {
                errorLiveDataVM.postValue("Exception: ${e.localizedMessage}")
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
        isLoading.value = true
        viewModelScope.launch {
            val response = repository.getTimeFrame(accessKey, source, targetCurrency, startDate, endDate)
            if (response?.success == true && response.quotes != null) {
                val key = source + targetCurrency.trim()
                val rateMap = response.quotes.mapValues { it.value[key] ?: 0.0 }
                historicalRatesVM.postValue(rateMap)
                Log.d("API_STATUS", "TimeFrame API call successful: $response")
                isLoading.postValue(false)

            } else {
                Log.d("API_STATUS", "TimeFrame API call unsuccessful: $response")
            }
        }
    }


}