package edu.stanford.speziclaid.datastore

import android.app.Application
import edu.stanford.healthconnectonfhir.Loinc
import edu.stanford.spezi.storage.local.LocalStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRetriever @Inject constructor(
    private val application: Application
) {
    @Inject
    lateinit var localStorage: LocalStorage

    public fun retrieve(loinc: Loinc) {

    }
}