package com.pixelzlab.app.feature.voice_recorder


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val amplitudes = mutableListOf<Float>()

    private val waveformPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val timelinePaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 32f
        isAntiAlias = true
    }

    private val centerLinePaint = Paint().apply {
        color = Color.parseColor("#3F51B5") // màu xanh dương
        strokeWidth = 3f
        isAntiAlias = true
    }

    private val barWidth = 6f
    private val spacing = 3f

    private var scrollOffset = 0f // thời gian đã trôi qua tính theo pixel

    fun addAmplitude(rawAmp: Float) {
        val amp = (rawAmp * 8f).coerceAtMost(height / 2f)
        amplitudes.add(amp)
        invalidate()
    }

    fun updateScrollOffset(timeMillis: Long) {
        val pxPerSecond = (barWidth + spacing) * 10 // giả định 10 samples mỗi giây
        scrollOffset = (timeMillis / 1000f) * pxPerSecond
        invalidate()
    }

    fun clear() {
        amplitudes.clear()
        scrollOffset = 0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val centerX = width / 2f

        // Vạch giữa cố định
        canvas.drawLine(centerX, 0f, centerX, height.toFloat(), centerLinePaint)

        // Vẽ waveform từ giữa về bên trái
        var x = centerX - scrollOffset
        for (amp in amplitudes) {
            if (x > centerX) {
                // bỏ qua phần bên phải vạch giữa
                x += barWidth + spacing
                continue
            }
            if (x + barWidth < 0) {
                // đã ra ngoài bên trái màn hình
                x += barWidth + spacing
                continue
            }

            canvas.drawLine(x, centerY - amp, x, centerY + amp, waveformPaint)
            x += barWidth + spacing
        }

        // Vẽ mốc thời gian (timeline) dưới waveform
        val pixelsPerSecond = (barWidth + spacing) * 10
        val totalSeconds = amplitudes.size / 10

        for (i in 0..totalSeconds) {
            val timeX = centerX - scrollOffset + i * pixelsPerSecond
            if (timeX > centerX || timeX < 0f) continue

            val label = String.format("00:%02d", i)
            canvas.drawText(label, timeX - 30, height.toFloat() - 10, timelinePaint)
            canvas.drawLine(timeX, 0f, timeX, height.toFloat(), timelinePaint)
        }
    }
}
