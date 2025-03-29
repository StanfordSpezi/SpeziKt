package edu.stanford.speziclaid.cough

import adamma.c4dhi.claid_android.collectors.audio.MicrophoneCollector
import ch.claid.cough_detection.CoughDetectionModule
import edu.stanford.speziclaid.module.SpeziCLAIDPipeline
import edu.stanford.speziclaid.module.WrappedModule

import edu.stanford.speziclaid.helper.structOf

class CoughRecognizerPipeline(
    private val name: String,
    private val inputs: Map<String, String> = mapOf(),
    private val outputs: Map<String, String> = mapOf()
) : SpeziCLAIDPipeline() {

    init {
        createPipeline()
    }

    private fun createPipeline() {
        addModules(listOf(
            WrappedModule(
                moduleClass = MicrophoneCollector::class.java,
                moduleId = "AudioRecorder",
                properties = structOf(),
                outputs = mapOf()
            ),
            //CoughDetectionPreprocessor(),
            WrappedModule(
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
                inputs = mapOf("InputChannel" to "MelSpectograms"),
                outputs = mapOf("OutputChannel" to "CoughEnsembleOutputs")
            ),
            //CoughDetectionPostprocessor()

        ))
    }
}