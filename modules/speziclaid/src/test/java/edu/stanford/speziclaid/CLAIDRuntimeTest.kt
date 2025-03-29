package edu.stanford.speziclaid

import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import adamma.c4dhi.claid_android.collectors.motion.AccelerometerCollector
import edu.stanford.speziclaid.helper.structOf
import edu.stanford.speziclaid.module.DataRecorder
import edu.stanford.speziclaid.module.WrappedModule
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
                    moduleId="MyAccelerometerCollector",
                    properties= structOf(
                        "samplingFrequency" to 50
                    ),
                    outputs=mapOf(
                        "AccelerationData" to "InternalAccelerometerData"
                    )
                ),
                DataRecorder(
                    moduleId = "MyDataRecorder",
                    properties = structOf()
                )
                    .record("InternalAccelerometerData")
                    .record("GyroscopeData"),

            )
        ).startInBackground(
            host="MyHost",
            userId="MyUserId",
            deviceId="MyDeviceId",
            CLAIDSpecialPermissionsConfig.regularConfig(),
            CLAIDPersistanceConfig.onBootAutoStart()
        )


    }


}
