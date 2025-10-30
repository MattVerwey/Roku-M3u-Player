package com.mattverwey.m3uplayer.data.model

data class RecentlyWatched(
    val channelId: String,
    val timestamp: Long,
    val lastPosition: Long = 0,
    val duration: Long = 0
)
