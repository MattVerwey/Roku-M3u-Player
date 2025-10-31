# How to Unblock dl.google.com and Maven Repositories for Local Testing

## Overview

The build system requires access to `dl.google.com` (Google Maven repository) to download the Android Gradle Plugin and Android dependencies. If you're experiencing network restrictions, here are several solutions to enable local builds.

## Solution 1: Check Your Network/Firewall Settings (Quickest)

### For Corporate/Enterprise Networks:

1. **Contact Your Network Administrator**
   - Request whitelisting for these domains:
     - `dl.google.com`
     - `maven.google.com`
     - `*.googleapis.com`
     - `repo.maven.apache.org`
     - `repo1.maven.org`

2. **VPN Configuration**
   - If your organization uses a VPN, try:
     ```bash
     # Disconnect from VPN temporarily
     # Then run the build
     ./gradlew clean assembleDebug
     ```
   - Or configure split-tunnel VPN to allow Google domains

3. **Proxy Settings**
   - If your network uses a proxy, configure Gradle to use it:
     ```bash
     # Create/edit ~/.gradle/gradle.properties
     systemProp.http.proxyHost=your-proxy-host
     systemProp.http.proxyPort=8080
     systemProp.https.proxyHost=your-proxy-host
     systemProp.https.proxyPort=8080
     
     # If proxy requires authentication:
     systemProp.http.proxyUser=username
     systemProp.http.proxyPassword=password
     systemProp.https.proxyUser=username
     systemProp.https.proxyPassword=password
     ```

### For Personal Computer:

1. **Check Firewall Rules**
   ```bash
   # On Linux (Ubuntu/Debian)
   sudo ufw status
   sudo ufw allow out to any port 443 comment 'HTTPS'
   
   # On macOS
   # System Preferences → Security & Privacy → Firewall → Firewall Options
   # Ensure "Block all incoming connections" is unchecked
   
   # On Windows
   # Control Panel → Windows Defender Firewall → Allow an app through firewall
   ```

2. **DNS Issues**
   ```bash
   # Test DNS resolution
   nslookup dl.google.com
   ping dl.google.com
   
   # If failed, try switching DNS servers:
   # Use Google DNS: 8.8.8.8 and 8.8.4.4
   # Or Cloudflare DNS: 1.1.1.1 and 1.0.0.1
   ```

3. **Hosts File Check**
   ```bash
   # Check if dl.google.com is blocked in hosts file
   cat /etc/hosts | grep google
   
   # If blocked, edit and remove the blocking entry:
   sudo nano /etc/hosts
   # Remove any line containing dl.google.com
   ```

## Solution 2: Use a VPN Service (If Corporate Network Blocks)

If your corporate network blocks Google services:

1. **Personal VPN Services:**
   - NordVPN, ExpressVPN, ProtonVPN (free tier available)
   - Connect to VPN before running build
   - Disconnect after dependencies are downloaded

2. **Free VPN Options:**
   - ProtonVPN (free tier)
   - Windscribe (10GB free/month)
   - TunnelBear (500MB free/month)

## Solution 3: Use Mobile Hotspot

If you have a smartphone with mobile data:

```bash
# Connect your computer to your phone's hotspot
# This bypasses corporate network restrictions
# Run the build:
./gradlew clean assembleDebug

# Once dependencies are cached, you can switch back to regular network
```

## Solution 4: Pre-Download Dependencies (Offline Build)

If you have access to a computer with unrestricted internet:

### On Unrestricted Computer:

```bash
# 1. Clone the repository
git clone https://github.com/MattVerwey/Roku-M3u-Player.git
cd Roku-M3u-Player

# 2. Download all dependencies
./gradlew clean assembleDebug --refresh-dependencies

# 3. Package the Gradle cache
tar -czf gradle-cache.tar.gz ~/.gradle/caches ~/.gradle/wrapper

# 4. Transfer gradle-cache.tar.gz to your restricted computer
```

### On Restricted Computer:

```bash
# 1. Extract the cache
tar -xzf gradle-cache.tar.gz -C ~/

# 2. Build offline
./gradlew clean assembleDebug --offline
```

## Solution 5: Use GitHub Actions (Already Set Up)

**This is the recommended approach if local builds are not possible:**

