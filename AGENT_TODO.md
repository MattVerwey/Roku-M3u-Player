# M3U Player - Agent TODO List

## 🚀 Project Status
- **Platform**: Amazon Fire TV Cube
- **Current State**: Basic functionality working, needs major UI/UX improvements
- **Build System**: ✅ Working (Kotlin 1.9.23, Gradle 8.2, JDK 17)
- **Installation**: ✅ Working (APK builds and installs via ADB)

---

## 🔥 CRITICAL ISSUES (Priority 1)

### 1. Main Menu Navigation Structure
**Problem**: Currently shows all Live TV channels directly after login. Need a clean main menu.

**Required Fix**:
- Create a **4-option main menu** on sidebar:
  1. 📺 **Live TV** (when selected, show categories)
  2. 🎬 **Movies** (when selected, show categories)
  3. 📺 **Series** (when selected, show categories)
  4. ⚙️ **Settings**

**Technical Details**:
- File: `app/src/main/java/com/mattverwey/m3uplayer/ui/browse/BrowseFragment.kt`
- Current implementation: Shows nested rows immediately
- Needed: Main menu items that open sub-menus with categories
- Consider creating a custom PageRow or using a different approach than BrowseSupportFragment

### 2. Remote Control Not Working
**Problem**: Fire TV remote D-pad navigation is unresponsive or inconsistent.

**Required Fix**:
- Implement proper key event handling for Fire TV remote
- Ensure LEFT/RIGHT/UP/DOWN arrows work correctly
- Test SELECT button for item selection
- Test BACK button for navigation
- Test MENU button for options

**Technical Details**:
- File: `app/src/main/java/com/mattverwey/m3uplayer/ui/browse/BrowseFragment.kt`
- Current: Basic onResume() with minimal key handling
- Issue: Leanback focus system may be conflicting
- Consider: Override onKeyDown/onKeyUp or use proper Leanback listeners

### 3. Sidebar Text Visibility
**Problem**: Sidebar text (headers) is hard to read - purple/pink on dark background.

