package edu.stanford.speziclaid.datastore

import android.app.Application
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataBase @Inject constructor(
    private val application: Application
) {

}