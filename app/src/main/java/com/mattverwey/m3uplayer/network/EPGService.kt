package com.mattverwey.m3uplayer.network

import com.mattverwey.m3uplayer.data.model.EPGProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class EPGService {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val parser = EPGParser()
    
    // Free EPG sources - using iptv-org EPG which is well-maintained and free
    private val defaultEPGUrls = listOf(
        "https://iptv-org.github.io/epg/guides/us/tvguide.com.epg.xml",
        "https://iptv-org.github.io/epg/guides/uk/tv.sky.com.epg.xml",
        "https://iptv-org.github.io/epg/guides/ca/tvguide.com.epg.xml"
    )
    
    /**
     * Downloads EPG data from a URL and parses it
     * @param epgUrl The URL to download EPG from, or null to use default sources
     * @return Map of channel ID to list of programs
     */
    suspend fun downloadEPG(epgUrl: String? = null): Result<Map<String, List<EPGProgram>>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = epgUrl ?: defaultEPGUrls.first()
                val request = Request.Builder()
                    .url(url)
                    .build()
                
                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to download EPG: ${response.code}"))
                }
                
                val xmlContent = response.body?.string() 
                    ?: return@withContext Result.failure(Exception("Empty EPG response"))
                
                val programs = parser.parse(xmlContent)
                Result.success(programs)
            } catch (e: Exception) {
                // Try fallback sources if the main URL fails
                if (epgUrl == null && defaultEPGUrls.size > 1) {
                    tryFallbackSources()
                } else {
                    Result.failure(e)
                }
            }
        }
    }
    
    /**
     * Tries multiple fallback EPG sources
     */
    private suspend fun tryFallbackSources(): Result<Map<String, List<EPGProgram>>> {
        for (url in defaultEPGUrls) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .build()
                
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val xmlContent = response.body?.string()
                    if (xmlContent != null) {
                        val programs = parser.parse(xmlContent)
                        if (programs.isNotEmpty()) {
                            return Result.success(programs)
                        }
                    }
                }
            } catch (e: Exception) {
                // Continue to next source
                continue
            }
        }
        
        return Result.failure(Exception("All EPG sources failed"))
    }
    
    /**
     * Gets current and upcoming programs for a channel
     */
    fun getCurrentAndUpcomingPrograms(
        channelEpgId: String?,
        allPrograms: Map<String, List<EPGProgram>>
    ): Pair<EPGProgram?, EPGProgram?> {
        if (channelEpgId == null) return Pair(null, null)
        
        val programs = allPrograms[channelEpgId] ?: return Pair(null, null)
        val now = System.currentTimeMillis()
        
        val current = programs.find { it.isLive(now) }
        val upcoming = programs
            .filter { it.isUpcoming(now) }
            .minByOrNull { it.startTime }
        
        return Pair(current, upcoming)
    }
}
