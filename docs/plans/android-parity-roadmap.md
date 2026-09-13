<!--
This source file is part of the Stanford Spezi open-source project

SPDX-FileCopyrightText: 2026 Stanford University and the project authors

SPDX-License-Identifier: MIT
-->

**SpeziKt completion roadmap — September 13, 2026**

Status: **Draft proposal for review.** Priorities and architecture recommendations are proposed, not an approved project commitment. Update this document as scope is agreed and milestones are completed.

The [milestones and PR plan](android-parity-pr-plan.md) breaks this proposal into dependent, reviewable changes, with detailed scopes for the first five implementation PRs.

Recommendation: finish a dependable, independently consumable Android SDK and an Android equivalent of SpeziTemplateApplication first. Keep the Kotlin monorepo. Then extend study-platform and specialized feature coverage according to real application requirements.

This review enumerated all 55 public StanfordSpezi repositories and inspected their READMEs and metadata, including five archived repositories. It also inspected selected current Swift source, repository trees, the Study Platform API, the FHIR implementation guide, and local Kotlin implementations, tests, build files and CI. This is an architecture and source review, not an exhaustive code audit of every repository. Private repositories are outside its scope. The reviewed SpeziKt revision was `03b4c4624295c6163f9aff23edc0411054c6ade0`. No local build or test suite was run, and no implementation code was changed.

