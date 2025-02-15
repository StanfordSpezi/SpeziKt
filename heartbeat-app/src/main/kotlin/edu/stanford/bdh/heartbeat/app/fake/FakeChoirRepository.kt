package edu.stanford.bdh.heartbeat.app.fake

import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.DisplayStatus
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormField
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormFieldValue
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormQuestion
import edu.stanford.bdh.heartbeat.app.choir.api.types.Onboarding
import edu.stanford.bdh.heartbeat.app.choir.api.types.Participant
import edu.stanford.bdh.heartbeat.app.choir.api.types.QuestionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeChoirRepository @Inject constructor() : ChoirRepository {
    private val assessments = assesmentSteps()
    private var assessmentIndex = 0

    override suspend fun putParticipant(participant: Participant): Result<Unit> {
        return success(Unit)
    }

    override suspend fun unenrollParticipant(): Result<Unit> {
        return success(Unit)
    }

    override suspend fun getOnboarding(): Result<Onboarding> {
        return success(onboarding)
    }

    override suspend fun continueAssessment(
        token: String,
        submit: AssessmentSubmit,
    ): Result<AssessmentStep> {
        val result = assessments.getOrNull(assessmentIndex) ?: return Result.failure(Error("Done"))
        return Result.success(result).also {
            assessmentIndex++
        }
    }

    override fun clear() {
        // No-op
    }

    private fun <T> success(value: T) = Result.success(value)

    private companion object {
        val displayStatus = DisplayStatus(
            compatLevel = "1.0",
            questionId = "q123",
            questionType = QuestionType.FORM,
            surveyToken = "token_ABC123",
            stepNumber = "1",
            progress = 0.25,
            surveyProviderId = "provider_XYZ",
            surveySectionId = "section_001",
            surveySystemName = "onboarding_survey",
            serverValidationMessage = null,
            sessionToken = "session_98765",
            sessionStatus = DisplayStatus.SessionStatus.QUESTION,
            resumeToken = "resume_456",
            resumeTimeoutMillis = "30000",
            styleSheetName = "default_theme",
            pageTitle = "Welcome to Onboarding",
            locale = "en-US",
            showBack = true,
        )

        val formQuestions = listOf(
            FormQuestion(
                title1 = "Have you been diagnosed with atrial fibrillation or atrial flutter? Atrial fibrillation or flutter causes the heart to flutter or not pump well, which can lead to blood clots.",
                fields = listOf(
                    FormField(
                        fieldId = "2:0:patient_afib",
                        type = FormField.Type.RADIOS,
                        required = true,
                        values = listOf(
                            FormFieldValue(id = "1", label = "Yes"),
                            FormFieldValue(id = "0", label = "No")
                        )
                    )
                )
            ),
            FormQuestion(
                title1 = "Based on your answers we would like to consider you for this study. Please provide your contact information:",
                fields = listOf(
                    FormField(
                        fieldId = "10:0:first_name",
                        type = FormField.Type.TEXT,
                        label = "First Name",
                        required = true
                    ),
                    FormField(
                        fieldId = "10:1:middle_name",
                        type = FormField.Type.TEXT,
                        label = "Middle Name"
                    ),
                    FormField(
                        fieldId = "10:2:last_name",
                        type = FormField.Type.TEXT,
                        label = "Last Name",
                        required = true
                    ),
                    FormField(
                        fieldId = "10:3:email",
                        type = FormField.Type.TEXT,
                        label = "Email Address",
                        required = true
                    ),
                    FormField(
                        fieldId = "10:4:home_phone",
                        type = FormField.Type.TEXT,
                        label = "Home Phone (with area code)"
                    ),
                    FormField(
                        fieldId = "10:5:mobile_phone",
                        type = FormField.Type.TEXT,
                        label = "Mobile Phone (with area code)",
                        required = true
                    ),
                    FormField(
                        fieldId = "10:6:sms_ok",
                        type = FormField.Type.RADIOS,
                        required = true,
                        values = listOf(
                            FormFieldValue(
                                id = "1",
                                label = "Yes, I agree to receive research-related contact."
                            ),
                            FormFieldValue(
                                id = "0",
                                label = "No, I do not want to receive research-related contact."
                            )
                        )
                    )
                )
            ),
            FormQuestion(
                title1 = "Please enter your preferred mailing address.",
                fields = listOf(
                    FormField(
                        fieldId = "30:0:address1",
                        type = FormField.Type.TEXT,
                        label = "Address Line 1",
                        required = true
                    ),
                    FormField(
                        fieldId = "30:1:address2",
                        type = FormField.Type.TEXT,
                        label = "Address Line 2"
                    ),
                    FormField(
                        fieldId = "30:2:city",
                        type = FormField.Type.TEXT,
                        label = "City",
                        required = true
                    ),
                    FormField(
                        fieldId = "30:3:state",
                        type = FormField.Type.DROPDOWN,
                        label = "State",
                        required = true,
                        values = listOf(
                            FormFieldValue(id = "1", label = "Alabama"),
                            FormFieldValue(id = "2", label = "Alaska"),
                            FormFieldValue(id = "3", label = "Arizona"),
                            FormFieldValue(id = "4", label = "Arkansas"),
                            FormFieldValue(id = "5", label = "California"),
                            FormFieldValue(id = "6", label = "Colorado")
                        )
                    ),
                    FormField(
                        fieldId = "30:4:zip",
                        type = FormField.Type.TEXT,
                        label = "Zip Code",
                        required = true
                    )
                )
            ),
            FormQuestion(
                title1 = """
        Please tell us your sex at birth<br>
        <p class="cursive">
        Sex refers to a set of biological attributes in humans and animals. It is associated with different biological
        and physiological characteristics of males and females, such as reproductive organs, chromosomes, hormones, etc.
        We need this information to make sure that we are being inclusive of people of different sexes.
        </p>
    """.trimIndent(),
                fields = listOf(
                    FormField(
                        fieldId = "34:0:birth_sex",
                        type = FormField.Type.RADIOS,
                        label = "",
                        required = true,
                        values = listOf(
                            FormFieldValue(id = "1", label = "Female"),
                            FormFieldValue(id = "2", label = "Male"),
                            FormFieldValue(id = "0", label = "Prefer not to say")
                        )
                    )
                )
            ),
            FormQuestion(
                title1 = "Do you have any of the below health conditions? Please select all that apply.",
                fields = listOf(
                    FormField(
                        fieldId = "45:0:health1",
                        type = FormField.Type.CHECKBOXES,
                        label = "",
                        required = true,
                        values = listOf(
                            FormFieldValue(id = "1", label = "High blood pressure"),
                            FormFieldValue(id = "2", label = "Diabetes"),
                            FormFieldValue(
                                id = "3",
                                label = "Atherosclerosis, a condition in which fatty deposits build up in the walls of the arteries"
                            ),
                            FormFieldValue(
                                id = "4",
                                label = "Heart problems for which you were admitted to a hospital"
                            ),
                            FormFieldValue(id = "0", label = "None of the above")
                        )
                    )
                )
            ),
            FormQuestion(
                title1 = """
        Please enter your year of birth.<br>
        <p class="cursive">We need this information because some studies are only suitable for people above or below
        a certain age.</p>
    """.trimIndent(),
                fields = listOf(
                    FormField(
                        fieldId = "32:0:birth_year",
                        type = FormField.Type.NUMBER,
                        label = "",
                        required = true,
                        min = "1900",
                        max = "2024"
                    )
                )
            ),
            FormQuestion(
                title1 = """
        <p class="title">STANFORD UNIVERSITY CONSENT<br>TO PARTICIPATE IN THE STANFORD HEARTBEAT STUDY</p>
        <table>
            <tr><td class="head-column">TITLE:</td><td class="text-column">STANFORD HEARTBEAT STUDY</td></tr>
            <tr><td class="head-column">PROTOCOL NO.:</td><td class="text-column">75132</td></tr>
            <tr><td class="head-column">SPONSOR:</td><td class="text-column">Janssen Research and Development LLC</td></tr>
            <tr><td class="head-column">PROTOCOL DIRECTOR:</td><td class="text-column">
                Marco Perez, MD<br>300 Pasteur Drive, Stanford, CA 94305, USA
            </td></tr>
            <tr><td class="head-column">CONTACT:</td><td class="text-column">(650)-307-7878</td></tr>
        </table>
        <p class="header">DESCRIPTION:</p>
        <p class="text">
            You are invited to participate in the Stanford Heartbeat Study. This registry connects volunteers for 
            Atrial Fibrillation research. If you wish to revoke your authorization, contact:
        </p>
        <b>Marco Perez, MD<br>300 Pasteur Drive, Stanford, CA 94305, USA</b>
        <p class="text"><b>Personal Information Used:</b> Contact info, demographics, medical history, and optional 
            smartphone data (GPS, PPG, ECG).</p>
        <p class="text"><b>Data Access:</b> Stanford University, study staff, regulatory bodies, and the study sponsor.</p>
        <p class="text"><b>Authorization Expiry:</b> December 31, 2075, or study completion.</p>
        <p class="text bold">Do you provide your authorization to use your health information for research?</p>
    """.trimIndent(),
                fields = listOf(
                    FormField(
                        fieldId = "22:0:consent_info",
                        type = FormField.Type.RADIOS,
                        label = "",
                        required = true,
                        values = listOf(
                            FormFieldValue("Yes, I provide my authorization.", "1"),
                            FormFieldValue("No, I do not provide my authorization.", "0")
                        )
                    ),
                    FormField(
                        fieldId = "22:1:consent_info_name",
                        type = FormField.Type.TEXT,
                        label = "Print Name of Adult Participant",
                        required = true
                    ),
                    FormField(
                        fieldId = "25:25",
                        type = FormField.Type.HEADING,
                        label = """
                <p class="text bold">Do you provide your consent to take part in the Stanford Heartbeat Study?</p>
            """.trimIndent()
                    ),
                    FormField(
                        fieldId = "25:0:consent",
                        type = FormField.Type.RADIOS,
                        label = "",
                        required = true,
                        values = listOf(
                            FormFieldValue("Yes, I provide my consent.", "1"),
                            FormFieldValue("No, I do not provide my consent.", "0")
                        )
                    ),
                    FormField(
                        fieldId = "25:1:consent_name",
                        type = FormField.Type.TEXT,
                        label = "Print Name of Adult Participant",
                        required = true
                    )
                )
            )
        )

        val onboarding = Onboarding(
            displayStatus = displayStatus,
            question = formQuestions.first()
        )

        fun assesmentSteps() = formQuestions.drop(1).map { question ->
            AssessmentStep(
                displayStatus = displayStatus,
                question = AssessmentStep.QuestionPayload(
                    value1 = question
                )
            )
        }

    }
}
