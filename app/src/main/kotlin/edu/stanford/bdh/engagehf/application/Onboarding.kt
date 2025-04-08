package edu.stanford.bdh.engagehf.application


import adamma.c4dhi.claid.Module.Module
import adamma.c4dhi.claid.Module.Properties

class Onboarding(id: String = "Onboarding") : Module(id) {

    val account by Dependency<Account>()

    init {
        println("Onboarding init")
    }

    override fun initialize(p0: Properties) {
        account.onboarded = true
        print("Hello account ${account.username}!")
    }

}