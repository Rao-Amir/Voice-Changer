package com.pixelzlab.app.feature.audio_effect.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_effect.AudioEffect
import com.pixelzlab.app.feature.audio_effect.adapter.AudioEffectAdapter
import com.pixelzlab.app.feature.audio_effect.adapter.Effect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [AudioEffectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AudioEffectFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var isPlaying = true
    var currentEffect: String = "None"
    private val audioEffect = AudioEffect()
    private val handler = Handler(Looper.getMainLooper())
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String? = null
    private lateinit var playButton: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeText: MaterialTextView
    private lateinit var totalTimeText: MaterialTextView
    private lateinit var selectedEffectIcon: ImageView
    private lateinit var rotateAnimator: ObjectAnimator

    val onNavigationSaved: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioFilePath = arguments?.getString("audio_file_path")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_effect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.effectRecyclerView)
        playButton = view.findViewById<ImageView>(R.id.playButton)
        seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val selectedEffectIcon = view.findViewById<ImageView>(R.id.selectedEffectIcon)
        currentTimeText = view.findViewById<MaterialTextView>(R.id.currentTimeText)
        totalTimeText = view.findViewById<MaterialTextView>(R.id.totalTimeText)

        val rotateAnimator = ObjectAnimator.ofFloat(selectedEffectIcon, View.ROTATION, 0f, 360f).apply {
            duration = 10000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        playButton.setImageResource(R.drawable.baseline_pause_24)
        rotateAnimator.start()

        playLoopedAudio()

        mediaPlayer?.let { player ->
            seekBar.max = player.duration
            totalTimeText.text = formatTime(player.duration)
            updateSeekBar(seekBar, currentTimeText)
        }

        playButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                rotateAnimator.pause()
            } else {
                mediaPlayer?.start()
                playButton.setImageResource(R.drawable.baseline_pause_24)
                if (rotateAnimator.isPaused) {
                    rotateAnimator.resume()
                } else {
                    rotateAnimator.start()
                }
                updateSeekBar(seekBar, currentTimeText)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    currentTimeText.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val effects = listOf(
            Effect("None", R.string.effect_none, R.drawable.ic_none),
            Effect("Robot", R.string.effect_robot, R.drawable.adb_48px),
            Effect("Helium", R.string.effect_helium, R.drawable.ic_helium),
            Effect("Devil", R.string.effect_devil, R.drawable.ic_devil_64),
            Effect("Echo", R.string.effect_echo, R.drawable.ic_echo_64),
            Effect("Child", R.string.effect_child, R.drawable.child_care_48px),
            Effect("Alien", R.string.effect_alien, R.drawable.ic_alien_100),
            Effect("Cave", R.string.effect_cave, R.drawable.ic_cave_100),
            Effect("Ghost", R.string.effect_ghost, R.drawable.ic_ghost_64),
            Effect("Radio", R.string.effect_radio, R.drawable.ic_radio_100),
            Effect("OldMan", R.string.effect_old_man, R.drawable.ic_old_man),
        )

        val adapter = AudioEffectAdapter(effects) { effect ->
            selectedEffectIcon.setImageResource(effect.iconResId)
            currentEffect = effect.name
            startPlayingWithEffect()
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = adapter

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    stopAudio()
                    // Cho phép back sau khi đã stop
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            })


        val lifecycleOwner = viewLifecycleOwner

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    mediaPlayer?.pause()
                    rotateAnimator.pause()
                    playButton.setImageResource(R.drawable.baseline_play_arrow_24)
                }

                Lifecycle.Event.ON_RESUME -> {
                    // Nếu muốn tự phát lại khi quay lại app:
//                    mediaPlayer?.start()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    rotateAnimator.cancel()
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

    }

    private fun startPlayingWithEffect() {
        lifecycleScope.launch {
            stopAudio()

            val inputFile = File(audioFilePath ?: return@launch)
            val rawBytes = inputFile.readBytes()
            val shortArray = ByteBuffer.wrap(rawBytes)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
                .let {
                    val arr = ShortArray(it.limit())
                    it.get(arr)
                    arr
                }

            val effected = when (currentEffect) {
                "None" -> shortArray
                else -> audioEffect.applyEffect(currentEffect, shortArray)
            }

            val outFile = File(requireContext().cacheDir, "processed.wav")
            writeWavFile(outFile, effected)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(outFile.absolutePath)
                isLooping = true
                prepare()
                start()
            }

            seekBar.max = mediaPlayer?.duration ?: 0
            totalTimeText.text = formatTime(mediaPlayer?.duration ?: 0)
            updateSeekBar(seekBar, currentTimeText)
        }
    }

    private fun updateSeekBar(seekBar: SeekBar, currentTimeText: TextView) {
        handler.post(object : Runnable {
            override fun run() {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        val pos = player.currentPosition
                        seekBar.progress = pos
                        currentTimeText.text = formatTime(pos)
                    }
                }
                handler.postDelayed(this, 500)
            }
        })
    }

    fun formatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun playLoopedAudio() {
        audioFilePath?.let { path ->
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                isLooping = true
                prepare()
                start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAudio()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
        handler.removeCallbacksAndMessages(null)
    }

    fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    suspend fun saveEffectedAudio(fileName: String): File = withContext(Dispatchers.IO) {
        val inputFile = File(audioFilePath ?: return@withContext File(""))
        val rawBytes = inputFile.readBytes()

        val shortArray = ByteBuffer.wrap(rawBytes)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()
            .let {
                val arr = ShortArray(it.limit())
                it.get(arr)
                arr
            }

        val effected = when (currentEffect) {
            "None" -> shortArray
            else -> audioEffect.applyEffect(currentEffect, shortArray)
        }

        val musicDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            "MVVM-Architecture-Compose"
        )
        if (!musicDir.exists()) musicDir.mkdirs()

        val finalName = if (fileName.endsWith(".wav")) fileName else "$fileName.wav"

        val outFile = File(musicDir, "${currentEffect}_${System.currentTimeMillis()}.wav")
        writeWavFile(outFile, effected)

        return@withContext outFile
    }

    fun writeWavFile(outputFile: File, samples: ShortArray, sampleRate: Int = 44100) {
        val byteData = ByteArray(samples.size * 2)
        ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(samples)

        val totalAudioLen = byteData.size
        val totalDataLen = totalAudioLen + 36

        outputFile.outputStream().use { out ->
            out.write("RIFF".toByteArray()) // ChunkID
            out.write(intToByteArray(totalDataLen)) // ChunkSize
            out.write("WAVE".toByteArray()) // Format

            // fmt subchunk
            out.write("fmt ".toByteArray()) // Subchunk1ID
            out.write(intToByteArray(16)) // Subchunk1Size
            out.write(shortToByteArray(1)) // AudioFormat (PCM)
            out.write(shortToByteArray(1)) // NumChannels
            out.write(intToByteArray(sampleRate)) // SampleRate
            out.write(intToByteArray(sampleRate * 2)) // ByteRate
            out.write(shortToByteArray(2)) // BlockAlign
            out.write(shortToByteArray(16)) // BitsPerSample

            // data subchunk
            out.write("data".toByteArray()) // Subchunk2ID
            out.write(intToByteArray(totalAudioLen)) // Subchunk2Size
            out.write(byteData) // PCM data
        }
    }

    fun intToByteArray(value: Int): ByteArray =
        ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()

    fun shortToByteArray(value: Short): ByteArray =
        ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(value).array()

    companion object {
        fun newInstance(audioFilePath: String): AudioEffectFragment {
            val fragment = AudioEffectFragment()
            val args = Bundle().apply {
                putString("audio_file_path", audioFilePath)
            }
            fragment.arguments = args
            return fragment
        }
    }
}