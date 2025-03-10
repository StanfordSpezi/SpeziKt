package edu.stanford.spezi.modules.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * A custom runner used to set up a hilt instrumented test application.
 *
 * Do not delete!!! It is referenced via fully qualified name in `SpeziAbstractConfigPlugin`
 * which is the base convention plugin used by all the modules of the project
 */
@Suppress("Unused")
class HiltApplicationTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
