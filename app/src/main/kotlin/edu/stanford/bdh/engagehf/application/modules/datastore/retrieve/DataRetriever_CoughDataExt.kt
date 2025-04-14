package edu.stanford.bdh.engagehf.application.modules.datastore.retrieve

import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.bdh.engagehf.application.Dependency
import edu.stanford.bdh.engagehf.application.modules.datastore.DataRetriever
import edu.stanford.bdh.engagehf.application.modules.datastore.LocalStorage
import edu.stanford.healthconnectonfhir.Loinc

public fun DataRetriever.getCoughSamples(): CoughSampleVector {
    val loinc: Loinc = Loinc.COUGH

    val coughData: CoughSampleVector = localStorage.read(loinc.toString())
    return coughData
}