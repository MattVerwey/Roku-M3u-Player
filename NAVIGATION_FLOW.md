# Navigation Flow Diagram

## Application Navigation Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                         Login Screen                             │
│                    (First Time Only)                            │
│                                                                  │
│  [M3U URL Input]  or  [Xtream Codes Credentials]              │
│                                                                  │
│                     [Login Button]                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                         HOME PAGE                                │
│                    (BrowseFragment)                             │
│                                                                  │
│  ┌──────────────┐  ┌─────────────────────────────────────────┐│
│  │   SIDEBAR    │  │         CONTENT AREA                    ││
│  │              │  │                                         ││
│  │ Recently     │  │  [Row: Recently Watched (up to 20)]    ││
│  │  Watched     │  │  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐  ││
│  │              │  │  │IMG │ │IMG │ │IMG │ │IMG │ │IMG │  ││
│  │ Recommended  │  │  └────┘ └────┘ └────┘ └────┘ └────┘  ││
│  │  For You     │  │                                         ││
│  │              │  │  [Row: Recommended For You (up to 20)] ││
│  │ Latest Added │  │  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐  ││
│  │              │  │  │IMG │ │IMG │ │IMG │ │IMG │ │IMG │  ││
│  │ Browse by    │  │  └────┘ └────┘ └────┘ └────┘ └────┘  ││
│  │  Category    │  │                                         ││
│  │              │  │  [Row: Latest Added (up to 20)]        ││
│  │ Settings     │  │  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐  ││
│  │              │  │  │IMG │ │IMG │ │IMG │ │IMG │ │IMG │  ││
│  └──────────────┘  │  └────┘ └────┘ └────┘ └────┘ └────┘  ││
│                    │                                         ││
│                    │  [Row: Browse by Category]             ││
│                    │  ┌────────┐ ┌────────┐ ┌────────┐    ││
│                    │  │  📺   │ │  🎬   │ │  📺   │    ││
│                    │  │Live TV │ │Movies │ │Series │    ││
│                    │  └────────┘ └────────┘ └────────┘    ││
│                    └─────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
           │                    │                    │
           │ Select             │ Select             │ Select
           │ Live TV            │ Movies             │ Series
           ▼                    ▼                    ▼
┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│  LIVE TV PAGE    │  │   MOVIES PAGE    │  │   SERIES PAGE    │
│(CategoryBrowse)  │  │(CategoryBrowse)  │  │(CategoryBrowse)  │
│                  │  │                  │  │                  │
│ [Category: Sports]│  │[Category: Action]│  │[Category: Drama] │
│  📺 Chan 1       │  │  🎬 Movie A      │  │  📺 Series A    │
│  📺 Chan 2       │  │  🎬 Movie B      │  │  📺 Series B    │
│  📺 Chan 5       │  │  🎬 Movie C      │  │  📺 Series C    │
│                  │  │                  │  │                  │
│ [Category: News] │  │[Category: Comedy]│  │[Category: Sci-Fi]│
│  📺 Chan 3       │  │  🎬 Movie D      │  │  📺 Series D    │
│  📺 Chan 7       │  │  🎬 Movie E      │  │  📺 Series E    │
│                  │  │  🎬 Movie F      │  │  📺 Series F    │
│                  │  │                  │  │                  │
│ (Sorted by       │  │ (Sorted          │  │ (Sorted          │
│  channel #)      │  │  alphabetically) │  │  alphabetically) │
└──────────────────┘  └──────────────────┘  └──────────────────┘
           │                    │                    │
           │ Select             │ Select             │ Select
           │ Channel            │ Movie              │ Series
           ▼                    ▼                    ▼
    ┌─────────────┐      ┌──────────────┐    ┌──────────────┐
    │  PLAYBACK   │      │   DETAILS    │    │   DETAILS    │
    │   SCREEN    │      │    SCREEN    │    │    SCREEN    │
    │             │      │              │    │              │
    │  [Video]    │      │  [Poster]    │    │  [Poster]    │
    │             │      │              │    │              │
    │  [Controls] │      │  Title       │    │  Title       │
    │             │      │  Rating: ★8.5│    │  Rating: ★9.0│
    │  - Play     │      │              │    │              │
    │  - Pause    │      │  Description │    │  Description │
    │  - Subtitle │      │  ...         │    │  ...         │
    │  - Audio    │      │              │    │              │
    │  - Quality  │      │  Genre: ...  │    │  Genre: ...  │
    │  - TV Guide │      │  Duration: 2h│    │  Episodes: 10│
    │             │      │              │    │              │
    │             │      │ [Watch Now]  │    │ [Watch Now]  │
    │             │      │ [Favorite]   │    │ [Favorite]   │
    └─────────────┘      └──────────────┘    └──────────────┘
                                │                    │
                                │ Select             │ Select
                                │ Watch Now          │ Watch Now
                                ▼                    ▼
                         ┌──────────────┐    ┌──────────────┐
                         │  PLAYBACK    │    │  PLAYBACK    │
                         │   SCREEN     │    │   SCREEN     │
                         │              │    │              │
                         │  [Video]     │    │  [Video]     │
                         │              │    │              │
                         │  [Controls]  │    │  [Controls]  │
                         │              │    │  [Next Ep]   │
                         └──────────────┘    └──────────────┘
```

## Navigation Patterns

### Home Page Navigation

**D-Pad Controls:**
- **Right**: Move from sidebar to content area
- **Left**: Move from content area to sidebar
- **Up/Down**: Navigate between rows in content area
- **Up/Down** (in sidebar): Navigate between menu items
- **Select**: Play content (Live TV) or show details (Movies/Series)
- **Back**: Hide sidebar (if showing) or exit app

**Sidebar Menu Items:**
1. **Recently Watched**: Shows recent content (when selected, content shows in main area)
2. **Recommended For You**: Shows recommendations (when selected, content shows in main area)
3. **Latest Added**: Shows newest content (when selected, content shows in main area)
4. **Browse by Category**: Shows category cards (when selected, content shows in main area)
5. **Settings**: Opens settings dialog

### Category Page Navigation

**D-Pad Controls:**
- **Right**: Move from category list (sidebar) to content
- **Left**: Move from content to category list
- **Up/Down**: Navigate between categories or content items
- **Select**: Play (Live TV) or show details (Movies/Series)
- **Back**: Return to home page

**Content Organization:**
- Categories displayed as sidebar items
- Each category shows filtered and sorted content
- Live TV: Sorted by channel number
- Movies/Series: Sorted alphabetically

### Details Page Navigation

**D-Pad Controls:**
- **Up/Down**: Navigate between action buttons
- **Select**: Execute action (Watch Now, Add to Favorites)
- **Back**: Return to previous page

**Displayed Information:**
- Poster/Logo image
- Title
- Subtitle: Category • Release Date • Rating
- Description
- Genre
- Duration (movies) or Episode info (series)
- Action buttons

### Playback Screen Navigation

**D-Pad Controls:**
- **Select/Menu**: Show/hide controls
- **Play/Pause**: Toggle playback
- **Left/Right**: Rewind/Fast forward 10 seconds
- **Up/Down**: Navigate control buttons
- **Back**: Return to previous screen (Live TV) or Details (Movies/Series)

**Controls Available:**
- Play/Pause
- Rewind (-10s)
- Fast Forward (+10s)
- Subtitles toggle
- Audio track selection
- Video quality selection
- TV Guide (Live TV only)
- Play Next Episode (Series only)

## Content Flow Examples

### Example 1: Watching a Live TV Channel
```
Home → Browse by Category → Live TV → Sports → Channel 5 → Playback
```

### Example 2: Watching a Movie
```
Home → Browse by Category → Movies → Action → Movie Title → Details → Watch Now → Playback
```

### Example 3: Watching from Recently Watched
```
Home → Recently Watched Row → Select Item → Details/Playback
```

### Example 4: Discovering Recommended Content
```
Home → Recommended For You Row → Select Item → Details → Watch Now → Playback
```

### Example 5: Settings Access
```
Home → Settings (in sidebar) → Options Dialog → Select Option
```

## Back Button Behavior

### From Home Page
- First press: Hide sidebar (if visible)
- Second press: Exit app confirmation

### From Category Page
- Press: Return to Home page

### From Details Page
- Press: Return to previous page (Home or Category page)

### From Playback Screen
- Press: Return to previous page (Details or Home)
- Note: Video stops playing

### From Settings Dialog
- Press: Close dialog, return to Home page

## Content Update Behavior

### After Playback
1. User finishes watching content
2. User presses Back
3. Returns to previous screen (Details or Home)
4. Home page automatically refreshes
5. Recently Watched row updates with new content
6. Recommendations may update based on new watch history

### After Settings Changes
1. User changes settings (clear cache, etc.)
2. User closes settings
3. Returns to Home page
4. Relevant data refreshed if needed

## Special Navigation Features

### Picture-in-Picture (PIP)
- **Trigger**: Press Home button during playback
- **Behavior**: Video continues in small window
- **Navigation**: Browse content while video plays
- **Exit**: Select new content or press Back

### Headers (Sidebar) Transition
- **Automatic**: Sidebar hides when navigating to content
- **Manual**: Use Left D-Pad to show sidebar
- **State**: Sidebar state persists during session
- **Focus**: Focus returns to last selected item when showing sidebar
