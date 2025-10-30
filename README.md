# M3U Player for Amazon Fire TV

A modern IPTV player for Amazon Fire TV Cube and Fire TV devices with support for M3U playlists and Xtream Codes API.

## Features

### Core Functionality
- ✅ **M3U Playlist Support** - Load channels from standard M3U playlist URLs
- ✅ **Xtream Codes API** - Full support for Xtream Codes authentication and streaming
- ✅ **Modern Android TV UI** - Built with Android TV Leanback library for optimal TV experience
- ✅ **Picture-in-Picture (PIP)** - Watch while browsing with native Android PIP support
- ✅ **Local Caching** - Playlists and EPG data cached on device for fast access
- ✅ **Recently Watched** - Track and quickly access recently watched channels
- ✅ **Image Loading** - Channel logos and poster images with caching

### User Interface
- Modern card-based browsing with poster grid layout
- Category organization (Live TV, Movies, Series)
- Channel details screen with metadata
- Smooth navigation optimized for D-pad control
- Dark theme optimized for TV viewing

### Video Playback
- Powered by ExoPlayer for robust streaming
- Support for multiple formats: HLS, RTMP, MP4, MKV, and more
- **Optimized buffering**: 60-second buffer for smooth playback
- **Adaptive streaming**: Starts SD, upgrades to HD automatically
- **Fast startup**: 2.5-second pre-buffer for quick playback
- Picture-in-Picture mode for multitasking
- Playback controls optimized for Fire TV remote

### Data Management
- Persistent caching of playlists (24-hour expiry)
- Secure credential storage
- Recently watched tracking (up to 50 channels)
- Offline access to cached data

## Technology Stack

- **Platform**: Android TV (Fire TV compatible)
- **Language**: Kotlin
- **UI Framework**: Android TV Leanback
- **Video Player**: ExoPlayer (Media3)
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Architecture**: MVVM with Repository pattern

## Documentation

### Testing and Guides
- 📘 **[Fire TV Testing Guide](FIRE_TV_TESTING_GUIDE.md)** - Complete guide for testing on Amazon Fire TV Cube
  - Installation methods (ADB, Downloader app)
  - Comprehensive testing checklist
  - UI and performance testing procedures
  - Troubleshooting common issues
  
- 🎨 **[UI Design Documentation](UI_DESIGN_DOCUMENTATION.md)** - Detailed UI specifications
  - Modern and sleek design principles
  - Screen-by-screen design breakdown
  - Color palette and typography
  - Responsive design guidelines
  
- ⚡ **[Buffering Optimization](BUFFERING_OPTIMIZATION.md)** - Video buffering improvements
  - Technical implementation details
  - Performance benchmarks
  - Troubleshooting buffering issues
  - Best practices for smooth playback

### Migration Guides
- 📄 **[Migration Guide](MIGRATION_GUIDE.md)** - Roku to Android TV migration
- 📄 **[Implementation Summary](IMPLEMENTATION_SUMMARY.md)** - Technical implementation details
- 📄 **[Legacy Roku Files](LEGACY_ROKU_FILES.md)** - Original Roku codebase reference

## Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or newer
- Fire TV Cube or Fire TV device (Android 5.0+)
- ADB enabled on Fire TV device

### Building the App

1. Clone the repository:
```bash
git clone https://github.com/MattVerwey/Roku-M3u-Player.git
cd Roku-M3u-Player
```

2. Open project in Android Studio

3. Sync Gradle dependencies

4. Build and run on Fire TV device:
```bash
./gradlew installDebug
```

### Sideloading to Fire TV

1. Enable ADB on Fire TV:
   - Settings → My Fire TV → Developer Options → ADB Debugging

2. Connect via ADB:
```bash
adb connect <FIRE_TV_IP>:5555
```

3. Install APK:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**For detailed installation instructions and testing procedures, see [FIRE_TV_TESTING_GUIDE.md](FIRE_TV_TESTING_GUIDE.md)**

## Usage

### First Time Setup

