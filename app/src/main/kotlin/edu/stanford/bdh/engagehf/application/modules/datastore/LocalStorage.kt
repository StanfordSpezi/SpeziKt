package edu.stanford.bdh.engagehf.application.modules.datastore

import edu.stanford.bdh.engagehf.application.modules.Module

class LocalStorage(
    id: String = "LocalStorage",
) : Module(id) {
    override fun configure() {
    }

    inline fun <reified T> store(key: String, value: T) {

    }

    inline fun <reified T> read(key: String): T? {
        return T::class.java.getDeclaredConstructor().newInstance()
    }
}