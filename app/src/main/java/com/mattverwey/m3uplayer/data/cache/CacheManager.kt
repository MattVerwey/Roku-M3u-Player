package com.mattverwey.m3uplayer.data.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.data.model.EPGProgram
import com.mattverwey.m3uplayer.data.model.RecentlyWatched
import com.mattverwey.m3uplayer.data.model.XtreamCredentials

class CacheManager(context: Context) {
    
    // Regular SharedPreferences for non-sensitive data (channels, cache)
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("m3u_player_cache", Context.MODE_PRIVATE)
    
    // Encrypted SharedPreferences for sensitive data (credentials, viewing history)
    private val securePrefs: SharedPreferences = createEncryptedPreferences(context)
    
    private val gson = Gson()
    
    private fun createEncryptedPreferences(context: Context): SharedPreferences {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            return EncryptedSharedPreferences.create(
                context,
                "m3u_player_secure",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Encryption is critical for security - do not allow app to run without it
            throw SecurityException(
                "Failed to initialize encrypted storage. The app cannot run without encryption support. " +
                "This device may not support Android Keystore or secure storage. Error: ${e.message}",
                e
            )
        }
    }
    
    companion object {
        // Keys for regular SharedPreferences (non-sensitive metadata only)
        private const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
        private const val KEY_SOURCE_TYPE = "source_type"
        
        // Keys for encrypted SharedPreferences (all sensitive data)
        private const val KEY_CHANNELS = "cached_channels"  // Contains stream URLs
        private const val KEY_EPG = "cached_epg"  // Contains viewing patterns
        private const val KEY_XTREAM_CREDS = "xtream_credentials"
        private const val KEY_M3U_URL = "m3u_url"
        private const val KEY_RECENTLY_WATCHED = "recently_watched"
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_TRACKING_ENABLED = "tracking_enabled"
        
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
    
    // M3U URL (stored securely)
    fun getM3UUrl(): String? = securePrefs.getString(KEY_M3U_URL, null)
    
    fun setM3UUrl(url: String) {
        securePrefs.edit().putString(KEY_M3U_URL, url).apply()
    }
    
    fun clearM3UUrl() {
        securePrefs.edit().remove(KEY_M3U_URL).apply()
    }
    
    // Xtream Credentials (stored securely)
    fun getXtreamCredentials(): XtreamCredentials? {
        val json = securePrefs.getString(KEY_XTREAM_CREDS, null) ?: return null
        return try {
            gson.fromJson(json, XtreamCredentials::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun setXtreamCredentials(credentials: XtreamCredentials) {
        val json = gson.toJson(credentials)
        securePrefs.edit().putString(KEY_XTREAM_CREDS, json).apply()
    }
    
    fun clearXtreamCredentials() {
        securePrefs.edit().remove(KEY_XTREAM_CREDS).apply()
    }
    
    // Channels Cache (stored securely since they contain stream URLs with potential credentials)
    fun getCachedChannels(): List<Channel>? {
        if (!isCacheValid()) return null
        
        val json = securePrefs.getString(KEY_CHANNELS, null) ?: return null
        return try {
            val type = object : TypeToken<List<Channel>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    fun cacheChannels(channels: List<Channel>) {
        val json = gson.toJson(channels)
        securePrefs.edit()
            .putString(KEY_CHANNELS, json)
            .apply()
        // Store timestamp in regular prefs (non-sensitive)
        prefs.edit()
            .putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }
    
    // EPG Cache (stored securely to protect viewing patterns)
    fun getCachedEPG(): Map<String, List<EPGProgram>>? {
        if (!isCacheValid()) return null
        
        val json = securePrefs.getString(KEY_EPG, null) ?: return null
        return try {
            val type = object : TypeToken<Map<String, List<EPGProgram>>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    fun cacheEPG(epg: Map<String, List<EPGProgram>>) {
        val json = gson.toJson(epg)
        securePrefs.edit().putString(KEY_EPG, json).apply()
    }
    
    // Viewing History Privacy Controls
    fun isTrackingEnabled(): Boolean {
        return securePrefs.getBoolean(KEY_TRACKING_ENABLED, true)
    }
    
    fun setTrackingEnabled(enabled: Boolean) {
        securePrefs.edit().putBoolean(KEY_TRACKING_ENABLED, enabled).apply()
        if (!enabled) {
            // Clear history when disabling tracking
            clearRecentlyWatched()
        }
    }
    
    // Recently Watched (stored securely)
    fun getRecentlyWatched(): List<RecentlyWatched> {
        if (!isTrackingEnabled()) return emptyList()
        
        val json = securePrefs.getString(KEY_RECENTLY_WATCHED, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<RecentlyWatched>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addRecentlyWatched(channelId: String, position: Long = 0, duration: Long = 0) {
        // Don't track if user has disabled tracking
        if (!isTrackingEnabled()) return
        
        val current = getRecentlyWatched().toMutableList()
        
        // Remove if already exists
        current.removeAll { it.channelId == channelId }
        
        // Add to front
        current.add(0, RecentlyWatched(channelId, System.currentTimeMillis(), position, duration))
        
        // Keep only recent items
        val trimmed = current.take(MAX_RECENTLY_WATCHED)
        
        val json = gson.toJson(trimmed)
        securePrefs.edit().putString(KEY_RECENTLY_WATCHED, json).apply()
    }
    
    fun clearRecentlyWatched() {
        securePrefs.edit().remove(KEY_RECENTLY_WATCHED).apply()
    }
    
    // Favorites (stored securely)
    fun getFavorites(): Set<String> {
        val json = securePrefs.getString(KEY_FAVORITES, null) ?: return emptySet()
        return try {
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    fun addToFavorites(channelId: String) {
        val current = getFavorites().toMutableSet()
        current.add(channelId)
        val json = gson.toJson(current)
        securePrefs.edit().putString(KEY_FAVORITES, json).apply()
    }
    
    fun removeFromFavorites(channelId: String) {
        val current = getFavorites().toMutableSet()
        current.remove(channelId)
        val json = gson.toJson(current)
        securePrefs.edit().putString(KEY_FAVORITES, json).apply()
    }
    
    fun isFavorite(channelId: String): Boolean {
        return getFavorites().contains(channelId)
    }
    
    fun clearFavorites() {
        securePrefs.edit().remove(KEY_FAVORITES).apply()
    }
    
    // Cache validation
    private fun isCacheValid(): Boolean {
        val timestamp = prefs.getLong(KEY_CACHE_TIMESTAMP, 0)
        return (System.currentTimeMillis() - timestamp) < CACHE_EXPIRY_MS
    }
    
    fun clearCache() {
        securePrefs.edit()
            .remove(KEY_CHANNELS)
            .remove(KEY_EPG)
            .apply()
        prefs.edit()
            .remove(KEY_CACHE_TIMESTAMP)
            .apply()
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
        securePrefs.edit().clear().apply()
    }
    
    fun clearAllCredentials() {
        clearXtreamCredentials()
        clearM3UUrl()
        clearRecentlyWatched()
    }
    
    // Secure logout - clears all sensitive data
    fun secureLogout() {
        clearAllCredentials()
        clearCache()
        // Note: JVM memory management is automatic. Explicit garbage collection is not guaranteed to clear sensitive data from memory.
    }
}