**Required Fix**:
- Ensure headers are **white text (#FFFFFF)** with strong contrast
- Add text shadows for readability
- Test on actual TV screen (not just emulator)

**Technical Details**:
- Files: 
  - `app/src/main/res/values/styles.xml` (HeaderStyle)
  - `app/src/main/res/values/colors.xml`
- Current fix attempted but may need adjustment
- Test with actual Fire TV display

---

## 🐛 KNOWN BUGS (Priority 2)

### 4. PlaybackActivity Theme Crash
**Status**: ✅ FIXED (added AppCompat theme to manifest)
**File**: `app/src/main/AndroidManifest.xml`
**Note**: Verify playback works correctly

### 5. Leanback ImageCardView Inflation Error
**Status**: ✅ FIXED (custom card presenter without ImageCardView)
**File**: `app/src/main/java/com/mattverwey/m3uplayer/ui/browse/ChannelCardPresenter.kt`
**Note**: Verify cards display correctly

### 6. Category Organization
**Problem**: Content is not well-organized by categories.

**Required Fix**:
- Group Live TV channels by their groupTitle (Sports, News, Entertainment, etc.)
- Group Movies by genre/category
- Group Series by genre/category
- Show category counts: "Sports (45 channels)"

**Technical Details**:
- File: `app/src/main/java/com/mattverwey/m3uplayer/ui/browse/BrowseFragment.kt`
- Method: `setupRows()`
- Consider: Better groupBy logic, filtering empty categories

---

## ✨ FEATURE ENHANCEMENTS (Priority 3)

### 7. Settings Menu
**Problem**: Settings option exists but does nothing.

**Required Implementation**:
- Create proper settings screen
- Options needed:
  - Refresh channel list
  - Clear cache
  - Change M3U URL
  - Logout
  - About app

**Technical Details**:
- File: `app/src/main/java/com/mattverwey/m3uplayer/ui/settings/SettingsActivity.kt`
- Already exists but needs proper UI

### 8. Search Functionality
**Problem**: Search is disabled.

**Required Implementation**:
- Enable search in BrowseFragment
- Search across Live TV, Movies, and Series
- Show search results in grid

### 9. Recently Watched
**Problem**: Not prominently displayed.

**Suggested Implementation**:
- Add "Recently Watched" as first item under each main category
- Show last 10-20 items watched
- Add ability to clear history

### 10. Channel/Movie Details
**Problem**: Details screen exists but may need improvements.

**Review Needed**:
- File: `app/src/main/java/com/mattverwey/m3uplayer/ui/details/DetailsActivity.kt`
- Test if details show correctly
- Ensure Play button works
- Show description, genre, ratings if available

---

## 🎨 UI/UX IMPROVEMENTS (Priority 4)

### 11. Card Design
**Current**: Basic cards with images and text.

**Improvements Needed**:
- Better focus effects (current: scale 1.1x)
- Loading states for images
- Better placeholder images
- Channel logos should be clear and readable

**File**: `app/src/main/java/com/mattverwey/m3uplayer/ui/browse/ChannelCardPresenter.kt`

### 12. App Icon
**Status**: ✅ FIXED (visible icon created)
**Note**: Consider better icon design

### 13. Loading States
**Problem**: No feedback during channel loading.

**Needed**:
- Show progress bar during login
- Show loading indicator when fetching channels
- Error messages if loading fails

---

## 🛠️ TECHNICAL DEBT (Priority 5)

### 14. Deprecated API Usage
**Warning**: `onActivityCreated()` is deprecated in BrowseFragment.

**Fix**: Migrate to onCreate() or proper lifecycle methods.

### 15. Leanback Library Version
**Current**: androidx.leanback:leanback:1.0.0
**Issue**: Downgraded from 1.1.0 due to ImageCardView bug
**Consider**: Check if newer versions (1.2.0+) have fixes

### 16. Code Cleanup
- Remove unused variables (warnings in build)
- Add proper error handling
- Add logging for debugging
- Remove test/placeholder code

---

## 📋 TESTING CHECKLIST

Before marking as complete, test these scenarios on Fire TV Cube:

- [ ] App launches successfully
- [ ] Login works with Xtream credentials
- [ ] Main menu shows only 4 options (Live TV, Movies, Series, Settings)
- [ ] Remote UP/DOWN navigates sidebar
- [ ] Remote LEFT/RIGHT switches between sidebar and content
- [ ] Selecting Live TV shows all Live TV categories
- [ ] Selecting Movies shows all Movie categories
- [ ] Selecting Series shows all Series categories
- [ ] Can navigate content grid with remote
- [ ] Selecting a channel/movie/series starts playback
- [ ] Playback controls work (play, pause, stop)
- [ ] BACK button returns to previous screen
- [ ] App icon is visible in launcher
- [ ] No crashes during normal operation

---

## 📦 BUILD & DEPLOYMENT

### Build Commands:
```powershell
cd "C:\Users\pixel\Roku-M3u-Player"
.\gradlew.bat clean assembleDebug
```

### Install via ADB:
```powershell
adb install -r "C:\Users\pixel\Roku-M3u-Player\app\build\outputs\apk\debug\app-debug.apk"
```

### View Logs:
```powershell
adb logcat -v time | Select-String "mattverwey|AndroidRuntime|FATAL"
```

---

## 📚 KEY FILES TO MODIFY

1. **BrowseFragment.kt** - Main navigation and menu structure
2. **ChannelCardPresenter.kt** - Card display and styling
3. **styles.xml** - Theme and color definitions
4. **colors.xml** - Color palette
5. **AndroidManifest.xml** - Activity themes and configuration

---

## 💡 ARCHITECTURAL SUGGESTIONS

### Current Architecture Issues:
- Leanback BrowseSupportFragment may not be ideal for a hierarchical menu
- Consider: Multiple fragments or activities for each main section
- Consider: ViewPager or custom navigation system

### Recommended Approach:
1. **Option A**: Use separate activities for Live TV, Movies, Series (simpler)
2. **Option B**: Use Fragment transactions within MainActivity (more complex)
3. **Option C**: Create custom navigation with RecyclerView (most flexible)

---

## 🔗 USEFUL RESOURCES

- [Leanback Library Guide](https://developer.android.com/training/tv/start)
- [Fire TV Development](https://developer.amazon.com/apps-and-games/fire-tv)
- [ExoPlayer Documentation](https://exoplayer.dev/)
- [M3U Format Reference](https://en.wikipedia.org/wiki/M3U)

---

## 📝 NOTES FOR AGENTS

- This app is for **Amazon Fire TV Cube** - test on actual device, not emulator
- Remote control handling is crucial - test thoroughly
- Leanback library has quirks - read documentation carefully
- The original developer used Leanback's BrowseSupportFragment which may need replacement
- Consider user experience on a 10-foot UI (TV screen from couch distance)

---

**Last Updated**: October 30, 2025
**Current Version**: Debug build with basic functionality
**Next Agent**: Focus on Priority 1 issues first (main menu + remote control)