1. Launch M3U Player from Fire TV Apps
2. Choose login method:
   - **M3U URL**: Enter direct link to .m3u playlist file
   - **Xtream Codes**: Enter server URL, username, and password

### Navigation

- **D-Pad**: Navigate through channels and categories
- **Select/Enter**: Play channel or view details
- **Back**: Return to previous screen
- **Home**: Enter Picture-in-Picture mode (during playback)
- **Menu** (3 lines button): Access quick options menu
  - Refresh Channels
  - Clear Cache
  - Logout

### Picture-in-Picture

- Press **Home** button during playback to enter PIP mode
- Video continues playing in corner while you browse
- Select another channel to switch streams
- Press **Back** to exit PIP and return to full screen

## Project Structure

```
app/
├── src/main/
│   ├── java/com/mattverwey/m3uplayer/
│   │   ├── data/
│   │   │   ├── cache/         # Caching layer
│   │   │   ├── local/         # Local storage
│   │   │   └── model/         # Data models
│   │   ├── network/           # API clients & parsers
│   │   │   ├── M3UParser.kt
│   │   │   └── XtreamApiService.kt
│   │   ├── repository/        # Data repositories
│   │   └── ui/
│   │       ├── browse/        # Main browsing UI
│   │       ├── details/       # Channel details
│   │       ├── login/         # Login screen
│   │       └── playback/      # Video player
│   └── res/
│       ├── layout/            # XML layouts
│       ├── values/            # Resources
│       └── drawable/          # Icons and images
```

## Configuration

### Cache Settings
Cache expiry time can be adjusted in `CacheManager.kt`:
```kotlin
private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L // 24 hours
```

### Recently Watched Limit
Maximum recently watched items in `CacheManager.kt`:
```kotlin
private const val MAX_RECENTLY_WATCHED = 50
```

## Supported Formats

### Streaming Protocols
- HLS (HTTP Live Streaming)
- RTMP
- HTTP/HTTPS direct streams

### Video Formats
- MP4, MKV, AVI, MOV, FLV
- MPEG, MPG, TS
- WebM, OGV

### Audio Formats
- MP3, AAC, OGG

## Troubleshooting

### Playback Issues
- Check internet connection
- Verify stream URL is accessible
- Try refreshing the playlist (Menu → Refresh Channels)
- Clear cache if experiencing issues (Menu → Clear Cache)

### Buffering Issues
- Ensure minimum 10 Mbps internet connection for HD
- Use Ethernet connection instead of WiFi (recommended for Fire TV Cube)
- Position Fire TV closer to router
- Close other apps consuming bandwidth
- See [BUFFERING_OPTIMIZATION.md](BUFFERING_OPTIMIZATION.md) for detailed troubleshooting

### Login Issues
- Ensure server URL includes http:// or https://
- Verify credentials are correct
- Check server is accessible from your network

### PIP Not Working
- PIP requires Android 8.0 (Oreo) or higher
- Some Fire TV devices may have PIP disabled

## Performance Optimization

### Video Playback
- **Custom buffering**: 60-second buffer for smooth playback
- **Adaptive streaming**: Starts with SD, upgrades to HD automatically
- **Quick startup**: 2.5-second pre-buffer for fast playback start
- See [BUFFERING_OPTIMIZATION.md](BUFFERING_OPTIMIZATION.md) for details

### General Performance
- Lazy loading of channel lists
- Image caching with Coil
- Background thread operations with Coroutines
- Efficient EPG data structures

## Future Enhancements

- [ ] Full EPG integration with program guide
- [ ] Series support with season/episode navigation
- [ ] Catch-up TV (timeshift)
- [ ] Favorites management
- [ ] Multiple profile support
- [ ] Parental controls
- [ ] Background audio playback
- [ ] Chromecast support

## Migration from Roku

This project was originally a Roku BrightScript application. Version 2.0+ is a complete rewrite for Android TV/Fire TV platform with enhanced features and modern architecture.

## License

Copyright 2024. Licensed under the Apache License, Version 2.0.

## Credits

- Original Roku version by repository contributors
- Android TV version redesigned for Fire TV compatibility
- Built with Android TV Leanback and ExoPlayer
