# Movie Browser App - Implementation Record

## Project: Android Movie Browser
## Package: com.example.android_project
## API: OMDb API (http://www.omdbapi.com/) Key: 359d64e8

---

## Architecture
- **Pattern**: MVVM (Model-View-ViewModel) with Repository
- **API**: Retrofit + Gson + OkHttp
- **Navigation**: Fragment-based with bottom navigation
- **UI**: XML layouts with ViewBinding

---

## File Structure

### Build Configuration
- `app/build.gradle.kts` - Added Retrofit, Gson, OkHttp, Coroutines, ViewModel dependencies + enabled viewBinding

### Package Structure
```
com.example.android_project/
├── shared_component/
│   ├── API.kt                    - Retrofit singleton builder
│   └── APIService.kt             - API endpoint definitions
├── home/
│   ├── data/
│   │   ├── model/
│   │   │   ├── MovieSearchResponse.kt  - Search response model
│   │   │   └── MovieDetailResponse.kt  - Detail response model
│   │   └── repository/
│   │       └── MovieRepository.kt       - Data repository
│   └── presentation/
│       ├── ui/
│       │   ├── HomeFragment.kt           - Main home screen
│       │   ├── MovieAdapter.kt           - Grid RecyclerView adapter
│       │   └── RandomMovieAdapter.kt     - Horizontal RecyclerView adapter
│       └── viewModel/
│           └── MovieViewModel.kt         - ViewModel + Factory
├── detail/
│   └── presentation/
│       └── ui/
│           └── DetailActivity.kt         - Movie detail screen
├── favourites/
│   └── presentation/
│       └── ui/
│           └── FavouritesFragment.kt     - Empty favourites state
├── watchlist/
│   └── presentation/
│       └── ui/
│           └── WatchlistFragment.kt      - Empty watchlist state
└── MainActivity.kt                       - Host activity with bottom nav
```

### XML Layouts
- `activity_main.xml` - FrameLayout container + bottom navigation bar
- `fragment_home.xml` - ScrollView with featured banner, search, random picks, genre grid
- `fragment_favourites.xml` - Empty state with heart icon
- `fragment_watchlist.xml` - Empty state with bookmark icon
- `activity_detail.xml` - Movie detail with poster, info chips, description
- `item_movie_grid.xml` - Grid card item (poster + IMDb badge + title + info)
- `item_movie_random.xml` - Horizontal card item (poster + IMDb badge + title + info)

### Drawable Resources
- `bg_search_bar.xml` - Rounded search bar background
- `bg_chip.xml` - Rounded chip background
- `bg_chip_imdb.xml` - IMDb badge background (yellow)
- `bg_gradient_bottom.xml` - Gradient overlay for poster
- `ic_search.xml` - Search icon
- `ic_close.xml` - Clear/close icon
- `ic_back.xml` - Back arrow
- `ic_heart.xml` - Heart icon (favourites)
- `ic_bookmark.xml` - Bookmark icon (watchlist)
- `ic_home.xml` - Home icon

### Resource Files
- `colors.xml` - Dark theme colors (bg_main, accent_yellow, accent_purple, etc.)
- `themes.xml` - Material3 NoActionBar theme with dark status bar
- `AndroidManifest.xml` - INTERNET permission, MainActivity + DetailActivity

---

## Implementation Steps (Chronological)

### Step 1: Build Configuration
- Added Retrofit 2.9.0, Gson converter, OkHttp, logging interceptor
- Added lifecycle-viewmodel-ktx, lifecycle-runtime-ktx, kotlinx-coroutines-android
- Enabled viewBinding in buildFeatures

