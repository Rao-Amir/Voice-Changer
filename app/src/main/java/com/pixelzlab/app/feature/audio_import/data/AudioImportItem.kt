package com.pixelzlab.app.feature.audio_import.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

data class AudioImportItem(
    val id: Long,
    val displayName: String,
    val duration: Long,
    val sizeBytes: Long,
    val dateAdded: Long
){
    fun getContentUri(): Uri =
        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
}
fun queryAudioFiles(context: Context): List<AudioImportItem> {
    val list = mutableListOf<AudioImportItem>()
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATE_ADDED
    )
    val cursor = context.contentResolver.query(
        uri, projection, null, null,
        "${MediaStore.Audio.Media.DATE_ADDED} DESC"
    )
    cursor?.use {
        val idIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
        val durIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val sizeIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        val dateIdx = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
        while (it.moveToNext()) {
            val id = it.getLong(idIdx)
            val dateAdded = it.getLong(dateIdx) * 1000
            list.add(
                AudioImportItem(
                    id = id,
                    displayName = it.getString(nameIdx),
                    duration = it.getLong(durIdx),
                    sizeBytes = it.getLong(sizeIdx),
                    dateAdded = dateAdded
                )
            )
        }
    }
    return list
}
