package edu.stanford.bdh.engagehf.application.cough

import adamma.c4dhi.claid_sensor_data.AudioChannels
import adamma.c4dhi.claid_sensor_data.AudioEncoding
import ch.claid.cough_detection.CoughDetectionModule
import edu.stanford.bdh.engagehf.application.SpeziConfig
import edu.stanford.bdh.engagehf.application.wrapModule
import edu.stanford.speziclaid.helper.structOf

class CoughPipeline(name: String, output: String) : SpeziConfig(
    Builder().apply {
        +AudioRecorderModule(
            id = "AudioRecorder",
            channels = AudioChannels.CHANNEL_MONO,
            encoding = AudioEncoding.ENCODING_PCM_16BIT,
            bitRate = 32,
            samplingRate = 16000,
            sampleRecordingDuration = 6
        ).outputs(mapOf("AudioData" to "$name/AudioData/Microphone"))

        +wrapModule(
            moduleType = "CoughDetectionPreprocessorModule",
            moduleId = "$name-CoughDetectionPreprocessor",
            inputs = mapOf("AudioInputChannel" to "$name/AudioData/Microphone"),
            outputs = mapOf("OutputChannel" to "$name/MelSpectograms")
        )

        +wrapModule(
            moduleType = CoughDetectionModule::class.java.simpleName,
            moduleId = "$name-CoughDetectionModule",
            properties = structOf(
                "detector_config" to mapOf(
                    "model_names" to listOf(
                        "cnnv3l3_mobileA_prob.tflite",
                        "cnnv3l3_mobileB_prob.tflite",
                        "cnnv3l3_mobileC_prob.tflite",
                        "cnnv3l3_mobileD_prob.tflite",
                        "cnnv3l3_mobileE_prob.tflite"
                    )
                )
            ),
            inputs = mapOf("InputChannel" to "$name/MelSpectograms"),
            outputs = mapOf("OutputChannel" to "$name/CoughEnsembleOutputs")
        )

        +wrapModule(
            moduleType = "CoughDetectionPostprocessorModule",
            moduleId = "$name-CoughDetectionPostprocessor",
            inputs = mapOf("InputChannel" to "$name/CoughEnsembleOutputs"),
            outputs = mapOf("OutputChannel" to output)
        )
    }
)