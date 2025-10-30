# Data Flow Architecture

This document shows how data flows through the system for the new features.

## Component Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         BrowseFragment                           │
│  (Displays rows of content to user)                             │
└───────────────────────┬─────────────────────────────────────────┘
                        │
                        │ calls
                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                      ChannelRepository                           │
│  • loadChannels()                                                │
│  • getLatestAddedContent()                                       │
│  • getRecommendations()                                          │
│  • getRecentlyWatchedChannels()                                  │
└────────┬──────────────────────┬──────────────────┬──────────────┘
         │                      │                  │
         │                      │                  │
         ▼                      ▼                  ▼
┌────────────────┐    ┌─────────────────┐  ┌────────────────────┐
│ XtreamApiService│    │ CacheManager    │  │RecommendationEngine│
│  • getSeries()  │    │ • cache data    │  │• score content     │
│  • getVODStreams│    │ • watch history │  │• genre matching    │
│  • getLiveStreams    │                 │  │• recency weighting │
└────────────────┘    └─────────────────┘  └────────────────────┘
         │
         │ HTTP requests
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Xtream Codes API                              │
│  (User's IPTV service provider)                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Feature Data Flows

### 1. Latest Added Content

```
┌──────────────────┐
│  User Opens App  │
└────────┬─────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ BrowseFragment.loadChannels()              │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ ChannelRepository.loadChannels()           │
│  1. Check cache                            │
│  2. If expired, fetch from API:            │
│     - getLiveStreams() → Channel[]         │
│     - getVODStreams() → Channel[]          │
│     - getSeries() → Channel[]  ⭐NEW       │
│  3. Convert to Channel objects             │
│  4. Extract 'added' timestamp              │
│  5. Cache results                          │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ Repository.getLatestAddedContent()         │
│  1. Filter: category = MOVIE or SERIES     │
│  2. Filter: added field is not null        │
│  3. Sort by: added DESC                    │
│  4. Take: 30 items                         │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ BrowseFragment.setupRows()                 │
│  Display "Latest Added" row                │
└────────────────────────────────────────────┘
```

### 2. Recommendations

```
┌──────────────────┐
│  User Watches    │
│  Content         │
└────────┬─────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ Repository.addToRecentlyWatched()          │
│  → CacheManager.addRecentlyWatched()       │
│     Stores: channelId, timestamp           │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ [User returns to browse screen]            │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ Repository.getRecommendations()            │
│  1. Get watch history from cache           │
│  2. Pass to RecommendationEngine           │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ RecommendationEngine.generateRecommendations()│
│                                            │
│  Step 1: Extract watched channel IDs       │
│  Step 2: Calculate preferences             │
│    ┌─────────────────────────────────┐    │
│    │ • Genre weights (with recency)  │    │
│    │ • Category weights              │    │
│    └─────────────────────────────────┘    │
│                                            │
│  Step 3: Score all unwatched content       │
│    For each channel:                       │
│    ┌─────────────────────────────────┐    │
│    │ Score = 0                       │    │
│    │ + Genre matches * 2.0           │    │
│    │ + Category match * 1.5          │    │
│    │ + Rating bonus                  │    │
│    │ + Recency bonus                 │    │
│    └─────────────────────────────────┘    │
│                                            │
│  Step 4: Sort by score DESC                │
│  Step 5: Take top 30                       │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ BrowseFragment.setupRows()                 │
│  Display "Recommended for You" row         │
└────────────────────────────────────────────┘
```

### 3. Series Support

```
┌──────────────────────────────────────────────┐
│ Xtream API: getSeries()                      │
│  Returns: List<XtreamSeries>                 │
│    • series_id                               │
│    • name                                    │
│    • cover (image URL)                       │
│    • plot                                    │
│    • genre                                   │
│    • last_modified (timestamp) ⭐           │
└────────┬─────────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ XtreamSeries.toChannel()                   │
│  Convert to Channel object:                │
│    • id = "series_{series_id}"             │
│    • category = SERIES                     │
│    • added = last_modified                 │
│    • seriesId = series_id                  │
└────────┬───────────────────────────────────┘
         │
         ▼
┌────────────────────────────────────────────┐
│ Channel objects now include series         │
│  Used for:                                 │
│    • "Series" browse row                   │
│    • "Latest Added" (if recent)            │
│    • "Recommended" (if match preferences)  │
└────────────────────────────────────────────┘
```

## Data Models

### Channel (Enhanced)
```kotlin
data class Channel(
    val id: String,              // "series_123", "vod_456", "live_789"
    val name: String,            // Display name
    val streamUrl: String,       // Playback URL
    val category: ChannelCategory, // LIVE_TV, MOVIE, SERIES
    val genre: String?,          // "Crime, Drama, Thriller"
    val added: String?,          // ⭐ "1698765432" (Unix timestamp)
    val seriesId: Int?,          // ⭐ 123 (for series only)
    // ... other fields
)
```

### RecentlyWatched
```kotlin
data class RecentlyWatched(
    val channelId: String,       // Links to Channel.id
    val timestamp: Long,         // When watched (Unix ms)
    val lastPosition: Long,      // Playback position
    val duration: Long          // Total duration
)
```

### XtreamSeries (New)
```kotlin
data class XtreamSeries(
    val series_id: Int,
    val name: String,
    val cover: String?,          // Poster image
    val plot: String?,           // Description
    val genre: String?,          // "Crime, Drama"
    val last_modified: String?,  // ⭐ Used as 'added' timestamp
    // ... other fields
)
```

## Cache Strategy

```
┌─────────────────────────────────────────┐
│         SharedPreferences               │
│                                         │
│  • cached_channels (24h expiry)         │
│  • recently_watched (max 50 items)      │
│  • xtream_credentials                   │
│                                         │
│  No separate recommendation cache -     │
│  calculated on-demand from history      │
└─────────────────────────────────────────┘
```

## Performance Characteristics

### Latest Added
- **Complexity:** O(n) filter + O(n log n) sort
- **Time:** < 10ms for 1000 channels
- **Memory:** Reference only (no duplication)

### Recommendations
- **Complexity:** O(h) preference calc + O(n) scoring
  - h = history size (max 50)
  - n = channel count
- **Time:** < 50ms for 1000 channels, 50 history items
- **Memory:** ~10KB for scoring data

### Series Loading
- **Network:** +1 API call per playlist load
- **Response Size:** ~5-50KB depending on catalog
- **Time:** +100-500ms to initial load

## Error Handling

```
┌─────────────────────────────────┐
│ Network Error                   │
│  • Use cached data (if valid)   │
│  • Show error toast             │
│  • Retry available in menu      │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ No Watch History                │
│  • Skip recommendations row     │
│  • Still show latest added      │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ Missing 'added' Field           │
│  • Filter out from latest added │
│  • Content still shows in       │
│    category rows                │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ Series API Unavailable          │
│  • Continue with VOD & Live     │
│  • No series row shown          │
│  • Latest/Recommended still work│
└─────────────────────────────────┘
```

## Threading Model

All network and computation happens on background threads:

```kotlin
// In ChannelRepository
suspend fun loadChannels(): Result<List<Channel>> {
    return withContext(Dispatchers.IO) {
        // Network calls here
    }
}

// Called from BrowseFragment
lifecycleScope.launch {
    val result = repository.loadChannels()
    // Update UI on main thread
}
```

## Security & Privacy

- ✅ No external analytics
- ✅ No recommendation data sent to servers
- ✅ All processing happens locally
- ✅ Watch history never leaves device
- ✅ User can clear all data via "Clear Cache"
