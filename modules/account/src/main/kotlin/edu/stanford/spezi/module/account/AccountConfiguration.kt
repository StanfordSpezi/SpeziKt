package edu.stanford.spezi.module.account

import androidx.annotation.MainThread
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

// SpeziAccount


/*

interface AccountKeyInput<Value : Any> {
    val id: String?
    val name: String
    val category: AccountKeyCategory
    val displayView: @Composable (Value) -> Unit
    val entryView: @Composable (MutableState<Value>) -> Unit
}

annotation class AccountValue<Input: AccountKeyInput<*>>

data class NameAccountKeyInput(val input: Unit): AccountKeyInput<String> {
    override val id: String? get() = null
    override val name: String get() = "Name"
    override val category: AccountKeyCategory get() = AccountKeyCategory.name
    override val displayView: @Composable (String) -> Unit get() = { text ->
        Text(text)
    }
    override val entryView: @Composable (MutableState<String>) -> Unit get() = { state ->
        TextField(value = state.value, onValueChange = { state.value = it })
    }
}

@AccountValue<NameAccountKeyInput>
val AccountDetails.name: String? get() = null
 */