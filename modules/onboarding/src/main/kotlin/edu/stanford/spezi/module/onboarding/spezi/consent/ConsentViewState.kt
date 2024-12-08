package edu.stanford.spezi.module.onboarding.spezi.consent

import android.graphics.pdf.PdfDocument
import edu.stanford.spezi.core.design.views.views.model.ViewState

sealed interface ConsentViewState {
    data class Base(val viewState: ViewState) : ConsentViewState
    data object NamesEntered : ConsentViewState
    data object Signing : ConsentViewState
    data object Signed : ConsentViewState
    data object Export : ConsentViewState
    data class Exported(val document: PdfDocument) : ConsentViewState
    data object Storing : ConsentViewState
}