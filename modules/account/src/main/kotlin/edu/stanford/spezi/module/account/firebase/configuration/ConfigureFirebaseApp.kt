package edu.stanford.spezi.module.account.firebase.configuration

import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.module.account.spezi.Module
import javax.inject.Inject


class ConfigureFirebaseApp @Inject constructor(
    @ApplicationContext private val context: Context // TODO: Check if this is correct
    // TODO: Think of possibly providing context in configure functions
    // TODO: Also think about simply doing configure inside the init
): Module {
    override fun configure() {
        FirebaseApp.initializeApp(context)
    }
}