package com.pixelzlab.app.feature.audio_list.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.platform.LocalView
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_list.data.AudioItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AudioListAdapter(
    private val audioList: List<AudioItem>,
    private val onClick: (AudioItem) -> Unit
) : RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {

    private var filteredList = audioList.toMutableList()
    var currentPlayingPosition = RecyclerView.NO_POSITION


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlayingIndicator: ImageView = view.findViewById(R.id.ivPlayingIndicator)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)
        val btnOptions: ImageButton = view.findViewById(R.id.btnOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.audio_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        holder.tvName.text = item.displayName
        holder.tvSub.text =
            "${formatTime(item.duration.toInt())} • ${"%.1f kB".format(item.sizeBytes / 1024.0)}"
        holder.tvIndex.text = "${position + 1}"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        holder.tvDate.text = sdf.format(Date(item.dateAdded))

        holder.itemView.setOnClickListener { onClick(item) }

        if (position == currentPlayingPosition) {
            holder.ivPlayingIndicator.visibility = View.VISIBLE

            val drawable = holder.ivPlayingIndicator.drawable
            if (drawable is AnimatedVectorDrawableCompat) {
                drawable.start()
            } else if (drawable is AnimatedVectorDrawableCompat) {
                drawable.start()
            }
            holder.tvIndex.visibility = View.GONE
        } else {
            holder.ivPlayingIndicator.visibility = View.GONE
            holder.tvIndex.visibility = View.VISIBLE

            val drawable = holder.ivPlayingIndicator.drawable
            if (drawable is AnimatedVectorDrawableCompat) {
                drawable.stop()
            } else if (drawable is AnimatedVectorDrawableCompat) {
                drawable.stop()
            }
        }
        holder.itemView.setOnClickListener {
            val previousPosition = currentPlayingPosition
            currentPlayingPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(currentPlayingPosition)
            onClick(item)
        }
        holder.btnOptions.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, it)
            popup.inflate(R.menu.audio_item_menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_share -> {
                        shareAudio(holder.itemView, item)
                        true
                    }

                    R.id.action_delete -> {
                        deleteAudio(holder.itemView, item, position)
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }

    }

    private fun formatTime(ms: Int): String {
        val m = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        val s = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }

    fun filter(query: String) {
        val lowerQuery = query.lowercase(Locale.getDefault())

        filteredList = if (lowerQuery.isBlank()) {
            audioList.toMutableList()
        } else {
            audioList.filter {
                it.displayName.lowercase(Locale.getDefault()).contains(lowerQuery)
            }.toMutableList()
        }

        notifyDataSetChanged()
    }

    private fun shareAudio(view: View, item: AudioItem) {
        val uri = item.getContentUri()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        view.context.startActivity(Intent.createChooser(shareIntent, view.context.getString(R.string.share_via)))
    }

    private fun deleteAudio(view: View, item: AudioItem, position: Int) {
        val context = view.context

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.delete_recording_title))
            .setMessage(context.getString(R.string.delete_recording_message))
            .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                val contentUri = getContentUriFromFilePath(context, item.filePath)
                if (contentUri != null) {
                    try {
                        val rowsDeleted = context.contentResolver.delete(contentUri, null, null)
                        if (rowsDeleted > 0) {
                            (filteredList as MutableList).removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, filteredList.size - position)
                            Toast.makeText(context, context.getString(R.string.delete_success), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, context.getString(R.string.delete_failed), Toast.LENGTH_SHORT).show()
                        }
                    } catch (securityEx: SecurityException) {
                        // Nếu không có quyền, mở UI xác nhận xóa
                        val intent = Intent(Intent.ACTION_DELETE).apply {
                            data = contentUri
                        }
                        context.startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.file_not_found_mediastore),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()
    }

    fun getContentUriFromFilePath(context: Context, filePath: String): Uri? {
        val projection = arrayOf(MediaStore.Audio.Media._ID)
        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(filePath)
        val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        context.contentResolver.query(queryUri, projection, selection, selectionArgs, null)
            .use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    return Uri.withAppendedPath(queryUri, id.toString())
                }
            }
        return null
    }


}