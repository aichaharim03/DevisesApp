package com.attijariwafabank.devisesapp

data class Currency(
    val currencies: Map<String, String>,
    val privacy: String,
    val success: Boolean,
    val terms: String
)
