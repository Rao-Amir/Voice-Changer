package com.pixelzlab.app.feature.home_voice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.card.MaterialCardView
import com.pixelzlab.app.R


class HomeVoiceFragment : Fragment() {

    var onNavigateToRecorder: (() -> Unit)? = null
    var onNavigateOpenFile: (() -> Unit)? = null
    var onNavigateToTextToSpeechScreen: (() -> Unit)? = null
    var onNavigateToAudioList: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_voice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardRecord = view.findViewById<MaterialCardView>(R.id.cardRecord)
        val cardOpenFile = view.findViewById<MaterialCardView>(R.id.cardOpenFile)
        val cardTextToSpeech = view.findViewById<MaterialCardView>(R.id.cardTextToSpeech)
        val cardMyVoice = view.findViewById<MaterialCardView>(R.id.cardMyVoice)

        cardRecord.setOnClickListener {
            onNavigateToRecorder?.invoke()
        }

        cardOpenFile.setOnClickListener {
            onNavigateOpenFile?.invoke()
        }

        cardTextToSpeech.setOnClickListener {
            onNavigateToTextToSpeechScreen?.invoke()
        }

        cardMyVoice.setOnClickListener {
            onNavigateToAudioList?.invoke()
        }

        val adView = view.findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}