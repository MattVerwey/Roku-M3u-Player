package com.mattverwey.m3uplayer.data.model

data class EPGProgram(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String? = null,
    val startTime: Long,
    val endTime: Long,
    val icon: String? = null,
    val category: String? = null
) {
    fun isLive(currentTime: Long = System.currentTimeMillis()): Boolean {
        return currentTime in startTime..endTime
    }

    fun isUpcoming(currentTime: Long = System.currentTimeMillis()): Boolean {
        return startTime > currentTime
    }
}
