package com.egamagz.nmak.model

data class FinishedStats(
    val elapsed: Float,
    val time: Long,
    val exit: String
)

data class HostsStats(
    val up: Int,
    val down: Int,
    val total: Int,
)

data class RunStats(
    val hosts: HostsStats,
    val finishedStats: FinishedStats
)
