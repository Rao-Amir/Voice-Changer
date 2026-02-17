package com.pixelzlab.app.feature.audio_effect

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

class AudioPlayer {
    private val sampleRate = 44100

    fun playAudio(data: ShortArray?) {
        if (data == null) return
        val track = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            data.size * 2,
            AudioTrack.MODE_STATIC
        )
        track.write(data, 0, data.size)
        track.play()
    }
}