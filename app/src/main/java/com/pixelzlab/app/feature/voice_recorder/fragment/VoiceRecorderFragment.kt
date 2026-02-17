package com.pixelzlab.app.feature.voice_recorder.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.button.MaterialButton
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_effect.AudioSaver
import com.pixelzlab.app.feature.voice_recorder.AudioRecorder
import com.pixelzlab.app.feature.voice_recorder.ClockView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoiceRecorderFragment : Fragment() {

    private val audioRecorder by lazy { AudioRecorder(requireContext()) }
    private var seconds = 0

    //    private lateinit var waveformView: WaveformView
//    private var recordingStartTime = 0L
    private lateinit var clockView: ClockView

    private lateinit var tvTimer: TextView
    private val timerHandler = Handler(Looper.getMainLooper())

    var onNavigationApplyEffect: ((String) -> Unit)? = null
    var onRecordingStateChanged: ((Boolean) -> Unit)? = null
    private var isRecording = false
        set(value) {
            field = value
            onRecordingStateChanged?.invoke(value)  // Báo ra ngoài mỗi khi thay đổi
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_recorder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btnStartRecord = view.findViewById<MaterialButton>(R.id.btn_record)
        val btnStopRecord = view.findViewById<MaterialButton>(R.id.btn_stop)
        val controlsLayout = view.findViewById<View>(R.id.record_controls)

        tvTimer = view.findViewById(R.id.tv_timer)

//        waveformView = view.findViewById(R.id.waveform_view)
        clockView = view.findViewById(R.id.clock_view)

        btnStartRecord.setOnClickListener {
            if (hasPermissions()) {
                startRecording()
                timerHandler.post(timerRunnable())
                btnStartRecord.visibility = View.GONE
                controlsLayout.visibility = View.VISIBLE
                controlsLayout.alpha = 0f
                controlsLayout.animate().alpha(1f).setDuration(300).start()
            } else {
                requestPermissions()
            }
        }

        btnStopRecord.setOnClickListener {
            showLoading(true)
            stopRecording()
            btnStartRecord.visibility = View.VISIBLE
            controlsLayout.visibility = View.GONE
//            onRecordingFinished?.invoke(outputFile)
        }

        val adView = view.findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun startRecording() {
        isRecording = true
//        showLoading(true)

//        recordingStartTime = System.currentTimeMillis()
        audioRecorder.recordAudio { data ->
            if (data != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val saver = AudioSaver(requireContext())
                    val file = saver.saveAudio(data)

                    withContext(Dispatchers.Main) {
                        showLoading(false)

                        if (file != null) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.record_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            tvTimer.text = "00:00"
                            onNavigationApplyEffect?.invoke(file.absolutePath)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.save_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), getString(R.string.record_failed), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun stopRecording() {
        if (!isRecording) {
            Toast.makeText(context, getString(R.string.not_started), Toast.LENGTH_SHORT).show()
            return
        }

        audioRecorder.stop()
        isRecording = false
        timerHandler.removeCallbacksAndMessages(null)
        Toast.makeText(context, getString(R.string.stopped_recording), Toast.LENGTH_SHORT).show()
        tvTimer.text = "00:00"
        seconds = 0
        clockView.updateTime(0)
    }

    private fun showLoading(show: Boolean) {
        view?.findViewById<View>(R.id.progress_loading)?.visibility =
            if (show) View.VISIBLE else View.GONE
    }

    private fun timerRunnable(): Runnable {
        return object : Runnable {
            override fun run() {
                seconds++
                val minutes = seconds / 60
                val sec = seconds % 60
                tvTimer.text = String.format("%02d:%02d", minutes, sec)

                clockView.updateTime(seconds)

                timerHandler.postDelayed(this, 1000)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacksAndMessages(null)
    }


    private fun hasPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )
        }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        // Nếu đã bị từ chối vĩnh viễn (Don't ask again)
        val permanentlyDenied = notGranted.any {
            !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it)
        }

        return when {
            notGranted.isEmpty() -> true

            permanentlyDenied -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permission_permanently_denied),
                    Toast.LENGTH_LONG
                ).show()
                showPermissionDeniedDialog()
                false
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.request_permission_to_record),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
        }
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )
        }

        ActivityCompat.requestPermissions(requireActivity(), permissions, 123)
    }
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.permission_request_title))
            .setMessage(getString(R.string.permission_request_message))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }


}