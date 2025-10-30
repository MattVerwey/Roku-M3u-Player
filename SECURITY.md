# Security Documentation

## Overview

This document outlines the security measures implemented in the M3U Player to protect user credentials and viewing privacy.

## Security Features

### 1. Encrypted Credential Storage

All sensitive user data is encrypted at rest using **Android Jetpack Security** library with AES-256-GCM encryption:

- **Xtream Codes Credentials** (username, password, server URL)
- **M3U Playlist URLs** (may contain authentication tokens)
- **Viewing History** (recently watched channels)
- **Channel Cache** (contains stream URLs with embedded credentials)
- **EPG Data** (viewing patterns)

#### Implementation Details

The app uses `EncryptedSharedPreferences` with:
- **Master Key**: AES256_GCM encryption scheme
- **Key Encryption**: AES256_SIV for preference keys
- **Value Encryption**: AES256_GCM for preference values
- **Key Storage**: Android Keystore system (hardware-backed when available)

```kotlin
// Credentials are automatically encrypted when stored
cacheManager.setXtreamCredentials(credentials)
cacheManager.setM3UUrl(url)
```

**Important**: If the device does not support encrypted storage (Android Keystore unavailable), the app will throw a `SecurityException` and display an error message to the user. The app will not run without encryption support, ensuring that credentials are never stored in plain text.

### 2. Viewing Privacy Controls

Users have complete control over their viewing history:

- **Toggle Tracking**: Enable/disable viewing history tracking
- **Clear History**: Delete all viewing history on demand
- **Auto-Clear on Disable**: History is automatically cleared when tracking is disabled
- **Clear on Logout**: All data is securely cleared on logout

Access privacy settings via the **Menu button** on the main screen.

### 3. Network Security

#### Cleartext Traffic Protection

- **Release Builds**: HTTP cleartext traffic is **disabled** by default
- **Debug Builds**: Cleartext traffic allowed for testing only
- Encourages use of HTTPS for all connections

#### Secure Communication

- All network communications use OkHttp with modern TLS protocols
- Certificate validation enabled by default
- No insecure trust managers or certificate bypasses

### 4. Data Protection

#### No Backup to Cloud

- `android:allowBackup="false"` prevents unencrypted cloud backups
- Prevents sensitive data exposure through backup mechanisms
- Users must manually configure their credentials on each device

#### Memory Protection

- Sensitive data cleared on logout with `System.gc()` call
- No sensitive data logged in release builds
- Credentials not stored in plain text anywhere in memory

### 5. Code Obfuscation & Hardening

#### ProGuard/R8 Rules for Release Builds

1. **Remove All Logging**
   ```proguard
   -assumenosideeffects class android.util.Log {
       public static *** d(...);
       public static *** v(...);
       public static *** i(...);
       public static *** w(...);
       public static *** e(...);
   }
   ```

2. **Obfuscate Sensitive Classes**
   - Credential data classes are obfuscated
   - Stream URL patterns harder to reverse-engineer

3. **Protect Encryption Libraries**
   - EncryptedSharedPreferences classes kept
   - Google Tink crypto library preserved

### 6. Secure Logout

The app provides a secure logout mechanism that:
1. Clears all encrypted credentials
2. Clears M3U URLs
3. Clears viewing history
4. Clears cached channels and EPG data
5. Forces garbage collection to clear memory
6. Returns user to login screen

```kotlin
// Secure logout clears all sensitive data
cacheManager.secureLogout()
```

## Privacy Features

### What We Track (With User Consent)

- Recently watched channels (up to 50 items)
- Last playback position for each channel
- Timestamp of last view

### What We DON'T Track

- No analytics or telemetry
- No data sent to third parties
- No user behavior profiling
- No advertising identifiers
- No location tracking
- No device fingerprinting

### User Control

Users can:
- Disable tracking entirely
- Clear viewing history at any time
- View current source type (M3U or Xtream)
- Logout and clear all data

## Security Best Practices for Users

### 1. Use HTTPS URLs

Always use HTTPS URLs when possible:
- ✅ `https://example.com/playlist.m3u8`
- ❌ `http://example.com/playlist.m3u8`

### 2. Keep Credentials Secure

- Don't share your login credentials
- Use strong, unique passwords for Xtream accounts
- Change passwords periodically

### 3. Use Secure Networks

- Avoid using public WiFi without VPN
- Use trusted networks for streaming
- Consider using a VPN for additional privacy

### 4. Clear Data Before Selling/Giving Away Device

