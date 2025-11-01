# Free M3U Playlists for Testing

This document provides free, legal M3U playlist URLs for testing the M3U Player app.

## 🌐 Free IPTV Test Playlists

### 1. IPTV-ORG Collection (Recommended)
A curated collection of free, legal IPTV channels organized by country.

**Global Channels:**
```
https://iptv-org.github.io/iptv/index.m3u
```

**By Category:**
- News: `https://iptv-org.github.io/iptv/index.category.m3u?category=news`
- Sports: `https://iptv-org.github.io/iptv/index.category.m3u?category=sports`
- Entertainment: `https://iptv-org.github.io/iptv/index.category.m3u?category=entertainment`
- Movies: `https://iptv-org.github.io/iptv/index.category.m3u?category=movies`
- Music: `https://iptv-org.github.io/iptv/index.category.m3u?category=music`

**By Country:**
- USA: `https://iptv-org.github.io/iptv/countries/us.m3u`
- UK: `https://iptv-org.github.io/iptv/countries/uk.m3u`
- Canada: `https://iptv-org.github.io/iptv/countries/ca.m3u`
- Australia: `https://iptv-org.github.io/iptv/countries/au.m3u`

### 2. FreeTuxTV Playlists
Community-maintained free IPTV playlists.

```
https://raw.githubusercontent.com/Free-TV/IPTV/master/playlists/playlist_world.m3u8
```

### 3. Legal Streaming Services

**Pluto TV (Free with Ads)**
```
https://i.mjh.nz/PlutoTV/all.m3u8
```

**Plex Live TV (Free)**
```
https://i.mjh.nz/Plex/all.m3u8
```

**Samsung TV Plus**
```
https://i.mjh.nz/SamsungTVPlus/all.m3u8
```

### 4. News Channels

**Reuters**
```
https://reuters-republicworld.samsung.wurl.tv/playlist.m3u8
```

**Bloomberg TV**
```
https://bloomberg-bloomberg-1-nl.samsung.wurl.tv/playlist.m3u8
```

**NBC News Now**
```
https://nbcnews-nbcnewsnow-firetv.amagi.tv/playlist.m3u8
```

## 🧪 Testing Guide

### Step 1: Install the App
```bash
# Via ADB
adb install app-debug.apk
```

### Step 2: Launch and Login

1. Open **M3U Player** from Fire TV Apps
2. Choose **"M3U URL"** login method
3. Enter one of the playlist URLs above
4. Wait for channels to load

### Step 3: Test Navigation

**Main Menu:**
- Use D-Pad UP/DOWN to navigate sidebar
- Select "Live TV", "Movies", or "Series"

**Channel Browsing:**
- Use D-Pad LEFT/RIGHT to browse channels
- Use D-Pad UP/DOWN to switch between rows
- Press SELECT to play a channel

**Playback:**
- Press MENU/CENTER to show controls
- Press BACK to exit playback
- Press HOME for Picture-in-Picture mode

### Step 4: Test Playback

**What to Test:**
- ✅ Video starts playing within 3-5 seconds
- ✅ No buffering during playback
- ✅ Audio is in sync with video
- ✅ Controls respond to remote
- ✅ Can pause/resume playback
- ✅ Can skip forward/backward 10 seconds
- ✅ Picture-in-Picture works

### Step 5: Test UI Performance

**Performance Checks:**
- ✅ Smooth scrolling through channel list
- ✅ Quick focus transitions (< 100ms)
- ✅ Images load within 1-2 seconds
- ✅ No lag when navigating
- ✅ App responds to all remote buttons

## 📊 Expected Performance

### Playback Metrics:
- **Startup Time**: < 3 seconds
- **Buffering**: Minimal (60-second buffer)
- **Quality**: Adaptive (SD → HD)
- **Pre-buffer**: 2.5 seconds

### UI Metrics:
- **List Scrolling**: 60 FPS
- **Focus Change**: < 100ms
- **Image Loading**: < 2 seconds
- **Category Switch**: < 500ms

## 🐛 Common Issues

### No Channels Loading
**Cause:** Invalid M3U URL or no internet connection  
**Fix:** Verify URL is accessible and internet is connected

### Playback Fails
**Cause:** Stream URL is invalid or geo-blocked  
**Fix:** Try a different channel or playlist

### Buffering Issues
**Cause:** Slow internet connection  
**Fix:** Ensure minimum 10 Mbps connection, use Ethernet

### Remote Not Working
**Cause:** Focus issues in Leanback UI  
**Fix:** Press BACK and try again, or restart app

## 📝 Notes

- All playlists listed are **free and legal** to use
- Content availability may vary by region
- Some streams may have ads
- Playlists are community-maintained and may change
- Always test with multiple playlists for best coverage

## 🔗 Additional Resources

- [IPTV-ORG GitHub](https://github.com/iptv-org/iptv)
- [Free-TV GitHub](https://github.com/Free-TV/IPTV)
- [EPG Sources](https://github.com/iptv-org/epg)

## ⚠️ Disclaimer

This app and these playlists are for testing purposes only. Users are responsible for ensuring they have the right to access and view any content. The playlists listed here contain only free, publicly available streams. Always respect copyright and licensing terms.
