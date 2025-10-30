package com.mattverwey.m3uplayer.ui.playback

import android.app.AlertDialog
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer

class TrackSelectionDialog(
    private val context: Context,
    private val player: ExoPlayer
) {
    
    enum class TrackType {
        SUBTITLE,
        AUDIO,
        VIDEO
    }
    
    fun showTrackSelectionDialog(trackType: TrackType) {
        val tracks = player.currentTracks
        
        val trackGroups = when (trackType) {
            TrackType.SUBTITLE -> tracks.groups.filter { it.type == C.TRACK_TYPE_TEXT }
            TrackType.AUDIO -> tracks.groups.filter { it.type == C.TRACK_TYPE_AUDIO }
            TrackType.VIDEO -> tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }
        }
        
        if (trackGroups.isEmpty()) {
            showNoTracksDialog(trackType)
            return
        }
        
        val trackNames = mutableListOf<String>()
        val trackSelections = mutableListOf<Pair<Int, Int>>() // groupIndex, trackIndex
        
        // Add "Off" option for subtitles
        if (trackType == TrackType.SUBTITLE) {
            trackNames.add(context.getString(com.mattverwey.m3uplayer.R.string.subtitles_off))
            trackSelections.add(Pair(-1, -1))
        }
        
        // Add "Auto" option for audio and video
        if (trackType == TrackType.AUDIO || trackType == TrackType.VIDEO) {
            trackNames.add(context.getString(com.mattverwey.m3uplayer.R.string.track_auto))
            trackSelections.add(Pair(-1, -1))
        }
        
        trackGroups.forEachIndexed { groupIndex, trackGroup ->
            for (trackIndex in 0 until trackGroup.length) {
                val format = trackGroup.getTrackFormat(trackIndex)
                val trackName = buildTrackName(format, trackType)
                trackNames.add(trackName)
                trackSelections.add(Pair(groupIndex, trackIndex))
            }
        }
        
        val currentSelection = getCurrentTrackSelection(trackType, trackGroups)
        
        AlertDialog.Builder(context)
            .setTitle(getDialogTitle(trackType))
            .setSingleChoiceItems(
                trackNames.toTypedArray(),
                currentSelection
            ) { dialog, which ->
                val selection = trackSelections[which]
                selectTrack(trackType, selection.first, selection.second)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    private fun buildTrackName(format: androidx.media3.common.Format, trackType: TrackType): String {
        return when (trackType) {
            TrackType.SUBTITLE -> {
                format.language?.let { lang ->
                    java.util.Locale(lang).displayLanguage
                } ?: format.label ?: "Unknown"
            }
            TrackType.AUDIO -> {
                buildString {
                    format.language?.let { lang ->
                        append(java.util.Locale(lang).displayLanguage)
                    }
                    format.label?.let { label ->
                        if (isNotEmpty()) append(" - ")
                        append(label)
                    }
                    if (format.channelCount > 0) {
                        if (isNotEmpty()) append(" - ")
                        append("${format.channelCount} ch")
                    }
                    if (isEmpty()) {
                        append("Track ${format.id}")
                    }
                }
            }
            TrackType.VIDEO -> {
                buildString {
                    if (format.width > 0 && format.height > 0) {
                        append("${format.height}p")
                    }
                    if (format.bitrate > 0) {
                        if (isNotEmpty()) append(" - ")
                        append("${format.bitrate / 1000} kbps")
                    }
                    if (isEmpty()) {
                        append("Track ${format.id}")
                    }
                }
            }
        }
    }
    
    private fun getCurrentTrackSelection(
        trackType: TrackType,
        trackGroups: List<Tracks.Group>
    ): Int {
        var selectedIndex = 0 // Default to first item (Off/Auto)
        
        trackGroups.forEachIndexed { groupIndex, trackGroup ->
            for (trackIndex in 0 until trackGroup.length) {
                if (trackGroup.isTrackSelected(trackIndex)) {
                    // Calculate the position in the dialog list
                    selectedIndex = 1 + // offset for Off/Auto
                            trackGroups.take(groupIndex).sumOf { it.length } +
                            trackIndex
                    return selectedIndex
                }
            }
        }
        
        return selectedIndex
    }
    
    private fun selectTrack(trackType: TrackType, groupIndex: Int, trackIndex: Int) {
        val trackSelector = player.trackSelector ?: return
        
        val parameters = trackSelector.parameters.buildUpon()
        
        when (trackType) {
            TrackType.SUBTITLE -> {
                if (groupIndex == -1) {
                    // Disable subtitles
                    parameters.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                } else {
                    // Enable subtitles and select specific track
                    parameters.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                    // Note: ExoPlayer will automatically select the best matching track
                }
            }
            TrackType.AUDIO -> {
                if (groupIndex == -1) {
                    // Auto selection - clear manual overrides
                    parameters.clearOverridesOfType(C.TRACK_TYPE_AUDIO)
                } else {
                    // Manual selection - override with specific track
                    val tracks = player.currentTracks
                    val audioGroups = tracks.groups.filter { it.type == C.TRACK_TYPE_AUDIO }
                    if (groupIndex < audioGroups.size) {
                        val selectedGroup = audioGroups[groupIndex]
                        val trackGroup = selectedGroup.mediaTrackGroup
                        
                        // Create override to select specific track
                        val override = androidx.media3.common.TrackSelectionOverride(
                            trackGroup,
                            listOf(trackIndex.coerceIn(0, trackGroup.length - 1))
                        )
                        parameters.clearOverridesOfType(C.TRACK_TYPE_AUDIO)
                        parameters.addOverride(override)
                    }
                }
            }
            TrackType.VIDEO -> {
                if (groupIndex == -1) {
                    // Auto selection - clear manual overrides
                    parameters.clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                } else {
                    // Manual selection - override with specific track
                    val tracks = player.currentTracks
                    val videoGroups = tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }
                    if (groupIndex < videoGroups.size) {
                        val selectedGroup = videoGroups[groupIndex]
                        val trackGroup = selectedGroup.mediaTrackGroup
                        
                        // Create override to select specific track
                        val override = androidx.media3.common.TrackSelectionOverride(
                            trackGroup,
                            listOf(trackIndex.coerceIn(0, trackGroup.length - 1))
                        )
                        parameters.clearOverridesOfType(C.TRACK_TYPE_VIDEO)
                        parameters.addOverride(override)
                    }
                }
            }
        }
        
        trackSelector.setParameters(parameters)
    }
    
    private fun showNoTracksDialog(trackType: TrackType) {
        AlertDialog.Builder(context)
            .setTitle(getDialogTitle(trackType))
            .setMessage(context.getString(com.mattverwey.m3uplayer.R.string.no_tracks_available))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
    
    private fun getDialogTitle(trackType: TrackType): String {
        return when (trackType) {
            TrackType.SUBTITLE -> context.getString(com.mattverwey.m3uplayer.R.string.subtitles)
            TrackType.AUDIO -> context.getString(com.mattverwey.m3uplayer.R.string.audio_track)
            TrackType.VIDEO -> context.getString(com.mattverwey.m3uplayer.R.string.video_quality)
        }
    }
}
