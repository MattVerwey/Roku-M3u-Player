# M3U Player Testing Checklist

**Version:** 2.0.0  
**Platform:** Amazon Fire TV / Android TV  
**Test Date:** _______________  
**Tester:** _______________  

---

## Pre-Testing Setup

### Environment Setup
- [ ] Fire TV device ready (Cube or Stick)
- [ ] Fire TV remote with Menu button (3 lines)
- [ ] ADB enabled on Fire TV
- [ ] Computer connected to same network
- [ ] APK built successfully
- [ ] APK installed on Fire TV

### Test Data Preparation
- [ ] M3U URL available for testing
- [ ] Xtream Codes credentials available
- [ ] Internet speed ≥ 10 Mbps
- [ ] Network stable (wired preferred)

---

## 1. Installation & First Launch

### Installation (5 min)
- [ ] APK installs without errors
- [ ] App icon appears in Fire TV apps
- [ ] App banner appears correctly
- [ ] App launches from Fire TV home

### First Launch (2 min)
- [ ] Splash screen displays correctly
- [ ] Login screen appears
- [ ] No crashes on first launch
- [ ] All UI elements visible

---

## 2. Authentication Testing

### M3U URL Login (5 min)
- [ ] M3U URL input field accepts text
- [ ] Login button is clickable
- [ ] Valid M3U URL logs in successfully
- [ ] Invalid M3U URL shows error
- [ ] Empty M3U URL shows validation error
- [ ] Loading indicator appears during login
- [ ] Success message displays on login
- [ ] Navigates to browse screen after login

### Xtream Codes Login (5 min)
- [ ] Server URL input field accepts text
- [ ] Username input field accepts text
- [ ] Password input field accepts text
- [ ] Login button is clickable
- [ ] Valid credentials log in successfully
- [ ] Invalid credentials show error
- [ ] Empty fields show validation error
- [ ] Server URL auto-adds http:// if missing
- [ ] Loading indicator appears during login
- [ ] Success message displays on login
- [ ] Navigates to browse screen after login

---

## 3. Browse Screen Testing

### UI Layout (10 min)
- [ ] App name displays in header
- [ ] Recently Watched row appears (if available)
- [ ] Latest Added row appears
- [ ] Live TV row appears
- [ ] Movies row appears
- [ ] Series row appears
- [ ] Custom categories appear (if in playlist)
- [ ] Channel cards display correctly
- [ ] Channel logos load correctly
- [ ] Card focus indicators work
- [ ] Card scaling animation works

### Navigation (10 min)
- [ ] D-pad Up/Down moves between rows
- [ ] D-pad Left/Right moves within rows
- [ ] First item in first row is focused on start
- [ ] Focus wraps correctly at row ends
- [ ] Select button opens details/plays channel
- [ ] Back button exits app (with confirmation)
- [ ] Scrolling is smooth (no lag)
- [ ] Focus stays on selected item
- [ ] Can navigate to all categories
- [ ] Can navigate to all channels

### Menu Button (3 lines) (5 min)
- [ ] Menu button opens options dialog
- [ ] Dialog shows three options:
  - [ ] Refresh Channels
  - [ ] Clear Cache
  - [ ] Logout
- [ ] Refresh Channels reloads playlist
- [ ] Clear Cache clears and reloads
- [ ] Logout shows confirmation dialog
- [ ] Logout cancels on "No"
- [ ] Logout exits on "Yes"
- [ ] Dialog dismisses with Back button

---

## 4. Channel Details Screen

### Live TV Details (5 min)
- [ ] Details screen opens for live channels
- [ ] Channel name displays
- [ ] Channel logo displays
- [ ] EPG data displays (if available)
- [ ] Watch Now button appears
- [ ] Watch Now button plays channel
- [ ] Back button returns to browse

### Movie Details (5 min)
- [ ] Details screen opens for movies
- [ ] Movie title displays
- [ ] Movie poster displays
- [ ] Movie description displays
- [ ] Movie metadata (rating, genre, year) displays
- [ ] Watch Now button appears
- [ ] Watch Now button plays movie
- [ ] Back button returns to browse

### Series Details (10 min)
- [ ] Details screen opens for series
- [ ] Series title displays
- [ ] Series cover displays
- [ ] Series description displays
- [ ] Season selector appears
- [ ] Episode list appears
- [ ] Can select different seasons
- [ ] Can select different episodes
- [ ] Watch Now plays selected episode
- [ ] Back button returns to browse

---

## 5. Video Playback Testing

### Playback Startup (10 min)
- [ ] Live TV plays on select
- [ ] Movie plays on select
- [ ] Series episode plays on select
- [ ] Video starts within 5 seconds
- [ ] Video starts in SD quality (fast)
- [ ] Loading indicator appears
- [ ] Video goes fullscreen
- [ ] System UI hides automatically

