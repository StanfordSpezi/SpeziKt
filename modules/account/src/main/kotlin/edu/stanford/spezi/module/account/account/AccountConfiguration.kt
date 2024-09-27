package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.service.AccountService
import edu.stanford.spezi.module.account.spezi.Module
import edu.stanford.spezi.module.account.spezi.Standard

class AccountConfiguration<Service: AccountService>: Module {
    private val logger by speziLogger()

    val account: Account = TODO()
    private val externalStorage: ExternalAccountStorage = TODO()
    private val accountService: Service = TODO()
    private val storageProvider: List<Module> = TODO()
    private val standard: Standard = TODO()

    override fun configure() {
        TODO()
    }
}