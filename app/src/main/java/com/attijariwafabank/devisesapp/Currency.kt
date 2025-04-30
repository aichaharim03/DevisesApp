package com.attijariwafabank.devisesapp

import com.google.gson.JsonObject
import okhttp3.ResponseBody

data class Currency(
    val quotes: JsonObject?,
    val privacy: String?,
    val success: Boolean?,
    val terms: String?,
    val source: String?,
    val timestamp: Long?
)
