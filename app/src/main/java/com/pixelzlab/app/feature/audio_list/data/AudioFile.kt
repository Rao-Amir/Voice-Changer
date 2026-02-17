package com.pixelzlab.app.feature.audio_list.data

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File

data class AudioItem(
    val displayName: String,
    val filePath: String,
    val duration: Long,
    val sizeBytes: Long,
    val dateAdded: Long
) {
    fun getContentUri(): Uri = Uri.fromFile(File(filePath))
}
fun queryAudioFiles(context: Context): List<AudioItem> {
    val audioItems = mutableListOf<AudioItem>()
    val appAudioDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
        "MVVM-Architecture-Compose"
    )

    if (appAudioDir.exists()) {
        appAudioDir.listFiles { file ->
            file.extension.equals("wav", ignoreCase = true)
        }?.forEach { file ->
            val displayName = file.name
            val filePath = file.absolutePath
            val sizeBytes = file.length()
            val duration = getDuration(file)
            val dateAdded = file.lastModified()

            audioItems.add(
                AudioItem(displayName, filePath, duration, sizeBytes, dateAdded)
            )
        }
    }

    return audioItems.sortedByDescending { it.dateAdded }
}

fun getDuration(file: File): Long {
    return try {
        val player = MediaPlayer()
        player.setDataSource(file.absolutePath)
        player.prepare()
        val duration = player.duration.toLong()
        player.release()
        duration
    } catch (e: Exception) {
        0L
    }
}