### Playback Quality (15 min)
- [ ] Video plays smoothly
- [ ] Audio syncs with video
- [ ] SD quality is acceptable
- [ ] HD upgrade occurs automatically (10-20 sec)
- [ ] HD quality is clear
- [ ] No pixelation or artifacts
- [ ] Buffering is minimal
- [ ] Stream doesn't stutter
- [ ] Aspect ratio is correct
- [ ] Colors look natural

### Playback Controls (15 min)
- [ ] Menu button shows controls
- [ ] Controls overlay appears
- [ ] All buttons are visible:
  - [ ] Play/Pause
  - [ ] Rewind (10 sec)
  - [ ] Fast Forward (10 sec)
  - [ ] Subtitles
  - [ ] Audio Track
  - [ ] Video Quality
  - [ ] TV Guide (Live TV)
  - [ ] Play Next (Series)
- [ ] Play/Pause toggles correctly
- [ ] Rewind skips back 10 seconds
- [ ] Fast Forward skips forward 10 seconds
- [ ] Controls auto-hide after 5 seconds
- [ ] Menu button re-shows controls
- [ ] Back button hides controls (first press)
- [ ] Back button exits playback (second press)

### Subtitle Controls (5 min)
- [ ] Subtitle button opens dialog
- [ ] Available subtitle tracks listed
- [ ] Can select subtitle track
- [ ] Subtitles display on screen
- [ ] Can turn off subtitles
- [ ] Subtitle selection doesn't interrupt playback

### Audio Track Controls (5 min)
- [ ] Audio Track button opens dialog
- [ ] Available audio tracks listed
- [ ] Audio language shown
- [ ] Can select audio track
- [ ] Audio switches correctly
- [ ] Audio selection doesn't interrupt playback

### Video Quality Controls (5 min)
- [ ] Video Quality button opens dialog
- [ ] Available qualities listed
- [ ] Can select quality
- [ ] Quality switches correctly
- [ ] "Auto" option available
- [ ] Quality selection doesn't interrupt playback

### TV Guide (EPG) - Live TV Only (10 min)
- [ ] TV Guide button appears (live TV)
- [ ] TV Guide button opens overlay
- [ ] Current program displays
- [ ] Upcoming programs display
- [ ] Program titles shown
- [ ] Program times shown
- [ ] Guide displays over video
- [ ] Video continues in background
- [ ] Back button closes guide
- [ ] Guide data is accurate (if available)

### Play Next - Series Only (5 min)
- [ ] Play Next button appears (series)
- [ ] Play Next button is clickable
- [ ] Clicking plays next episode
- [ ] Next episode auto-plays on completion
- [ ] Auto-play can be cancelled
- [ ] Last episode shows no Play Next

---

## 6. Picture-in-Picture (PIP) Testing

### PIP Functionality (10 min)
- [ ] Home button enters PIP mode
- [ ] Video shrinks to corner
- [ ] Video continues playing in PIP
- [ ] Audio continues in PIP
- [ ] Can navigate browse screen in PIP
- [ ] Can select another channel in PIP
- [ ] New channel replaces PIP video
- [ ] Back button exits PIP
- [ ] PIP returns to fullscreen
- [ ] Playback position maintained

### PIP Compatibility (2 min)
- [ ] PIP works on Fire TV Cube
- [ ] PIP works on Fire TV Stick (4K)
- [ ] PIP works on Fire TV Stick (HD)
- [ ] Note: Some devices may not support PIP

---

## 7. Recently Watched Testing

### Recently Watched Functionality (10 min)
- [ ] Played channels appear in Recently Watched
- [ ] Recently Watched row updates after playback
- [ ] Recently Watched shows up to 50 channels
- [ ] Most recent appears first
- [ ] Duplicate plays don't duplicate entries
- [ ] Can play from Recently Watched
- [ ] Recently Watched persists after app restart
- [ ] Recently Watched syncs across app sessions

---

## 8. Settings & Privacy Testing

### Settings Access (5 min)
- [ ] Settings accessible from menu
- [ ] Settings screen displays
- [ ] All settings visible
- [ ] Settings are organized
- [ ] Back button returns to browse

### Viewing History Controls (10 min)
- [ ] Tracking toggle exists
- [ ] Tracking toggle works
- [ ] Disabling stops tracking
- [ ] Enabling resumes tracking
- [ ] Clear History button exists
- [ ] Clear History shows confirmation
- [ ] Clear History clears data
- [ ] Recently Watched clears after clear
- [ ] Settings persist after restart

