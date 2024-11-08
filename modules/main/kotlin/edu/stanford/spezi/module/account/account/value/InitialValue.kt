package edu.stanford.spezi.module.account.account.value

import edu.stanford.spezi.module.account.account.value.InitialValue.Default
import edu.stanford.spezi.module.account.account.value.InitialValue.Empty

sealed interface InitialValue<Value> {
    data class Empty<Value>(internal val value: Value) : InitialValue<Value>
    data class Default<Value>(internal val value: Value) : InitialValue<Value>
}

internal val <Value> InitialValue<Value>.value: Value get() = when (this) {
    is Empty -> value
    is Default -> value
}