# GitHub Actions APK Build Guide

This guide explains how to build and download APK files for the Roku M3U Player using GitHub Actions.

## Automated Builds

The GitHub Actions workflow automatically builds APKs when:

- **Push to branches**: `master`, `develop`, or any `copilot/**` branch
- **Pull requests**: To the `master` branch

## Manual Build

You can manually trigger a build at any time:

1. Go to the [Actions tab](../../actions) in your GitHub repository
2. Select the **"Build Android APK"** workflow from the left sidebar
3. Click the **"Run workflow"** button (on the right)
4. Select the branch you want to build
5. Click **"Run workflow"** to start the build

## Downloading APK Files

After a workflow run completes:

1. Go to the workflow run details page
2. Scroll down to the **"Artifacts"** section at the bottom
3. Download the APK you need:
   - **app-debug-apk**: Debug version (available for all builds, retained for 30 days)
   - **app-release-apk**: Release version (only built on `master` branch, retained for 90 days)

## APK Types

### Debug APK
- Built for all branches
- Includes debugging information
- Allows cleartext HTTP traffic for testing
- Useful for development and testing
- Available for 30 days after build

### Release APK
- Only built from the `master` branch
- Optimized and minified
- Production-ready
- Restricts cleartext HTTP traffic for security
- Available for 90 days after build

## Installing the APK

### On Fire TV / Android TV:
1. Enable **"Apps from Unknown Sources"** in Settings
2. Transfer the APK to your device via USB or network
3. Use a file manager app to locate and install the APK

### On Android Phone/Tablet:
1. Enable **"Install unknown apps"** for your browser or file manager
2. Download the APK file
3. Open the APK file to install

## Build Requirements

The workflow automatically:
- Sets up JDK 17
- Caches Gradle dependencies
- Makes gradlew executable
- Builds the APK using Gradle

No local setup is required - everything runs in GitHub's cloud infrastructure.

## Troubleshooting

If a build fails:
1. Check the workflow run logs in the Actions tab
2. Look for error messages in the build output
3. Ensure all required files are present in the repository
4. Verify that `build.gradle` files are properly configured

## Build Status

You can check the status of recent builds in the [Actions tab](../../actions).
