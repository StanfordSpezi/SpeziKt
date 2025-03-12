package edu.stanford.spezi.ui.validation.internal

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions

internal fun KeyboardActions.copy(
    onDone: (KeyboardActionScope.() -> Unit)? = this.onDone,
    onGo: (KeyboardActionScope.() -> Unit)? = this.onGo,
    onNext: (KeyboardActionScope.() -> Unit)? = this.onNext,
    onPrevious: (KeyboardActionScope.() -> Unit)? = this.onPrevious,
    onSearch: (KeyboardActionScope.() -> Unit)? = this.onSearch,
    onSend: (KeyboardActionScope.() -> Unit)? = this.onSend,
) = KeyboardActions(
    onDone = onDone,
    onGo = onGo,
    onNext = onNext,
    onPrevious = onPrevious,
    onSearch = onSearch,
    onSend = onSend,
)
