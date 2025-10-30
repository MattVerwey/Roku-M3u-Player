package com.mattverwey.m3uplayer.repository

import com.mattverwey.m3uplayer.data.cache.CacheManager
import com.mattverwey.m3uplayer.data.model.*
import com.mattverwey.m3uplayer.network.M3UParser
import com.mattverwey.m3uplayer.network.XtreamApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChannelRepository(private val cacheManager: CacheManager) {
    
    private val m3uParser = M3UParser()
    private var xtreamApiService: XtreamApiService? = null
    
    // Load channels from cache or network
    suspend fun loadChannels(forceRefresh: Boolean = false): Result<List<Channel>> {
        return withContext(Dispatchers.IO) {
            try {
                // Try cache first
                if (!forceRefresh) {
                    val cached = cacheManager.getCachedChannels()
                    if (cached != null && cached.isNotEmpty()) {
                        return@withContext Result.success(cached)
                    }
                }
                
                // Load from network based on source type
                val channels = when (cacheManager.getSourceType()) {
                    CacheManager.SourceType.M3U -> loadM3UChannels()
                    CacheManager.SourceType.XTREAM -> loadXtreamChannels()
                }
                
                if (channels.isNotEmpty()) {
                    cacheManager.cacheChannels(channels)
                    Result.success(channels)
                } else {
                    Result.failure(Exception("No channels found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    private suspend fun loadM3UChannels(): List<Channel> {
        val url = cacheManager.getM3UUrl() ?: throw Exception("M3U URL not set")
        
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to load M3U: ${response.code}")
        }
        
        val content = response.body?.string() ?: throw Exception("Empty M3U response")
        return m3uParser.parse(content)
    }
    
    private suspend fun loadXtreamChannels(): List<Channel> {
        val credentials = cacheManager.getXtreamCredentials() 
            ?: throw Exception("Xtream credentials not set")
        
        // Initialize API service if needed
        if (xtreamApiService == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(credentials.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            xtreamApiService = retrofit.create(XtreamApiService::class.java)
        }
        
        val service = xtreamApiService!!
        val channels = mutableListOf<Channel>()
        
        // Get live streams
        val liveResponse = service.getLiveStreams(
            credentials.username,
            credentials.password
        )
        
        if (liveResponse.isSuccessful) {
            liveResponse.body()?.forEach { stream ->
                channels.add(stream.toChannel(credentials))
            }
        }
        
        // Get VOD streams
        val vodResponse = service.getVODStreams(
            credentials.username,
            credentials.password
        )
        
        if (vodResponse.isSuccessful) {
            vodResponse.body()?.forEach { vod ->
                channels.add(vod.toChannel(credentials))
            }
        }
        
        return channels
    }
    
    // Authenticate with Xtream API
    suspend fun authenticateXtream(credentials: XtreamCredentials): Result<XtreamAuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(credentials.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                
                val service = retrofit.create(XtreamApiService::class.java)
                val response = service.authenticate(credentials.username, credentials.password)
                
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    // Check if authentication was successful
                    if (authResponse.user_info?.auth == 1) {
                        cacheManager.setXtreamCredentials(credentials)
                        cacheManager.setSourceType(CacheManager.SourceType.XTREAM)
                        xtreamApiService = service
                        Result.success(authResponse)
                    } else {
                        Result.failure(Exception(authResponse.user_info?.message ?: "Authentication failed"))
                    }
                } else {
                    Result.failure(Exception("Authentication failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // Set M3U URL
    fun setM3UUrl(url: String) {
        cacheManager.setM3UUrl(url)
        cacheManager.setSourceType(CacheManager.SourceType.M3U)
    }
    
    // Recently watched
    fun getRecentlyWatchedChannels(allChannels: List<Channel>): List<Channel> {
        val recentIds = cacheManager.getRecentlyWatched().map { it.channelId }
        return recentIds.mapNotNull { id ->
            allChannels.find { it.id == id }
        }
    }
    
    fun addToRecentlyWatched(channelId: String) {
        cacheManager.addRecentlyWatched(channelId)
    }
    
    // Clear cache
    fun clearCache() {
        cacheManager.clearCache()
    }
}

// Extension functions to convert Xtream models to Channel
private fun XtreamStream.toChannel(credentials: XtreamCredentials): Channel {
    val streamUrl = "${credentials.serverUrl}/live/${credentials.username}/${credentials.password}/$stream_id.m3u8"
    return Channel(
        id = "live_$stream_id",
        name = name,
        streamUrl = streamUrl,
        logoUrl = stream_icon,
        groupTitle = category_id,
        epgChannelId = epg_channel_id,
        category = ChannelCategory.LIVE_TV,
        rating = rating
    )
}

private fun XtreamVOD.toChannel(credentials: XtreamCredentials): Channel {
    val streamUrl = "${credentials.serverUrl}/movie/${credentials.username}/${credentials.password}/$stream_id.$container_extension"
    return Channel(
        id = "vod_$stream_id",
        name = name,
        streamUrl = streamUrl,
        logoUrl = stream_icon,
        category = ChannelCategory.MOVIE,
        rating = rating
    )
}
