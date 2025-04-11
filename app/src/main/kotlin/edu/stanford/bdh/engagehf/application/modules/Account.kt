package edu.stanford.bdh.engagehf.application.modules

import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration

class Account(
    id: String = "Account",
    val username: String = "John Doe"
) : Module(id) {

    var onboarded = false
    val activeSeconds = MutableStateFlow(0)

    override fun configure() {
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