Always logout before:
- Selling your Fire TV device
- Factory resetting
- Giving device to someone else

Use the "Logout (Clear All Data)" button in Settings.

### 5. Keep App Updated

- Install security updates promptly
- Check for app updates regularly
- Review release notes for security fixes

## Security Architecture

### Data Flow

```
User Credentials → EncryptedSharedPreferences → Android Keystore
                                              ↓
                                   AES-256-GCM Encryption
                                              ↓
                                    Encrypted Storage
```

### Encryption at Rest

All sensitive data paths:

1. **Login** → Credentials encrypted → Stored
2. **Play Channel** → Add to history (if enabled) → Encrypted storage
3. **Cache Channels** → Stream URLs encrypted → Stored
4. **Logout** → All encrypted data deleted → Memory cleared

### Encryption Keys

- Master key stored in Android Keystore
- Hardware-backed encryption when available (Fire TV Cube)
- Keys cannot be extracted from device
- Keys deleted if device is factory reset

## Threat Model

### Protected Against

✅ **Local Storage Attacks**
- File system access (rooted devices)
- Backup extraction
- ADB access to SharedPreferences

✅ **Memory Dumping**
- Clear text credentials in memory
- Viewing history exposure

✅ **Network Eavesdropping** (in release builds)
- Cleartext traffic blocked
- HTTPS enforced where possible

✅ **Code Analysis**
- Obfuscated credential classes
- Logging removed in release

### Not Protected Against

⚠️ **Compromised Device**
- Malware with root access
- Keyloggers
- Screen recording malware

⚠️ **Network MITM** (if using HTTP)
- HTTP streams can be intercepted
- Credentials in HTTP URLs visible

⚠️ **Server-Side Vulnerabilities**
- Compromised IPTV servers
- Weak server authentication

## Security Updates

### How to Report Security Issues

If you discover a security vulnerability:

1. **DO NOT** create a public GitHub issue
2. Email security details privately to the maintainer
3. Include:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

### Security Update Policy

- Critical security issues: Patched within 7 days
- High severity: Patched within 30 days
- Medium/Low: Addressed in next release

## Compliance

### Data Protection

- No GDPR requirements (no data sent to servers)
- No CCPA requirements (no personal data collected)
- Local-only data storage
- User controls for all data

### Platform Security

- Compliant with Android Security Best Practices
- Follows Fire TV security guidelines
- Uses platform-provided encryption APIs

## Testing Security Features

### Manual Testing Checklist

- [ ] Verify credentials are encrypted in storage
- [ ] Test tracking enable/disable
- [ ] Verify history clears on disable
- [ ] Test logout clears all data
- [ ] Confirm app requires login after logout
- [ ] Verify no sensitive data in logcat
- [ ] Test cleartext traffic blocked in release

### Security Testing Tools

Recommended tools for security verification:
- **ADB**: Inspect SharedPreferences files
- **Frida**: Runtime analysis
- **APKTool**: Decompile and inspect resources
- **Wireshark**: Network traffic analysis

## Version History

### Version 2.0.0 (Current)
- ✅ Added EncryptedSharedPreferences (AES-256)
- ✅ Implemented viewing history privacy controls
- ✅ Added secure logout functionality
- ✅ Created Privacy & Security Settings UI
- ✅ Disabled cleartext traffic in release
- ✅ Added ProGuard rules for sensitive data
- ✅ Disabled Android backup

### Future Enhancements

Planned security improvements:
- [ ] Certificate pinning for known servers
- [ ] Biometric authentication option
- [ ] Session timeout/auto-logout
- [ ] PIN protection for app launch
- [ ] Encrypted export/import of settings

## Appendix

### Encryption Specifications

- **Algorithm**: AES-256-GCM
- **Key Size**: 256 bits
- **Mode**: Galois/Counter Mode (GCM)
- **Key Storage**: Android Keystore
- **Key Derivation**: PBKDF2 (platform-managed)

### Dependencies

Security-critical dependencies:
- `androidx.security:security-crypto:1.1.0-alpha06`
- Android Keystore API
- OkHttp 4.12.0 (with TLS 1.3 support)
- Retrofit 2.9.0

### References

- [Android Keystore System](https://developer.android.com/training/articles/keystore)
- [EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences)
- [Android Security Best Practices](https://developer.android.com/topic/security/best-practices)
- [OWASP Mobile Security](https://owasp.org/www-project-mobile-security/)

---

**Last Updated**: October 30, 2024  
**Document Version**: 1.0  
**App Version**: 2.0.0
