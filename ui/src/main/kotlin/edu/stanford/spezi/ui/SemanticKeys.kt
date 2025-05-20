package edu.stanford.spezi.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics

object SemanticKeys {
    val Content = SemanticsPropertyKey<String>("TestContentSemanticKey")
    val Color = SemanticsPropertyKey<Color>("TestColorSemanticKeys")
    val DrawableRes = SemanticsPropertyKey<Int>("TestDrawableResSemanticKeys")
}

fun Modifier.testColorIdentifier(color: Color) = semantics {
    this[SemanticKeys.Color] = color
}

fun Modifier.testContentIdentifier(content: String) = semantics {
    this[SemanticKeys.Content] = content
}

fun Modifier.testDrawableResIdentifier(@DrawableRes id: Int) = semantics {
    this[SemanticKeys.DrawableRes] = id
}
