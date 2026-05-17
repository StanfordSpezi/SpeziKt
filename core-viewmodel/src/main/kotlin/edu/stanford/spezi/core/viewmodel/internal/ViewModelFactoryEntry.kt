package edu.stanford.spezi.core.viewmodel.internal

import androidx.lifecycle.ViewModel
import edu.stanford.spezi.core.viewmodel.ViewModelFactoryScope
import kotlin.reflect.KClass

/**
 * Wraps a single [ViewModel] factory lambda registered via
 * [edu.stanford.spezi.core.viewmodel.viewModel].
 */
@PublishedApi
internal data class ViewModelFactoryEntry(
    val factory: ViewModelFactoryScope.() -> ViewModel,
) {
    companion object {
        fun <VM : ViewModel> identifierFor(clazz: KClass<VM>): String = "viewmodel:${clazz.qualifiedName}"
    }
}
