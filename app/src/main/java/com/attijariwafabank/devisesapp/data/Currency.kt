package com.attijariwafabank.devisesapp.data

import com.google.gson.JsonObject

data class Currency(
    val quotes: JsonObject?,
    val privacy: String?,
    val success: Boolean?,
    val terms: String?,
    val source: String?,
    val timestamp: Long?
)
