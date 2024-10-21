# Module onboarding

This module provides multiple onboarding screens:

<img src="screens/consent.jpg" width="300" alt="Consent Screen"/>

<img src="screens/invitation.jpg" width="300" alt="Invitation Screen"/>

<img src="screens/onboarding.jpg" width="300" alt="Onboarding Screen"/>

<img src="screens/sequential_step_1.jpg" width="300" alt="Sequential Onboarding Screen Step 1"/>

<img src="screens/sequential_step_2.jpg" width="300" alt="Sequential Onboarding Screen Step 2"/>

# Package edu.stanford.spezi.module.onboarding

The `onboarding` package provides the `OnboardingNavigationEvent` sealed class, which defines the
possible navigation events for the onboarding module. The `OnboardingNavigationEvent` class is used
to navigate between the different onboarding screens.

# Package edu.stanford.spezi.module.onboarding.consent

The `consent` package handles user consent screens, ensuring that users agree to the necessary terms
and conditions before proceeding. The consent document gets build by a Markdown Text. The text can
be provided via an implementation of the `ConsentManager` interface. This interface also provides
functions to handle the case when the user has consented as well as a consent failure. The Screen
can be used anywhere after providing this interface.

Example implementation:

```kotlin
class EngageConsentManager @Inject internal constructor(
    private val navigator: Navigator,
    private val messageNotifier: MessageNotifier,
) : ConsentManager {

    override suspend fun getMarkdownText(): String {
        return """
        # Consent
        The ENGAGE-HF Android Mobile Application will connect to external devices via Bluetooth to record personal health information, including weight, heart rate, and blood pressure.
            
        Your personal information will only be shared with the research team conducting the study.
        """.trimIndent()
    }

    override suspend fun onConsented() {
        navigator.navigateTo(AppNavigationEvent.AppScreen(clearBackStack = true))
    }

    override suspend fun onConsentFailure(error: Throwable) {
        messageNotifier.notify(message = "Something went wrong, failed to submit the consent!")
    }
}
```

# Package edu.stanford.spezi.module.onboarding.invitation

The `invitation` package manages the invitation process, allowing users to join an app.
The `InvitationCodeScreenData` provides all necessary data for the view like the title, the
description and the redeem Action. It is provided via a implementation of the `InvitationCodeScreen`
interface. The Screen can be used anywhere after providing this interface.

Example implementation:

```kotlin
class EngageInvitationCodeRepository @Inject constructor(
    private val navigator: Navigator,
) : InvitationCodeRepository {

    override fun getScreenData(): InvitationCodeScreenData {
        return InvitationCodeScreenData(
            title = "Invitation Code",
            description = "Please enter your invitation code to join the ENGAGE-HF study.",
            redeemAction = { navigator.navigateTo(AppNavigationEvent.AppScreen(true)) },
        )
    }
}
```

# Package edu.stanford.spezi.module.onboarding.onboarding

The `onboarding` package contains the main onboarding screens, guiding users through the initial
setup and introduction to the app. The Screen can be used anywhere after providing
the `OnboardingData` via an implementation of the `OnboardingRepository` interface.

Example implementation:

```kotlin
class EngageOnboardingRepository @Inject constructor(
    private val navigator: Navigator,
) : OnboardingRepository {

    override suspend fun getOnboardingData(): Result<OnboardingData> = Result.success(
        OnboardingData(
            areas = listOf(
                Area(
                    title = "Join the Study",
                    iconId = R.drawable.ic_groups,
                    description = "Connect to your study via an invitation code from the researchers."
                ),
                Area(
                    title = "Complete Health Checks",
                    iconId = R.drawable.ic_assignment,
                    description = "Record and report health data automatically according to a schedule set by the research team."
                ),
                Area(
                    title = "Visualize Data",
                    iconId = R.drawable.ic_vital_signs,
                    description = "Visualize your heart health progress throughout participation in the study."
                )
            ),
            title = "Welcome to ENGAGE-HF",
            subTitle = "Remote study participation made easy.",
            continueButtonText = "Learn more",
            continueButtonAction = { navigator.navigateTo(OnboardingNavigationEvent.SequentialOnboardingScreen) }
        )
    )
}

```

# Package edu.stanford.spezi.module.onboarding.sequential

The `sequential` package ensures that the onboarding steps are followed in a specific sequence,
providing a smooth and logical flow for the user. The Screen can be used anywhere after providing
the `SequentialOnboardingData` via an implementation of the `SequentialOnboardingRepository`
interface.

Example implementation:

```kotlin
class EngageSequentialOnboardingRepository @Inject internal constructor(
    private val navigator: Navigator,
) : SequentialOnboardingRepository {
    override suspend fun getSequentialOnboardingData(): SequentialOnboardingData {
        return SequentialOnboardingData(
            steps = listOf(
                Step(
                    title = "Pair with Devices",
                    description = "Pair with the provided weight scale and blood pressure cuff in Bluetooth settings.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_bluetooth
                ),
                Step(
                    title = "Record Health Data",
                    description = "Use the weight scale and blood pressure cuff to record health data in Heart Health.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_assignment
                ),
                Step(
                    title = "Tune Medications",
                    description = "See your medication dosage, schedule, and updates in Medications.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_medication
                ),
                Step(
                    title = "Summarize",
                    description = "Generate and export a full PDF health report in Health Summary.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_assignment
                ),
                Step(
                    title = "Learn",
                    description = "Learn more about your medications and heart health in Education.",
                    icon = edu.stanford.spezi.core.design.R.drawable.ic_school
                )
            ),
            actionText = "Start",
            onAction = {
                navigator.navigateTo(AccountNavigationEvent.LoginScreen)
            }
        )
    }
}
```
