package com.pixelzlab.app.feature.audio_import.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_import.data.AudioImportItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AudioImportAdapter(
    private val list: List<AudioImportItem>,
    private val onClick: (AudioImportItem) -> Unit
) : RecyclerView.Adapter<AudioImportAdapter.ViewHolder>() {

    private var filteredList = list.toMutableList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_audio_import, parent, false)
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
    }

    private fun formatTime(ms: Int): String {
        val m = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        val s = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", m, s)
    }

    fun filter(query: String) {
        val lowerQuery = query.lowercase(Locale.getDefault())

        filteredList = if (lowerQuery.isBlank()) {
            list.toMutableList()
        } else {
            list.filter {
                it.displayName.lowercase(Locale.getDefault()).contains(lowerQuery)
            }.toMutableList()
        }

        notifyDataSetChanged()
    }
}