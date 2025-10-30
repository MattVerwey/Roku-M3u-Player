package com.mattverwey.m3uplayer.ui.playback

import com.mattverwey.m3uplayer.data.model.XtreamCredentials
import com.mattverwey.m3uplayer.data.model.XtreamEpisode
import com.mattverwey.m3uplayer.data.model.XtreamSeriesInfo

/**
 * Helper class to manage series playback and episode navigation
 */
class SeriesPlaybackHelper(
    private val seriesInfo: XtreamSeriesInfo?,
    private val credentials: XtreamCredentials
) {
    
    /**
     * Gets the next episode after the current one
     * @param currentSeason Current season number
     * @param currentEpisode Current episode number
     * @return Next episode or null if no next episode exists
     */
    fun getNextEpisode(currentSeason: Int, currentEpisode: Int): XtreamEpisode? {
        val episodes = seriesInfo?.episodes ?: return null
        
        // Try to find next episode in current season
        val currentSeasonEpisodes = episodes[currentSeason.toString()]
        if (currentSeasonEpisodes != null) {
            val nextInSeason = currentSeasonEpisodes.find { 
                it.episode_num == currentEpisode + 1 
            }
            if (nextInSeason != null) {
                return nextInSeason
            }
        }
        
        // Try to find first episode of next season
        val nextSeasonNum = currentSeason + 1
        val nextSeasonEpisodes = episodes[nextSeasonNum.toString()]
        if (nextSeasonEpisodes != null && nextSeasonEpisodes.isNotEmpty()) {
            return nextSeasonEpisodes.minByOrNull { it.episode_num }
        }
        
        return null
    }
    
    /**
     * Gets the stream URL for an episode
     */
    fun getEpisodeStreamUrl(episode: XtreamEpisode): String {
        val extension = episode.container_extension ?: "mp4"
        return "${credentials.serverUrl}/series/${credentials.username}/${credentials.password}/${episode.id}.$extension"
    }
    
    /**
     * Checks if there is a next episode available
     */
    fun hasNextEpisode(currentSeason: Int, currentEpisode: Int): Boolean {
        return getNextEpisode(currentSeason, currentEpisode) != null
    }
    
    /**
     * Gets episode title for display
     */
    fun getEpisodeTitle(episode: XtreamEpisode): String {
        val seasonNum = episode.season
        val episodeNum = episode.episode_num
        val title = episode.title
        return "S${seasonNum}E${episodeNum} - $title"
    }
}
