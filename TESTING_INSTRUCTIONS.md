# M3U Player - Complete Testing Instructions

## 🚀 Quick Start

Since the build environment has network restrictions (Google Maven repository blocked), we've set up **GitHub Actions** to automatically build the APK for you.

### Option 1: Download Pre-built APK from GitHub Actions (Recommended)

1. **Go to GitHub Actions**: Visit [Actions Tab](https://github.com/MattVerwey/Roku-M3u-Player/actions/workflows/build-apk.yml)

2. **Find Latest Successful Build**: Look for a green checkmark ✅ on the latest workflow run

3. **Download APK Artifact**:
   - Click on the successful workflow run
   - Scroll to the "Artifacts" section at the bottom
   - Download `app-debug-apk.zip`
   - Extract `app-debug.apk` from the ZIP file

### Option 2: Build Locally (If You Have Unrestricted Internet)

```bash
# Clone repository
git clone https://github.com/MattVerwey/Roku-M3u-Player.git
cd Roku-M3u-Player

# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build Debug APK
./gradlew clean assembleDebug

# APK Location
# app/build/outputs/apk/debug/app-debug.apk
```

### Option 3: Use Android Studio

1. Open Android Studio
2. File → Open → Select `Roku-M3u-Player` folder
3. Wait for Gradle sync to complete
4. Build → Build Bundle(s) / APK(s) → Build APK(s)
5. Find APK at `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 Installation on Fire TV

### Method 1: ADB (Recommended)

**Prerequisites:**
- ADB installed on your computer
- Fire TV connected to same network

**Steps:**

1. **Enable ADB on Fire TV**:
   - Settings → My Fire TV → Developer Options
   - Turn ON "ADB Debugging"
   - Note your Fire TV IP address (Settings → My Fire TV → About → Network)

2. **Connect via ADB**:
   ```bash
   # Replace with your Fire TV IP
   adb connect 192.168.1.XXX:5555
   
   # Verify connection
   adb devices
   ```

3. **Install APK**:
   ```bash
   adb install -r app-debug.apk
   ```

4. **Launch App**:
   - Navigate to Fire TV home screen
   - Go to Apps → Your Apps & Channels
   - Find "M3U Player" and launch it

### Method 2: Apps2Fire (Easier, No Terminal)

1. Install "Apps2Fire" on your Android phone or computer
2. Connect Apps2Fire to your Fire TV
3. Select the APK file
4. Tap "Install"

### Method 3: Downloader App

1. Upload APK to a web server or Dropbox
2. Open "Downloader" app on Fire TV
3. Enter the URL to your APK
4. Install when prompted

---

## 🧪 Testing Checklist

### Initial Setup

- [ ] **App Launches Successfully**
  - No crashes on startup
  - Splash screen appears (if any)
  - Login screen displays correctly

- [ ] **Login with M3U URL**
  - Select "M3U URL" option
  - Enter test playlist URL (see TEST_PLAYLISTS.md)
  - Channels load within 5-10 seconds
  - No error messages

### Free M3U Test URLs

Use these free, legal playlists for testing:

**Quick Test (Recommended):**
```
https://iptv-org.github.io/iptv/countries/us.m3u
```

**More Options:**
```
https://iptv-org.github.io/iptv/index.category.m3u?category=news
https://iptv-org.github.io/iptv/index.category.m3u?category=sports
https://i.mjh.nz/PlutoTV/all.m3u8
```

See `TEST_PLAYLISTS.md` for complete list.

### Navigation Testing

- [ ] **Remote Control Responsiveness**
  - D-Pad UP/DOWN navigates sidebar menu
  - D-Pad LEFT/RIGHT browses content
  - SELECT button opens/plays content
  - BACK button returns to previous screen
  - MENU button shows options (if applicable)

- [ ] **Main Menu Structure**
  - Check if sidebar shows: Live TV, Movies, Series, Settings
  - Verify menu items are selectable
  - Confirm menu text is readable (white, not purple)

- [ ] **Channel/Content Browsing**
  - Browse through different categories
  - Scroll smoothly without lag
  - Channel cards display correctly
  - Channel logos load (if available)

### Playback Testing

- [ ] **Video Playback**
  - Select a channel to play
  - Video starts within 3-5 seconds
  - No initial buffering issues
  - Audio is in sync with video
  - Stream quality is acceptable (SD/HD)

- [ ] **Playback Controls**
  - Press MENU/CENTER to show controls
  - Play/Pause button works
  - Rewind 10 seconds works
  - Fast Forward 10 seconds works
  - Controls auto-hide after 5 seconds

- [ ] **Picture-in-Picture (PIP)**
  - During playback, press HOME button
  - Video continues in corner of screen
  - Can navigate to other content while PIP active
  - Select another channel switches stream
  - Press BACK to return to full screen

### Performance Testing

- [ ] **UI Responsiveness**
  - No lag when scrolling
  - Focus transitions are smooth (< 100ms)
  - Images load quickly (< 2 seconds)
  - App doesn't freeze or stutter

- [ ] **Memory/Stability**
  - Play multiple channels consecutively
  - Switch between Live TV, Movies, Series
  - App doesn't crash
  - No memory warnings or slowdowns

### Feature Testing

- [ ] **Recently Watched**
  - Check if recently watched section appears
  - Verify items are tracked correctly
  - Confirm most recent items show first

- [ ] **Settings Menu**
  - Access Settings from sidebar
  - Try "Refresh Channels" option
  - Test "Clear Cache" option
  - Verify "Logout" works

- [ ] **Details Screen**
  - Select a movie or series
  - Details screen displays information
  - Play button works
  - BACK button returns to browse

---

## 📊 Expected Performance Benchmarks

### Startup
- **App Launch**: < 2 seconds to login screen
- **Channel Load**: < 10 seconds after entering M3U URL
- **Playback Start**: < 3 seconds after selecting channel

### Playback
- **Initial Buffer**: 2.5 seconds
- **Total Buffer**: 60 seconds (for smooth playback)
- **Quality**: Adaptive streaming (starts SD, upgrades to HD)
- **Latency**: Minimal, suitable for live TV

### UI Performance
- **Scrolling**: 60 FPS (smooth)
- **Focus Change**: < 100ms
- **Image Load**: < 2 seconds
- **Category Switch**: < 500ms

---

## 🐛 Troubleshooting

### App Won't Install
**Error: "App not installed"**
- Solution: Uninstall any previous version first
- Solution: Check if "Unknown Sources" is enabled

### No Channels Loading
**Channels list is empty**
- Check internet connection on Fire TV
- Verify M3U URL is accessible
- Try a different playlist from TEST_PLAYLISTS.md
- Check for error messages

### Playback Fails or Buffers
**Video won't start or keeps buffering**
- Ensure minimum 10 Mbps internet connection
- Try using Ethernet instead of WiFi
- Position Fire TV closer to router
- Try a different channel (some streams may be offline)
- Clear cache in Settings

### Remote Control Not Working
**D-Pad navigation is unresponsive**
- Restart the app
- Check Fire TV remote batteries
- Try pressing BACK and navigating again
- Report as bug if persistent

### App Crashes
**App closes unexpectedly**
- Check logs: `adb logcat -v time | grep -i "mattverwey\|androidruntime"`
- Note what action caused the crash
- Report with steps to reproduce

### Poor Video Quality
**Stream is low quality or pixelated**
- Adaptive streaming may take 10-30 seconds to upgrade to HD
- Check your internet speed (need 10+ Mbps for HD)
- Some free IPTV streams may only offer SD quality

---

## 📝 Testing Report Template

After testing, create a report with this format:

```markdown
## Test Environment
- Device: Fire TV Cube / Stick (Model: ____)
- Android Version: ____
- App Version: 2.0.0 (debug/release)
- Test Date: ____

## Test Results

### ✅ Working Features
- Feature 1: Description
- Feature 2: Description

### ❌ Issues Found
1. **Issue Title**
   - Steps to reproduce:
   - Expected behavior:
   - Actual behavior:
   - Severity: Critical / Major / Minor

### 📊 Performance
- Startup time: ____ seconds
- Channel load time: ____ seconds
- Playback start time: ____ seconds
- UI smoothness: Excellent / Good / Poor

### 💡 Suggestions
- Suggestion 1
- Suggestion 2

### 📸 Screenshots
- Attach screenshots of key screens
- Include any error messages
```

---

## 🎯 Focus Areas for Testing

Based on previous feedback (see AGENT_TODO.md), pay special attention to:

1. **Main Menu Navigation** - Should be clean and hierarchical
2. **Remote Control Handling** - Must be responsive for TV use
3. **Sidebar Text Visibility** - Headers should be white, not purple
4. **Category Organization** - Content should be well-organized
5. **Playback Stability** - No crashes during video playback

---

## 📞 Support

If you encounter issues:

1. Check logs: `adb logcat -v time | grep -E "mattverwey|AndroidRuntime"`
2. Check BUILD_NETWORK_ISSUE.md for known build problems
3. Check TEST_PLAYLISTS.md for working M3U URLs
4. Report issues with detailed steps to reproduce

---

## 🔗 Additional Resources

- [Fire TV Testing Guide](FIRE_TV_TESTING_GUIDE.md) - Detailed Fire TV setup
- [UI Design Documentation](UI_DESIGN_DOCUMENTATION.md) - UI specifications
- [Buffering Optimization](BUFFERING_OPTIMIZATION.md) - Performance tuning
- [Test Playlists](TEST_PLAYLISTS.md) - Free M3U URLs

---

**Happy Testing! 🎉**

For best results, test on actual Fire TV hardware (not just emulator) with a good internet connection and free IPTV playlists.
