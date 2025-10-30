# Legacy Roku BrightScript Files

⚠️ **DEPRECATED**: The following files are from the original Roku BrightScript implementation and are kept for reference only.

## Legacy Files (No Longer Used)

These files were part of the original Roku application (v1.x) and have been completely replaced by the Android TV implementation (v2.0+):

### Components
- `components/MainScene/MainScene.brs` - Original main scene logic
- `components/MainScene/MainScene.xml` - Original scene layout
- `components/get_channel_list/get_channel_list.brs` - Old M3U parser
- `components/get_channel_list/get_channel_list.xml` - Task definition
- `components/save_feed_url/save_feed_url.brs` - Registry storage
- `components/save_feed_url/save_feed_url.xml` - Task definition

### Source
- `source/main.brs` - Original entry point

### Assets
- `images/` - Roku-specific images and splash screens

### Configuration
- `manifest` - Roku channel manifest (not used for Android TV)

## Migration Notes

All functionality from these legacy files has been reimplemented in Kotlin for Android TV:

| Legacy Roku File | New Android TV File |
|------------------|---------------------|
| `components/get_channel_list/get_channel_list.brs` | `app/src/main/java/.../network/M3UParser.kt` |
| `components/save_feed_url/save_feed_url.brs` | `app/src/main/java/.../data/cache/CacheManager.kt` |
| `components/MainScene/MainScene.brs` | `app/src/main/java/.../ui/browse/BrowseFragment.kt` |
| `source/main.brs` | `app/src/main/java/.../ui/MainActivity.kt` |

## Why Keep These Files?

These files are retained in the repository for:
1. **Historical reference** - Understanding the original implementation
2. **Documentation** - Migration guide references
3. **Comparison** - Before/after architecture comparison

## Removal

These files can be safely deleted if:
- No plans to support Roku platform
- Migration is fully complete and tested
- No need for historical reference

To remove:
```bash
rm -rf components/ source/ images/ manifest
git add .
git commit -m "Remove legacy Roku files"
```

## Current Platform

**Active Platform**: Android TV (Amazon Fire TV)  
**Active Codebase**: `app/src/main/` directory  
**Version**: 2.0.0+

For current implementation details, see:
- `README.md` - Usage and setup
- `MIGRATION_GUIDE.md` - Technical migration details
- `IMPLEMENTATION_SUMMARY.md` - Complete feature overview
