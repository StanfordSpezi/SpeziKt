package edu.stanford.bdh.engagehf.application.cough

import adamma.c4dhi.claid.Module.Channel
import adamma.c4dhi.claid_android.Permissions.MicrophonePermission
import adamma.c4dhi.claid_android.collectors.audio.AudioRecorder
import adamma.c4dhi.claid_sensor_data.AudioChannels
import adamma.c4dhi.claid_sensor_data.AudioData
import adamma.c4dhi.claid_sensor_data.AudioEncoding
import com.google.type.TimeOfDay
import edu.stanford.bdh.engagehf.application.modules.Module
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

class AudioRecorderModule(
    id: String?,
    private val channels: AudioChannels,
    private val encoding: AudioEncoding,
    private val bitRate: Int,
    private val samplingRate: Int,
    private val sampleRecordingDuration: Int,
    private val startTime: TimeOfDay? = null,
    private val endTime: TimeOfDay? = null
) : Module(id) {

    private lateinit var audioDataChannel: Channel<AudioData>
    private lateinit var recorder: AudioRecorder

    companion object {
        const val OUTPUT_CHANNEL_NAME = "AudioData"
    }

    private var recordingThread: Thread? = null

    override fun configure() {
        println("Calling init of MicrophoneCollector")

        MicrophonePermission().blockingRequest()

        recorder = AudioRecorder(samplingRate, bitRate, encoding, channels)

        if (!recorder.initialize()) {
            moduleFatal("Failed to initialize AudioRecorder.")
        }

        scheduleRecording(startTime, endTime)

        audioDataChannel = publish(OUTPUT_CHANNEL_NAME, AudioData::class.java)
    }

    private fun scheduleRecording(startTime: TimeOfDay?, endTime: TimeOfDay?) {
        if (startTime == null) {
            startRecording()
        } else {
            val todayAtTime = LocalDateTime.now().with(
                LocalTime.of(startTime.hours, startTime.minutes, startTime.seconds)
            )
            registerPeriodicFunction("StartRecording", ::startRecording, Duration.ofDays(1), todayAtTime)
        }

        endTime?.let {
            val todayAtTime = LocalDateTime.now().with(
                LocalTime.of(it.hours, it.minutes, it.seconds)
            )
            registerPeriodicFunction("StopRecording", ::stopRecording, Duration.ofDays(1), todayAtTime)
        }
    }

    private fun startRecording() {
        moduleInfo("Start recording called.")
        if (recorder.isRecording) {
            moduleError("Cannot start recording, recorder is already running")
            return
        }
        recorder.startRecording()
        recordingThread = Thread { continuousRecording() }.also { it.start() }
    }

    private fun stopRecording() {
        moduleInfo("Stop recording called.")
        if (!recorder.isRecording) return

        recorder.stopRecording()
        recordingThread?.let {
            try {
                it.join()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            recordingThread = null
        }
    }

    private fun continuousRecording() {
        while (recorder.isRecording) {
            val data = recorder.record(sampleRecordingDuration)
            if (data == null) {
                moduleError("Failed to record, AudioData is null!")
                continue
            }
            audioDataChannel.post(data)
        }
    }
}
