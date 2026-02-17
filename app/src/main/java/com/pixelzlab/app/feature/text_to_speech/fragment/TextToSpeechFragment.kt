package com.pixelzlab.app.feature.text_to_speech.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.button.MaterialButton
import com.pixelzlab.app.R
import java.io.File
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [TextToSpeechFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TextToSpeechFragment : Fragment(), TextToSpeech.OnInitListener {
    // TODO: Rename and change types of parameters
    private lateinit var spinner: Spinner
    private lateinit var editText: EditText
    private lateinit var playButton: MaterialButton
    private lateinit var convertButton: MaterialButton
    private var tts: TextToSpeech? = null

    private lateinit var loadingOverlay: View


    var onTtsGenerated: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("saved_text", editText.text.toString())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_to_speech, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = view.findViewById(R.id.spinner_language)
        editText = view.findViewById(R.id.edit_text_input)
        playButton = view.findViewById(R.id.button_play)
        convertButton = view.findViewById(R.id.button_text_to_audio)
        loadingOverlay = view.findViewById(R.id.progress_loading)


        val savedText = savedInstanceState?.getString("saved_text", "")
        editText.setText(savedText)

        val languages =
            listOf("English (Australia)", "English (UK)", "English (US)", "Tiếng Vệt (VN)")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, languages)
        spinner.adapter = adapter

        // Khởi tạo TextToSpeech
        tts = TextToSpeech(requireContext(), this, "com.google.android.tts")

        convertButton.setOnClickListener {
            hideKeyboard()
            convertTextToAudio()
        }

        playButton.setOnClickListener {
            hideKeyboard()
            speakText()
        }

        val adView = view.findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun speakText() {
        val text = editText.text.toString()
        if (text.isBlank()) {
            Toast.makeText(requireContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedLanguage = spinner.selectedItem.toString()
        val locale = when (selectedLanguage) {
            "English (Australia)" -> Locale("en", "AU")
            "English (UK)" -> Locale.UK
            "English (US)" -> Locale.US
            "Tiếng Việt (VN)" -> Locale("vi", "VN")
            else -> Locale.getDefault()
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(requireContext(), "Ngôn ngữ không được hỗ trợ hoặc thiếu dữ liệu. Vui lòng tải Google TTS.", Toast.LENGTH_LONG).show()
            promptInstallGoogleTTS()
            return
        }

        // Ưu tiên dùng voice từ Google TTS nếu có
        val googleVoice = tts?.voices?.firstOrNull {
            it.locale == locale && it.name.contains("google", ignoreCase = true)
        }
        googleVoice?.let {
            tts?.voice = it
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    }
    private fun convertTextToAudio() {
        val text = editText.text.toString()
        if (text.isBlank()) {
            Toast.makeText(requireContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedLanguage = spinner.selectedItem.toString()
        val locale = when (selectedLanguage) {
            "English (Australia)" -> Locale("en", "AU")
            "English (UK)" -> Locale.UK
            "English (US)" -> Locale.US
            "Tiếng Việt (VN)" -> Locale("vi", "VN")
            else -> Locale.getDefault()
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(requireContext(), "Ngôn ngữ không được hỗ trợ hoặc thiếu dữ liệu. Vui lòng tải Google TTS.", Toast.LENGTH_LONG).show()
            promptInstallGoogleTTS()
            return
        }

        // Tạo file tạm để lưu audio .wav
        val audioFile = File(requireContext().cacheDir, "tts_output_${System.currentTimeMillis()}.wav")

        val params = Bundle()

        // synthesizeToFile hoạt động bất đồng bộ, cần callback để biết khi nào xong
        val utteranceId = "ttsFile"

        // Đăng ký callback để bắt sự kiện hoàn thành
        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Có thể show progress nếu cần
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId == "ttsFile") {
                    Handler(Looper.getMainLooper()).post {
                        loadingOverlay.visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadingOverlay.visibility = View.GONE
                            onTtsGenerated?.invoke(audioFile.absolutePath)
                        }, 1500) // delay 1.5 giây để hiển loading
                    }
                }

            }

            override fun onError(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(requireContext(), "Tạo file âm thanh thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        })

        val synthResult = tts?.synthesizeToFile(text, params, audioFile, utteranceId)

        if (synthResult != TextToSpeech.SUCCESS) {
            Toast.makeText(requireContext(), "Không thể bắt đầu tạo âm thanh", Toast.LENGTH_SHORT).show()
        }
    }
    fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun promptInstallGoogleTTS() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tts")
        startActivity(intent)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        } else {
            Toast.makeText(requireContext(), "TTS init thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}