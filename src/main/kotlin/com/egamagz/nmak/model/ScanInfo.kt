package com.egamagz.nmak.model

data class Services(
    val services: String,
    val numServices: Int,
)

data class ScanInfo(
    val protocol: String,
    val type: String,
    val services: Services
)
