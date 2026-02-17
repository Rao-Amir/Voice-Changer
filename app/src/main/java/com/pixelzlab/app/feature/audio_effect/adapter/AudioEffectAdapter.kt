package com.pixelzlab.app.feature.audio_effect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pixelzlab.app.R

data class Effect(val name: String, val nameResId: Int, val iconResId: Int)

class AudioEffectAdapter(
    private val list: List<Effect>,
    val onClick: (Effect) -> Unit
) : RecyclerView.Adapter<AudioEffectAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val icon = view.findViewById<ImageView>(R.id.effectIcon)
        val name = view.findViewById<TextView>(R.id.effectName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_effect, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.setText(item.nameResId)
        holder.icon.setImageResource(item.iconResId)
        holder.itemView.setOnClickListener { onClick(item) }
    }
}