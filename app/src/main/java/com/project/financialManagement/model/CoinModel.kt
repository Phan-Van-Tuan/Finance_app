package com.project.financialManagement.model

enum class CoinModel(val id: Int, val value: String) {
    USD(0, "United States Dollar (USD)"),
    EUR(1, "Euro (EUR)"),
    JPY(2, "Japanese Yen (JPY)"),
    GBP(3, "British Pound (GBP)"),
    AUD(4, "Australian Dollar (AUD)"),
    CAD(5, "Canadian Dollar (CAD)"),
    CHF(6, "Swiss Franc (CHF)"),
    CNY(7, "Chinese Yuan (CNY)"),
    SEK(8, "Swedish Krona (SEK)"),
    VND(9,"Viet Nam Dong (VND)")
}