# Module health

The `health` module integrates [Android Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect) into a Spezi application. It is registered as a Spezi `Module` and provides a type-safe DSL for declaring read/write permissions, configuring background record collection, querying health records (one-shot, paginated, or continuous), and inserting records.

## Components

- **`Health`** — the Spezi `Module`. Exposes `query`, `continuousQuery`, `insert`, `oldestSampleDate`, permission requests (`requestPermissionsIfNeeded`, `requestReadPermission`, `requestWritePermission`), authorization checks (`isAuthorizedToRead`, `isAuthorizedToWrite`, `isFullyAuthorizedState`), `resetRecordCollection`, and `dataAccessRequirements`.
- **`HealthModuleBuilder`** — `@SpeziDsl` builder used via the `ConfigurationBuilder.health { ... }` extension. Declares `requestReadAccess`, `requestWriteAccess`, `collectRecord`, and a `privacy { ... }` block.
- **`RecordType<T : Record>` / `AnyRecordType`** — typed wrapper over Health Connect `Record` classes with `readPermission`/`writePermission`. The companion object provides typed references (e.g. `RecordType.heartRate`, `RecordType.weight`, `RecordType.bloodPressure`, `RecordType.steps`) plus `RecordType.from(record)`.
- **`HealthQueryTimeRange`** — value type with factory helpers: `ever()`, `today()`, `currentWeek()`, `currentMonth()`, `last(Duration)`, `lastDays(n)`, `startingAt(Instant)`, etc.
- **`QueryResult<T>`** — paginated query result (`added`, `deletedIds`, `nextAnchor`); **`QuerySort`** controls ordering.
- **`CollectionMode`** (`Manual` / `Automatic(pollingInterval)`) and **`CollectionTimeRange`** (`NewRecords` / `StartingAt(date)`) — configure background collection.
- **`HealthDataAccessRequirements`** — declared read/write record sets.
- **`HealthConstraint`** — a `Standard` extension whose `handleNewRecords`, `handleDeletedRecords`, and `onFullyResyncRequired` callbacks receive collected Health Connect changes.
- **`PrivacyConfigBuilder`** — configures the permissions-rationale screen via `explanationText`, `composable`, or `content`.

## Usage

Register the module in your Spezi `Configuration`:

```kotlin
override val configuration = Configuration {
    health {
        requestReadAccess(RecordType.bloodPressure, RecordType.weight, RecordType.heartRate)
        requestWriteAccess(RecordType.heartRate)

        collectRecord(
            recordType = RecordType.heartRate,
            start = CollectionMode.Automatic(pollingInterval = 15.minutes),
            continueInBackground = true,
            timeRange = CollectionTimeRange.NewRecords,
        )

        privacy {
            explanationText(
                title = StringResource("Health Data Access"),
                description = StringResource("This app uses Health Connect to read and write health data."),
            )
        }
    }
}
```

Request permissions and query records:

```kotlin
health.requestPermissionsIfNeeded(activity)

val readings: List<HeartRateRecord> = health.query(
    type = RecordType.heartRate,
    timeRange = HealthQueryTimeRange.today(),
    sortedBy = QuerySort.BY_START_TIME_DESC,
)

// Continuous, paginated updates as a Flow
health.continuousQuery(
    type = RecordType.heartRate,
    timeRange = HealthQueryTimeRange.lastDays(7),
).collect { result: QueryResult<HeartRateRecord> ->
    println("added=${result.added.size}, deleted=${result.deletedIds.size}")
}
```
