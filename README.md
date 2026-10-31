# Study Focus — CP3406/CP5307 Assessment 1

A small Kotlin Android utility app built from the supplied Jetpack Compose starter.
Its primary purpose is to provide an easy-to-read study session timer.

## Features

- Focus screen: countdown timer, Start/Pause/Reset controls and completed session count.
- Settings screen: 15-, 25- and 45-minute sessions, plus show/hide quote preference.
- Study quote: fetch a random quote from DummyJSON using Retrofit.
- Handles unavailable network by displaying a friendly error without crashing.

## Technologies

- Kotlin, Jetpack Compose and Material Design 3
- ViewModel + `StateFlow` for state and lifecycle-aware collection
- Repository pattern (`QuoteRepository`) and manual constructor injection
- Retrofit + Gson, public `https://dummyjson.com/quotes/random` API
- JUnit unit test for the timer formatting helper

## Project structure

```text
app/src/main/java/au/edu/jcu/assessment/utilityapp/
├── MainActivity.kt              # Creates repository + ViewModel
├── data/
│   ├── QuoteService.kt           # Retrofit endpoint + response
│   └── QuoteRepository.kt        # Data access wrapper
├── viewmodel/
│   └── FocusViewModel.kt         # Timer logic and UI state
└── ui/screens/
    └── FocusApp.kt              # Compose UI and 2-tab navigation
```

## How to run

1. Open the project folder in Android Studio.
2. Sync Gradle; allow Android Studio to install any missing SDK components.
3. Choose an Android emulator or connect an Android phone with USB debugging.
4. Run the `app` configuration.
5. Open Settings to select a duration, then Focus to start the timer.

## Testing

- Start, pause and reset the timer.
- Change session duration and confirm the timer resets.
- Turn quotes on/off and refresh the quote.
- Disable internet and confirm an error message appears without a crash.
- Run `FormatTimeTest` from Android Studio.

## Current limitations

- Settings and completed sessions are not saved between app launches.
- Countdown is updated with coroutine delays while the process is alive; it is not an alarm that continues reliably when the process is killed.
- Quotes need network access.

## Author and academic integrity

Name: Song Khanh Hong 
Course: CP3406-SIN-CAM-TR3 Mobile Computing

## Source

- DummyJSON quotes API: https://dummyjson.com/docs/quotes
