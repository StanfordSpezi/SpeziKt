package edu.stanford.speziclaid

import adamma.c4dhi.claid_android.collectors.motion.AccelerometerCollector
import org.junit.Test
import javax.inject.Inject

class CLAIDRuntimeTest {

    @Inject
    lateinit var claidRuntime: CLAIDRuntime

    @Test
    fun `test start CLAIDRuntime`() {

        claidRuntime.addModules(
            listOf(
                WrappedModule(
                    moduleClass=AccelerometerCollector::class.java,
                    moduleId="MyAccelerometerCollector"
                ),
                DataRecorder(
                    moduleId = "MyDataRecorder",
                    properties = mapOf()
                )
                    .record("AccelerationData")
                    .record("GyroscopeData"),

            )
        ).startInBackground(
            host="MyHost",
            userId="MyUserId",
            deviceId="MyDeviceId"
        )
    }


}
