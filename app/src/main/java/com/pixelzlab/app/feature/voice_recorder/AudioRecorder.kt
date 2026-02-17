package com.pixelzlab.app.feature.voice_recorder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat

class AudioRecorder(private val context: Context) {

    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
    private var isRecording = false
    private var recordingThread: Thread? = null

    fun recordAudio(
        callback: (ShortArray?) -> Unit,
//        onFinish: (ShortArray?) -> Unit,
//        onDataChunk: (ShortArray) -> Unit
    ) {
        if (isRecording) return
        isRecording = true

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Gọi callback báo lỗi hoặc gửi null
            callback(null)
//            onFinish(null)
            isRecording = false
            return
        }

        recordingThread = Thread {
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            val buffer = ShortArray(bufferSize)
            val recordedList = mutableListOf<Short>()

            audioRecord.startRecording()

            while (isRecording) {
                val read = audioRecord.read(buffer, 0, buffer.size)
                if (read > 0) {
                    recordedList.addAll(buffer.take(read))

//                    val chunk = buffer.copyOf(read)
//                    recordedList.addAll(chunk.toList())
//
//                    onDataChunk(chunk)
                }
            }
            audioRecord.stop()
            audioRecord.release()
            callback(recordedList.toShortArray())

//            onFinish(recordedList.toShortArray())
        }
        recordingThread?.start()
    }

    fun stop() {
        isRecording = false
        recordingThread?.join() // đợi thread kết thúc an toàn
        recordingThread = null
    }

}