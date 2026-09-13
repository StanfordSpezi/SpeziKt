<!--
This source file is part of the Stanford Spezi open-source project

SPDX-FileCopyrightText: 2026 Stanford University and the project authors

SPDX-License-Identifier: MIT
-->

**SpeziKt milestones and PR plan — September 13, 2026**

Status: **Draft proposal for review**, accompanying the [parity roadmap](android-parity-roadmap.md). These are planning IDs, not GitHub issue or PR numbers. Every item is proposed; none of the implementation or validation below has been completed by writing this plan.

Use milestones for demonstrable outcomes and PRs for independently reviewable changes. Detail the next five PRs now; refine subsequent candidates when their prerequisites land. Each implementation PR should include the relevant regression tests, usage documentation, and migration notes. Split a candidate if it introduces multiple independently useful behaviors or needs unrelated prerequisite changes. Do not leave failing regression tests on main between a test-only PR and its fix.

One GitHub milestone can track each outcome below, with one issue per near-term PR. The roadmap holds direction, this file holds ordering and scope, issues hold ownership and current status, and PRs hold implementation evidence. Create issues for the first milestone after agreeing its scope; avoid creating an entire speculative backlog at once. Creating this plan does not create GitHub milestones or issues.

| Milestone | Outcome to demonstrate | Exit gate |
| --- | --- | --- |
| M1: Reliable foundation | Existing modules handle documented failure and lifecycle cases | Baseline build is reproducible; storage failures are observable; health sync recovers; account transitions and runtime cleanup have behavioral tests. |
| M2: Consumable SDK preview | A separate Android app imports a supported subset of SpeziKt | Published artifacts resolve without repository-local build conventions; minified consumer and compatibility checks pass; preview scope and limitations are explicit. |
| M3: Connected data flow | One survey and one health record reach the selected backend | Explicit data contract, durable acceptance, account isolation, retry/deduplication and deletion handling are verified end to end. |
| M4: Complete Android template | A participant completes the reference-app journey | Signup, consent, permissions, scheduled questionnaire, health collection, synchronization and account management work together, including offline/restart cases. |
| M5: Study Platform client | Android participates in a remotely configured study | Compatible study definitions and identity/API integration drive supported components, enrollment and withdrawal against the selected server. |
| M6: Selected extensions | A named app gains a required additional capability | Device, access-guard, location, AI or other feature has its own supported-capability contract and acceptance demonstration. |

The default release sequence is M1 → M2 → M3 → M4 → M5, with M6 selected by application need. This is release ordering, not a requirement to serialize all development: publication design, the M3 data contract and pure scheduler logic can begin earlier. A narrow M2 preview can exclude modules with unresolved M1 blockers. No release should claim those excluded capabilities are production-ready.

**M1 candidates: harden the code that already exists.**

| ID | Proposed PR | Depends on | Scope and acceptance |
| --- | --- | --- | --- |
| M1-01 | Establish a reproducible validation baseline | — | Verify JDK 17, sample build, existing unit/instrumentation/screenshot commands and CI; document exact commands and fix only demonstrated setup blockers. If no changes are necessary, record evidence in the tracking issue rather than manufacture a PR. |
| M1-02 | Make local storage failures observable and writes atomic | M1-01 | Preserve old data on failed writes, distinguish missing data from corruption, propagate cancellation/errors, and stop deleting encrypted preferences automatically on open failure. Tests and caller migrations accompany the change. |
| M1-03 | Support authenticated encryption for general file payloads | M1-02 | Versioned authenticated file format, suitable key handling, and a tested policy for existing RSA files and caller-supplied key pairs. Large payloads round-trip; tampering fails visibly. |
| M1-04 | Acknowledge health batches after successful delivery | M1-02 | Correct token commit ordering, drain changes pages in manual mode, preserve cancellation and retry failed delivery without skipping changes. Include minimal fake-client/test seams. |
| M1-05 | Implement historical import and expired-token recovery | M1-04 | Initial snapshot plus changes catch-up, pagination, restart checkpoints, deduplication contract and deletion reconciliation. Imported time ranges and permission limits are explicit. |
| M1-06 | Gate health permissions by supported capabilities | M1-01 | Feature-aware background/history requests, denial/revocation state, and app-selected manifest permissions with a consumer migration example. Unsupported features cannot leave the app waiting for impossible authorization. |
| M1-07 | Define dependency-resolution correctness | M1-01 | Reproduce and address concurrent singleton/fallback creation and transient cycle handling; test required/optional dependencies and fail-fast configuration constraints. Keep lifecycle changes in M1-08. |
| M1-08 | Add module shutdown and owned service lifecycles | M1-07 | Define runtime ownership and shutdown, migrate existing coroutine/listener owners, and verify configure/clear/reconfigure without orphaned work. Android shutdown callbacks are not a persistence guarantee. |
| M1-09 | Harden account state and external-storage transitions | M1-02, M1-08 | Focused account tests and Firebase emulator coverage for sign-in/out, anonymous linking, updates and switching users. Late callbacks cannot repopulate the previous user's state; partial writes are observable. |
| M1-10 | Make account deletion recoverable | M1-09 | Agree authenticated cleanup/reauthentication semantics and implement observable retryable deletion states. If a server operation is required, link a separate backend PR and verify it before claiming end-to-end cleanup. |
| M1-11 | Run health collection through persistent background work | M1-05, M1-06, M1-08 | Persist collector configuration, resume from committed progress and prevent overlapping collectors. Verify restart, revocation and retry behavior; document scheduling limits. |

