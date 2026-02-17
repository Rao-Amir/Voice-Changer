package com.pixelzlab.app.feature.voice_recorder


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var totalSeconds = 0

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private val paintSecondHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintMinuteHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 22f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    //Độ dày vạch chia
    private val paintTick = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        strokeWidth = 4f
    }

    fun updateTime(secondsElapsed: Int) {
        this.totalSeconds = secondsElapsed
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - 20f

        // ===== Vẽ vòng ngoài =====
        canvas.drawCircle(centerX, centerY, radius, paintCircle)

        // ===== Vẽ 60 vạch chia nhỏ =====
        for (i in 0 until 60) {
            val angleRad = Math.toRadians(i * 6.0 - 90)
            val isMajor = i % 5 == 0
            val startRadius = if (i % 5 == 0) radius - 20 else radius - 15
            val endRadius = radius

            paintTick.strokeWidth = if (isMajor) 6f else 3f

            val startX = (centerX + cos(angleRad) * startRadius).toFloat()
            val startY = (centerY + sin(angleRad) * startRadius).toFloat()
            val endX = (centerX + cos(angleRad) * endRadius).toFloat()
            val endY = (centerY + sin(angleRad) * endRadius).toFloat()

            canvas.drawLine(startX, startY, endX, endY, paintTick)
        }

        // ===== Vẽ số từ 0 đến 55, mỗi 5 đơn vị =====
        val currentSecond = totalSeconds % 60
        for (i in 0 until 60 step 5) {
            val angleRad = Math.toRadians(i * 6.0 - 90)
            val textRadius = radius - 70f // khoảng cách giưuã số và vạch chia
            val x = (centerX + cos(angleRad) * textRadius).toFloat()
            val y = (centerY + sin(angleRad) * textRadius + 10f).toFloat()

            // Tô đậm số chia 15
            if (i == currentSecond) {
                paintText.textSize = 54f
                paintText.color = Color.RED
                paintText.typeface = Typeface.DEFAULT_BOLD
            } else if (i % 15 == 0) {
                paintText.textSize = 48f //cỡ chữ cảu các số 0 15 30 45
                paintText.color = Color.BLACK
                paintText.typeface = Typeface.DEFAULT_BOLD
            } else {
                paintText.textSize = 44f // cỡ chữa của số
                paintText.color = Color.BLACK
                paintText.typeface = Typeface.DEFAULT
            }

            val label = if (i == 0) "0" else i.toString()
            canvas.drawText(label, x, y, paintText)
        }

        // ===== Kim phút =====
        val minuteAngle = Math.toRadians((totalSeconds / 60f * 6.0 - 90).toDouble())
        val minuteLength = radius * 0.6
        val minEndX = (centerX + cos(minuteAngle) * minuteLength).toFloat()
        val minEndY = (centerY + sin(minuteAngle) * minuteLength).toFloat()
        canvas.drawLine(centerX, centerY, minEndX, minEndY, paintMinuteHand)

        // ===== Kim giây =====
        val secondAngle = Math.toRadians((totalSeconds % 60) * 6.0 - 90)
        val secondLength = radius * 0.74 // khoảng cách giữa kim giây và số
        val secEndX = (centerX + cos(secondAngle) * secondLength).toFloat()
        val secEndY = (centerY + sin(secondAngle) * secondLength).toFloat()
        canvas.drawLine(centerX, centerY, secEndX, secEndY, paintSecondHand)
    }
}