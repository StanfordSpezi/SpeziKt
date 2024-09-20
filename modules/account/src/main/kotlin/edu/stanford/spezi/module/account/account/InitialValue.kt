package edu.stanford.spezi.module.account.account

sealed class InitialValue<Value> {
    data class Empty<Value>(private val value: Value): InitialValue<Value>()
    data class Default<Value>(private val value: Value): InitialValue<Value>()
}