M1-03 and M1-04 can proceed independently after M1-02. M1-06 and M1-07 can proceed independently after baseline verification. Each PR branches from current main unless explicitly stacked on its named prerequisite; keep stacks short and identify their base in the PR description.

**The first five implementation plans are ready to refine into issues.**

| Plan | M1-01: Reproducible validation baseline |
| --- | --- |
| Problem | The source review did not establish a current local build baseline. Existing CI is useful but is not proof for a new checkout or every behavior. |
| Entry points | [CI](../../.github/workflows/build-test-analyze.yml), [Gradle catalog](../../gradle/libs.versions.toml), [Fastlane](../../fastlane/Fastfile), [README](../../README.md). |
| Work | Run the existing workflow with JDK 17; verify test discovery, LFS screenshot availability and sample setup; record the supported emulator/device configuration. Resolve only failures that actually occur. |
| Acceptance | Documented commands run on a clean checkout in the selected environment. Clearly distinguish unit, device and screenshot checks. Record any external-service requirements. |
| Boundary | No broad dependency upgrade, feature implementation or newly invented coverage threshold. |

| Plan | M1-02: Storage failure semantics |
| --- | --- |
| Problem | Local storage can hide a failed write, and opening encrypted preferences can erase existing data. Health progress and account caches depend on these semantics. |
| Entry points | [LocalStorage](../../storage-local/src/main/kotlin/edu/stanford/spezi/storage/local/LocalStorage.kt), [KeyValueStorageFactory](../../storage-local/src/main/kotlin/edu/stanford/spezi/storage/local/KeyValueStorageFactory.kt), [storage tests](../../storage-local/src/androidTest/kotlin/edu/stanford/spezi/storage/local/LocalStorageTests.kt). |
| Work | Select a consistent public failure API. Prefer the existing suspend signatures with documented exceptions unless review justifies a Result migration. Reserve null for a missing entry, propagate cancellation, use atomic replacement, and preserve unreadable stores for explicit recovery. Audit callers that currently depend on errors becoming null. |
| Acceptance | Injected write failure preserves prior bytes; missing read differs from malformed data; failed preference initialization retains the file; cancellation reaches the caller. Existing successful reads/writes still pass. |
| Migration | Document the observable behavior change and update affected account/health callers in the same PR. Do not silently reinterpret a failed cache read as a signed-out user or a fresh sync. |
| Boundary | Retain the existing encrypted format here. Cryptographic format changes belong to M1-03. |

| Plan | M1-03: Encrypted file format |
| --- | --- |
| Problem | Direct RSA encryption of full serialized objects does not support general file payload sizes. |
| Entry points | [LocalStorage](../../storage-local/src/main/kotlin/edu/stanford/spezi/storage/local/LocalStorage.kt), [settings](../../storage-local/src/main/kotlin/edu/stanford/spezi/storage/local/LocalStorageSetting.kt), [key storage](../../storage-local/src/main/kotlin/edu/stanford/spezi/storage/local/KeyStorage.kt). |
| Work | Specify format version, authenticated metadata and per-write nonce behavior. Choose a Keystore-backed symmetric key or envelope encryption appropriate to the API. Explicitly preserve or deprecate the caller-supplied KeyPair mode. Read supported legacy files and atomically migrate on a defined operation; surface unsupported/corrupt formats without erasing them. |
| Acceptance | A representative 1 MiB payload round-trips; repeated writes use distinct nonces; modified/truncated ciphertext fails; wrong/missing key failures are observable; a legacy fixture survives migration. Use instrumented tests for Android Keystore behavior. |
| Boundary | No new database, cloud storage service or blanket replacement of credential storage. |

