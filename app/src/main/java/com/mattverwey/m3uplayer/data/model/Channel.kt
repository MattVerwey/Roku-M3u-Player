package com.mattverwey.m3uplayer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val groupTitle: String? = null,
    val epgChannelId: String? = null,
    val category: ChannelCategory = ChannelCategory.LIVE_TV,
    val description: String? = null,
    val rating: String? = null,
    val duration: Int? = null, // For VOD
    val releaseDate: String? = null,
    val genre: String? = null
) : Parcelable

enum class ChannelCategory {
    LIVE_TV,
    MOVIE,
    SERIES,
    RECENTLY_WATCHED
}
