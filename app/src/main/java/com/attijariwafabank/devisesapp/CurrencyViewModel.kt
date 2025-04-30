package com.attijariwafabank.devisesapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {
    private val repository = CurrencyRepository()
    val currenciesLiveData = MutableLiveData<List<String>>()
    val errorLiveData = MutableLiveData<String>()
    private val _errorLiveData = MutableLiveData<String>()



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
    fun fetchCurrencies(accessKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getCurrencies(accessKey)
                if (response != null && response.success == true && response.quotes != null) {
                    val currencies = response.quotes.keySet().toList()
                    _currencies.postValue(currencies)
                } else {
                    _errorLiveData.postValue("Error: Unable to fetch currencies")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }



}