The [organization inventory](https://github.com/orgs/StanfordSpezi/repositories) includes mobile SDKs, web applications, backend services, infrastructure, templates, developer tooling and archived projects. Repository count is not a meaningful feature-completion percentage.

The checkout contains 25 included Gradle projects: 20 framework/library projects, four test-support projects and one sample app. Existing modules implement substantial behavior.

| Existing Kotlin area | Assessment |
| --- | --- |
| core, foundation, core-* | Configuration DSL, dependency registry, Standard, lifecycle state, coroutines, logging, time and ViewModel support exist. Complete runtime contracts and lifecycle ownership. |
| account, account-firebase, ui-account | Typed account keys/details, pluggable service/storage, Firebase authentication and Firestore account storage, plus Compose UI building blocks exist. Finish assembled flows, errors, lifecycle coordination and tests. |
| health | Health Connect permissions, typed records, reads/writes and change collection exist. Synchronization and persistent background execution need hardening. |
| questionnaire | Real FHIR R4 rendering via Android FHIR SDC exists. Add draft/resume and application submission integration, and verify questionnaire behavior against shared fixtures. |
| storage-local, storage-credential | Persistence and encryption APIs exist. The file-encryption and failure semantics need correction before relying on them for clinical payloads. |
| ui, ui-theme, ui-markdown, ui-personalinfo, ui-validation, contact | Significant Compose components and UI tests exist. Complete the supported feature matrix, localization, accessibility and integration. |
| testing-* and CI | Unit, instrumentation, screenshot and documentation infrastructure already exists. Behavioral tests are notably absent from account, account-firebase and health source trees. |
| sample-app | Registers in-memory accounts, displays a Health entry, and logs collected health callbacks. It does not yet demonstrate the complete iOS template journey. |

Evidence: [module list](../../settings.gradle.kts), [sample configuration](../../sample-app/src/main/kotlin/edu/stanford/spezi/sample/app/SampleApplication.kt), [sample navigation](../../sample-app/src/main/kotlin/edu/stanford/spezi/sample/app/home/HomeViewModel.kt), and [CI](../../.github/workflows/build-test-analyze.yml). The [latest returned upstream PR run](https://github.com/StanfordSpezi/SpeziKt/actions/runs/28327160344) succeeded on June 28, 2026; that does not establish the build status of this local checkout.

Existing work should be assessed before starting new implementations. Open SpeziKt proposals at PR preparation time include [Bluetooth #106](https://github.com/StanfordSpezi/SpeziKt/pull/106), [account/Firebase #107](https://github.com/StanfordSpezi/SpeziKt/pull/107), [signature input using the Ink API #201](https://github.com/StanfordSpezi/SpeziKt/pull/201), and [LLM plugins #212](https://github.com/StanfordSpezi/SpeziKt/pull/212). Their implementations have not been audited here. Gaps below describe the reviewed main-branch revision, not the absence of work on other branches.

Review should settle three questions before implementation: which Android template journey defines the first supported release, which backend/FHIR contract it targets, and which open contributions should be completed or reused. Record accepted scope and link implementation issues as those decisions are made.

**Fix these concrete reliability gaps first.**

1. Health collection advances its persisted change token before the Standard has successfully handled the batch. A callback failure can therefore skip undelivered changes on retry. Persist progress only after durable acceptance, and make processing idempotent. Manual collection handles one changes page; drain all pages. A StartingAt filter does not perform an initial historical read, and expiry handling delegates resync without supplying a complete reusable recovery implementation. Add explicit historical import, deduplication and deletion reconciliation. See [HealthDataCollector](../../health/src/main/kotlin/edu/stanford/spezi/health/internal/HealthDataCollector.kt) and [Android synchronization guidance](https://developer.android.com/health-and-fitness/health-connect/sync-data).
2. Background collection is a coroutine polling loop with application-lifecycle handling. It is not durable execution after process death. Add persistent scheduling and durable checkpoints, using WorkManager for deferrable sync. Feature-gate background/history permissions and make the consuming app declare only permissions it needs; the library manifest currently includes a broad list. See [health client](../../health/src/main/kotlin/edu/stanford/spezi/health/internal/DefaultHealthClient.kt), [manifest](../../health/src/main/AndroidManifest.xml), [persistent work guidance](https://developer.android.com/develop/background-work/background-tasks/persistent), and [feature availability](https://developer.android.com/health-and-fitness/health-connect/features/availability).
3. Encrypted LocalStorage sends the entire serialized payload through RSA doFinal. RSA has a bounded plaintext size, so this is unsuitable for general FHIR documents or consent PDFs. Storage also catches failures and exposes no write failure to callers; encrypted preferences are deleted when opening fails. Use authenticated symmetric encryption with a Keystore-protected key, atomic writes, versioned formats, explicit failures and deliberate recovery/migration. See [LocalStorage](../../storage-local/src/main/kotlin/edu/stanford/spezi/storage/local/LocalStorage.kt), [preferences factory](../../storage-local/src/main/kotlin/edu/stanford/spezi/storage/local/KeyValueStorageFactory.kt), and [Android cryptography guidance](https://developer.android.com/privacy-and-security/cryptography).
4. Firebase account deletion deletes the Auth user before attempting external account-data deletion and only logs external deletion failures. Depending on backend rules, losing authentication can prevent cleanup. Define recoverable account lifecycle operations and server-assisted deletion, and surface partial failures. Add emulator tests for reauthentication, anonymous linking, profile updates, account switching, logout and deletion. See [FirebaseAccountServiceImpl](../../account-firebase/src/main/kotlin/edu/stanford/spezi/account/firebase/internal/FirebaseAccountServiceImpl.kt).
5. Core only exposes configure() on Module; clear() drops the global graph without a cleanup contract. Add owned coroutine scopes, explicit shutdown/listener cleanup, deterministic initialization requirements and fail-fast Standard constraint checks. Review concurrent fallback creation, transient dependency cycle detection and reflection behavior in minified builds. Current Swift [service lifecycle](https://github.com/StanfordSpezi/Spezi/blob/main/Sources/Spezi/Capabilities/Lifecycle/ServiceModule.swift) and [Standard injection](https://github.com/StanfordSpezi/Spezi/blob/main/Sources/Spezi/Standard/Module%2BStandard.swift) are useful behavioral references.
6. The Markdown parser implements only a small subset. Resolve the existing [Markdown issue #141](https://github.com/StanfordSpezi/SpeziKt/issues/141) before using it to render consent content. Test unsupported questionnaire items and lifecycle changes explicitly; for example, the current cancel dialog also cancels on dismissal.

These are source-derived findings and proposed fixes; they were not reproduced through runtime tests in this review.

**Use six milestones with observable exit criteria.**

| Order | Deliverable | Definition of done |
| --- | --- | --- |
| 1 | Dependable existing modules | Clean JDK 17 build baseline; critical storage, health and account failure-path tests; safe lifecycle teardown; minified sample smoke test; supported Android/Health Connect matrix. |
| 2 | First consumable SDK release | Versioned Maven artifacts per public module, shared version alignment/BOM, sources and API docs, release automation, API compatibility checks, and an external sample consuming artifacts without internal convention plugins. |
| 3 | Shared data and backend path | FHIR model/store facade, initial Health Connect mappings, durable local data/outbox and a Firebase adapter. A questionnaire response and a health record reach the selected backend exactly once in effect, despite retries and app restarts. |
| 4 | Android template parity | Resumable onboarding, account flow, consent document/signature/export, health permissions, persisted scheduler/outcomes, notifications, questionnaires and contacts work together in one reference app. |
| 5 | Study Platform participation | Versioned StudyDefinition/bundle support, enrollment and withdrawal, supported component rendering, schedule integration and a Kotlin API client. The Android app consumes studies produced by the existing authoring system. |
| 6 | Selected extended parity | Add BLE/devices, access guard, location, chat/speech/LLM, CHOIR and specific sensor features with feature-level acceptance tests and declared platform limitations. |

Publication can start after milestone 1 with a narrow preview. Milestones 3 and 4 can then grow that preview into a release suitable for real application integration. Avoid promising a calendar for full ecosystem parity before choosing supported devices, study components and data types.

The first reference-app acceptance journey should be: launch → sign up → read and sign consent → request needed permissions → receive a scheduled task → answer a FHIR questionnaire → save and synchronize the response and health data → reopen the app without losing progress → view/update account details and exercise account deletion. Verify retries, denied permissions, process recreation and offline recovery as part of that journey.

**Choose data compatibility explicitly.**

The current [iOS template Standard](https://github.com/StanfordSpezi/SpeziTemplateApplication/blob/main/TemplateApplication/TemplateApplicationStandard.swift) converts HealthKit samples and stores questionnaires and consent artifacts. SpeziKt's Standard callbacks currently log received records. Build a reusable Android data path: Health Connect or questionnaires → typed FHIR resources → durable local store/outbox → chosen backend adapter.

Keep patient identity, source record IDs, units/codes, timestamps and time zones, device/source provenance, questionnaire canonical URLs/versions, deletion semantics and consent references explicit. Test equivalent JSON fixtures across Android, iOS and backend validators.

There is no single demonstrated wire contract across all current repositories: SpeziKt questionnaires use R4, the [draft implementation guide](https://github.com/StanfordSpezi/spezi-fhir-ig/blob/main/sushi-config.yaml) declares FHIR 4.0.1, while [spezi-firebase-template](https://github.com/StanfordSpezi/spezi-firebase-template) documents R4B. Its collection names also differ from the iOS template's. Select a target backend/schema version, pin it, and implement tested translations where required. Do not assume matching resource names guarantee compatibility.

Use [SpeziFHIR](https://github.com/StanfordSpezi/SpeziFHIR) as the current resource-management reference, and the draft guide as input to profile decisions. Review its iOS-specific extensions before representing Android provenance. Start with a defined subset such as steps, heart rate, weight and blood pressure; expand only after their units, aggregation, IDs and deletion behavior are tested.

A scheduler must model tasks, recurrence, occurrences and outcomes independently of notification delivery. Preserve completed history when definitions change, and test time-zone/DST transitions, completion windows and duplicate notification actions. This matches the domain described by [SpeziScheduler](https://github.com/StanfordSpezi/SpeziScheduler). WorkManager handles durable work, not exact user reminder timing; use the appropriate Android notification/alarm mechanism with its supported precision.

For the Study Platform milestone, use the shared [OpenAPI specification](https://github.com/StanfordSpezi/SpeziStudyPlatform-API/blob/main/openapi.yaml) and [current server](https://github.com/StanfordSpezi/SpeziStudyPlatform-Server). The prior [NestJS service](https://github.com/StanfordSpezi/spezi-web-service-study-platform) is archived. Validate supported participant operations against the API instead of assuming every enrollment flow is already exposed. Add a separate identity adapter for the chosen Study Platform deployment; Firebase authentication is not automatically interchangeable with its Keycloak-backed environment.

**Retain the monorepo and Android-native implementation.**

A Swift repository often contains multiple products: SpeziViews includes validation and personal-info libraries; SpeziFirebase separates several backend services. Repository boundaries therefore need not match Gradle project boundaries.

Publish independently selectable artifacts from the existing monorepo, initially with one version train and a BOM. Keep test fixtures, sample apps, build conventions and parity tests together. Split repositories later only when ownership or release independence creates a clear benefit. No Maven publication configuration was found in the reviewed Gradle files, and the GitHub releases endpoint returned no releases.

Use Compose, coroutines and Android APIs for implementation. Match product behavior, extension points and serialized data. Do not reproduce Swift property wrappers mechanically. In particular, Swift Provide/Collect is typed contribution collection; a Kotlin typed contribution registry can reproduce that contract, while StateFlow and suspend APIs handle runtime state and operations. A generic event bus is not an equivalent replacement by itself.

The checkout is Android-oriented, not a Kotlin Multiplatform project. Keep shared domain logic independent of Android where practical, but a wholesale KMP conversion should not gate Android completion. Explicitly document platform differences for HealthKit/SensorKit, Apple identity, background work and device APIs.

The following inventory covers every public repository returned during this review. Priority and recommended treatment are this review's judgments; repository role and archive status come from the linked upstream sources.

| Repository | Language/status | Role in completion | Recommended treatment |
| --- | --- | --- | --- |
| [Spezi](https://github.com/StanfordSpezi/Spezi) | Swift | Core | Harden core; add service lifecycle, explicit Standard constraints, and typed provider/collector contributions. |
| [SpeziTemplateApplication](https://github.com/StanfordSpezi/SpeziTemplateApplication) | Swift | First milestone | Build an Android template with the same complete onboarding, consent, account, schedule, survey, and data-storage journey. |
| [SpeziFirebase](https://github.com/StanfordSpezi/SpeziFirebase) | Swift | First milestone | Extend account-firebase with general Firestore, object-storage, configuration, and emulator adapters. |
| [SpeziFHIR](https://github.com/StanfordSpezi/SpeziFHIR) | Swift | First milestone | Add FHIR resource/store APIs, Health Connect mapping, attachments as needed, and reusable fixtures. |
| [SpeziQuestionnaire](https://github.com/StanfordSpezi/SpeziQuestionnaire) | Swift | Existing partial counterpart | Keep Android FHIR SDC rendering; verify a shared questionnaire corpus and add draft persistence and submission integration. |
| [SpeziHealthKitToFHIRAdapter](https://github.com/StanfordSpezi/SpeziHealthKitToFHIRAdapter) | Archived | Archived | Historical reference; use current SpeziFHIR integration and current data contracts. |
| [SpeziScheduler](https://github.com/StanfordSpezi/SpeziScheduler) | Swift | First milestone | Add persisted tasks, schedules, occurrences/events, outcomes, task versioning, and Compose schedule UI. |
| [SpeziAccount](https://github.com/StanfordSpezi/SpeziAccount) | Swift | Existing partial counterpart | Finish account orchestration, provider-driven screens, account lifecycle notifications, and behavioral tests. |
| [SpeziFHIRtoFirestoreAdapter](https://github.com/StanfordSpezi/SpeziFHIRtoFirestoreAdapter) | Archived | Archived | Historical reference; implement against the chosen current backend contract. |
| [SpeziContact](https://github.com/StanfordSpezi/SpeziContact) | Swift | Existing partial counterpart | Finish API/UX comparison, accessibility, localization, and template integration. |
| [SpeziOnboarding](https://github.com/StanfordSpezi/SpeziOnboarding) | Swift | First milestone | Add reusable sequential/resumable Compose onboarding and permission steps. |
| [SpeziStorage](https://github.com/StanfordSpezi/SpeziStorage) | Swift | Existing partial counterpart | Fix encrypted file storage and failure handling; define migration, backup, and key-recovery behavior. |
| [SpeziViews](https://github.com/StanfordSpezi/SpeziViews) | Swift | Existing partial counterpart | Retain Compose UI, personal-info and validation modules; finish public APIs, localization and accessibility. |
| [SpeziHealthKit](https://github.com/StanfordSpezi/SpeziHealthKit) | Swift | Existing partial counterpart | Use Health Connect as Android counterpart; fix synchronization, background execution, and capability detection. |
| [.github](https://github.com/StanfordSpezi/.github) | — | Shared infrastructure | Reuse contributor guidance and relevant CI conventions; no Kotlin port. |
| [SpeziLLM](https://github.com/StanfordSpezi/SpeziLLM) | Swift | Optional extension | Add provider/session abstractions, streamed output and cancellation; select Android local/fog implementations only when needed. |
| [stanfordspezi.github.io](https://github.com/StanfordSpezi/stanfordspezi.github.io) | HTML | Website | Link Android documentation into ecosystem discovery; no Kotlin port. |
| [SpeziSMARTonFHIR](https://github.com/StanfordSpezi/SpeziSMARTonFHIR) | Archived | Archived | Historical web/EHR demo; not a required Android module. |
| [SpeziMockWebService](https://github.com/StanfordSpezi/SpeziMockWebService) | Swift | Developer experience | Provide fake data sinks/backends and deterministic sample data for offline demos and tests. |
| [SpeziAccessGuard](https://github.com/StanfordSpezi/SpeziAccessGuard) | Swift | Extended parity | Add reusable PIN/biometric-gated Compose content and lifecycle-driven relocking. |
| [SpeziBluetooth](https://github.com/StanfordSpezi/SpeziBluetooth) | Swift | Extended parity | Build Android BLE scanning, connection, GATT operations, reconnection, and test abstractions. |
| [SpeziMedication](https://github.com/StanfordSpezi/SpeziMedication) | Archived | Archived | Implement only for an explicit medication product requirement, not as a baseline parity obligation. |
| [SpeziFoundation](https://github.com/StanfordSpezi/SpeziFoundation) | Swift | Existing partial counterpart | Extend foundational utilities selectively; preserve Kotlin/coroutines conventions and add localization support where needed. |
| [SpeziSpeech](https://github.com/StanfordSpezi/SpeziSpeech) | Swift | Optional extension | Add Android speech-recognition and synthesis adapters. |
| [SpeziChat](https://github.com/StanfordSpezi/SpeziChat) | Swift | Optional extension | Add reusable Compose chat UI and message state; integrate speech separately. |
| [SpeziLocation](https://github.com/StanfordSpezi/SpeziLocation) | Swift | Extended parity | Add location permission/state APIs and Android collection adapters, if required by the target app. |
| [SpeziStudyPlatform-iOS](https://github.com/StanfordSpezi/SpeziStudyPlatform-iOS) | Swift | Later acceptance app | Use as a study-participation behavior reference after the Android template is complete. |
| [SpeziDataPipeline](https://github.com/StanfordSpezi/SpeziDataPipeline) | Python | Shared integration | Validate Android-produced data against existing Python analysis workflows; reuse the pipeline. |
| [SpeziFileFormats](https://github.com/StanfordSpezi/SpeziFileFormats) | Swift | Supporting extension | Port only binary/file codecs required by selected device or study features. |
| [SpeziLicense](https://github.com/StanfordSpezi/SpeziLicense) | Swift | Release polish | Generate and display Android dependency license acknowledgments. |
| [SpeziKt](https://github.com/StanfordSpezi/SpeziKt) | Kotlin | Implementation target | Keep the monorepo; publish versioned modules and complete the staged roadmap in this document. |
| [SpeziNetworking](https://github.com/StanfordSpezi/SpeziNetworking) | Swift | Supporting extension | Contains byte coding and numeric infrastructure; port required protocol pieces, not an assumed generic HTTP layer. |
| [SpeziDevices](https://github.com/StanfordSpezi/SpeziDevices) | Swift | Extended parity | Build pairing/measurement orchestration and Compose device UI on BLE; add specific drivers such as Omron when needed. |
| [SpeziDataPipelineTemplate](https://github.com/StanfordSpezi/SpeziDataPipelineTemplate) | Jupyter Notebook | Shared integration | Reuse notebooks as downstream compatibility checks for Android data. |
| [SpeziNotifications](https://github.com/StanfordSpezi/SpeziNotifications) | Swift | First milestone | Add local notification permissions, channels, scheduling, actions and deep links; add FCM registration separately. |
| [spezi-web-design-system](https://github.com/StanfordSpezi/spezi-web-design-system) | TypeScript | Web ecosystem | Reuse design intent and data contracts; implement Android screens in Compose. |
| [spezi-web-configurations](https://github.com/StanfordSpezi/spezi-web-configurations) | TypeScript | Web tooling | No Kotlin port; follow equivalent Gradle/lint conventions. |
| [spezi-web-health-components](https://github.com/StanfordSpezi/spezi-web-health-components) | TypeScript | Web ecosystem | Check shared health data representations; README is sparse, so do not infer mature feature coverage. |
| [spezi-web-template-application](https://github.com/StanfordSpezi/spezi-web-template-application) | TypeScript | Shared integration | Use the web frontend as a consumer of agreed backend contracts. |
| [spezi-web-service-template](https://github.com/StanfordSpezi/spezi-web-service-template) | — | Infrastructure template | Sparse repository documentation; not an Android parity deliverable. |
| [SpeziStudy](https://github.com/StanfordSpezi/SpeziStudy) | Swift | Study milestone | Add study-definition/bundle serialization, validation, enrollment, withdrawal, and component orchestration. |
| [SpeziCHOIR](https://github.com/StanfordSpezi/SpeziCHOIR) | Swift | Optional integration | Implement the CHOIR API/account adapter and Android survey experience only for CHOIR use cases. |
| [spezi-firebase](https://github.com/StanfordSpezi/spezi-firebase) | TypeScript | Shared backend | Reuse TypeScript Firebase utilities and FCM backend; align Android device-registration and notification payloads. |
| [spezi-firebase-template](https://github.com/StanfordSpezi/spezi-firebase-template) | TypeScript | Shared backend | Use an explicit Firebase data contract; this template documents FHIR R4B and different collection paths from the iOS template. |
| [SpeziStudyPlatform-Web](https://github.com/StanfordSpezi/SpeziStudyPlatform-Web) | TypeScript | Shared application | Reuse the study authoring frontend; validate Android compatibility with published study definitions. |
| [spezi-web-service-study-platform](https://github.com/StanfordSpezi/spezi-web-service-study-platform) | Archived | Archived | Old NestJS service; do not choose it as the default new client target. |
| [SpeziStudyPlatform-Infrastructure](https://github.com/StanfordSpezi/SpeziStudyPlatform-Infrastructure) | HCL | Shared infrastructure | Reuse deployment and local backing services; no Kotlin port. |
| [spezi-fhir-ig](https://github.com/StanfordSpezi/spezi-fhir-ig) | GLSL | Shared specification | Use draft R4 profiles as a reference; review Apple-specific extensions and agree Android metadata representations. |
| [SpeziConsent](https://github.com/StanfordSpezi/SpeziConsent) | Swift | First milestone | Add document display, selections, participant/signature capture, PDF export and consent-version persistence. |
| [SpeziSensorKit](https://github.com/StanfordSpezi/SpeziSensorKit) | Swift | Platform-specific extension | Define Android-supported sensor capabilities individually; document unavailable Apple-specific data rather than promising equivalence. |
| [SpeziVibe](https://github.com/StanfordSpezi/SpeziVibe) | Shell | Developer experience | Add Kotlin template/module guidance after the framework is consumable and its examples are verified. |
| [SpeziVapor](https://github.com/StanfordSpezi/SpeziVapor) | Swift | Server integration | Reuse server-side Swift integration where applicable; no Android port. |
| [SpeziStudyPlatform-Server](https://github.com/StanfordSpezi/SpeziStudyPlatform-Server) | Swift | Shared backend | Integrate with current Vapor REST service through its shared API; keep study logic independent of Firebase. |
| [SpeziVibeReactNativeTemplate](https://github.com/StanfordSpezi/SpeziVibeReactNativeTemplate) | TypeScript | Adjacent platform | Useful scaffolding reference; native Kotlin completion remains a separate implementation target. |
| [SpeziStudyPlatform-API](https://github.com/StanfordSpezi/SpeziStudyPlatform-API) | Swift | Study milestone | Generate or implement a Kotlin client from the shared OpenAPI specification and test domain serialization. |
