package edu.stanford.speziclaid.cough

import adamma.c4dhi.claid_android.collectors.audio.MicrophoneCollector
import adamma.c4dhi.claid_sensor_data.AudioChannels
import adamma.c4dhi.claid_sensor_data.AudioEncoding
import ch.claid.cough_detection.CoughDetectionModule
import edu.stanford.speziclaid.module.SpeziCLAIDPipeline
import edu.stanford.speziclaid.module.WrappedModule

import edu.stanford.speziclaid.helper.structOf
import edu.stanford.speziclaid.helper.toProtoValue
import edu.stanford.speziclaid.module.wrapModule

class CoughRecognizerPipeline(
    private val name: String,
    private val output: String
) : SpeziCLAIDPipeline() {

    init {
        createPipeline()
    }

    private fun createPipeline() {
        addModules(listOf(
            // Audio recorder
            wrapModule(
                moduleClass = MicrophoneCollector::class.java,
                moduleId = "$name-AudioRecorder",
                properties = structOf(
                    "channels" to AudioChannels.CHANNEL_MONO.valueDescriptor.name,
                    "encoding" to "ENCODING_PCM_16BIT",
                    "bitrate" to 32,
                    "sampling_rate" to 16000,
                    "sample_recording_duration_seconds" to 6
                ),
                outputs = mapOf("AudioData" to "$name/AudioData/Microphone")
            ),
            // Preprocessor
            WrappedModule(
                moduleType = "CoughDetectionPreprocessorModule",
                moduleId = "$name-CoughDetectionPreprocessor",
                inputs = mapOf("AudioInputChannel" to "$name/AudioData/Microphone"),
                outputs = mapOf("OutputChannel" to "$name/MelSpectograms")
            ),
            // ML cough detector
            wrapModule(
                CoughDetectionModule::class.java,
                "$name-CoughDetectionModule",
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
            ),
            // Postprocessor
            WrappedModule(
                moduleType = "CoughDetectionPostprocessorModule",
                moduleId = "$name-CoughDetectionPostprocessor",
                inputs = mapOf("InputChannel" to "$name/CoughEnsembleOutputs"),
                outputs = mapOf("OutputChannel" to output)
            )
        ))
    }
}