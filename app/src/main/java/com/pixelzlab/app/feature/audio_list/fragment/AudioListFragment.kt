package com.pixelzlab.app.feature.audio_list.fragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_list.adapter.AudioListAdapter
import com.pixelzlab.app.feature.audio_list.data.AudioItem
import com.pixelzlab.app.feature.audio_list.data.queryAudioFiles

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [AudioListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var adapter: AudioListAdapter
    private var mediaPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvAudio)
        val list = queryAudioFiles(requireContext())
        val etSearch = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSearch)

        adapter = AudioListAdapter(list) { item ->
            playAudio(item)
        }


        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(rv.context, DividerItemDecoration.VERTICAL)
        rv.addItemDecoration(dividerItemDecoration)

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                adapter.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val adView = view.findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
    private fun playAudio(item: AudioItem) {
        mediaPlayer?.release() // Giải phóng nếu đang phát
        mediaPlayer = MediaPlayer().apply {
            setDataSource(requireContext(), item.getContentUri())
            prepare()
            start()
            setOnCompletionListener {
                // Khi hết nhạc, reset trạng thái adapter
                adapter.currentPlayingPosition = RecyclerView.NO_POSITION
                adapter.notifyDataSetChanged()
            }
        }
    }

}