# Amazon Fire TV Cube Testing Guide

Complete guide for testing the M3U Player on Amazon Fire TV Cube and other Fire TV devices.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Building the APK](#building-the-apk)
- [Installation Methods](#installation-methods)
- [Testing Checklist](#testing-checklist)
- [UI Testing](#ui-testing)
- [Performance Testing](#performance-testing)
- [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Hardware
- Amazon Fire TV Cube (any generation) or Fire TV Stick
- Fire TV Remote (with voice/menu button)
- Computer with ADB installed
- Same network connection for both devices

### Required Software
- Android Studio (latest version) OR
- Android SDK Platform Tools (for ADB only)
- JDK 17 or higher (for building APK)
- M3U Player APK file

### Network Requirements
- WiFi network (2.4GHz or 5GHz)
- Stable internet connection for streaming
- Router with sufficient bandwidth (minimum 10 Mbps recommended)

## Building the APK

This section explains how to build the M3U Player APK file from source code using the Android SDK command-line tools.

### Prerequisites for Building

Before building, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   ```bash
   # Check Java version
   java -version
   
   # Should show version 17 or higher
   # Example output: openjdk version "17.0.9" 2023-10-17
   ```

   If you don't have JDK 17, download it from:
   - [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
   - [OpenJDK](https://adoptium.net/)

2. **Android SDK Command Line Tools**
   
   Download from: https://developer.android.com/studio#command-line-tools-only
   
   Extract and set up the SDK:
   ```bash
   # Extract command line tools
   mkdir -p ~/android-sdk/cmdline-tools
   unzip commandlinetools-*.zip -d ~/android-sdk/cmdline-tools
   mv ~/android-sdk/cmdline-tools/cmdline-tools ~/android-sdk/cmdline-tools/latest
   
   # Set environment variables (add to ~/.bashrc or ~/.zshrc)
   export ANDROID_HOME=~/android-sdk
   export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
   export PATH=$PATH:$ANDROID_HOME/platform-tools
   ```

3. **Install Required Android SDK Packages**
   ```bash
   # Accept licenses
   sdkmanager --licenses
   
   # Install required SDK components
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   ```

### Setting Up the Project

1. **Clone the repository** (if not already done):
   ```bash
   git clone https://github.com/MattVerwey/Roku-M3u-Player.git
   cd Roku-M3u-Player
   ```

2. **Generate Gradle Wrapper** (if not present):
   
   The project uses Gradle Wrapper, which may need to be generated:
   
   **Option 1: Using system Gradle (Recommended)**
   ```bash
   # Install Gradle if not already installed
   # On macOS with Homebrew:
   brew install gradle
   
   # On Linux (Debian/Ubuntu):
   sudo apt-get install gradle
   
   # On Windows with Chocolatey:
   choco install gradle
   
   # Generate wrapper using system Gradle (version 8.2 or compatible)
   gradle wrapper --gradle-version 8.2
   
   # This creates:
   # - gradlew (Linux/Mac script)
   # - gradlew.bat (Windows script)
   # - gradle/wrapper/gradle-wrapper.jar
   ```
   
   **Option 2: Using Gradle without installing**
   
   If you cannot install Gradle system-wide, download and use it temporarily:
   ```bash
   # Download Gradle distribution
   curl -L https://services.gradle.org/distributions/gradle-8.2-bin.zip -o gradle.zip
   unzip gradle.zip
   
   # Use downloaded Gradle to generate wrapper
   ./gradle-8.2/bin/gradle wrapper --gradle-version 8.2
   
   # Clean up temporary Gradle
   rm -rf gradle-8.2 gradle.zip
   ```

### Building the APK

Once prerequisites are set up, build the APK:

#### Build Debug APK (for testing)

```bash
# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# On Windows:
# gradlew.bat assembleDebug
```

**Output location:**
```
app/build/outputs/apk/debug/app-debug.apk
```

**Build time:** First build may take 2-5 minutes (downloads dependencies). Subsequent builds are faster (30-60 seconds).

#### Build Release APK (for distribution)

For a release build with optimizations:

```bash
# Build release APK (unsigned)
./gradlew assembleRelease

# On Windows:
# gradlew.bat assembleRelease
```

**Output location:**
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

**Note:** Release APKs need to be signed before installation. For testing purposes, debug APKs are recommended.

#### Signing a Release APK (Optional)

If you need a signed release APK:

1. **Create a keystore** (first time only):
   ```bash
   keytool -genkey -v -keystore my-release-key.keystore \
     -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Sign the APK**:
   ```bash
   # Build the release APK first
   ./gradlew assembleRelease
   
   # Sign using jarsigner
   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
     -keystore my-release-key.keystore \
     app/build/outputs/apk/release/app-release-unsigned.apk \
     my-key-alias
   
   # Verify signature
   jarsigner -verify -verbose -certs \
     app/build/outputs/apk/release/app-release-unsigned.apk
   ```

   Or use `apksigner` (recommended):
   ```bash
   # Sign the APK
   apksigner sign --ks my-release-key.keystore \
     --out app/build/outputs/apk/release/app-release-signed.apk \
     app/build/outputs/apk/release/app-release-unsigned.apk
   
   # Verify signature
   apksigner verify app/build/outputs/apk/release/app-release-signed.apk
   ```

### Cleaning the Build

To clean build artifacts and start fresh:

```bash
# Clean build
./gradlew clean

# Clean and rebuild
./gradlew clean assembleDebug
```

### Troubleshooting Build Issues

#### "SDK location not found"
```bash
# Solution 1: Create local.properties file
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Solution 2: Set ANDROID_HOME/ANDROID_SDK_ROOT environment variable
export ANDROID_HOME=~/android-sdk
export ANDROID_SDK_ROOT=~/android-sdk
```

#### "Could not find or load main class org.gradle.wrapper.GradleWrapperMain"
```bash
# Solution: Regenerate the Gradle wrapper
# Install Gradle if needed, then run:
gradle wrapper --gradle-version 8.2

# Or download Gradle wrapper JAR from official distribution
# Note: This uses Gradle 8.2 distribution; adjust version if needed
rm -f gradle/wrapper/gradle-wrapper.jar
curl -L https://services.gradle.org/distributions/gradle-8.2-bin.zip -o temp-gradle.zip
unzip -j temp-gradle.zip "gradle-8.2/lib/gradle-wrapper.jar" -d gradle/wrapper/
rm temp-gradle.zip
```

#### "Unsupported Java version"
```bash
# Solution: Check Java version (needs 17+)
java -version

# If version is wrong, set JAVA_HOME
export JAVA_HOME=/path/to/jdk-17
export PATH=$JAVA_HOME/bin:$PATH
```

#### Build fails with "Could not resolve dependencies"
```bash
# Solution: Clear Gradle cache and retry
rm -rf ~/.gradle/caches
./gradlew assembleDebug --refresh-dependencies
```

### Quick Build Reference

For quick reference, here's the typical build workflow:

```bash
# 1. Clone repo
git clone https://github.com/MattVerwey/Roku-M3u-Player.git
cd Roku-M3u-Player

# 2. Ensure gradlew exists and is executable
chmod +x gradlew

# 3. Build APK
./gradlew assembleDebug

# 4. Output APK location
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

## Installation Methods

### Method 1: ADB Sideloading (Recommended)

#### Step 1: Enable ADB on Fire TV Cube

1. Go to **Settings** on your Fire TV
2. Select **My Fire TV** (or **Device** on older versions)
3. Select **About**
4. Click on the device name 7 times to enable Developer Options
5. Go back and select **Developer Options**
6. Turn on **ADB Debugging**
7. Turn on **Apps from Unknown Sources**
8. Note your Fire TV's IP address (Settings → My Fire TV → About → Network)

#### Step 2: Connect via ADB

On your computer, open a terminal/command prompt:

```bash
# Connect to Fire TV using IP address
adb connect <FIRE_TV_IP>:5555

# Example:
adb connect 192.168.1.100:5555

# Verify connection
adb devices
```

You should see output like:
```
List of devices attached
192.168.1.100:5555    device
```

#### Step 3: Install the APK

**Note:** If you don't have the APK file yet, see the [Building the APK](#building-the-apk) section above to build it from source.

```bash
# Navigate to the repository directory
cd /path/to/Roku-M3u-Player

# Install the APK (assumes you've already built it)
adb install app/build/outputs/apk/debug/app-debug.apk

# Or to reinstall (if already installed)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### Step 4: Launch the App

The app should now appear in your Fire TV's apps list. You can also launch it via ADB:

```bash
adb shell am start -n com.mattverwey.m3uplayer/.ui.MainActivity
```

### Method 2: Using Downloader App

1. On Fire TV, go to **Find** → **Search**
2. Search for "**Downloader**" and install it
3. Open Downloader app
4. Enter the URL where your APK is hosted
5. Download and install the APK
6. Grant permissions when prompted

### Method 3: Using Apps2Fire (Desktop App)

1. Download and install **Apps2Fire** on your computer
2. Connect to your Fire TV via IP address
3. Select the APK file
4. Click "Upload" to install

## Testing Checklist

### Initial Setup Testing

- [ ] App launches successfully
- [ ] Login screen appears on first launch
- [ ] Both login methods are available (M3U URL and Xtream Codes)
- [ ] Keyboard input works correctly
- [ ] Login button responds to remote
- [ ] Error messages display for invalid credentials

### UI Navigation Testing

#### Main Browse Screen
- [ ] App displays with modern card-based interface
- [ ] Channel logos load correctly
- [ ] Categories are organized clearly:
  - [ ] Recently Watched (if available)
  - [ ] Live TV
  - [ ] Movies
  - [ ] Series
  - [ ] Custom categories from M3U
- [ ] D-pad navigation is smooth and responsive
- [ ] Focus highlight is clearly visible
- [ ] Scrolling through channels is fluid

#### Fire TV Remote Controls
- [ ] **D-pad Up/Down** - Navigate between rows
- [ ] **D-pad Left/Right** - Navigate within rows
- [ ] **Select/OK** - Play channel or show details
- [ ] **Back button** - Return to previous screen
- [ ] **Home button** - Enter PIP mode (during playback)
- [ ] **Menu button (3 lines)** - Show options menu with:
  - [ ] Refresh Channels
  - [ ] Clear Cache
  - [ ] Logout

### Playback Testing

#### Video Quality
- [ ] Video starts playing within 2-5 seconds
- [ ] Initial buffering is minimal
- [ ] Video quality is clear (HD when available)
- [ ] Adaptive streaming adjusts to network conditions
- [ ] No pixelation or artifacts during normal playback
- [ ] Audio is synchronized with video
- [ ] No audio dropouts or distortion

#### Buffering Performance
- [ ] Initial buffer completes quickly (2-5 seconds)
- [ ] Minimal re-buffering during playback
- [ ] Buffer indicator appears when buffering
- [ ] Playback resumes smoothly after buffering
- [ ] Fast forward/rewind work without excessive buffering

#### Playback Controls
- [ ] Play/Pause button works
- [ ] Playback progress shows correctly
- [ ] Channel name displays for 3 seconds, then hides
- [ ] On-screen controls appear when needed
- [ ] Controls hide automatically during playback

#### Picture-in-Picture (PIP)
- [ ] Press **Home** button during playback
- [ ] Video continues in small window (corner)
- [ ] Can browse channels while PIP is active
- [ ] Selecting new channel switches PIP stream
- [ ] Press **Back** to exit PIP and return to fullscreen
- [ ] PIP window size is appropriate (not too small)

### Details Screen Testing

- [ ] Details screen appears for VOD content
- [ ] Channel information displays correctly
- [ ] Poster/logo loads properly
- [ ] "Watch Now" button works
- [ ] Back button returns to browse screen

### Performance Testing

#### App Responsiveness
- [ ] App launches in under 2 seconds
- [ ] Channel list loads in under 3 seconds (cached)
- [ ] Navigation has no lag or stutter
- [ ] UI animations are smooth
- [ ] No freezing or hanging

#### Memory Usage
- [ ] App remains responsive during extended use
- [ ] No memory leaks during playback
- [ ] Can switch channels without crashes
- [ ] App handles low memory gracefully

#### Network Handling
- [ ] App handles poor network conditions
- [ ] Appropriate error messages for network issues
- [ ] Retry mechanism works
- [ ] Cached data loads when offline (initially)

## UI Design Assessment

### Modern and Sleek Design Checklist

#### Visual Design
- [x] **Dark theme** - Optimized for TV viewing, reduces eye strain
- [x] **Card-based layout** - Modern grid view with poster images
- [x] **High-quality graphics** - Channel logos and posters load clearly
- [x] **Clean typography** - Readable text at TV distances
- [x] **Consistent spacing** - Professional layout with proper margins
- [x] **Smooth animations** - Transitions between screens are fluid

#### TV Guide (Category Organization)
- [x] **Intuitive categories** - Clear separation of Live TV, Movies, Series
- [x] **Recently Watched** - Quick access to favorite channels
- [x] **Custom categories** - M3U group-title support
- [x] **Easy scrolling** - Horizontal scrolling within categories
- [x] **Vertical navigation** - Easy category switching

#### Remote Control Optimization
- [x] **D-pad navigation** - All features accessible via D-pad
- [x] **Focus indicators** - Clear visual feedback
- [x] **Quick access** - Menu button for settings (3 lines button)
- [x] **One-handed operation** - All common tasks doable with remote

### User Experience Features

#### Convenience
- [x] **Recently Watched** - Automatic tracking of viewed channels
- [x] **Quick playback** - One click to play live TV
- [x] **Details view** - Two-step for VOD (details → play)
- [x] **PIP mode** - Browse while watching
- [x] **Fast channel switching** - Minimal buffering between channels

#### Feedback and Information
- [x] **Loading indicators** - Progress bars during loading
- [x] **Channel names** - Display on playback start
- [x] **Error messages** - Clear error descriptions
- [x] **Buffering indicators** - Visual feedback during buffering

## Performance Optimization Features

### Video Buffering Improvements

The app includes several optimizations to minimize buffering:

1. **Custom Load Control**
   - Increased buffer size to 60 seconds (from 50s default)
   - Pre-buffering of 2.5 seconds before playback starts
   - 5-second buffer after rebuffering for smoother experience

2. **Adaptive Streaming**
   - Starts with SD quality for faster startup
   - Automatically upgrades to HD when bandwidth allows
   - Dynamically adjusts based on network conditions

3. **Track Selection**
   - Intelligent bitrate selection
   - Prioritizes playback stability over maximum quality
   - Reduces re-buffering events

### Network Optimization
- HTTP/HTTPS support with connection pooling
- Automatic retry on network errors
- Efficient stream format detection (HLS, RTMP, etc.)

## Troubleshooting

### ADB Connection Issues

**Problem:** Cannot connect via ADB
```bash
# Solution 1: Check Fire TV IP address
# Go to Settings → My Fire TV → About → Network

# Solution 2: Restart ADB server
adb kill-server
adb start-server
adb connect <FIRE_TV_IP>:5555

# Solution 3: Check firewall
# Ensure port 5555 is not blocked
```

**Problem:** "Device unauthorized"
```bash
# Solution: Accept authorization prompt on Fire TV
# A dialog will appear on Fire TV asking to authorize the computer
# Check "Always allow" and tap OK
```

### Installation Issues

**Problem:** "Installation failed"
```bash
# Solution 1: Uninstall existing version
adb uninstall com.mattverwey.m3uplayer

# Solution 2: Clear package data
adb shell pm clear com.mattverwey.m3uplayer

# Solution 3: Reinstall with -r flag
adb install -r app-debug.apk
```

**Problem:** "Apps from Unknown Sources" disabled
```
Solution:
1. Go to Settings → My Fire TV → Developer Options
2. Enable "Apps from Unknown Sources"
3. Retry installation
```

### Playback Issues

**Problem:** Video won't play
```
Checks:
1. Verify internet connection
2. Test stream URL in browser or VLC
3. Check server is accessible
4. Verify M3U/Xtream credentials
5. Try different channel
```

**Problem:** Excessive buffering
```
Solutions:
1. Check network speed (minimum 10 Mbps)
2. Close other streaming apps
3. Move Fire TV closer to router
4. Use wired Ethernet adapter (Fire TV Cube)
5. Restart Fire TV device
6. Clear app cache (Menu → Clear Cache)
```

**Problem:** Poor video quality
```
Solutions:
1. Wait 10-20 seconds for adaptive streaming to upgrade quality
2. Check available bandwidth
3. Verify stream source provides HD
4. Restart playback to re-negotiate quality
```

### PIP Issues

**Problem:** PIP not working
```
Checks:
1. Verify Fire TV OS version (Android 8.0+ required)
2. Some Fire TV devices may not support PIP
3. Check if PIP is enabled in system settings
```

### App Crashes

**Problem:** App crashes on launch
```bash
# Check logs
adb logcat | grep m3uplayer

# Clear app data
adb shell pm clear com.mattverwey.m3uplayer

# Reinstall
adb install -r app-debug.apk
```

**Problem:** App crashes during playback
```
Solutions:
1. Check available memory (close other apps)
2. Restart Fire TV device
3. Update to latest app version
4. Check crash logs for specific error
```

## Advanced Testing

### Performance Monitoring

```bash
# Monitor memory usage
adb shell dumpsys meminfo com.mattverwey.m3uplayer

# Monitor CPU usage
adb shell top | grep m3uplayer

# Check battery impact (for Fire TV Stick)
adb shell dumpsys batterystats com.mattverwey.m3uplayer

# View app logs in real-time
adb logcat | grep m3uplayer
```

### Network Monitoring

```bash
# Monitor network traffic
adb shell dumpsys netstats | grep m3uplayer

# Test specific stream
adb shell am start -a android.intent.action.VIEW -d "<STREAM_URL>"
```

## Test Results Template

Use this template to document test results:

```
## Test Date: [DATE]
## Device: [Fire TV Cube Gen X / Fire TV Stick 4K, etc.]
## App Version: [Version Number]

### Installation
- Installation method: [ADB / Downloader / Other]
- Installation time: [X seconds]
- Issues encountered: [None / Description]

### UI Testing
- Browse screen loads: [✓/✗]
- Navigation responsiveness: [Excellent/Good/Fair/Poor]
- Visual quality: [Excellent/Good/Fair/Poor]
- Menu button works: [✓/✗]

### Playback Testing
- Channel 1 URL: [URL]
  - Start time: [X seconds]
  - Quality: [HD/SD/Poor]
  - Buffering: [None/Occasional/Frequent]
  
- Channel 2 URL: [URL]
  - Start time: [X seconds]
  - Quality: [HD/SD/Poor]
  - Buffering: [None/Occasional/Frequent]

### PIP Testing
- PIP mode works: [✓/✗]
- Browse during PIP: [✓/✗]
- PIP quality: [Good/Acceptable/Poor]

### Overall Assessment
- UI Design: [Score 1-10]
- Performance: [Score 1-10]
- Usability: [Score 1-10]
- Buffering: [Score 1-10]

### Issues Found
1. [Description of issue 1]
2. [Description of issue 2]

### Recommendations
1. [Recommendation 1]
2. [Recommendation 2]
```

## Best Practices

### For Optimal Performance

1. **Network Setup**
   - Use 5GHz WiFi when possible
   - Position Fire TV close to router
   - Use Ethernet adapter for Fire TV Cube (recommended)
   - Ensure adequate bandwidth (25+ Mbps for HD)

2. **Device Maintenance**
   - Restart Fire TV regularly
   - Clear app cache periodically (Menu → Clear Cache)
   - Keep Fire TV OS updated
   - Close unused apps

3. **Content Sources**
   - Use reliable IPTV providers
   - Prefer HLS streams over RTMP
   - Verify stream URLs before testing
   - Test with multiple sources

## Support and Feedback

### Reporting Issues

When reporting issues, include:
- Fire TV device model and OS version
- App version
- Steps to reproduce
- Screenshots or video (if possible)
- ADB logs (if available)

### Logs Collection

```bash
# Collect full logs
adb logcat > firetv-logs.txt

# Or filter for app logs only
adb logcat | grep m3uplayer > app-logs.txt
```

## Summary

This guide covers all aspects of testing the M3U Player on Amazon Fire TV devices. The app features:

- ✅ Modern, sleek TV-optimized UI
- ✅ Well-organized TV guide with categories
- ✅ Quick access menu (3-lines button on remote)
- ✅ High-quality video playback
- ✅ Optimized buffering with 60-second buffer
- ✅ Picture-in-Picture mode
- ✅ Fire TV remote optimized controls

For the best experience, use a Fire TV Cube with Ethernet connection and ensure adequate network bandwidth for HD streaming.
