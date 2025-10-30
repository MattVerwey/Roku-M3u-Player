package com.mattverwey.m3uplayer.data.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.data.model.EPGProgram
import com.mattverwey.m3uplayer.data.model.RecentlyWatched
import com.mattverwey.m3uplayer.data.model.XtreamCredentials

class CacheManager(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("m3u_player_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_CHANNELS = "cached_channels"
        private const val KEY_EPG = "cached_epg"
        private const val KEY_RECENTLY_WATCHED = "recently_watched"
        private const val KEY_XTREAM_CREDS = "xtream_credentials"
        private const val KEY_M3U_URL = "m3u_url"
        private const val KEY_SOURCE_TYPE = "source_type"
        private const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L // 24 hours
        private const val MAX_RECENTLY_WATCHED = 50
    }
    
    // Source Type
    enum class SourceType {
        M3U, XTREAM
    }
    
    fun getSourceType(): SourceType {
        val type = prefs.getString(KEY_SOURCE_TYPE, SourceType.M3U.name)
        return SourceType.valueOf(type ?: SourceType.M3U.name)
    }
    
    fun setSourceType(type: SourceType) {
        prefs.edit().putString(KEY_SOURCE_TYPE, type.name).apply()
    }
    
    // M3U URL
    fun getM3UUrl(): String? = prefs.getString(KEY_M3U_URL, null)
    
    fun setM3UUrl(url: String) {
        prefs.edit().putString(KEY_M3U_URL, url).apply()
    }
    
    // Xtream Credentials
    fun getXtreamCredentials(): XtreamCredentials? {
        val json = prefs.getString(KEY_XTREAM_CREDS, null) ?: return null
        return try {
            gson.fromJson(json, XtreamCredentials::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun setXtreamCredentials(credentials: XtreamCredentials) {
        val json = gson.toJson(credentials)
        prefs.edit().putString(KEY_XTREAM_CREDS, json).apply()
    }
    
    fun clearXtreamCredentials() {
        prefs.edit().remove(KEY_XTREAM_CREDS).apply()
    }
    
    // Channels Cache
    fun getCachedChannels(): List<Channel>? {
        if (!isCacheValid()) return null
        
        val json = prefs.getString(KEY_CHANNELS, null) ?: return null
        return try {
            val type = object : TypeToken<List<Channel>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    fun cacheChannels(channels: List<Channel>) {
        val json = gson.toJson(channels)
        prefs.edit()
            .putString(KEY_CHANNELS, json)
            .putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }
    
    // EPG Cache
    fun getCachedEPG(): Map<String, List<EPGProgram>>? {
        if (!isCacheValid()) return null
        
        val json = prefs.getString(KEY_EPG, null) ?: return null
        return try {
            val type = object : TypeToken<Map<String, List<EPGProgram>>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    fun cacheEPG(epg: Map<String, List<EPGProgram>>) {
        val json = gson.toJson(epg)
        prefs.edit().putString(KEY_EPG, json).apply()
    }
    
    // Recently Watched
    fun getRecentlyWatched(): List<RecentlyWatched> {
        val json = prefs.getString(KEY_RECENTLY_WATCHED, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<RecentlyWatched>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addRecentlyWatched(channelId: String, position: Long = 0, duration: Long = 0) {
        val current = getRecentlyWatched().toMutableList()
        
        // Remove if already exists
        current.removeAll { it.channelId == channelId }
        
        // Add to front
        current.add(0, RecentlyWatched(channelId, System.currentTimeMillis(), position, duration))
        
        // Keep only recent items
        val trimmed = current.take(MAX_RECENTLY_WATCHED)
        
        val json = gson.toJson(trimmed)
        prefs.edit().putString(KEY_RECENTLY_WATCHED, json).apply()
    }
    
    fun clearRecentlyWatched() {
        prefs.edit().remove(KEY_RECENTLY_WATCHED).apply()
    }
    
    // Cache validation
    private fun isCacheValid(): Boolean {
        val timestamp = prefs.getLong(KEY_CACHE_TIMESTAMP, 0)
        return (System.currentTimeMillis() - timestamp) < CACHE_EXPIRY_MS
    }
    
    fun clearCache() {
        prefs.edit()
            .remove(KEY_CHANNELS)
            .remove(KEY_EPG)
            .remove(KEY_CACHE_TIMESTAMP)
            .apply()
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
