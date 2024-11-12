package edu.stanford.spezi.module.account.account.modifier

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.state.ReceiveValidation
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.validation.views.ValidationResultsComposable
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

val LocalModifiedDetails = compositionLocalOf { AccountDetails() }

@Composable
fun <Value : Any> ValidateRequired(
    key: AccountKey<Value>,
    value: MutableState<Value>,
    content: @Composable () -> Unit,
) {
    // TODO: Why do we actually need the value to be used here? :D I just copied iOS here
    val innerValidation = remember { mutableStateOf(ValidationContext()) }
    val validation = remember { mutableStateOf(ValidationContext()) }
    val modifiedDetails = LocalModifiedDetails.current

    ReceiveValidation(validation) {
        if (innerValidation.value.isEmpty) {
            Validate(modifiedDetails.contains(key), StringResource("This field is required.")) {
                ReceiveValidation(innerValidation) {
                    content()
                }
            }

            Row {
                ValidationResultsComposable(
                    validation.value.allDisplayedValidationResults
                )
            }
        } else {
            ReceiveValidation(innerValidation) {
                content()
            }
        }
    }
}
