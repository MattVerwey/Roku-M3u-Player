package com.mattverwey.m3uplayer.network

import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.data.model.ChannelCategory
import java.io.BufferedReader
import java.io.StringReader
import java.util.UUID

class M3UParser {
    
    fun parse(m3uContent: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = BufferedReader(StringReader(m3uContent))
        
        var currentName: String? = null
        var currentLogoUrl: String? = null
        var currentGroupTitle: String? = null
        var currentEpgChannelId: String? = null
        
        reader.useLines { lines ->
            lines.forEach { line ->
                val trimmedLine = line.trim()
                
                when {
                    trimmedLine.startsWith("#EXTINF:") -> {
                        // Parse EXTINF line
                        currentName = extractName(trimmedLine)
                        currentLogoUrl = extractAttribute(trimmedLine, "tvg-logo")
                        currentGroupTitle = extractAttribute(trimmedLine, "group-title")
                        currentEpgChannelId = extractAttribute(trimmedLine, "tvg-id")
                    }
                    trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#") && currentName != null -> {
                        // This is the stream URL
                        currentName?.let { name ->
                            channels.add(
                                Channel(
                                    id = UUID.randomUUID().toString(),
                                    name = name,
                                    streamUrl = trimmedLine,
                                    logoUrl = currentLogoUrl,
                                    groupTitle = currentGroupTitle,
                                    epgChannelId = currentEpgChannelId,
                                    category = ChannelCategory.LIVE_TV
                                )
                            )
                        }
                        // Reset for next channel
                        currentName = null
                        currentLogoUrl = null
                        currentGroupTitle = null
                        currentEpgChannelId = null
                    }
                }
            }
        }
        
        return channels
    }
    
    private fun extractName(extinf: String): String {
        // Name is after the last comma
        val lastCommaIndex = extinf.lastIndexOf(',')
        return if (lastCommaIndex != -1 && lastCommaIndex < extinf.length - 1) {
            extinf.substring(lastCommaIndex + 1).trim()
        } else {
            "Unknown Channel"
        }
    }
    
    private fun extractAttribute(extinf: String, attributeName: String): String? {
        // Match patterns like tvg-logo="url" or group-title="title"
        val pattern = """$attributeName="([^"]*)"""".toRegex()
        val match = pattern.find(extinf)
        return match?.groupValues?.getOrNull(1)
    }
}
