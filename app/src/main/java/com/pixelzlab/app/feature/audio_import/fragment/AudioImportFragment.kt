package com.pixelzlab.app.feature.audio_import.fragment

import android.net.Uri
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
import com.pixelzlab.app.feature.audio_import.adapter.AudioImportAdapter
import com.pixelzlab.app.feature.audio_import.data.queryAudioFiles

/**
 * A simple [Fragment] subclass.
 * Use the [AudioImportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioImportFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val onAudioSelected: ((Uri) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_import, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rvAudio)
        val list = queryAudioFiles(requireContext())
        val etSearch = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSearch)

        val adapter = AudioImportAdapter(list) { item ->
            val result = Bundle().apply {
                putParcelable("uri", item.getContentUri())
            }
            parentFragmentManager.setFragmentResult("audio_pick_result", result)
        }


        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
//        rv.adapter = AudioImportAdapter(list) { item ->
//            val result = Bundle().apply {
//                putParcelable("uri", item.getContentUri())
//            }
//            parentFragmentManager.setFragmentResult("audio_pick_result", result)
//        }

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
}