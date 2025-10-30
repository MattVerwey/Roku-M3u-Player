# Home Page UI Layout

## Visual Description of the New Home Page

### Overall Layout

```
┌─────────────────────────────────────────────────────────────────────────┐
│  [App Icon] Home                                              [Time]    │
├──────────────┬──────────────────────────────────────────────────────────┤
│              │                                                           │
│  SIDEBAR     │                   CONTENT AREA                           │
│              │                                                           │
│ [⏱️] Recently │  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐     │
│    Watched   │  │     │ │     │ │     │ │     │ │     │ │     │ ...  │
│              │  │ IMG │ │ IMG │ │ IMG │ │ IMG │ │ IMG │ │ IMG │      │
│ [💡] Recommend│  │     │ │     │ │     │ │     │ │     │ │     │     │
│    For You   │  └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘     │
│              │  Channel  Channel  Channel  Channel  Channel  Channel   │
│ [🆕] Latest   │    1        2        3        4        5        6      │
│    Added     │                                                          │
│              │                                                          │
│ [📺] Live TV  │                                                          │
│              │                                                          │
│ [🎬] Movies   │                                                          │
│              │                                                          │
│ [📺] Series   │                                                          │
│              │                                                          │
│ [⚙️] Settings │                                                          │
│              │                                                          │
└──────────────┴──────────────────────────────────────────────────────────┘
```

## Screen States

### 1. Initial Login - Home Page

**What User Sees:**
- Title: "Home" (top left with app icon)
- Sidebar menu visible on the left (dark background)
- First row: "⏱️ Recently Watched" with horizontal scrolling cards
- Content area shows channel/movie poster cards in a horizontal row
- Focus starts on sidebar menu

**User Flow:**
1. User logs in with M3U URL or Xtream Codes
2. App loads channels in background (progress spinner)
3. Home page appears with all sections populated
4. Focus on first item in sidebar ("Recently Watched")

### 2. Navigating Recently Watched Row

```
┌─────────────────────────────────────────────────────────────────────────┐
│  [App Icon] Home                                                        │
├──────────────┬──────────────────────────────────────────────────────────┤
│              │  ⏱️ Recently Watched                                      │
│  SIDEBAR     │  ┌──────────┐ ┌─────┐ ┌─────┐ ┌─────┐                  │
│              │  │ SELECTED │ │     │ │     │ │     │                   │
│ [⏱️] Recently │  │ CHANNEL  │ │     │ │     │ │     │                  │
│  > Watched   │  │ [FOCUSED]│ │     │ │     │ │     │                  │
│              │  │          │ │     │ │     │ │     │                  │
│ [💡] Recommend│  └──────────┘ └─────┘ └─────┘ └─────┘                  │
│    For You   │  Channel Name                                            │
│              │  Category • Rating                                       │
└──────────────┴──────────────────────────────────────────────────────────┘
```

**User Actions:**
- D-Pad Right: Enter row, focus on first channel
- D-Pad Left/Right: Browse channels horizontally
- Select: Play channel (Live TV) or show details (Movies/Series)
- D-Pad Up/Down: Switch between rows
- Back: Return to sidebar menu

### 3. All Sections Visible When Scrolling Down

```
┌─────────────────────────────────────────────────────────────────────────┐
│  [App Icon] Home                                                        │
├──────────────┬──────────────────────────────────────────────────────────┤
│              │                                                           │
│  SIDEBAR     │  ⏱️ Recently Watched                                      │
│              │  [Channel Cards 1-20]                                    │
│ [⏱️] Recently │                                                           │
│    Watched   │  💡 Recommended For You                                   │
│              │  [Channel Cards based on watch history]                  │
│ [💡] Recommend│                                                           │
│    For You   │  🆕 Latest Added                                          │
│              │  [Newest content 1-20]                                   │
│ [🆕] Latest   │                                                           │
│    Added     │  📺 Live TV                                              │
│              │  [Live channels 1-30]                                    │
│ [📺] Live TV  │                                                           │
│              │  🎬 Movies                                               │
│ [🎬] Movies   │  [Movies 1-30]                                           │
│              │                                                           │
│ [📺] Series   │  📺 Series                                               │
│              │  [Series 1-30]                                           │
│ [⚙️] Settings │                                                           │
│              │                                                           │
└──────────────┴──────────────────────────────────────────────────────────┘
```

### 4. Settings Menu Interaction

```
┌─────────────────────────────────────────────────────────────────────────┐
│  [App Icon] Home                                                        │
├──────────────┬──────────────────────────────────────────────────────────┤
│              │                                                           │
│  SIDEBAR     │         ┌─────────────────────────────┐                 │
│              │         │        Options              │                 │
│ [⏱️] Recently │         │                             │                 │
│    Watched   │         │  Refresh Channels           │                 │
│              │         │  Privacy & Security         │                 │
│ [💡] Recommend│         │      Settings               │                 │
│    For You   │         │                             │                 │
│              │         │         [Cancel]            │                 │
│ [🆕] Latest   │         └─────────────────────────────┘                 │
│    Added     │                                                          │
│              │                                                          │
│ [📺] Live TV  │                                                          │
│              │                                                          │
│ [🎬] Movies   │                                                          │
│              │                                                          │
│ [📺] Series   │                                                          │
│              │                                                          │
│ [⚙️] Settings │  (Settings menu opens automatically when              │
│  > FOCUSED   │   this row is focused in sidebar)                       │
│              │                                                          │
└──────────────┴──────────────────────────────────────────────────────────┘
```

