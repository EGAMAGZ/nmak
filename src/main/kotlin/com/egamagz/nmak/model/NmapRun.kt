package com.egamagz.nmak.model

data class NmapRun(
    val args: String,
    val runStats: RunStats,
    val scanInfo: List<ScanInfo>,
    val hosts: List<Host>
)