| Plan | M1-04: Health batch acknowledgment |
| --- | --- |
| Problem | Advancing a token before Standard callbacks succeed can lose delivery; manual collection processes only one changes page. |
| Entry points | [collector](../../health/src/main/kotlin/edu/stanford/spezi/health/internal/HealthDataCollector.kt), [token store](../../health/src/main/kotlin/edu/stanford/spezi/health/internal/ChangesTokenStore.kt), [Standard contract](../../health/src/main/kotlin/edu/stanford/spezi/health/HealthConstraint.kt). |
| Work | Fetch a page, deliver its additions/deletions, and persist its next token only after successful acceptance. Drain hasMore pages in manual mode. Add a narrow test seam for client responses and storage faults; retain cancellation. Document replay when one callback succeeds and another fails. |
| Acceptance | Successful batch advances progress; callback failure does not; failed token persistence is observable and safely replayable; multi-page manual import drains; cancellation ends work. Regression tests should fail against the old ordering. |
| Contract | At-least-once delivery with idempotent receivers. A callback must durably accept a batch before returning successfully; merely starting an upload is insufficient. Do not claim exactly-once network delivery. |
| Boundary | No historical snapshot algorithm, WorkManager integration, FHIR conversion or cloud sink in this PR. |

| Plan | M1-05: Initial import and recovery |
| --- | --- |
| Problem | Filtering a changes stream by StartingAt does not import already-existing records, and a replacement token alone cannot reconstruct missed history. |
| Entry points | [collector](../../health/src/main/kotlin/edu/stanford/spezi/health/internal/HealthDataCollector.kt), [health client](../../health/src/main/kotlin/edu/stanford/spezi/health/internal/DefaultHealthClient.kt), [collection range](../../health/src/main/kotlin/edu/stanford/spezi/health/CollectionTimeRange.kt). |
| Work | Specify and test the snapshot-to-changes transition, obtaining a changes boundary before the snapshot so concurrent writes are replayed. Persist sufficient recovery state; define a resync protocol that permits downstream reconciliation of missing IDs within the authorized scope. |
| Acceptance | Preexisting in-range records import; out-of-range records do not; writes during import are retained; multiple pages and restart recovery work; duplicates are tolerable by documented IDs; expired-token recovery reconciles deletions without treating unavailable history or revoked access as deletion. |
| Boundary | If recovery requires a substantial new public protocol, split its specification and a minimal compatible implementation into reviewable steps. Persistent background scheduling remains M1-11. |

**M2 candidates: publish a supported preview.**

| ID | Proposed PR | Depends on | Acceptance |
| --- | --- | --- | --- |
| M2-01 | Add Maven publication conventions and version alignment | M1 baseline; selected API scope | Selected modules publish artifacts, sources, docs and correct transitive dependencies to a local test repository; identifiers and version policy are documented. |
| M2-02 | Add an external consumer and compatibility checks | M2-01; required M1 fixes | Separate app resolves artifacts, uses normal Android plugins and exercises a minified build. Public API baselines detect incompatible changes. |
| M2-03 | Add signed release workflow and preview documentation | M2-02 | Test publication is verified before enabling a real release; release contents match the declared supported subset. Release credentials stay external. |

M2 is an SDK distribution milestone. Play Store deployment of the sample is a separate application delivery concern.

**M3 candidates: complete one durable data path.**

| ID | Proposed PR | Depends on | Acceptance |
| --- | --- | --- | --- |
| M3-01 | Specify the first backend/FHIR contract and shared fixtures | Scope review; can start during M1 | Agree R4/R4B handling, patient/source IDs, paths, timestamps, units, questionnaire versions and deletion semantics. Pin source revisions. Fixtures are accepted by the selected backend validator. |
| M3-02 | Introduce FHIR resource APIs and serialization | M3-01 | Existing questionnaire types interoperate with the chosen model/store facade; canonical fixtures round-trip with required fields intact. |
| M3-03 | Map the first Health Connect record type to FHIR | M3-02, M1-05 | Start with one type, such as weight. IDs, units, provenance, time and deletion mapping match fixtures. Add steps, heart rate and blood pressure as follow-up PRs, with explicit aggregation semantics. |
| M3-04 | Add durable resource storage and a retry queue | M3-02, M1-02, M1-03, M1-08 | Resource updates and queued operations commit atomically. Restart, duplicate IDs, user switching and logout preserve isolation. Choose the database in this plan's implementation review. |
| M3-05 | Implement the selected Firebase data adapter | M3-01, M3-04 | Emulator tests cover authorization, repeated upserts/deletes and transient failures. Backend rule/function changes are separate linked PRs in the owning repository. |
| M3-06 | Connect survey and health collection to durable delivery | M3-03, M3-04, M3-05, M1-09 | Sample accepts one response and one health record offline, persists before acknowledging, then syncs without duplicate logical records after restart/retry. |

The important dependency is M3-01 → M3-02 → M3-04 → M3-05 → M3-06; health mapping branches from M3-02. Do not select an entire cloud architecture merely to unblock M1 reliability fixes.

**M4 candidates: assemble the participant journey.**

