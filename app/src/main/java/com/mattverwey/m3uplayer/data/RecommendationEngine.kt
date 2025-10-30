package com.mattverwey.m3uplayer.data

import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.data.model.ChannelCategory
import com.mattverwey.m3uplayer.data.model.RecentlyWatched

/**
 * Simple recommendation engine based on user's watch history
 * Uses genre matching, category preference, and recency weighting
 */
class RecommendationEngine {
    
    /**
     * Generate recommendations based on watch history and available channels
     * @param watchHistory List of recently watched items with timestamps
     * @param allChannels All available channels
     * @param maxRecommendations Maximum number of recommendations to return
     * @return List of recommended channels sorted by relevance score
     */
    fun generateRecommendations(
        watchHistory: List<RecentlyWatched>,
        allChannels: List<Channel>,
        maxRecommendations: Int = 30
    ): List<Channel> {
        if (watchHistory.isEmpty() || allChannels.isEmpty()) {
            return emptyList()
        }
        
        // Get watched channel IDs to exclude from recommendations
        val watchedIds = watchHistory.map { it.channelId }.toSet()
        
        // Get watched channels to analyze preferences
        val watchedChannels = allChannels.filter { it.id in watchedIds }
        
        if (watchedChannels.isEmpty()) {
            return emptyList()
        }
        
        // Calculate preferences from watch history
        val preferences = calculatePreferences(watchedChannels, watchHistory)
        
        // Score all unwatched channels
        val scoredChannels = allChannels
            .filter { it.id !in watchedIds }
            .map { channel ->
                channel to calculateRecommendationScore(channel, preferences)
            }
            .filter { it.second > 0.0 } // Only include channels with positive scores
            .sortedByDescending { it.second }
            .take(maxRecommendations)
            .map { it.first }
        
        return scoredChannels
    }
    
    private fun calculatePreferences(
        watchedChannels: List<Channel>,
        watchHistory: List<RecentlyWatched>
    ): UserPreferences {
        val now = System.currentTimeMillis()
        val genreCounts = mutableMapOf<String, Double>()
        val categoryCounts = mutableMapOf<ChannelCategory, Double>()
        
        // Weight recent watches more heavily
        watchedChannels.forEachIndexed { index, channel ->
            val recentlyWatched = watchHistory.find { it.channelId == channel.id }
            val ageMs = if (recentlyWatched != null) {
                now - recentlyWatched.timestamp
            } else {
                Long.MAX_VALUE
            }
            
            // Exponential decay: recent watches (< 7 days) weighted higher
            val recencyWeight = calculateRecencyWeight(ageMs)
            
            // Count genres with recency weighting
            channel.genre?.split(",")?.forEach { genre ->
                val trimmedGenre = genre.trim().lowercase()
                if (trimmedGenre.isNotEmpty()) {
                    genreCounts[trimmedGenre] = genreCounts.getOrDefault(trimmedGenre, 0.0) + recencyWeight
                }
            }
            
            // Count categories with recency weighting
            categoryCounts[channel.category] = categoryCounts.getOrDefault(channel.category, 0.0) + recencyWeight
        }
        
        return UserPreferences(
            genreWeights = genreCounts,
            categoryWeights = categoryCounts
        )
    }
    
    private fun calculateRecencyWeight(ageMs: Long): Double {
        val ageDays = ageMs / (1000.0 * 60 * 60 * 24)
        return when {
            ageDays < 1 -> 5.0   // Last 24 hours: highest weight
            ageDays < 7 -> 3.0   // Last week: high weight
            ageDays < 30 -> 1.5  // Last month: medium weight
            else -> 1.0          // Older: base weight
        }
    }
    
    private fun calculateRecommendationScore(
        channel: Channel,
        preferences: UserPreferences
    ): Double {
        var score = 0.0
        
        // Score based on genre match
        channel.genre?.split(",")?.forEach { genre ->
            val trimmedGenre = genre.trim().lowercase()
            val genreWeight = preferences.genreWeights[trimmedGenre] ?: 0.0
            score += genreWeight * 2.0 // Genre is 2x important
        }
        
        // Score based on category match
        val categoryWeight = preferences.categoryWeights[channel.category] ?: 0.0
        score += categoryWeight * 1.5 // Category is 1.5x important
        
        // Bonus for highly rated content
        channel.rating?.toDoubleOrNull()?.let { rating ->
            if (rating >= 7.0) {
                score += rating * 0.5
            }
        }
        
        // Bonus for newer content (if has release date)
        channel.releaseDate?.let { releaseDate ->
            val year = releaseDate.take(4).toIntOrNull()
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            if (year != null && year >= currentYear - 3) {
                score += 2.0 // Recent releases get a bonus
            }
        }
        
        return score
    }
    
    private data class UserPreferences(
        val genreWeights: Map<String, Double>,
        val categoryWeights: Map<ChannelCategory, Double>
    )
}
