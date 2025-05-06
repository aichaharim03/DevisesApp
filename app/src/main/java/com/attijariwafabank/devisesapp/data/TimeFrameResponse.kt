package com.attijariwafabank.devisesapp.data


data class TimeFrameResponse(
    val success: Boolean?,
    val terms: String?,
    val privacy: String?,
    val timeframe: Boolean?,
    val start_date: String?,
    val end_date: String?,
    val source: String?,
    val quotes: Map<String, Map<String, Double>>?,
)