### Cache Management (5 min)
- [ ] Clear Cache option in menu
- [ ] Clear Cache shows confirmation
- [ ] Clear Cache clears data
- [ ] Channels reload after clear
- [ ] Cache rebuild works
- [ ] Offline cached data accessible

### Logout (5 min)
- [ ] Logout option in menu
- [ ] Logout shows confirmation
- [ ] Cancel keeps session
- [ ] Confirm logs out
- [ ] Credentials cleared on logout
- [ ] Viewing history cleared on logout
- [ ] Returns to login screen
- [ ] Cannot access app without login

---

## 9. Performance Testing

### Startup Performance (5 min)
- [ ] Cold start < 3 seconds
- [ ] Warm start < 2 seconds
- [ ] Login screen appears quickly
- [ ] Browse screen loads quickly
- [ ] Channel data loads < 5 seconds

### Memory Usage (10 min)
- [ ] App doesn't leak memory
- [ ] Memory usage reasonable (<200MB)
- [ ] No memory warnings in logs
- [ ] App doesn't slow down over time
- [ ] Multiple playbacks don't increase memory

### Video Performance (15 min)
- [ ] Video startup < 5 seconds
- [ ] SD startup < 3 seconds
- [ ] HD upgrade smooth
- [ ] Buffering rare (< 1/30min)
- [ ] Seek operations fast
- [ ] Quality changes smooth
- [ ] Track changes smooth
- [ ] PIP transition smooth

### Network Performance (10 min)
- [ ] Works on 10 Mbps connection
- [ ] HD works on 15 Mbps connection
- [ ] Handles network fluctuations
- [ ] Recovers from brief disconnects
- [ ] Shows appropriate errors on failure

---

## 10. Stability Testing

### Crash Testing (20 min)
- [ ] No crashes during normal use
- [ ] No crashes on login
- [ ] No crashes on browse
- [ ] No crashes on playback
- [ ] No crashes on PIP
- [ ] No crashes on settings
- [ ] No crashes on logout
- [ ] No crashes on network issues
- [ ] No crashes on invalid data

### Error Handling (15 min)
- [ ] Invalid login shows error
- [ ] Network timeout shows error
- [ ] Invalid stream URL shows error
- [ ] Missing data shows fallback
- [ ] Errors are user-friendly
- [ ] Can recover from errors
- [ ] App doesn't freeze on errors

---

## 11. Security Testing

### Credential Storage (10 min)
- [ ] Credentials not in logs
- [ ] Credentials not in plain text
- [ ] Credentials encrypted at rest
- [ ] Credentials cleared on logout
- [ ] No credentials in memory after logout

### Privacy Testing (10 min)
- [ ] Viewing history encrypted
- [ ] History clearable by user
- [ ] Tracking can be disabled
- [ ] No data sent to third parties
- [ ] All data stored locally

---

## 12. Edge Case Testing

### Poor Network Conditions (15 min)
- [ ] Works on slow connection (5 Mbps)
- [ ] Degrades to SD on slow network
- [ ] Shows loading on slow network
- [ ] Recovers when network improves
- [ ] Doesn't crash on disconnect
- [ ] Shows error on no internet

### Invalid Data (10 min)
- [ ] Handles empty playlists
- [ ] Handles malformed M3U
- [ ] Handles invalid URLs
- [ ] Handles missing logos
- [ ] Handles missing EPG
- [ ] Shows appropriate messages

### Long-term Use (30 min)
- [ ] Stable after 30 minutes
- [ ] Stable after 1 hour
- [ ] No memory leaks
- [ ] No performance degradation
- [ ] Recently Watched doesn't overflow

---

## Test Summary

### Overall Results
- **Total Tests:** _____ / _____
- **Passed:** _____
- **Failed:** _____
- **Blocked:** _____
- **Pass Rate:** _____%

### Critical Issues (Blockers)
1. _________________________________
2. _________________________________
3. _________________________________

### Major Issues (High Priority)
1. _________________________________
2. _________________________________
3. _________________________________

### Minor Issues (Low Priority)
1. _________________________________
2. _________________________________
3. _________________________________

### Performance Metrics
- **Cold Start:** _____ seconds
- **Channel Load:** _____ seconds
- **Video Start:** _____ seconds
- **Memory Usage:** _____ MB
- **Buffering Events:** _____ per 30 min

### Device Information
- **Model:** _____________________
- **OS Version:** _________________
- **Internet Speed:** _____ Mbps
- **Connection Type:** Wired / WiFi

### Overall Assessment
- [ ] ✅ Ready for Release
- [ ] ⚠️ Ready with Minor Issues
- [ ] ❌ Not Ready - Major Issues

### Tester Notes
_________________________________________________
_________________________________________________
_________________________________________________
_________________________________________________

---

**Test Completed:** _______________  
**Tester Signature:** _______________
