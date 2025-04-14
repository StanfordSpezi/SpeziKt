package edu.stanford.bdh.engagehf.application.modules.datastore

import ch.claid.cough_detection.CoughSampleVector
import com.google.protobuf.Message
import edu.stanford.bdh.engagehf.application.modules.Module

class LocalStorage(
    id: String = "LocalStorage",
) : Module(id) {

    val data = mutableMapOf<String, Any>()
    override fun configure() {
    }

    inline fun <reified T: Message> store(key: String, value: T) {
        data[key] = value as Any
    }

    inline fun <reified T: Message> read(key: String): T {
        if (data.containsKey(key)) {
            if (data[key] is T) {
                return data[key] as T
            }
        }

        val defaultInstance = T::class.java
            .getMethod("getDefaultInstance")
            .invoke(null) as T

        data[key] = defaultInstance
        return defaultInstance
    }
}