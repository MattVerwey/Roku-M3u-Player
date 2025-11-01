package com.mattverwey.m3uplayer.data.model

/**
 * Represents a category selector card for navigation to category browsing pages.
 */
data class CategorySelector(
    val categoryType: ChannelCategory,
    val title: String,
    val icon: String
)
