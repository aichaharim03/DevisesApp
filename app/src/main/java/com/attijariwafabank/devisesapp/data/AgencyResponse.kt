package com.attijariwafabank.devisesapp.data

import com.google.gson.annotations.SerializedName

data class AgencyResponse(
    @SerializedName("agence")
    val agency: List<Agency>

)
