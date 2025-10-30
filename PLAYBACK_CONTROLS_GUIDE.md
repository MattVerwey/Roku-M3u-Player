# Enhanced Playback Controls Guide

## Overview

This guide covers the enhanced video playback controls implemented in M3U Player for Fire TV. These controls provide a smooth, TV-optimized experience with subtitle management, audio/video track selection, EPG integration, and series navigation.

## Features

### 1. Custom Playback Controls

The player features a custom control overlay that appears when you press the Menu or Center button on your Fire TV remote.

#### Control Layout

```
┌─────────────────────────────────────────┐
│  [Subtitles] [Audio Track] [Quality]   │  ← Top Controls
│                                         │
│            Video Playing Here           │
│                                         │
│  [Rewind]  [Play/Pause]  [Fast Fwd]   │  ← Main Controls
│                                         │
│    [TV Guide]    [Play Next Episode]   │  ← Context Controls
└─────────────────────────────────────────┘
```

#### Auto-Hide Behavior

- Controls automatically appear when you press Menu or Center button
- Controls auto-hide after 5 seconds of inactivity
- Any button press resets the 5-second timer
- Press Back to manually hide controls

### 2. Subtitle Controls

#### Features
- Toggle subtitles on/off without interrupting playback
- Select from multiple subtitle tracks
- Automatic language detection and display
- Smooth switching without rebuffering

#### Usage
1. Press **Menu** to show controls
2. Navigate to **Subtitles** button
3. Press **Select** to open subtitle menu
4. Choose:
   - "Subtitles Off" to disable subtitles
   - Any available subtitle track to enable

#### Supported Formats
- Embedded subtitles in video stream
- External subtitle tracks (SRT, VTT)
- Multi-language support

### 3. Audio Track Selection

#### Features
- Select from multiple audio tracks
- View language and channel information (e.g., "English - 5.1 ch")
- Auto mode for automatic selection
- Instant switching without playback interruption

#### Usage
1. Press **Menu** to show controls
2. Navigate to **Audio Track** button
3. Press **Select** to open audio menu
4. Choose:
   - "Auto" for automatic selection
   - Any available audio track

#### Information Displayed
- Language name (English, Spanish, etc.)
- Track label (if available)
- Channel count (Stereo, 5.1, 7.1)

### 4. Video Quality Selection

#### Features
- Manual quality selection
- View resolution and bitrate information
- Auto mode for adaptive streaming
- Smooth quality switching

#### Usage
1. Press **Menu** to show controls
2. Navigate to **Video Quality** button
3. Press **Select** to open quality menu
4. Choose:
   - "Auto" for adaptive streaming (recommended)
   - Specific resolution (e.g., "1080p - 5000 kbps")

#### Quality Options
- Auto (adaptive bitrate streaming)
- 1080p (Full HD)
- 720p (HD)
- 480p (SD)
- Lower resolutions based on available streams

### 5. Seek Controls (Rewind/Fast Forward)

#### Features
- 10-second skip intervals
- Smooth seeking without rebuffering
- Works with both live streams and VOD

#### Usage
- Press **Rewind Button** to skip back 10 seconds
- Press **Fast Forward Button** to skip forward 10 seconds
- Press **Center** on rewind/forward button in controls for same effect

#### Remote Key Support
- `MEDIA_REWIND` key: Skip back 10 seconds
- `MEDIA_FAST_FORWARD` key: Skip forward 10 seconds

### 6. TV Guide (EPG) Integration

#### Features
- Electronic Program Guide for live TV channels
- Current program information
- Upcoming program preview
- Free EPG data from iptv-org

#### Usage (Live TV Only)
1. Press **Menu** to show controls
2. Navigate to **TV Guide** button (only visible for live TV)
3. Press **Select** to toggle guide overlay
4. View:
   - Now Playing: Current program title, time, and description
   - Up Next: Next program information

#### EPG Data Sources
- US: TVGuide.com EPG
- UK: Sky.com EPG
- Canada: TVGuide.com EPG
- Automatic fallback to alternate sources

#### EPG Display Format
```
┌────────────────────────────────┐
│ TV Guide                       │
├────────────────────────────────┤
│ NOW PLAYING                    │
│ Program Title                  │
│ 8:00 PM - 9:00 PM             │
│ Program description...         │
├────────────────────────────────┤
│ UP NEXT                        │
│ Next Program Title             │
│ 9:00 PM - 10:00 PM            │
│ Description...                 │
└────────────────────────────────┘
```

### 7. Series Navigation

#### Features
- Play next episode button
- Auto-play next episode on completion
- Season and episode information
- Seamless episode transitions

#### Usage (Series Only)
1. While watching a series episode, press **Menu**
2. Navigate to **Play Next Episode** button (only visible if next episode exists)
3. Press **Select** to play next episode
4. Or wait for episode to finish for auto-play

#### Episode Navigation Logic
1. Plays next episode in current season
2. If last episode of season, plays first episode of next season
3. If last episode of series, playback ends