### Step 2: API Layer
- Created `API.kt` with Retrofit singleton (base URL: http://www.omdbapi.com/)
- Created `APIService.kt` with two endpoints:
  - `searchMovies(query)` - GET ?s={query}&apikey=359d64e8
  - `getMovieDetail(imdbId)` - GET ?i={imdbId}&apikey=359d64e8

### Step 3: Data Models
- `MovieSearchResponse` - Contains Search array with MovieSearchItem (title, year, imdbId, type, poster)
- `MovieDetailResponse` - Full movie details (title, year, rated, released, runtime, genre, director, actors, plot, poster, imdbRating, etc.)

### Step 4: Repository
- `MovieRepository` - Wraps API calls with Result<T> for error handling
  - `searchMovies(query)` - Returns Result<MovieSearchResponse>
  - `getMovieDetail(imdbId)` - Returns Result<MovieDetailResponse>

### Step 5: ViewModel
- `MovieViewModel` - Manages UI state with LiveData
  - `searchResults` - Search results list
  - `featuredMovie` - Featured movie (War Horse - tt1568913)
  - `randomPicks` - Random movies from multiple queries
  - `horrorMovies`, `sciFiMovies`, `dramaMovies` - Genre-specific lists
  - `searchMovies(query)` - Live search function
  - Factory pattern with `MovieViewModelFactory` and `MovieModule`

### Step 6: Resources
- Updated colors.xml with dark theme palette
- Updated themes.xml for dark status/navigation bars
- Created drawable resources (icons, shapes, gradients)

### Step 7: XML Layouts
- `activity_main.xml` - ConstraintLayout with FrameLayout container + LinearLayout bottom nav
- `fragment_home.xml` - ScrollView with featured banner, search bar, horizontal RecyclerView, genre chips + grid RecyclerView
- `fragment_favourites.xml` - Centered empty state with heart icon
- `fragment_watchlist.xml` - Centered empty state with bookmark icon
- `activity_detail.xml` - ScrollView with poster, info chips, genre, plot, director, actors, released
- `item_movie_grid.xml` - Card with poster (2:3 ratio), IMDb badge, title, info
- `item_movie_random.xml` - Fixed-width card (160dp) with poster, IMDb badge, title, info

### Step 8: Adapters
- `MovieAdapter` - Grid adapter with updateMovies() method
- `RandomMovieAdapter` - Horizontal adapter with updateMovies() method
- Both use ViewBinding and accept click listener lambda

### Step 9: Fragments
- `HomeFragment` - Sets up:
  - Horizontal RecyclerView (LinearLayoutManager.HORIZONTAL)
  - Grid RecyclerView (GridLayoutManager, 3 columns)
  - Search RecyclerView (GridLayoutManager, 3 columns)
  - TextWatcher for live search (onTextChanged triggers search)
  - Genre chip listener (ChipGroup.setOnCheckedStateChangeListener)
  - Observes ViewModel LiveData to update all adapters
  - Opens DetailActivity on movie click with imdbId

### Step 10: Activities
- `DetailActivity` - Receives imdb_id via Intent, calls API directly via Repository, displays all movie details
- `MainActivity` - Hosts fragments via FrameLayout, handles bottom nav click → fragment transactions + icon color updates

### Step 11: Manifest
- Added INTERNET permission
- Registered DetailActivity

---

## Key Features
1. **Live Search** - TextWatcher.onTextChanged() triggers API call per keystroke
2. **Horizontal RecyclerView** - Random picks section with LinearLayoutManager.HORIZONTAL
3. **Genre Grid** - 3-column GridLayoutManager with ChipGroup for genre selection
4. **Fragment Navigation** - Bottom nav switches between Home, Favourites, Watchlist fragments
5. **Detail Screen** - Full movie info with back navigation

---

## API Endpoints Used
- Search: `GET http://www.omdbapi.com/?s={query}&apikey=359d64e8`
- Detail: `GET http://www.omdbapi.com/?i={imdbId}&apikey=359d64e8`

---

## Dependencies Added
```kotlin
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.9.1")
implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

---

## Notes
- Featured movie is hardcoded as War Horse (tt1568913)
- Random picks load from multiple search queries (batman, avengers, inception, interstellar, joker)
- Favourites and Watchlist are empty placeholder screens (phase 2)
- All UI is XML-based (no Compose)
- Uses course patterns: MVVM, Repository, ViewModelFactory, ViewBinding
