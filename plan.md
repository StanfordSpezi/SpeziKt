This plan is written to be self-contained. It will be pasted into the SpeziKt repository, where a fresh Claude instance (without access to the thesis repo) will continue the work. All knowledge from the thesis that is needed to build the plugins is embedded below verbatim, so the executing Claude does not need to read the thesis source files. The thesis is
 at github.com/PaulKraft/Research-PaulKraft for reference only; it is not a required input.

 ---
 1. Context and goal

 The thesis "Building a Digital Health Development Ecosystem for Native Mobile Applications" (Paul Johannes Kraft, TUM, 2024) produced the initial Kotlin/Android port of the Stanford Spezi framework plus a written translation guide, a module pattern catalog, and design-process advice. The thesis is frozen — submitted, graded, not under revision.

 Why this plan exists: the knowledge encoded in the thesis (translation rules, module patterns, design principles) is worth distributing as Claude Code plugins. Two plugins are in scope:

 - swift-kotlin-bridge — a generalizable plugin for any dev porting between Swift and Kotlin. Audience is wider than Spezi.
 - spezi — a Spezi-ecosystem plugin: module scaffolding, account/Firebase/onboarding wiring helpers, FHIR questionnaire authoring, and a design-principle code-review subagent.

 Critical constraint — SpeziKt has moved on since the thesis was written. The thesis's translation guide is a starting point, not ground truth. For every rule or pattern this plan embeds from the thesis, the executing Claude must verify it against the current SpeziKt code before encoding it into a plugin. Where the code has diverged from the thesis, the
 current code wins. See §5 for the exact verification protocol.

 ---
 2. What "done" looks like

 Two plugin directories inside this SpeziKt repo (or a sibling plugins repo — see §8 for the packaging decision deferred to the implementing team):

 - plugins/swift-kotlin-bridge/ — 1 skill with progressive-disclosure supporting files, 1 subagent for reviewing paired translations, 1 skill for walking through an existing translation as a learning aid.
 - plugins/spezi/ — 1 subagent (spezi-review) and 5 skills (module scaffold, account wiring, Firebase setup, onboarding flow, FHIR questionnaire authoring).

 Both installable via claude plugin install <path> from a clean config.

 ---
 3. Embedded thesis knowledge (the plugin content source)

 This section is the durable knowledge the plugins need. It was extracted from these thesis files so you don't need to read them: content/5_ObjectDesign/{1_BasePrinciple, 2_Foundation, 3_Spezi, 4_Storage, 5_Views, 6_Contact, 7_Onboarding, 8_Questionnaire, 9_Account, 10_Firebase, 11_TranslationGuide}.tex; content/4_SystemDesign/*.tex;
 content/7_Discussion/*.tex; code/*.swift and code/*.kt.

 3.1 Swift ↔ Kotlin translation rules

 Type system:
 - Simple enum → enum class.
 - enum with associated values → sealed interface + data class per case.
 - OptionSet → EnumSet<T> (prefer over BitSet for readability).
 - Protocol with static requirements: split into two interfaces — one for instance members, one for statics (invoked on a companion or singleton). Kotlin cannot express static protocol requirements directly.
 - Type erasure: Swift any SomeType → Kotlin SomeType<*>.
 - actor (exclusive access) → manual Mutex or ReentrantLock. No direct actor equivalent.
 - Property wrapper → delegated property (by). No equivalent of Swift's projected value ($property); access the delegate object directly.
 - Result builder (@resultBuilder) → type-safe builder with lambda-with-receiver. More verbose but equivalent.

 Concurrency:
 - Swift async/await → Kotlin suspend functions + a CoroutineScope.
 - Swift can open a Task without a scope; Kotlin requires an injected CoroutineScope — inject it via Hilt in Spezi Android. This has architectural impact: plan scope ownership early.

 System functionality:
 - FileManager → java.io / java.nio.file. Watch for path separator differences, permission models, cloud sync semantics.
 - Info.plist + requestAuthorization → AndroidManifest.xml + runtime requestPermissions. Method-call order matters on Android in ways it doesn't on iOS.

 Storage key identity:
 - iOS allows class name as storage key (type identity is stable).
 - Android requires explicit string keys — minification/R8/obfuscation will rename classes. This is a hard rule, not a preference.

 UI / SwiftUI → Jetpack Compose:
 - @Binding (two-way) → (value, onValueChanged) callback pair.
 - @Environment → CompositionLocal, but use sparingly. Prefer direct function parameters or Hilt-injected ViewModels. Behavior hidden in Environment will break when ported to Compose.
 - @Observable macro → ViewModel holding immutable state, updated via an onAction-style function.
 - @State → default to ViewModel; use remember { mutableStateOf(...) } only for local, ephemeral state.
 - Customization: SwiftUI modifiers frequently hide options in Environment. Compose exposes them as direct parameters. Always analyze the customization surface of a SwiftUI component before translating, or you will silently lose configuration points.
 - Navigation / view identity: SwiftUI can identify views by type; composables are functions and cannot. Use explicit string IDs on Android wherever Swift used type identity.

 Dependency injection:
 - Swift: SpeziAppDelegate + @Dependency property wrapper — runtime resolution.
 - Kotlin: Dagger Hilt — @Module + @Provides / @Binds, installed with @InstallIn(SingletonComponent::class). Compile-time validation.
 - Consequence: Kotlin needs more upfront wiring but catches errors earlier. Kotlin cannot inject into property-wrapper-like sites freely — plan injection points.

 3.2 Module pattern catalog

 BasePrinciple (translation philosophy). Three approaches were considered: (A) translate platform-specific APIs one-to-one, (B) build a shared abstraction layer, (C) separate platform implementations with aligned added features. The thesis chose (C): preserve platform idioms, harmonize the added capability. Rule for the plugins: when scaffolding a new Spezi
 module, platform-specific APIs stay native; added functionality is kept consistent across platforms.

 SpeziFoundation — KnowledgeSource + SharedRepository. Apps extend types by defining their own keys; values live in a shared repository keyed by those sources. Swift uses static properties on a protocol to define keys; Kotlin cannot — use object singletons or instance properties instead.

 Spezi (DI). See 3.1 above.

 SpeziStorage — three-way split (single-responsibility refactor from iOS):
 - KeyStorage — encryption keys (create / retrieve / delete).
 - CredentialStorage — username / password / server tuples.
 - LocalStorage — file I/O. Type-based key on iOS; explicit string key on Android.
 - Different encryption algorithms are allowed across platforms as long as the interface matches.

 SpeziViews:
 - Button wraps the platform button, adapts an async action to a loading state. ViewState enum tracks idle | processing | error; caller passes state + onStateChanged callback instead of a Binding.
 - Validation: ValidationEngine runs ValidationRules over input and produces CapturedValidationState; ValidationContext batches validation across a form. Kotlin uses composable wrappers (Validate, ReceiveValidation) where Swift uses modifiers.
 - Localization: StringResource / ImageResource types defer resolution to render time, because SwiftUI resolves at instantiation and Compose is different.
 - Markdown: Compose has no built-in markdown, so the framework ships a deliberately-minimal parser covering headers and bold. Don't overreach.

 SpeziContact. Simple: Contact data class holding PersonNameComponents, image, title, organization; ContactOption for call / email / web actions.

 SpeziOnboarding.
 - Swift uses a view-builder DSL; steps are identified by view type or .onboardingIdentifier modifier.
 - Kotlin uses a function + resolver that produces composables keyed by explicit string IDs; routes are pre-defined.
 - Content patterns: one step per screen (avoid cognitive overload), consent flow renders Markdown + name + signature into a PDF.

 SpeziQuestionnaire.
 - Swift stack: Apple ResearchKit + Apple FHIRModels + StanfordBDHG's ResearchKitOnFHIR.
 - Kotlin stack: Google Android FHIR SDK + HAPI FHIR for encode/decode.
 - Minimal surface: a single QuestionnaireView / QuestionnaireComposable per platform accepting a FHIR Questionnaire resource. Different SDKs below, unified top-level API.

 SpeziAccount.
 - Account holds user state, notifications, user details.
 - AccountService protocol: login / logout / delete. Firebase implementation is provided.
 - IdentityProvider strategy: email/password, anonymous, Apple SSO, Google SSO.
 - AccountKey<Value> protocol defines each account property (birth date, gender, custom fields): UI display + entry + serialization.
 - AccountDetails is the key-value store for account attributes.
 - AccountStorageProvider abstracts persistence (local or Firestore).
 - DI circularity caveat on Android: Account must inject into AccountService using Hilt's inject() function (not constructor injection) due to the cycle. Document this on the skill; scaffolded code must show the pattern.

 SpeziFirebase.
 - ConfigureFirebase is an initialization component that ensures Firebase is set up before any dependent service. Rule: every Firebase-using module declares a dependency on ConfigureFirebase so initialization order is correct. This is the single most error-prone Firebase gotcha the thesis calls out.
 - FirebaseAccountService — implements AccountService against Firebase Auth (email/password, anonymous, Apple SSO, Google SSO).
 - FirestoreAccountStorage — implements AccountStorageProvider against Firestore.

 3.3 Translation-process rules (from Discussion chapter)

 1. Specify the public API via stubs first. Parallelizes work; insulates app developers from translation churn.
 2. TDD underlying components before the UI integrates them.
 3. Implement and test data models + services next.
 4. Build UI components last, using previews and UI tests.

 Hard rules:
 - Never do one-to-one line translation. Translate by structure and dependency graph.
 - Identify dependencies first. Providers and utilities before their consumers.
 - Analyze customization surface before translating a SwiftUI modifier; hidden Environment knobs are the #1 source of lost capability.
 - Don't rely on SwiftUI Environment for core behavior if you plan to port — design for explicit parameter passing from the start.
 - Inject CoroutineScope. Don't assume free task-spawning.
 - String keys on Android. Never use class-name keys for anything that will be minified.
 - ViewModel first. Default to ViewModel-based state, not scattered @State, for anything that will have a Compose counterpart.

 3.4 Design goals (G1–G5)

 - G1 Familiarity — platform developers get platform idioms (Swift conventions ≠ Kotlin conventions).
 - G2 Consistency — across platforms: align naming, responsibilities, core architecture.
 - G3 Access to new/low-level APIs — don't abstract away platform power.
 - G4 Flexibility / reusability — avoid app-domain specifics.
 - G5 Dependability — medical context demands reliability.

 G1 and G2 can conflict. The thesis's resolution: expose platform-specific APIs natively (G1), but standardize the framework-added features across platforms (G2), and document divergences explicitly.

 3.5 Paired code examples (from Thesis/code/)

 These pairs exist in the thesis; they're reproduced in spirit (not copied) in the plugin's examples/ directories. When the executing Claude builds the plugin, it should regenerate equivalent examples from current SpeziKt code rather than from the thesis snippets, because SpeziKt has evolved.

 - Configuration.swift ↔ Configuration.kt — app startup and DI setup. Shows SpeziAppDelegate + @Dependency vs. Hilt @Module + @Provides / @Binds.
 - OnboardingStack.swift ↔ OnboardingStack.kt — onboarding flow construction. Shows builder DSL vs. function + resolver + explicit string IDs.
 - SpeziOnboarding.swift ↔ SpeziOnboarding.kt — module entry points.
 - ValidationExample.swift ↔ ValidationExample.kt — form validation. Shows .validate modifier + @receiveValidation vs. Validate / ReceiveValidation composables + suspend buttons.
 - Button.kt — illustrates the ViewState pattern alone.
 - AccountService.kt — illustrates the Hilt injection pattern and DI circularity workaround.

 ---
 4. Deliverables in detail

 4.1 Plugin: swift-kotlin-bridge

 Audience: any developer translating between Swift and Kotlin. No Spezi knowledge required.

 Structure:

 plugins/swift-kotlin-bridge/
 ├── plugin.json
 ├── README.md
 ├── skills/
 │   ├── swift-kotlin-bridge/
 │   │   ├── SKILL.md
 │   │   ├── type-system.md
 │   │   ├── state-management.md
 │   │   ├── concurrency.md
 │   │   ├── dependency-injection.md
 │   │   ├── property-wrappers-and-builders.md
 │   │   ├── storage-and-permissions.md
 │   │   ├── view-state-pattern.md
 │   │   └── translation-process.md
 │   └── translation-walkthrough/
 │       ├── SKILL.md
 │       └── examples/        # paired Swift/Kotlin excerpts drawn from current SpeziKt
 └── agents/
     └── translation-review.md

 skills/swift-kotlin-bridge/SKILL.md — top-level rules table, brief, with links into the supporting files. Auto-invokes when:
 - a file pair of .swift and .kt with the same base name is open, or
 - the user asks to "port", "translate", or "convert" between Swift and Kotlin, or
 - both languages appear in a single diff or review.

 Stays silent for pure single-language work. Content is §3.1 above, condensed to a rules table with one-line rationale per rule and a link to the relevant supporting file.

 Supporting files — one per conceptual area in §3.1. Each contains: the rule, a short Swift snippet, the Kotlin equivalent, and the why. Examples come from current SpeziKt (see §5 for how to verify).

 agents/translation-review.md (subagent: translation-review). The user asked for a way to check and verify existing translations. This is a read-only subagent.

 - Tools: Read, Grep, Glob. No Write, Edit, Bash.
 - Input: one of — (a) two file paths (Swift + Kotlin), (b) a directory to scan for pairs, (c) a diff.
 - Output: a Markdown punch list. For each paired API or type, note: "matches rule X (ok)", "violates rule Y (problem)", or "no clear counterpart — human judgment needed". Cite the specific rule from the skill. Empty list if everything checks out.
 - Rules applied: all of §3.1, plus the process rules from §3.3.
 - Fires on explicit user invocation. Not auto-invoked — review is heavy.

 skills/translation-walkthrough/SKILL.md. The user also asked for a way to learn existing translations. This is a teaching skill.

 - Invocation: explicit (/translation-walkthrough <path-to-swift> or similar), or auto when the user asks "why is this translated this way?" or "explain this Swift→Kotlin pair".
 - Behavior: given a Swift file with a corresponding Kotlin file (or vice versa), walk the reader through the translation in sequence: for each construct, identify the Swift idiom, name the applicable rule from §3.1, show the Kotlin equivalent, explain the why (what would go wrong with the naive translation), and flag any customization-surface losses or wins.
 - Good source material: the Configuration, OnboardingStack, ValidationExample pairs in current SpeziKt.

 4.2 Plugin: spezi

 Audience: Spezi contributors and users.

 Structure:

 plugins/spezi/
 ├── plugin.json
 ├── README.md
 ├── agents/
 │   └── spezi-review.md
 └── skills/
     ├── spezi-module-scaffold/
     │   ├── SKILL.md
     │   └── templates/        # boilerplate for a new module
     ├── spezi-account-wiring/
     │   └── SKILL.md
     ├── spezi-firebase-setup/
     │   └── SKILL.md
     ├── spezi-onboarding-flow/
     │   └── SKILL.md
     └── fhir-questionnaire-author/
         └── SKILL.md

 agents/spezi-review.md (subagent: spezi-review). Read-only design-principle reviewer.

 - Tools: Read, Grep, Glob. No writes.
 - Input: a directory, a diff, or a list of files.
 - Checks (all drawn from §3):
   - @State used where a ViewModel is indicated for cross-platform code.
   - Static protocol requirements that won't port to Kotlin.
   - Behavior hidden in @Environment that will not survive a Compose port.
   - Storage code using class names as keys on Android.
   - Firebase-using code that doesn't declare a ConfigureFirebase dependency.
   - Async buttons without the ViewState pattern.
   - AccountService wiring that creates DI circularity (constructor injection where inject() is needed).
   - Absence of a stubs-first / layered translation approach when a new cross-platform module is being added.
   - G1/G2 violations where a platform idiom has been forced onto the other platform.
 - Output: Markdown punch list, empty when clean.

 Skill: spezi-module-scaffold.
 - Inputs: module name, target platform (swift | kotlin | both).
 - Outputs: package layout, DI wiring (SpeziAppDelegate entries for Swift; Hilt @Module with @Provides/@Binds for Kotlin), a KnowledgeSource example, tests folder, README skeleton, SPDX headers in the Stanford BDHG format already present across this repo.
 - Uses templates/ directory with parameterized files.

 Skill: spezi-account-wiring.
 - Walks through: defining custom AccountKeys, selecting IdentityProviders (email/password, anonymous, Apple, Google), picking a storage provider (local or FirestoreAccountStorage), wiring without DI circularity using inject() on Android.
 - Generates Hilt module code on Android; SpeziAppDelegate configuration on Swift.

 Skill: spezi-firebase-setup.
 - Generates ConfigureFirebase registration + FirebaseAccountService + FirestoreAccountStorage wiring.
 - Enforces the dependency ordering rule in the generated code (comments + actual @Dependency declarations).

 Skill: spezi-onboarding-flow.
 - Scaffolds an OnboardingStack — consent step (with Markdown + signature + PDF output), questionnaire step, account step, notifications step.
 - Swift emits the builder DSL; Kotlin emits function + resolver + explicit string IDs per step.

 Skill: fhir-questionnaire-author.
 - Creates a HL7 FHIR Questionnaire resource from a brief spec; validates against the FHIR schema.
 - Wires it into SpeziQuestionnaire on either platform — ResearchKitOnFHIR on iOS, Android FHIR SDK + HAPI on Android.
 - Includes a renderability check: does every item.type in the questionnaire map to something renderable on both platforms, given the item types each SDK supports?

 ---
 5. Verify-against-current-SpeziKt protocol (mandatory)

 Before the executing Claude commits any rule or pattern from §3 to a plugin file:

 1. Locate the corresponding current-code artifact in SpeziKt. E.g. before writing the ViewState rule, find the actual ViewState definition in the SpeziViews Kotlin module today. Use Grep / Glob to locate it.
 2. Compare. If the current code matches the thesis description → encode the rule as-is, citing the current file path in the supporting .md.
 3. If the current code has diverged (renamed types, different enum cases, different split, different injection pattern, different module boundaries): the current code wins. Update the plan's embedded knowledge in the plugin's source file and add a note in that file's front matter along the lines of: # Updated from thesis §X.Y — current SpeziKt diverges: ….
 4. If the current code has dropped the pattern entirely (e.g. the thesis's three-way Storage split was later merged back): do not encode the dropped pattern. Note the divergence in the plugin's README under a "Known differences from the thesis" section.
 5. Extract paired examples from the current code, not from the thesis's Thesis/code/ directory. The thesis examples are frozen; the plugin should teach on live code.
 6. List the checked modules in the plugin README so future maintainers know what was verified and when (dated entry).

 This step is non-negotiable. If it is skipped, the plugins will teach rules that SpeziKt has moved past.

 ---
 6. Packaging and repo layout

 Both plugins live under plugins/ in the SpeziKt repo root. Each plugin has its own plugin.json and is installable independently via claude plugin install ./plugins/<name>.

 If the SpeziKt maintainers later prefer to extract swift-kotlin-bridge to its own repo (since it's broader than Spezi), the split is clean: the plugin has no imports from the rest of the SpeziKt codebase, and its supporting files are self-contained. This decision is deferred — co-location is simpler to start with, and splitting later is cheap.

 plugin.json minimal contents for each:

 {
   "name": "swift-kotlin-bridge",
   "version": "0.1.0",
   "description": "Rules, review, and walkthroughs for Swift ↔ Kotlin translation.",
   "author": "Paul Johannes Kraft",
   "license": "MIT"
 }

 (Analogous for spezi.)

 ---
 7. Build order

 Recommended order. Each step is verifiable in isolation; nothing forces the next step until its acceptance criteria are met.

 1. Set up repo scaffolding. Create plugins/swift-kotlin-bridge/ and plugins/spezi/ with empty plugin.json, README.md, and the directory layout from §4. Commit.
 2. Verify against current SpeziKt (§5). Grep for: ViewState, AccountKey, AccountService, ConfigureFirebase, KnowledgeSource, Hilt module patterns, the Onboarding resolver. Produce a short internal VERIFICATION.md in each plugin listing what was checked and where it lives in current code.
 3. Build swift-kotlin-bridge/skills/swift-kotlin-bridge/. Start with SKILL.md top-level rules table; fill supporting files one at a time, grounding examples in current SpeziKt.
 4. Build swift-kotlin-bridge/agents/translation-review.md. Test it on three known pairs drawn from current SpeziKt and confirm the punch list is empty (known-good) or points to known issues.
 5. Build swift-kotlin-bridge/skills/translation-walkthrough/. Walk through the Configuration pair (current SpeziKt version) as the canonical example; include the output verbatim in examples/.
 6. Build spezi/agents/spezi-review.md using the checks listed in §4.2. Test on a deliberately-bad sample and a known-good sample.
 7. Build the five Spezi skills in order of how much they depend on other skills: spezi-module-scaffold first, then spezi-firebase-setup (small and foundational), then spezi-account-wiring (depends on Firebase setup), then spezi-onboarding-flow, then fhir-questionnaire-author.
 8. READMEs. Each plugin's README gets a one-line install command, the auto-invocation triggers, the thesis chapter each rule traces to, and a "Known differences from the thesis" section enumerating anything §5 surfaced.

 ---
 8. Verification — how to know it works

 Per plugin:

 swift-kotlin-bridge:
 - claude plugin install ./plugins/swift-kotlin-bridge succeeds from a clean Claude Code config.
 - Opening a directory with a paired .swift/.kt file and asking "why is this translated this way?" auto-invokes the walkthrough skill.
 - Pure-Swift or pure-Kotlin work unrelated to porting does not trigger the skill.
 - translation-review run against a known-good SpeziKt pair returns an empty punch list; run against a deliberately-broken pair (e.g. a Swift actor translated naively as a Kotlin class) surfaces the violation and names the rule.
 - Every rule in the skill files cites a specific location in current SpeziKt.

 spezi:
 - claude plugin install ./plugins/spezi succeeds.
 - spezi-review returns a non-empty punch list on a deliberately-bad sample (e.g. SwiftUI view using @State for cross-platform data + @Environment for a feature toggle + class-name storage key) and an empty list on a file from current SpeziKt that follows the patterns.
 - Each of the five skills, when invoked, emits output that parses (Kotlin) or compiles (Swift) against the current module versions in this repo. "Compiles" = ./gradlew assemble on a throwaway target module that includes the generated code.
 - The scaffold skill's output matches the existing conventions in this repo: license headers present, package naming consistent, DI wiring style matching what SpeziKt already uses.

 Both:
 - READMEs render on GitHub; install commands are copy-pasteable.
 - A dated "Known differences from the thesis" section exists where relevant.

 ---
 9. Considered but deferred

 Captured so future work can pick them up:

 - bdhg-license plugin — PreToolUse hook + pre-commit check that inserts or validates Stanford BDHG SPDX headers and .license siblings across any BDHG repo. Useful, small, reusable; deprioritized by the user for this pass.
 - design-goals-gate — G1–G5 audit for arbitrary cross-platform libraries. Probably better delivered as a CLAUDE.md template than a plugin.
 - cross-platform-parity-audit — two-repo diff subagent that enumerates API parity between a Swift and a Kotlin counterpart. Needs a clearer input contract.
 - translation-stub generator — given a Swift module, emit Kotlin signature stubs to parallelize translation work. Would need AST parsing; probably a separate CLI tool the skill invokes.
 - digital-health-primer — reads more like documentation than an agent; probably belongs in a docs/ page, not a plugin.

 ---
 10. Files the executing Claude must not rely on

 The executing Claude in SpeziKt will not have access to:
 - The thesis LaTeX source (Thesis/content/**, Thesis/code/**). All needed content is in §3 above.
 - The thesis references.bib or PDF.
 - Any private notes from the thesis author.

 It will have access to:
 - Current SpeziKt code (this repo).
 - Public Spezi GitHub organization (github.com/StanfordSpezi, github.com/StanfordBDHG) — use sparingly, only when SpeziKt itself is ambiguous.
 - Claude Code documentation for plugin structure, skill frontmatter, subagent conventions.

 If something in §3 cannot be verified against current SpeziKt and no public-code source clarifies it, flag it in the plugin's README under "Unverified — originated from thesis, not confirmed in current code" rather than encoding it silently.

