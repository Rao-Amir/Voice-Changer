package com.pixelzlab.app.feature.audio_effect

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class AudioSaver(private val context: Context) {

    fun saveAudio(data: ShortArray?): File?{
        if (data == null) return null
        val file = File(context.getExternalFilesDir(null), "processed_output.wav")
        return try {
            saveToWav(file.absolutePath, data, 44100)
            Handler(Looper.getMainLooper()).post{
                Toast.makeText(context, "Đã lưu file: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
            file
        }catch (e: Exception){
            e.printStackTrace()
            null
        }

    }

    private fun saveToWav(filename: String, audioData: ShortArray, sampleRate: Int) {
        val outputStream = FileOutputStream(filename)
        val byteRate = sampleRate * 2

        // WAV Header
        val header = ByteArray(44)
        val totalDataLen = 36 + audioData.size * 2
        val audioLen = audioData.size * 2

        // ChunkID "RIFF"
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        writeInt(header, 4, totalDataLen)
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        writeInt(header, 16, 16)
        writeShort(header, 20, 1) // PCM
        writeShort(header, 22, 1) // Mono
        writeInt(header, 24, sampleRate)
        writeInt(header, 28, byteRate)
        writeShort(header, 32, 2) // Block align
        writeShort(header, 34, 16) // Bits per sample
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        writeInt(header, 40, audioLen)

        outputStream.write(header)

        // Write data
        for (sample in audioData) {
            outputStream.write((sample.toInt() and 0xff))
            outputStream.write((sample.toInt() shr 8) and 0xff)
        }

        outputStream.close()
    }

    private fun writeInt(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xff).toByte()
        buffer[offset + 1] = (value shr 8).toByte()
        buffer[offset + 2] = (value shr 16).toByte()
        buffer[offset + 3] = (value shr 24).toByte()
    }

    private fun writeShort(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xff).toByte()
        buffer[offset + 1] = (value shr 8).toByte()
    }
}