**Settings Options:**
1. Refresh Channels - Clears cache and reloads from source
2. Privacy & Security Settings - Opens SettingsActivity with:
   - Toggle viewing history tracking
   - Clear viewing history
   - Clear cache
   - Logout

### 5. Card Design

Each channel/content card displays:

```
┌────────────┐
│            │
│   POSTER   │
│   IMAGE    │
│            │
│            │
└────────────┘
Channel Name
Category • Rating
```

**Card States:**
- **Normal**: Gray border, normal brightness
- **Focused**: White border, increased brightness, slight zoom
- **Selected**: Plays (Live TV) or shows details (Movies/Series)

### 6. Empty State Handling

**If No Recently Watched:**
- Section is not shown (automatically hidden)
- Next section (Recommendations) appears first

**If No Recommendations:**
- Section is not shown
- Skips to Latest Added

**If New User (no watch history):**
```
Home Page shows:
1. 🆕 Latest Added (if available)
2. 📺 Live TV
3. 🎬 Movies
4. 📺 Series
5. ⚙️ Settings
```

### 7. After Watching Content

**Flow:**
1. User selects and watches a channel
2. User presses Back or content finishes
3. Returns to Home page
4. `onResume()` triggers and refreshes rows
5. Recently Watched now includes the just-watched channel at position 1
6. User sees updated Recently Watched section

## Color Scheme

**Sidebar:**
- Background: #0D0D0D (very dark gray)
- Text: White
- Selected item: White with highlight

**Content Area:**
- Background: Dark gray/black (standard Android TV)
- Card borders (normal): Gray
- Card borders (focused): White
- Text: White

**Icons:**
- Emoji-based icons for easy recognition:
  - ⏱️ Recently Watched
  - 💡 Recommendations
  - 🆕 Latest Added
  - 📺 Live TV / Series
  - 🎬 Movies
  - ⚙️ Settings

## Responsive Behavior

**Number of Cards Visible:**
- Depends on screen size (Fire TV displays)
- Typically 5-6 cards visible at once
- Smooth horizontal scrolling with D-Pad Left/Right
- Cards have consistent sizing across all rows

**Scrolling:**
- Horizontal scrolling within rows
- Vertical scrolling between rows
- Smooth animations (handled by Leanback library)

## Accessibility

**Navigation:**
- All controls accessible via D-Pad
- Clear focus indicators (white borders)
- Logical tab order (top to bottom, left to right)
- Back button always returns to expected location

**Text:**
- Large, readable fonts
- High contrast (white text on dark background)
- Truncated text with ellipsis for long titles

## Performance

**Loading:**
- Progress spinner while loading channels
- Rows populate as data becomes available
- No blocking on slower connections

**Caching:**
- Uses existing cache system
- Fast subsequent loads (instant from cache)
- Background refresh available via Settings menu

## Comparison: Before vs After

### BEFORE:
```
┌─────────────────────────────────────────────────────────────┐
│  M3U Player                                                 │
├──────────────┬──────────────────────────────────────────────┤
│ [📺] Live TV │  [10 sample channels]                        │
│ [🎬] Movies  │                                              │
│ [📺] Series  │                                              │
│ [⚙️] Settings│                                              │
└──────────────┴──────────────────────────────────────────────┘
```
- No Recently Watched
- No Recommendations
- No Latest Added
- Only 10 sample items per category
- Not a true home page

### AFTER:
```
┌─────────────────────────────────────────────────────────────┐
│  [Icon] Home                                                │
├──────────────┬──────────────────────────────────────────────┤
│ [⏱️] Recently│  [20 recently watched items]                 │
│    Watched   │                                              │
│ [💡] Recommend│  [20 personalized recommendations]           │
│    For You   │                                              │
│ [🆕] Latest  │  [20 newest content items]                   │
│    Added     │                                              │
│ [📺] Live TV │  [30 live channels]                          │
│ [🎬] Movies  │  [30 movies]                                 │
│ [📺] Series  │  [30 series]                                 │
│ [⚙️] Settings│  [Auto-opens options menu]                   │
└──────────────┴──────────────────────────────────────────────┘
```
- Complete home page experience
- Personalized content discovery
- More items per category
- Better navigation
- Clear context ("Home" title)

## Future UI Enhancements

Potential improvements for future versions:
1. **Progress Indicators** - Show watch progress on partially watched content
2. **Genre Rows** - Separate rows by genre (Action, Comedy, Drama)
3. **Favorites Button** - Quick add to favorites from home page
4. **Continue Watching** - Resume partially watched content
5. **Search Bar** - Add search functionality to find content quickly
6. **Customization** - Let users reorder or hide sections
7. **More Images** - Show backdrop images for better visual appeal