#### Episode Display Format
- Format: `S01E02 - Episode Title`
- Example: `S02E05 - The Big Reveal`

### 8. Play/Pause Control

#### Usage
- Press **Play/Pause** button on remote
- Or navigate to play/pause button in controls and press **Select**
- Button shows current state (Play or Pause)

## Remote Control Reference

### Fire TV Remote Keys

| Button | Action (No Controls) | Action (Controls Visible) |
|--------|---------------------|--------------------------|
| Menu | Show controls | Hide controls |
| Center/Select | Show controls | Activate focused button |
| Back | Exit player | Hide controls |
| Play/Pause | Toggle playback | Toggle playback |
| Rewind | Skip back 10s | Skip back 10s |
| Fast Forward | Skip forward 10s | Skip forward 10s |
| D-Pad | (none) | Navigate controls |

### Fire TV Cube Voice Commands

Not yet implemented, but planned for future release:
- "Alexa, pause"
- "Alexa, play"
- "Alexa, turn on subtitles"

## Performance Considerations

### Optimized for Smooth Playback

1. **No Quality Degradation**
   - All control operations happen without stopping playback
   - Track switching uses ExoPlayer's seamless switching
   - Buffer is maintained during all operations

2. **Buffering Strategy**
   - 60-second maximum buffer
   - 2.5-second pre-buffer for fast startup
   - Adaptive bitrate starts with SD, upgrades to HD

3. **Control Responsiveness**
   - Controls appear instantly (no delay)
   - Button focus is optimized for D-Pad navigation
   - Auto-hide timer prevents control clutter

4. **EPG Performance**
   - EPG downloads in background
   - No impact on playback performance
   - Cached for fast access
   - Fallback sources for reliability

## Troubleshooting

### Subtitles Not Appearing

1. Check if stream includes subtitle tracks
2. Ensure subtitles are not disabled in track selection
3. Try different subtitle tracks from menu
4. Some streams may not include subtitles

### Audio Out of Sync

1. Try selecting different audio track
2. Restart playback
3. Check if issue is with specific stream

### Controls Not Showing

1. Press **Menu** button
2. If still not showing, press **Back** then **Menu** again
3. Check if remote batteries are low

### TV Guide Not Loading

1. Ensure device has internet connection
2. Wait a few seconds for EPG to download
3. TV guide only available for live TV channels
4. Some channels may not have EPG data

### Next Episode Not Available

1. Ensure you're watching a series (not a movie)
2. Check if you're on the last episode of series
3. Series info must be provided when starting playback

## Technical Details

### Architecture

```
PlaybackActivity
├── ExoPlayer (Media3)
│   ├── Video Renderer
│   ├── Audio Renderer
│   └── Text Renderer (Subtitles)
├── TrackSelectionDialog
│   ├── Subtitle Track Manager
│   ├── Audio Track Manager
│   └── Video Track Manager
├── EPGService
│   ├── EPGParser (XMLTV)
│   └── EPG Downloader
└── SeriesPlaybackHelper
    └── Episode Navigator
```

### ExoPlayer Configuration

- **LoadControl**: Custom buffering (60s max, 2.5s pre-buffer)
- **TrackSelector**: DefaultTrackSelector with adaptive parameters
- **Player Type**: ExoPlayer with HLS support
- **Seek Parameters**: DefaultSeekParameters for smooth seeking

### EPG Data Format

- **Format**: XMLTV (standard EPG format)
- **Encoding**: UTF-8
- **Update Frequency**: Daily
- **Cache Duration**: 24 hours

## Best Practices

### For Best Playback Experience

1. **Use Ethernet Connection**: For most reliable streaming
2. **Keep Controls Hidden**: Auto-hide prevents distraction
3. **Use Auto Quality**: Adaptive streaming adjusts to connection
4. **Enable Subtitles When Needed**: Easy on/off toggle available
5. **Use EPG**: See what's playing and what's next

### For Series Watching

1. **Let Episodes Auto-Play**: Seamless binge-watching experience
2. **Use Play Next Button**: Manual control when desired
3. **Check Episode Info**: Season/episode display helps track progress

### For Live TV

1. **Check TV Guide**: Know what's currently airing
2. **Use EPG for Schedule**: See upcoming programs
3. **Rewind Works on Live**: 10-second skip back available

## Future Enhancements

Planned features for upcoming releases:

- [ ] Customizable seek intervals (5s, 10s, 30s, 60s)
- [ ] Picture-in-Picture with TV guide
- [ ] Bookmarks and resume points
- [ ] Advanced subtitle styling
- [ ] Audio/subtitle language preferences
- [ ] EPG search and filtering
- [ ] Voice control integration
- [ ] Catch-up TV integration
- [ ] Recording scheduled programs

## Feedback

For issues or feature requests related to playback controls, please open an issue on GitHub with:
- Fire TV device model
- Android version
- Description of the issue
- Steps to reproduce
- Expected vs actual behavior
