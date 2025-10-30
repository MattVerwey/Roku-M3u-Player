package com.mattverwey.m3uplayer.network

import com.mattverwey.m3uplayer.data.model.EPGProgram
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.*

class EPGParser {
    
    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US)
    
    fun parse(xmlContent: String): Map<String, List<EPGProgram>> {
        val programsByChannel = mutableMapOf<String, MutableList<EPGProgram>>()
        
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xmlContent))
            
            var eventType = parser.eventType
            var currentChannelId: String? = null
            var currentTitle: String? = null
            var currentDescription: String? = null
            var currentStart: Long? = null
            var currentStop: Long? = null
            var currentIcon: String? = null
            var currentCategory: String? = null
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "programme" -> {
                                currentChannelId = parser.getAttributeValue(null, "channel")
                                currentStart = parseDate(parser.getAttributeValue(null, "start"))
                                currentStop = parseDate(parser.getAttributeValue(null, "stop"))
                                currentTitle = null
                                currentDescription = null
                                currentIcon = null
                                currentCategory = null
                            }
                            "title" -> {
                                var foundText = false
                                while (!foundText) {
                                    val nextEvent = parser.next()
                                    if (nextEvent == XmlPullParser.TEXT) {
                                        currentTitle = parser.text
                                        foundText = true
                                    } else if (nextEvent == XmlPullParser.END_TAG && parser.name == "title") {
                                        foundText = true
                                    } else if (nextEvent == XmlPullParser.END_DOCUMENT) {
                                        foundText = true
                                    }
                                }
                            }
                            "desc" -> {
                                var foundText = false
                                while (!foundText) {
                                    val nextEvent = parser.next()
                                    if (nextEvent == XmlPullParser.TEXT) {
                                        currentDescription = parser.text
                                        foundText = true
                                    } else if (nextEvent == XmlPullParser.END_TAG && parser.name == "desc") {
                                        foundText = true
                                    } else if (nextEvent == XmlPullParser.END_DOCUMENT) {
                                        foundText = true
                                    }
                                }
                            }
                            "icon" -> {
                                currentIcon = parser.getAttributeValue(null, "src")
                            }
                            "category" -> {
                                var foundText = false
                                while (!foundText) {
                                    val nextEvent = parser.next()
                                    if (nextEvent == XmlPullParser.TEXT) {
                                        currentCategory = parser.text
                                        foundText = true
                                    } else if (nextEvent == XmlPullParser.END_TAG && parser.name == "category") {
                                        foundText = true
                                    } else if (nextEvent == XmlPullParser.END_DOCUMENT) {
                                        foundText = true
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "programme" && 
                            currentChannelId != null && 
                            currentTitle != null &&
                            currentStart != null && 
                            currentStop != null) {
                            
                            val program = EPGProgram(
                                id = UUID.randomUUID().toString(),
                                channelId = currentChannelId,
                                title = currentTitle,
                                description = currentDescription,
                                startTime = currentStart,
                                endTime = currentStop,
                                icon = currentIcon,
                                category = currentCategory
                            )
                            
                            programsByChannel.getOrPut(currentChannelId) { mutableListOf() }.add(program)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return programsByChannel
    }
    
    private fun parseDate(dateStr: String?): Long? {
        if (dateStr == null) return null
        return try {
            dateFormat.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }
}
