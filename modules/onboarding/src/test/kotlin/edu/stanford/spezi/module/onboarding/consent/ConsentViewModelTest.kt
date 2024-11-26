package edu.stanford.spezi.module.onboarding.consent

import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.Path
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.design.component.markdown.MarkdownParser
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.module.onboarding.spezi.consent.ConsentDataSource
import edu.stanford.spezi.module.onboarding.spezi.consent.ConsentDocumentExportConfiguration
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

    private val markdownParser: MarkdownParser = mockk(relaxed = true)
    private val consentDataSource: ConsentDataSource = mockk(relaxed = true)
    private val consentPdfService: ConsentPdfService = mockk(relaxed = true)
    private val userSessionManager: UserSessionManager = mockk(relaxed = true)
    private val viewModel by lazy {
        ConsentViewModel(
            pdfService = consentPdfService,
            consentDataSource = consentDataSource,
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
        assertThat(name).isEqualTo(uiState.name.givenName)
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
        assertThat(lastName).isEqualTo(uiState.name.familyName)
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

    /*
    @Test
    fun `it should invoke handle consent action correctly on success case`() = runTestUnconfined {
        // given
        val pdfDocument = PdfDocument()
        val documentIdentifier = "testDocument"
        val configuration = ConsentDocumentExportConfiguration()
        coEvery {
            consentPdfService.createDocument(
                configuration,
                viewModel.uiState.value.name,
                viewModel.uiState.value.paths,
                viewModel.uiState.value.markdownElements
            )
        } returns pdfDocument

        // when
        viewModel.onAction(action = ConsentAction.Consent(documentIdentifier, configuration))

        // then
        coVerify { consentDataSource.store({ pdfDocument }, documentIdentifier) }
    }
     */
}
