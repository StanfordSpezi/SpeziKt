# Storage & permissions

Two areas where translating Swift idioms produces *valid* Kotlin that breaks in production: file/storage access, and permission flows. The platform models genuinely diverge here — preserve semantics, not syntax.

## File access — `FileManager` → `java.io` / `java.nio.file`

Swift:

```swift
let url = FileManager.default
    .urls(for: .documentDirectory, in: .userDomainMask)
    .first!
    .appendingPathComponent("notes.txt")

try "hello".write(to: url, atomically: true, encoding: .utf8)
```

Kotlin (Android) — use `Context.filesDir` + `java.io` or `java.nio.file`:

```kotlin
class NotesStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun write(content: String) {
        val file = File(context.filesDir, "notes.txt")
        file.writeText(content, Charsets.UTF_8)
    }
}
```

Differences that catch translations:

- **Path separators.** Swift's URL handling abstracts the separator; on Android/JVM, prefer `File(parent, name)` or `Path.of(...)` over string concatenation.
- **Sandbox model.** iOS's documents directory is per-app. Android scoped storage further restricts what you can read outside `filesDir` / `cacheDir`. Translating "read user's downloads" needs MediaStore or SAF on Android, not a `FileManager`-equivalent.
- **Atomic writes.** Swift's `atomically: true` writes to a temp file then renames. JVM equivalent is `Files.write(Path, bytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)` followed by `Files.move(tempPath, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)`. Don't drop atomicity silently.
- **iCloud sync semantics.** No Android equivalent. If the Swift code relied on iCloud-backed file sync, the Kotlin port must use a different sync mechanism (Firebase, app-specific cloud SDK).

## Permissions — `Info.plist` + `requestAuthorization` → manifest + runtime

Swift:

```xml
<!-- Info.plist -->
<key>NSCameraUsageDescription</key>
<string>To capture profile photos.</string>
```

```swift
import AVFoundation

func ensureCamera() async -> Bool {
    switch AVCaptureDevice.authorizationStatus(for: .video) {
    case .authorized: return true
    case .notDetermined: return await AVCaptureDevice.requestAccess(for: .video)
    default: return false
    }
}
```

Kotlin (Android):

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.CAMERA" />
```

```kotlin
@Composable
fun CameraGate(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted = it },
    )
    LaunchedEffect(Unit) {
        if (!granted) launcher.launch(Manifest.permission.CAMERA)
    }
    if (granted) content() else PermissionDenied()
}
```

Differences that catch translations:

- **Order matters on Android.** You cannot call a permission-protected API before the system grants the permission, even within the same task. iOS allows the API call to *trigger* the prompt; Android does not. The Kotlin code must explicitly request, then act on the result.
- **Permission lifecycle.** Android can revoke permissions while the app is suspended. Re-check on resume; don't cache the "granted" status across the lifecycle.
- **Rationale UI.** Android expects you to show a rationale before re-requesting a previously-denied permission (`shouldShowRequestPermissionRationale`). iOS has no equivalent obligation. Add this UI when porting.
- **Background/foreground.** Background location, notifications, BLE scanning, and similar require additional permissions on Android (often runtime-only past API 31+).

## Storage keys — the rule that *always* matters

iOS code can use a class name as a storage / decoding key because Swift's type identity is stable across builds:

```swift
enum StorageKey {
    static func key<T>(for type: T.Type) -> String {
        String(describing: type)  // e.g., "UserProfile"
    }
}
```

This works on iOS. **It does not work on Android.** R8 / minification renames classes in release builds; `String(describing: type)` becomes `"a"` or `"b1"`. Codable JSON written under one build's name won't be readable under another's.

Use **explicit string keys** on Android, always:

```kotlin
data object UserProfileKey {
    const val IDENTIFIER: String = "user_profile"
}

data object SignedInUserKey {
    const val IDENTIFIER: String = "signed_in_user"
}
```

Or, in a Spezi-style `KnowledgeSource` / `AccountKey`-shaped construct, declare the identifier as a `val` on the key:

```kotlin
data object EmailKey {
    val identifier: String = "email"
    // …
}
```

This is a hard rule, not a stylistic preference. If you find a Kotlin port that uses `T::class.simpleName` or `T::class.qualifiedName` for serialization keys, that is a bug waiting for the next release build.

## What about `keepclass` rules?

You can add ProGuard/R8 keep rules to preserve specific class names, but doing so for serialization keys defeats the purpose of minification on those types. The right answer is: explicit string keys. Reserve keep rules for entry points (Activities, services, JNI) and reflection roots.

## Why these rules matter

These three areas — file access, permissions, and storage keys — produce some of the most pernicious translation bugs because the Kotlin code compiles, runs in debug, and breaks under conditions you'll only see in release or on user devices:

- File access: works in dev; fails on Android API 30+ scoped storage.
- Permissions: works in dev (often pre-granted); fails on first install.
- Class-name keys: works in dev (no minification); fails in release.

Translate semantics, not syntax. Walk the runtime model, not the API names.
