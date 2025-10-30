# Quick Start Testing Guide

**Get the M3U Player tested on Fire TV in 30 minutes!**

---

## ⚡ Quick Setup (5 minutes)

### 1. Build the APK
```bash
cd /path/to/Roku-M3u-Player
./gradlew clean assembleDebug
```
APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

### 2. Enable ADB on Fire TV
- Go to **Settings → My Fire TV → Developer Options**
- Enable **ADB Debugging**
- Note your Fire TV IP address (Settings → Network)

### 3. Install via ADB
```bash
adb connect <FIRE_TV_IP>:5555
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🎯 Essential Tests (10 minutes)

### Test 1: Login
- [ ] Open M3U Player on Fire TV
- [ ] Enter M3U URL or Xtream credentials
- [ ] Verify successful login

### Test 2: Browse
- [ ] Navigate with D-pad through categories
- [ ] Check Recently Watched row
- [ ] Check Latest Added row
- [ ] Check Live TV, Movies, Series rows
- [ ] Press **Menu button** (3 lines) - verify options appear

### Test 3: Playback
- [ ] Select a Live TV channel - verify it plays
- [ ] Press **Menu button** during playback - verify controls appear
- [ ] Try **Rewind** (10 sec back)
- [ ] Try **Fast Forward** (10 sec forward)
- [ ] Press **Home** button - verify PIP mode

### Test 4: Settings
- [ ] Press **Menu button** on browse screen
- [ ] Select **Refresh Channels** - verify it works
- [ ] Select **Clear Cache** - verify it works
- [ ] Select **Logout** - verify it logs out

---

## 📊 Quick Performance Check (5 minutes)

### Startup Performance
- [ ] App launches in < 3 seconds
- [ ] Channels load in < 5 seconds
- [ ] Video starts in < 5 seconds

### Video Quality
- [ ] Video starts in SD quality (fast startup)
- [ ] Video upgrades to HD within 10-20 seconds
- [ ] Minimal buffering during playback
- [ ] Smooth transitions between scenes

### Navigation
- [ ] D-pad navigation is smooth (no lag)
- [ ] Focus indicators are clear
- [ ] No crashes or freezes

---

## ✅ Pass/Fail Criteria

### ✅ PASS if:
- Login works with valid credentials
- Browse screen displays all categories
- Video playback starts within 5 seconds
- Playback controls work correctly
- PIP mode works
- No crashes during normal use

### ❌ FAIL if:
- Cannot login with valid credentials
- Browse screen doesn't load channels
- Video fails to play or crashes
- Playback controls don't appear
- App crashes during normal use

---

## 🐛 Common Issues & Quick Fixes

### Issue: "Login Failed"
- **Check:** Is the M3U URL accessible?
- **Check:** Are Xtream credentials correct?
- **Check:** Does the Fire TV have internet?

### Issue: "No channels available"
- **Fix:** Press Menu → Refresh Channels
- **Fix:** Press Menu → Clear Cache
- **Fix:** Logout and login again

### Issue: "Video won't play"
- **Check:** Internet connection (need 10+ Mbps)
- **Check:** Stream URL is valid
- **Try:** Different channel
- **Try:** Restart app

### Issue: "Buffering constantly"
- **Check:** Internet speed (run speed test)
- **Try:** Use Ethernet instead of WiFi
- **Try:** Move Fire TV closer to router
- **Try:** Close other apps using bandwidth

### Issue: "PIP not working"
- **Check:** Fire TV is Android 8.0+ (Oreo)
- **Note:** Some Fire TV devices don't support PIP

---

## 📋 Report Template

**Testing Date:** _____________  
**Fire TV Model:** _____________  
**App Version:** 2.0.0  
**Internet Speed:** _______ Mbps  

### Test Results
- [ ] Login: PASS / FAIL
- [ ] Browse: PASS / FAIL
- [ ] Playback: PASS / FAIL
- [ ] Settings: PASS / FAIL
- [ ] Performance: PASS / FAIL

### Issues Found
1. _________________________________
2. _________________________________
3. _________________________________

### Overall Assessment
- [ ] ✅ Ready for release
- [ ] ⚠️ Minor issues (still usable)
- [ ] ❌ Major issues (needs fixes)

---

## 📚 Full Documentation

For comprehensive testing, see:
- **[APP_READINESS_REPORT.md](APP_READINESS_REPORT.md)** - Complete functionality review
- **[FIRE_TV_TESTING_GUIDE.md](FIRE_TV_TESTING_GUIDE.md)** - Detailed testing procedures
- **[UI_DESIGN_DOCUMENTATION.md](UI_DESIGN_DOCUMENTATION.md)** - UI design specs
- **[BUFFERING_OPTIMIZATION.md](BUFFERING_OPTIMIZATION.md)** - Performance details

---

## 🚀 Advanced Testing (Optional)

If basic tests pass, proceed with:
- Full feature testing (all categories, all content types)
- Security testing (encrypted storage, logout)
- Performance testing (memory, CPU, network)
- Edge case testing (poor network, invalid URLs)
- Multiple device testing (different Fire TV models)

See [FIRE_TV_TESTING_GUIDE.md](FIRE_TV_TESTING_GUIDE.md) for detailed procedures.

---

**Happy Testing! 🎉**
