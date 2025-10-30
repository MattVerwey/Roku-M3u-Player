# UI Design Documentation

## Overview

The M3U Player features a modern, sleek design optimized for Amazon Fire TV and Android TV devices. The interface follows Android TV Leanback design guidelines, providing an intuitive and visually appealing experience.

## Design Principles

### 1. TV-First Design
- **10-foot UI**: All elements are sized and positioned for viewing from across the room
- **Large text**: Readable from typical TV viewing distances (6-12 feet)
- **High contrast**: Dark theme with bright text for optimal visibility
- **Focus indicators**: Clear visual feedback for remote control navigation

### 2. Modern and Sleek
- **Card-based layout**: Contemporary design with poster grid views
- **Visual hierarchy**: Clear organization of content categories
- **Smooth animations**: Fluid transitions between screens
- **Minimalist approach**: Clean interface without clutter

### 3. Remote Control Optimized
- **D-pad navigation**: All features accessible via directional pad
- **Minimal clicks**: Quick access to common actions
- **Contextual controls**: Appropriate buttons for each screen
- **Menu button support**: Quick access to additional options

## Screen Designs

### 1. Login Screen

**Purpose**: Initial authentication for M3U or Xtream services

**Design Elements**:
- Clean, centered layout
- Two login method options:
  - M3U URL input
  - Xtream Codes (Server, Username, Password)
- Large, accessible input fields
- Clear labels and placeholders
- Login button with focus state
- Error messaging area

