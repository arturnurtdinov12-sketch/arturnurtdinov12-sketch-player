# TGPlayer

Lightweight Android music player powered by your Telegram channels and groups.

TGPlayer is **not a messenger** — it has zero messaging functionality. No chats,
no private messages, no photos, no videos. It is a pure audio player that reads
audio files from Telegram channels and groups, and plays them with a Spotify /
Yandex Music-style UX.

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3 (dark graphite Telegram palette)
- **Telegram:** TDLib (official) via JNI
- **Audio engine:** Jetpack Media3 / ExoPlayer
- **Local DB:** Room
- **DI:** Hilt
- **Architecture:** MVVM + Clean Architecture + Repository pattern
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34

## Project structure

```
app/
├── data/
│   ├── local/          # Room DB, DAOs, entities
│   ├── remote/         # TDLib wrapper, Telegram repository
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Track, Channel, Playlist models
│   └── usecase/        # (reserved)
├── presentation/
│   ├── auth/           # Welcome, phone, code, password
│   ├── channels/       # Channel list, channel selection, track list
│   ├── player/         # Mini player, full-screen player
│   ├── playlists/      # Playlists list & detail (drag-drop reorder)
│   └── settings/       # Settings
├── service/
│   └── PlayerService.kt  # Media3 foreground service (lock-screen + notification)
└── di/                 # Hilt modules
```

## First-time setup

### 1. Telegram API credentials

The app needs an `API ID` and `API Hash` to identify itself to Telegram
servers (this is *application*-level identity, separate from each user's
phone-number login). The repo ships with the public TDLib sample credentials
baked into `BuildConfig` so the app works out of the box for development.

For a production / Play Store release you must register your own application
at <https://my.telegram.org/apps> and override the credentials. The build
looks them up in this order:

1. `TD_API_ID` / `TD_API_HASH` in `local.properties`
2. environment variables of the same names
3. the public TDLib sample defaults (development only)

Example `local.properties`:

```properties
TD_API_ID=123456
TD_API_HASH=abcdef0123456789abcdef0123456789
```

### 2. TDLib native libraries

Real TDLib bindings + native `libtdjni.so` for `arm64-v8a`, `armeabi-v7a`,
`x86_64`, and `x86` are committed under `app/src/main/jniLibs/` and
`app/src/main/java/org/drinkless/tdlib/`. The project compiles into a
~107 MB debug APK that connects to Telegram on first launch.

If you need to rebuild TDLib (newer commit, different `OPENSSL` version,
etc.), follow the official instructions at
<https://github.com/tdlib/td/blob/master/example/android/README.md> and run
`scripts/setup-tdlib.sh /path/to/tdlib/build` to refresh the bundled files.

### 3. Build & run

```sh
./gradlew assembleDebug
```

Install on a connected device:

```sh
./gradlew installDebug
```

## Authorization flow

Each user signs in with their own phone number through the Welcome → Phone →
Code → (optional Password) flow. The TDLib session is persisted under the
app's private files directory, so the user is never asked to log in again
after the first time.

## Important rules

- The app NEVER shows private messages, chats, or any text content.
- The app NEVER allows sending content to Telegram.
- Only audio messages (`messageAudio` and audio-mime-type `messageDocument`)
  are loaded from a chat's history.

## License

Proprietary. © Артур.
