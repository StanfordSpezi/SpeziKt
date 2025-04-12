package edu.stanford.bdh.engagehf.application.modules


import edu.stanford.bdh.engagehf.application.Dependency

class Onboarding(id: String = "Onboarding") : Module(id) {
    val account by Dependency<Account>()

    init {
        println("Onboarding init")
    }

    override fun configure() {
        account.onboarded = true
        print("Hello account ${account.username}!")
    }

}