**Color Scheme**:
- Background: Dark (#121212)
- Primary button: Blue (#1E88E5)
- Text: White/Light gray
- Error text: Red/Orange

**Navigation**:
- D-pad: Navigate between fields
- Select: Activate field/button
- Back: N/A (initial screen)

### 2. Main Browse Screen

**Purpose**: Primary navigation hub for all content

**Layout Structure**:
```
┌─────────────────────────────────────────────┐
│  M3U Player                    [Settings]   │
├─────────────────────────────────────────────┤
│                                             │
│  Recently Watched                        ▼  │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐      │
│  │ Logo │ │ Logo │ │ Logo │ │ Logo │  ... │
│  │      │ │      │ │      │ │      │      │
│  └──────┘ └──────┘ └──────┘ └──────┘      │
│                                             │
│  Live TV                                 ▼  │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐      │
│  │ Logo │ │ Logo │ │ Logo │ │ Logo │  ... │
│  │      │ │      │ │      │ │      │      │
│  └──────┘ └──────┘ └──────┘ └──────┘      │
│                                             │
│  Movies                                  ▼  │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐      │
│  │Poster│ │Poster│ │Poster│ │Poster│  ... │
│  │      │ │      │ │      │ │      │      │
│  └──────┘ └──────┘ └──────┘ └──────┘      │
│                                             │
└─────────────────────────────────────────────┘
```

**Design Elements**:
- **Title Bar**: App name on left
- **Category Rows**: Horizontal scrolling rows
- **Channel Cards**: 
  - 180x180dp (HD) or 240x240dp (poster)
  - Rounded corners (8dp)
  - Drop shadow for depth
  - Logo/poster image with fallback placeholder
  - Channel name below (if space allows)
- **Focus State**: 
  - Scale up slightly (1.1x)
  - Bright border highlight
  - Elevation increase
- **Loading State**: Progress spinner overlay

**Categories (in order)**:
1. Recently Watched (if available)
2. Live TV
3. Movies
4. Series
5. Custom categories (from M3U group-title)

**Color Scheme**:
- Background: Very dark gray (#0F0F0F)
- Card background: Dark gray (#1E1E1E)
- Selected card: Blue highlight (#1E88E5)
- Text: White (#FFFFFF)
- Secondary text: Light gray (#B0B0B0)

**Navigation**:
- **D-pad Up/Down**: Switch between category rows
- **D-pad Left/Right**: Scroll within category
- **Select**: Play (Live TV) or show details (VOD)
- **Menu button**: Open options menu
  - Refresh Channels
  - Clear Cache
  - Logout
- **Back**: Exit app (confirmation dialog)

### 3. Channel Details Screen

**Purpose**: Display metadata for VOD content before playback

**Layout Structure**:
```
┌─────────────────────────────────────────────┐
│                                             │
│  ┌─────────────┐  Channel Name              │
│  │             │                            │
│  │   Poster    │  Category: Movies          │
│  │   Image     │  Duration: 1h 45m          │
│  │             │  Rating: 8.5/10            │
│  │             │                            │
│  └─────────────┘  [Watch Now]  [Back]       │
│                                             │
│  Description:                               │
│  Lorem ipsum dolor sit amet, consectetur    │
│  adipiscing elit...                         │
│                                             │
└─────────────────────────────────────────────┘
```

**Design Elements**:
- **Large Poster**: 300x450dp on left
- **Metadata Section**: Right of poster
  - Channel name (large, bold)
  - Category tag
  - Duration (if available)
  - Rating (if available)
  - Genre tags
- **Action Buttons**:
  - "Watch Now" (primary, blue)
  - Additional actions if needed
- **Description**: Full-width below poster area
- **Background**: Blurred backdrop (if available) or dark gradient

**Color Scheme**:
- Background: Dark gradient or blurred backdrop
- Text: White with shadows for readability
- Primary button: Blue (#1E88E5)
- Secondary button: Gray (#424242)

**Navigation**:
- **D-pad**: Navigate between buttons
- **Select**: Activate button
- **Back**: Return to browse

### 4. Playback Screen

**Purpose**: Full-screen video playback with minimal UI

**Layout Structure (UI visible)**:
```
┌─────────────────────────────────────────────┐
│  Channel Name          ┌────────────────┐   │
│                        │                │   │
│                        │                │   │
│                        │     VIDEO      │   │
│                        │                │   │
│                        │                │   │
│                        └────────────────┘   │
│                                             │
│  [Play/Pause]  ●──────────────○  [1:23:45] │
└─────────────────────────────────────────────┘
```

**UI States**:

**1. Initial State (0-3 seconds)**:
- Channel name displays in top-left corner
- Semi-transparent black background (#AA000000)
- Large, bold white text
- Auto-hides after 3 seconds

**2. Playing State (UI hidden)**:
- Full-screen video
- No visible controls
- Status bar hidden
- Navigation bar hidden
- Immersive mode

**3. Controls Visible (on remote interaction)**:
- Playback controls at bottom
  - Play/Pause button
  - Progress bar
  - Current time / Duration
- Channel info at top
- Semi-transparent overlays
- Auto-hide after 5 seconds of inactivity

**4. Buffering State**:
- Video continues in background
- Circular progress indicator in center
- Transparent backdrop
- "Buffering..." text (optional)

**Design Elements**:
- **Video View**: Full-screen, aspect ratio preserved (fit mode)
- **Progress Bar**: Thin, blue (#1E88E5) with gray background
- **Buffering Indicator**: Material design circular spinner
- **Controls**: Material design icons
- **Timestamps**: White text with shadow

**Color Scheme**:
- Background: Black (#000000)
- Controls background: Semi-transparent black (#CC000000)
- Primary accent: Blue (#1E88E5)
- Text: White (#FFFFFF)

**Navigation**:
- **Play/Pause**: Toggle playback
- **Home button**: Enter PIP mode
- **Back**: Exit playback, return to browse
- **Menu button**: Show playback options (future)

### 5. Picture-in-Picture (PIP) Mode

**Purpose**: Watch video in small window while browsing

**Design**:
```
┌─────────────────────────────────────────────┐
│  M3U Player                                 │
├─────────────────────────────────────────────┤
│                            ┌──────────────┐ │
│  Recently Watched          │              │ │
│  ┌──────┐ ┌──────┐        │     PIP      │ │
│  │ Logo │ │ Logo │        │    Video     │ │
│  │      │ │      │        │              │ │
│  └──────┘ └──────┘        └──────────────┘ │
│                                             │
│  Live TV                                    │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐      │
│  │ Logo │ │ Logo │ │ Logo │ │ Logo │      │
└─────────────────────────────────────────────┘
```

**Design Elements**:
- **PIP Window**: 
  - Size: ~20-30% of screen width
  - Position: Top-right or bottom-right corner
  - Aspect ratio: 16:9
  - Border: Thin white border (1-2dp)
  - Drop shadow for elevation
- **Background**: Browse screen remains fully functional
- **Controls**: Minimal, hidden by default

**Behavior**:
- Video continues playing in corner
- Can browse and select new channels
- Selecting new channel switches PIP stream
- Press Back to return to fullscreen
- Press Home to minimize app (PIP continues)

**Color Scheme**:
- PIP border: White (#FFFFFF) or blue (#1E88E5)
- Drop shadow: Black with alpha

### 6. Options Menu (Menu Button)

**Purpose**: Quick access to app settings and actions

**Design**:
```
┌────────────────────┐
│  Options           │
├────────────────────┤
│  Refresh Channels  │
│  Clear Cache       │
│  Logout            │
├────────────────────┤
│  Cancel            │
└────────────────────┘
```

**Design Elements**:
- **Dialog Style**: Material AlertDialog
- **Size**: Centered, ~30% of screen width
- **List Items**: Large, touch/remote friendly
- **Focus State**: Blue highlight
- **Icons**: Optional icons for each action

**Color Scheme**:
- Background: Dark gray (#303030)
- Text: White (#FFFFFF)
- Selected: Blue (#1E88E5)
- Divider: Gray (#424242)

## Typography

### Font Family
- **Primary**: Roboto (Android system default)
- **Fallback**: Sans-serif

### Text Sizes
- **App Title**: 32sp
- **Category Headers**: 24sp
- **Channel Names**: 16sp
- **Body Text**: 14sp
- **Small Text**: 12sp
- **Button Text**: 18sp

### Font Weights
- **Title**: Bold (700)
- **Headers**: Medium (500)
- **Body**: Regular (400)

## Color Palette

### Primary Colors
```
Primary Blue:     #1E88E5 (Material Blue 600)
Primary Dark:     #1565C0 (Material Blue 800)
Accent Orange:    #FF5722 (Material Deep Orange 500)
```

### Background Colors
```
Background Dark:       #0F0F0F (Almost black)
Surface Dark:          #1E1E1E (Dark gray)
Card Background:       #2C2C2C (Medium dark gray)
Overlay Background:    #AA000000 (Black 66% alpha)
```

### Text Colors
```
Primary Text:      #FFFFFF (White)
Secondary Text:    #B0B0B0 (Light gray)
Disabled Text:     #757575 (Medium gray)
Error Text:        #FF5252 (Red)
Success Text:      #4CAF50 (Green)
```

### State Colors
```
Focus/Selected:    #1E88E5 (Primary blue)
Hover:            #424242 (Dark gray)
Pressed:          #303030 (Darker gray)
Disabled:         #1E1E1E (Background surface)
```

## Spacing and Layout

### Margins and Padding
```
Extra Small:   4dp
Small:         8dp
Medium:        16dp
Large:         24dp
Extra Large:   32dp
```

### Grid System
- **Card Width**: 180dp (standard) or 240dp (posters)
- **Card Height**: 180dp (standard) or 360dp (posters)
- **Card Spacing**: 12dp horizontal, 16dp vertical
- **Row Height**: Auto-fit content + 64dp vertical padding
- **Screen Margins**: 48dp horizontal (TV safe zone)

## Animations and Transitions

### Durations
- **Fast**: 150ms (hover, focus changes)
- **Normal**: 300ms (page transitions, dialog appear)
- **Slow**: 500ms (large content loads)

### Animation Types
- **Focus Scale**: Scale from 1.0 to 1.1 (150ms ease-out)
- **Screen Transition**: Fade + slide (300ms)
- **Dialog Appear**: Fade + scale (300ms)
- **Loading**: Continuous rotation (1000ms linear)

### Easing
- **Ease Out**: For appearing elements
- **Ease In**: For disappearing elements
- **Linear**: For continuous animations (loading spinners)

## Accessibility

### Focus Management
- Clear focus indicators (blue border, scale up)
- Logical focus order (left-to-right, top-to-bottom)
- Focus memory (returns to last focused item)
- Focus wrapping within rows

### Contrast Ratios
- Normal text: Minimum 4.5:1
- Large text: Minimum 3:1
- UI components: Minimum 3:1

### Touch Targets
- Minimum size: 48x48dp
- Recommended: 64x64dp for TV remotes
- Spacing: Minimum 8dp between targets

## Responsive Design

### Screen Sizes Supported
- **720p (HD)**: 1280x720 - Minimum supported
- **1080p (Full HD)**: 1920x1080 - Optimized for
- **4K (UHD)**: 3840x2160 - Supported with scaling

### Density Support
- **MDPI**: 1x (not common for TV)
- **HDPI**: 1.5x (720p)
- **XHDPI**: 2x (1080p)
- **XXHDPI**: 3x (4K)

### Layout Adaptations
- Card sizes scale with screen density
- Text sizes use sp units (scales with system settings)
- Margins maintain proportions
- Safe zones respected on all screen sizes

## Design Assets

### Required Images
- **App Icon**: 512x512px (PNG)
- **App Banner**: 1280x720px (PNG) - Fire TV home screen
- **Channel Placeholders**: 180x180px (SVG/PNG)
- **Poster Placeholders**: 240x360px (SVG/PNG)

### Image Loading
- **Coil Library**: Handles loading and caching
- **Placeholders**: Show during load
- **Error Images**: Display on load failure
- **Fade-in Animation**: 300ms when image loads

## Performance Considerations

### UI Responsiveness
- Target: 60 FPS (16ms frame time)
- Card rendering: Hardware accelerated
- Image loading: Background threads
- List scrolling: RecyclerView with ViewHolder pattern

### Memory Management
- Image cache: Limited to reasonable size
- Release resources on background
- Efficient bitmap handling
- Lazy loading of off-screen content

## Future Enhancements

### Planned UI Improvements
- [ ] EPG (Electronic Program Guide) integration
- [ ] Search screen with keyboard
- [ ] Favorites management screen
- [ ] Settings screen with preferences
- [ ] Detailed playback controls (quality selector)
- [ ] Series episode selector
- [ ] Parental control PIN screen

### Potential Design Updates
- [ ] Customizable themes (light/dark/custom colors)
- [ ] Different card layout options
- [ ] Grid view vs. list view toggle
- [ ] Custom backdrop images
- [ ] Animated backgrounds

## Conclusion

The M3U Player UI is designed with a focus on:
- **Modern aesthetics** - Clean, contemporary design
- **TV optimization** - Perfect for 10-foot interface
- **Usability** - Intuitive navigation with Fire TV remote
- **Performance** - Smooth, responsive interactions
- **Accessibility** - Clear focus, high contrast, logical layout

The design follows Android TV best practices while maintaining a unique, polished appearance suitable for a professional IPTV player application.