| ID | Proposed PR | Depends on | Acceptance |
| --- | --- | --- | --- |
| M4-01 | Assemble reusable account setup and management screens | M1-09, M1-10 | Signup/login, verification/reset, edits, reauthentication and deletion states work against configured providers; finish as separate flows if review size warrants. Compare existing account work before extending it. |
| M4-02 | Correct supported Markdown rendering | Baseline; issue #141 | Defined Markdown examples render correctly with accessible links and text; document unsupported constructs. |
| M4-03 | Add a signed consent document and PDF export flow | M4-02, M1-03; assess PR #201 | Defined initial consent format supports required acknowledgment, signer details/signature and a retrievable PDF. Match the agreed first-study requirements; expand interactive selections separately. |
| M4-04 | Persist consent versions and deliver consent artifacts | M4-03, M3-04, M3-05 | Interrupted upload is retryable; the signed document remains tied to its version and user. Add an object-storage adapter here if needed; completion semantics distinguish local acceptance from uploaded state. |
| M4-05 | Add resumable onboarding orchestration | M4-01, M4-03, M1-06 | Steps compose from existing screens, resume after recreation, and respond correctly to denied permissions or changed accounts. |
| M4-06 | Define scheduler recurrence and occurrence calculation | M1 baseline | Pure domain tests cover dates/time zones, DST, completion windows and stable occurrence identity. No notifications in this PR. |
| M4-07 | Persist task versions and outcomes | M4-06, M3-04 | Schedule changes preserve completed history; duplicate completion does not create duplicate outcomes. Storage migrations are tested. |
| M4-08 | Add a Compose schedule and task-completion UI | M4-07 | Today/upcoming/completed views use public scheduler APIs and preserve state across recreation. |
| M4-09 | Add local reminders and task deep links | M4-07, M4-08 | Permission denial is handled; edits/cancellations reconcile reminders; tapping a reminder opens the correct occurrence once. Document timing precision and rescheduling behavior. FCM remains a separate optional PR. |
| M4-10 | Persist questionnaire drafts and bind responses to events | M3-04, M4-07 | Resume an interrupted supported questionnaire; saved response identifies its questionnaire version and task occurrence. Validate branching, repeats, required fields and unsupported items against an agreed corpus. |
| M4-11 | Complete the template and its release acceptance journey | M3-06, M4-01 through M4-10 | Wire public modules together, include contacts/license acknowledgments, run the participant journey, and fix remaining accessibility/localization defects in focused follow-ups before release. |

M4-11 verifies integration; module behavior must already be tested in the earlier PRs. It should not become the place to implement all missing functionality. Sensor permission screens, consent UI and scheduler logic may be developed earlier, but the milestone closes only when the integrated journey passes.

**M5 candidates: support remotely configured studies.**

| ID | Proposed PR | Depends on | Acceptance |
| --- | --- | --- | --- |
| M5-01 | Add study-definition and bundle compatibility | M3 contract experience; pin SpeziStudy revision | Decode shared fixtures, validate supported components and reject unsupported versions explicitly. |
| M5-02 | Add Study Platform API and identity adapters | M5-01; agreed server deployment | Pin OpenAPI version; test serialization, errors and the selected identity flow. Inventory participant endpoints and identify any server work before relying on them. |
| M5-03 | Implement study enrollment and withdrawal state | M5-01, M5-02, M3-04 | Persist state across restarts, synchronize transitions and define local/remote data handling on withdrawal. Server gaps require linked backend PRs. |
| M5-04 | Render configured study components in Android | M5-03, M4 | Study definitions configure consent/information, supported questionnaires, schedules and health collection. Validate with a study created in the existing authoring app. |

**M6 is a menu of independently scoped extensions.**

Assess [Bluetooth #106](https://github.com/StanfordSpezi/SpeziKt/pull/106) and [LLM plugins #212](https://github.com/StanfordSpezi/SpeziKt/pull/212) before proposing replacement work. Choose one actual app requirement, then split it into a public contract, a working platform/provider adapter, and application integration with acceptance tests. For BLE, use scanning/connection → a single device profile → pairing/measurement UI, with real-device validation. For AI, use session/stream/cancellation contract → a selected provider → chat integration. Separate local inference and speech into later plans. Do not gate the template release on every extension in the organization.

**Every implementation issue should make its PR reviewable before coding begins.**

Use this compact template:

```text
Milestone / planning ID:
Problem and concrete before/after behavior:
Dependencies and related existing PRs:
Files/modules expected to change:
Proposed API or data-format change:
Scope boundaries:
Acceptance scenarios and verification commands:
Compatibility, migration and failure recovery:
Evidence required before merge:
```

The first implementation decision is to approve M1-01 and M1-02, then use their results to finalize M1-03 through M1-05. Keep all later entries as candidates until their API, backend and ownership assumptions have been checked.
