# Recommendation and Content Discovery Features

This document describes the new content discovery features added to the M3U Player.

## Features Overview

### 1. Latest Added Content

Shows the 30 most recently added movies and series from your Xtream playlist.

**How it works:**
- Uses the `added` timestamp field from the Xtream API
- Filters for movies (`ChannelCategory.MOVIE`) and series (`ChannelCategory.SERIES`)
- Sorts by timestamp in descending order (newest first)
- Displays in a dedicated "Latest Added" row near the top of the browse screen

**API Field Used:**
- For VOD (movies): `XtreamVOD.added` - Unix timestamp as string
- For Series: `XtreamSeries.last_modified` - Unix timestamp as string
- For Live Streams: `XtreamStream.added` - Unix timestamp as string (not shown in Latest Added)

### 2. Personalized Recommendations

Intelligent recommendations that appear at the bottom of the browse screen.

**How it works:**
- Analyzes your watch history to understand your preferences
- Scores unwatched content based on multiple factors
- Returns up to 30 recommended items sorted by relevance

**Recommendation Algorithm:**

The `RecommendationEngine` uses a weighted scoring system:

1. **Genre Matching (2.0x weight)**
   - Extracts genres from your watched content
   - Matches against unwatched content's genre field
   - Recent watches weighted more heavily (see Recency Weighting)

2. **Category Preference (1.5x weight)**
   - Tracks whether you prefer Movies or Series
   - Scores content in your preferred category higher

3. **Content Quality (0.5x rating value)**
   - Bonus points for highly-rated content (rating >= 7.0)
   - Uses the `rating` field from Xtream API

4. **Recent Releases (2.0 bonus)**
   - Bonus for content released in the last 3 years
   - Uses the `releaseDate` field

**Recency Weighting:**
- Last 24 hours: 5.0x weight
- Last 7 days: 3.0x weight
- Last 30 days: 1.5x weight
- Older than 30 days: 1.0x weight

This ensures your most recent viewing habits have the most influence on recommendations.

## User Interface

The browse screen now shows content in the following order:

1. **Recently Watched** - Your viewing history
2. **Latest Added** ⭐ NEW - 30 most recent additions
3. **Live TV** - All live channels
4. **Movies** - All movie content
5. **Series** ⭐ NEW - All series content
6. **Live TV Categories** - Grouped by category
7. **Recommended for You** ⭐ NEW - Personalized suggestions

## Technical Details

### Data Models

**Channel Model Updates:**
```kotlin
data class Channel(
    // ... existing fields ...
    val added: String? = null,        // NEW: Unix timestamp when added
    val seriesId: Int? = null         // NEW: Series ID for episode loading
)
```

**New Series Models:**
- `XtreamSeries` - Series list item
- `XtreamSeriesInfo` - Detailed series information
- `SeriesSeason` - Season metadata
- `SeriesEpisode` - Episode data

### API Endpoints Added

```kotlin
// Get all series
suspend fun getSeries(username, password): List<XtreamSeries>

// Get series by category
suspend fun getSeriesByCategory(username, password, categoryId): List<XtreamSeries>

// Get detailed series info with episodes
suspend fun getSeriesInfo(username, password, seriesId): XtreamSeriesInfo
```

### Repository Methods

```kotlin
// Get 30 most recently added movies/series
fun getLatestAddedContent(allChannels: List<Channel>, limit: Int = 30): List<Channel>

// Get personalized recommendations
fun getRecommendations(allChannels: List<Channel>, maxRecommendations: Int = 30): List<Channel>
```

## Configuration

**Maximum Recently Watched Items:**
The system tracks up to 50 recently watched items (configurable in `CacheManager.kt`):
```kotlin
private const val MAX_RECENTLY_WATCHED = 50
```

**Recommendation Limits:**
Default: 30 items per section (configurable when calling the methods)

## Testing Recommendations

To verify the recommendation system is working:

1. **Watch diverse content** - Watch 5-10 items from different genres
2. **Check Latest Added** - Should show newest content from your playlist
3. **Check Recommendations** - Should suggest content similar to what you watched
4. **Test recency bias** - Watch content from one genre today, different yesterday
   - Today's genre should dominate recommendations

## Future Enhancements

Potential improvements for the recommendation system:

- [ ] Collaborative filtering (learn from similar users)
- [ ] A/B testing different scoring weights
- [ ] Explicit user feedback (like/dislike buttons)
- [ ] Trending content (what others are watching)
- [ ] "Because you watched X" sections
- [ ] Season-aware series recommendations
- [ ] Watch time tracking (partial vs complete views)
- [ ] Time-of-day patterns (recommend action at night, kids shows in morning)

## Troubleshooting

**No "Latest Added" showing:**
- Verify your Xtream API provides the `added` field for VOD/Series
- Check that movies/series exist in your playlist
- Ensure cache is up to date (Menu → Refresh Channels)

**Poor recommendations:**
- Need at least 1 watched item for recommendations to appear
- Watch more diverse content to improve accuracy
- Genre field must be populated in Xtream API data
- Clear cache if recommendations seem stale (Menu → Clear Cache)

**Series not appearing:**
- Verify Xtream account has series content
- Check API response includes series data
- Ensure `getSeries()` endpoint is accessible

## Privacy & Data

- All watch history is stored locally on your device
- No data is sent to external servers
- Recommendation calculations happen entirely on-device
- Clear cache to reset all watch history and recommendations
