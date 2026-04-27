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

### 1. Get your Telegram API credentials

The app authenticates each user as a regular Telegram user (not a bot).
You need an **API ID** and **API Hash** for the application itself, obtained
from <https://my.telegram.org/apps>.

Add them to `local.properties`:

```properties
TD_API_ID=123456
TD_API_HASH=abcdef0123456789abcdef0123456789
```

### 2. Provide the TDLib native library

The repo ships with a **stub** `org.drinkless.tdlib.Client` /
`org.drinkless.tdlib.TdApi` so the project compiles out of the box. With the
stub, all TDLib calls return an `Error`, which means **the app builds but does
not actually talk to Telegram** until the real library is dropped in.

To enable real connectivity:

1. Build TDLib for Android (or grab a pre-built distribution). The official
   instructions live at
   <https://github.com/tdlib/td/blob/master/example/android/README.md>.
2. After the build, you'll have:
   - `TdApi.java` and `Client.java` (auto-generated Java bindings)
   - `libtdjni.so` for each ABI: `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`
3. Run the helper script:
   ```sh
   scripts/setup-tdlib.sh /path/to/tdlib/build
   ```
   It replaces the stub Java sources and copies the `.so` files into
   `app/src/main/jniLibs/<abi>/`.

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
