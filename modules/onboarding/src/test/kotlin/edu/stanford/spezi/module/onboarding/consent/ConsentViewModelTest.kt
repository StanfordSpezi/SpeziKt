package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.ui.graphics.Path
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.spezi.ui.views.text.MarkdownElement
import edu.stanford.spezi.spezi.ui.views.text.MarkdownParser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConsentViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val consentManager: ConsentManager = mockk(relaxed = true)
    private val markdownParser: MarkdownParser = mockk(relaxed = true)
    private val pdfCreationService: PdfCreationService = mockk(relaxed = true)
    private val userSessionManager: UserSessionManager = mockk(relaxed = true)
    private val viewModel by lazy {
        ConsentViewModel(
            consentManager = consentManager,
            markdownParser = markdownParser,
            pdfCreationService = pdfCreationService,
            userSessionManager = userSessionManager
        )
    }

    @Before
    fun setup() {
        every { markdownParser.parse(any()) } returns emptyList()
    }

    @Test
    fun `it should update firstName on TextFieldUpdate action correctly`() = runTestUnconfined {
        // Given
        val name = "Kilian"
        val action = ConsentAction.TextFieldUpdate(name, TextFieldType.FIRST_NAME)

        // When
        viewModel.onAction(action)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(name).isEqualTo(uiState.firstName.value)
    }

    @Test
    fun `it should update lastName on TextFieldUpdate action correctly`() = runTestUnconfined {
        // Given
        val lastName = "Lastname"
        val action =
            ConsentAction.TextFieldUpdate(newValue = lastName, type = TextFieldType.LAST_NAME)

        // When
        viewModel.onAction(action)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(lastName).isEqualTo(uiState.lastName.value)
    }

    @Test
    fun `it should handle AddPath action correctly`() = runTestUnconfined {
        // Given
        val action = ConsentAction.AddPath(Path())

        // When
        viewModel.onAction(action)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(uiState.paths.size).isEqualTo(1)
    }

    @Test
    fun `it should handle Undo action correctly`() = runTestUnconfined {
        // Given
        viewModel.onAction(ConsentAction.AddPath(Path()))
        val action = ConsentAction.Undo

        // When
        viewModel.onAction(action)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(uiState.paths.size).isEqualTo(0)
    }

    @Test
    fun `init block should fetch ConsentData correctly`() = runTestUnconfined {
        // Given
        val markdownText = "some markdown text"
        val elements: List<MarkdownElement> = emptyList()
        every { markdownParser.parse(markdownText) } returns elements
        coEvery { consentManager.getMarkdownText() } returns markdownText

        // When
        val uiState = viewModel.uiState.first()

        // Then
        assertThat(uiState.markdownElements).isEqualTo(elements)
    }

    @Test
    fun `it should invoke handle consent action correctly on success case`() = runTestUnconfined {
        // given
        val pdfBytes = byteArrayOf()
        coEvery { pdfCreationService.createPdf(viewModel.uiState.value) } returns pdfBytes
        coEvery { userSessionManager.uploadConsentPdf(pdfBytes) } returns Result.success(Unit)

        // when
        viewModel.onAction(action = ConsentAction.Consent)

        // then
        coVerify { consentManager.onConsented() }
    }

    @Test
    fun `it should invoke handle consent action correctly on error case`() = runTestUnconfined {
        // given
        val error: Throwable = mockk()
        val pdfBytes = byteArrayOf()
        coEvery { pdfCreationService.createPdf(viewModel.uiState.value) } returns pdfBytes
        coEvery { userSessionManager.uploadConsentPdf(pdfBytes) } returns Result.failure(error)

        // when
        viewModel.onAction(action = ConsentAction.Consent)

        // then
        coVerify { consentManager.onConsentFailure(error) }
    }
}
