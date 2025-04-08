package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.Module.Properties
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration
import java.time.LocalDateTime

class Account(
    id: String = "Account",
    public val username: String = "John Doe"
) : Module(id) {

    var onboarded = false
    val activeSeconds = MutableStateFlow(0)

    override fun initialize(p0: Properties) {
        registerPeriodicFunction(
            "MyFunction",
            ::someFunc,
            Duration.ofSeconds(1)
        )
    }

    fun someFunc() {
        println("Account some func called")
        activeSeconds.value += 1
    }

}