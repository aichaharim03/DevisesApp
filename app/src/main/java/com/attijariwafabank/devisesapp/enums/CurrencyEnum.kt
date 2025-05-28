package com.attijariwafabank.devisesapp.enums


enum class CurrencyEnum(val code: String) {
    USD("USD"),
    EUR("EUR"),
    GBP("GBP"),
    CAD("CAD"),
    AUD("AUD"),
    JPY("JPY"),
    CHF("CHF"),
    CNY("CNY"),
    SEK("SEK"),
    NZD("NZD"),
    INR("INR"),
    MLR("MLR"),
    MAD("MAD");

    override fun toString(): String = code

    companion object {
        fun fromString(text: String): CurrencyEnum? {
            val code = text.split(":")[0].trim()
            return entries.find { it.code == code }
        }
    }
}