package com.egamagz.nmak.model

data class HostName(
    val name: String,
    val type: String,
)

data class Status(
    val state: String,
    val reason: String,
    val reasonTtl: Int,
)

data class Host(
    val startTime: Long,
    val endTime: Long,
    val status: Status,
    val hostNames: List<HostName>,
)
