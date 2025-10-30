# Recommendation Algorithm Example

This document provides a concrete example of how the recommendation algorithm works.

## Example Scenario

### User's Watch History (Last 7 Days)

| Channel | Category | Genre | Days Ago | Recency Weight |
|---------|----------|-------|----------|----------------|
| Breaking Bad S01E01 | Series | Drama, Crime, Thriller | 0.5 | 5.0x |
| The Godfather | Movie | Crime, Drama | 2 | 3.0x |
| Ozark S01E01 | Series | Crime, Drama | 3 | 3.0x |
| Pulp Fiction | Movie | Crime, Drama | 10 | 1.5x |
| Goodfellas | Movie | Crime, Drama | 25 | 1.5x |

### Calculated Preferences

**Genre Weights:**
- Crime: 5.0 + 3.0 + 3.0 + 1.5 + 1.5 = **14.0**
- Drama: 5.0 + 3.0 + 3.0 + 1.5 + 1.5 = **14.0**
- Thriller: 5.0 = **5.0**

**Category Weights:**
- Series: 5.0 + 3.0 = **8.0**
- Movie: 3.0 + 1.5 + 1.5 = **6.0**

### Unwatched Content to Score

#### Option 1: Better Call Saul S01E01
- **Category:** Series
- **Genre:** Crime, Drama
- **Rating:** 8.9
- **Release Year:** 2015

**Score Calculation:**
```
Genre Score:
  Crime match: 14.0 * 2.0 = 28.0
  Drama match: 14.0 * 2.0 = 28.0
  
Category Score:
  Series match: 8.0 * 1.5 = 12.0
  
Rating Bonus:
  8.9 >= 7.0: 8.9 * 0.5 = 4.45
  
Total: 28.0 + 28.0 + 12.0 + 4.45 = 72.45
```

#### Option 2: The Big Bang Theory S01E01
- **Category:** Series
- **Genre:** Comedy
- **Rating:** 8.1
- **Release Year:** 2007

**Score Calculation:**
```
Genre Score:
  Comedy match: 0.0 * 2.0 = 0.0
  
Category Score:
  Series match: 8.0 * 1.5 = 12.0
  
Rating Bonus:
  8.1 >= 7.0: 8.1 * 0.5 = 4.05
  
Total: 0.0 + 12.0 + 4.05 = 16.05
```

#### Option 3: Scarface
- **Category:** Movie
- **Genre:** Crime, Drama
- **Rating:** 8.3
- **Release Year:** 1983

**Score Calculation:**
```
Genre Score:
  Crime match: 14.0 * 2.0 = 28.0
  Drama match: 14.0 * 2.0 = 28.0
  
Category Score:
  Movie match: 6.0 * 1.5 = 9.0
  
Rating Bonus:
  8.3 >= 7.0: 8.3 * 0.5 = 4.15
  
Total: 28.0 + 28.0 + 9.0 + 4.15 = 69.15
```

#### Option 4: Extraction 2
- **Category:** Movie
- **Genre:** Action, Thriller
- **Rating:** 7.2
- **Release Year:** 2023

**Score Calculation:**
```
Genre Score:
  Action match: 0.0 * 2.0 = 0.0
  Thriller match: 5.0 * 2.0 = 10.0
  
Category Score:
  Movie match: 6.0 * 1.5 = 9.0
  
Rating Bonus:
  7.2 >= 7.0: 7.2 * 0.5 = 3.6
  
Recent Release Bonus:
  2023 within last 3 years: +2.0
  
Total: 0.0 + 10.0 + 9.0 + 3.6 + 2.0 = 24.6
```

### Final Recommendation Ranking

1. **Better Call Saul S01E01** - Score: 72.45 ⭐ Best Match
2. **Scarface** - Score: 69.15
3. **Extraction 2** - Score: 24.6
4. **The Big Bang Theory S01E01** - Score: 16.05

### Why Better Call Saul Wins

1. **Perfect Genre Match:** Matches user's top two genres (Crime, Drama)
2. **Category Preference:** User slightly prefers Series (8.0) over Movies (6.0)
3. **High Quality:** Excellent rating (8.9) adds bonus points
4. **Recency Effect:** User's recent Crime/Drama watching heavily influences score

### Why Extraction 2 Ranks Low

1. **Weak Genre Match:** Only Thriller matches (5.0 weight), Action doesn't match at all
2. **Partial Category Match:** Movie category, but user prefers Series
3. **Recent Release Bonus:** Helps but not enough to overcome genre mismatch
4. **Conclusion:** System correctly identifies this as not a good fit despite being new

## Real-World Behavior

As the user watches more content, the system adapts:

### After Watching 3 Action Movies

**Updated Weights:**
- Action: 5.0 + 3.0 + 3.0 = 11.0 (NEW)
- Crime: 14.0 (unchanged)
- Drama: 14.0 (unchanged)

**Extraction 2 New Score:**
```
Action match: 11.0 * 2.0 = 22.0 (was 0.0)
Thriller match: 5.0 * 2.0 = 10.0
Movie match: 9.0 + (new Movie watches) = higher
Rating: 3.6
Recent: 2.0

Total: ~46.6 (was 24.6) - Now ranks higher!
```

### After 30 Days Pass

**Recency Decay:**
- Original watches now have 1.0x weight (was 5.0x, 3.0x, 1.5x)
- Genre weights decrease proportionally
- Recent watches dominate recommendations
- System adapts to changing tastes

## Edge Cases

### New User (No Watch History)
- No recommendations shown
- Need at least 1 watch to generate recommendations
- System shows: Recently Watched, Latest Added, but not Recommended

### User Watches Only One Genre
- Recommendations heavily favor that genre
- Other genres get 0 score
- Encourages exploration of similar content

### User Watches Everything
- All genres have similar weights
- System falls back to rating and recency
- Shows highest-rated recent releases

### No Genre Information
- Genre matching skipped (0 score)
- Falls back to category preference, rating, recency
- Still provides useful recommendations

## Testing the Algorithm

To verify it works correctly:

1. **Test 1: Watch 5 Action movies**
   - Expect: Action movie/series recommendations dominate

2. **Test 2: Watch 3 Comedies, 2 Dramas**
   - Expect: Mixed recommendations favoring Comedy

3. **Test 3: Watch only Series**
   - Expect: Series recommendations even if genre matches movies

4. **Test 4: Watch 10 old items, 1 recent Sci-Fi**
   - Expect: Sci-Fi heavily weighted due to recency (5.0x)

5. **Test 5: Clear cache and restart**
   - Expect: No recommendations until you watch something
