# Build Issue: Network Restrictions

## Problem

The build cannot complete in this environment due to network restrictions that block access to `dl.google.com`, which hosts the Android Gradle Plugin and Android dependencies.

## Error Details

```
Could not GET 'https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/8.1.4/gradle-8.1.4.pom'.
> dl.google.com: No address associated with hostname
```

## Root Cause

- DNS resolution for `dl.google.com` is REFUSED at the network level
- Common mirror sites (aliyun.com, tencent.com, huawei.com, etc.) are also blocked
- Only `github.com` and `repo.maven.apache.org` (Maven Central) are accessible
- Android Gradle Plugin is exclusively published to Google's Maven repository

## Accessible Repositories

✅ github.com  
✅ repo.maven.apache.org (Maven Central)  
✅ repo1.maven.org (Maven Central Mirror)  
❌ dl.google.com (Google Maven - BLOCKED)  
❌ maven.google.com (redirects to dl.google.com - BLOCKED)  
❌ maven.aliyun.com (Alibaba Cloud Mirror - BLOCKED)  
❌ mirrors.tencent.com (Tencent Mirror - BLOCKED)  
❌ repo.huaweicloud.com (Huawei Mirror - BLOCKED)  
❌ jitpack.io (JitPack - BLOCKED)  

## Solutions

### Option 1: Build in a Different Environment (Recommended)

Build the APK in an environment with access to Google services:

```bash
# On a machine with open internet access:
cd Roku-M3u-Player
./gradlew clean assembleDebug

# The APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Option 2: Use GitHub Actions

Create `.github/workflows/build.yml`:

```yaml
name: Build APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### Option 3: Pre-download Dependencies

On a machine with Google access, pre-download all dependencies:

```bash
./gradlew clean assembleDebug --refresh-dependencies

# Package the .gradle cache
tar -czf gradle-cache.tar.gz ~/.gradle/caches

# Transfer to restricted environment and extract
tar -xzf gradle-cache.tar.gz -C ~/
```

### Option 4: Use Android Studio

Android Studio includes bundled Android SDK and gradle plugins:

1. Open project in Android Studio
2. Sync Gradle
3. Build → Build Bundle(s) / APK(s) → Build APK(s)

## Current Build Configuration

The project uses:
- Android Gradle Plugin 8.1.4
- Kotlin 1.9.23
- Gradle 8.2
- JDK 17
- Target SDK 34
- Min SDK 26

## Next Steps

1. ✅ Build APK in unrestricted environment
2. ✅ Upload APK to GitHub Releases
3. ✅ Test on Fire TV device
4. ✅ Document deployment process

## Temporary Workaround Applied

Updated `build.gradle` and `settings.gradle` to attempt using `maven.google.com` as alternative, but it redirects to the blocked `dl.google.com`.

## References

- [Android Build Dependencies](https://developer.android.com/studio/build/dependencies)
- [Gradle Repository Configuration](https://docs.gradle.org/current/userguide/declaring_repositories.html)
- [Offline Build Cache](https://docs.gradle.org/current/userguide/build_cache.html)