1. **Approve the Workflow:**
   - Visit: https://github.com/MattVerwey/Roku-M3u-Player/actions
   - Click on "Build Android APK" workflow
   - Click "Approve and run" for pending runs

2. **Download the Built APK:**
   - Wait 3-5 minutes for build to complete
   - Click on successful run (green checkmark)
   - Download from "Artifacts" section
   - Extract `app-debug.apk`

3. **No Network Restrictions:**
   - GitHub Actions runs in cloud with full internet access
   - Automatically builds on every push
   - No local setup required

## Solution 6: Use Docker with Host Network

If you have Docker and your host has unrestricted network:

```bash
# Create a Dockerfile
cat > Dockerfile << 'EOF'
FROM ubuntu:22.04

# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    wget \
    unzip \
    git

# Set JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

WORKDIR /workspace
EOF

# Build and run
docker build -t android-builder .
docker run --network host -v $(pwd):/workspace android-builder ./gradlew clean assembleDebug
```

## Solution 7: Configure Android Studio to Use Proxy

If using Android Studio:

1. **File → Settings (or Android Studio → Preferences on Mac)**
2. **Appearance & Behavior → System Settings → HTTP Proxy**
3. **Select "Manual proxy configuration"**
4. **HTTP Proxy:**
   - Host: `your-proxy-host`
   - Port: `8080`
5. **HTTPS Proxy:** (same as HTTP)
6. **Click "Check connection" to test**

## Verification

After unblocking, verify access:

```bash
# Test DNS resolution
nslookup dl.google.com
# Should return IP addresses like 142.250.x.x

# Test HTTP/HTTPS access
curl -I https://dl.google.com/dl/android/maven2/
# Should return "HTTP/1.1 301 Moved Permanently" or "HTTP/1.1 200 OK"

# Test Gradle build
./gradlew clean --refresh-dependencies
# Should complete without "No address associated with hostname" errors
```

## Troubleshooting

### Error: "dl.google.com: No address associated with hostname"
- **Cause:** DNS resolution is blocked
- **Fix:** Use Solutions 1 (DNS), 2 (VPN), or 3 (Mobile Hotspot)

### Error: "Connection refused"
- **Cause:** Firewall blocking outbound HTTPS
- **Fix:** Check firewall rules (Solution 1)

### Error: "Connection timeout"
- **Cause:** Network policy blocking Google domains
- **Fix:** Use VPN (Solution 2) or GitHub Actions (Solution 5)

### Build works but fails after some time
- **Cause:** Dependencies cached locally, but new ones being blocked
- **Fix:** Use `--offline` flag if all dependencies are cached:
  ```bash
  ./gradlew clean assembleDebug --offline
  ```

## Recommended Approach

**For most users with network restrictions:**

1. **First try:** Mobile hotspot (Solution 3) - quickest, no configuration
2. **Second try:** GitHub Actions (Solution 5) - no local setup, always works
3. **Third try:** Pre-download dependencies (Solution 4) - one-time setup

**For developers who need local builds frequently:**

1. **Best long-term:** Request network administrator to whitelist Google domains
2. **Alternative:** Use VPN service (Solution 2) for development sessions

## Additional Resources

- **Gradle Proxy Configuration:** https://docs.gradle.org/current/userguide/build_environment.html#sec:accessing_the_web_via_a_proxy
- **Android Studio Network Settings:** https://developer.android.com/studio/intro/studio-config#proxy
- **GitHub Actions Documentation:** https://docs.github.com/en/actions

## Quick Command Reference

```bash
# Test network access
curl -I https://dl.google.com/dl/android/maven2/

# Configure Gradle proxy (if needed)
cat >> ~/.gradle/gradle.properties << 'EOF'
systemProp.https.proxyHost=proxy.example.com
systemProp.https.proxyPort=8080
EOF

# Build with existing cache (offline)
./gradlew clean assembleDebug --offline

# Build with VPN
# 1. Connect to VPN
# 2. Run build
./gradlew clean assembleDebug
# 3. Disconnect VPN (optional)
```

---

**Need Help?** If none of these solutions work, the GitHub Actions workflow (Solution 5) is guaranteed to work as it runs in an unrestricted cloud environment.
