package edu.stanford.bdh.engagehf.application.modules.datastore

import edu.stanford.bdh.engagehf.application.modules.Module

class DataStorer(
    id: String = "Account",
    val username: String = "John Doe"
) : Module(id) {
    override fun configure() {

    }

    fun store() {

    }

}