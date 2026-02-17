package com.pixelzlab.app.feature.audio_effect

import kotlin.math.pow
import kotlin.math.sin

class AudioEffect {
    private val sampleRate = 44100

    fun applyEffect(effect: String, input: ShortArray): ShortArray {
        return when (effect) {
            "Helium" -> pitchShiftHelium(input, 1.8f)
            "Robot" -> robotEffect(input)
            "Devil" -> devilEffect(input)
            "Echo" -> echoEffect(input)
            "Child" -> childEffect(input)
            "Alien" -> alienEffect(input)
            "Cave" -> caveEffect(input)
            "Ghost" -> ghostEffect(input)
            "Radio" -> radioEffect(input)
            "OldMan" -> oldManEffect(input)
            else -> input
        }
    }

    fun pitchShiftHelium(
        input: ShortArray,
        factor: Float // 1.5 = cao hơn, 0.7 = trầm hơn
    ): ShortArray {
        val newLength = (input.size / factor).toInt()
        val result = ShortArray(newLength)

        for (i in result.indices) {
            val index = (i * factor).toInt()
            if (index < input.size) {
                result[i] = input[index]
            }
        }
        return result
    }

    fun robotEffect(input: ShortArray): ShortArray{
        val output = ShortArray(input.size)
        val modulationFreq = 30.0 // Thay đổi để kiểm tra
        val twoPiF = 2.0 * Math.PI * modulationFreq

        for (i in input.indices) {
            val t = i / sampleRate.toDouble()
            val modulator = sin(twoPiF * t)
            val modulated = input[i] * modulator
            output[i] = modulated.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
        }

        return output
    }

    fun devilEffect(input: ShortArray): ShortArray {
        // 1. Pitch down (giảm tần số) bằng cách lặp mẫu
        val pitchFactor = 0.7f // < 1.0 => giọng trầm hơn
        val newLength = (input.size / pitchFactor).toInt()
        val stretched = ShortArray(newLength)

        for (i in stretched.indices) {
            val index = (i * pitchFactor).toInt()
            if (index < input.size) {
                stretched[i] = input[index]
            }
        }

        // 2. Distortion nhẹ: áp dụng hàm tanh giả lập
        for (i in stretched.indices) {
            val normalized = stretched[i] / 32768.0 // [-1.0, 1.0]
            val distorted = Math.tanh(3.0 * normalized) // Tăng 3.0 để tăng cường độ méo
            stretched[i] = (distorted * 32767).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        return stretched
    }

    fun echoEffect(input: ShortArray): ShortArray {
        val delayMs = 250          // Độ trễ giữa mỗi echo
        val decay = 0.5f           // Mức giảm âm lượng mỗi echo
        val repeatCount = 4        // Số lần lặp echo

        val delaySamples = (sampleRate * delayMs) / 1000
        val outputLength = input.size + delaySamples * repeatCount
        val output = ShortArray(outputLength)

        // Ghi âm gốc
        for (i in input.indices) {
            output[i] = input[i]
        }

        // Thêm nhiều echo lặp lại
        for (echoIndex in 1..repeatCount) {
            val currentDecay = decay.pow(echoIndex)
            for (i in input.indices) {
                val echoPos = i + echoIndex * delaySamples
                if (echoPos < outputLength) {
                    val echoSample = (input[i] * currentDecay).toInt()
                    val mixed = output[echoPos] + echoSample
                    output[echoPos] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
        }

        return output
    }

    fun childEffect(input: ShortArray): ShortArray {
        val pitchFactor = 1.5f  // 0.7 ~ giọng trẻ con
        val newLength = (input.size / pitchFactor).toInt()
        val output = ShortArray(newLength)

        for (i in output.indices) {
            val index = (i * pitchFactor).toInt()
            if (index < input.size) {
                output[i] = input[index]
            }
        }
        return output
    }
    fun alienEffect(input: ShortArray): ShortArray {
        val pitchFactor = 1.6f         // cao hơn để "khác loài"
        val ringFreq = 130.0           // ring modulation mạnh hơn
        val tremoloFreq = 3.0          // nhẹ rung
        val noiseAmount = 300.0        // thêm noise nhẹ

        val newLength = (input.size / pitchFactor).toInt()
        val output = ShortArray(newLength)

        for (i in output.indices) {
            val t = i / sampleRate.toDouble()

            // Pitch shift đơn giản
            val index = (i * pitchFactor).toInt()
            val sample = if (index < input.size) input[index].toDouble() else 0.0

            // Ring modulation
            val ring = kotlin.math.sin(2.0 * Math.PI * ringFreq * t)

            // Tremolo
            val tremolo = 0.5 * (1 + kotlin.math.sin(2.0 * Math.PI * tremoloFreq * t))

            // Thêm một chút noise trắng
            val noise = (Math.random() - 0.5) * noiseAmount

            val processed = (sample * ring * tremolo) + noise

            // Giới hạn và gán vào mảng kết quả
            output[i] = processed.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
        }

        return output
    }
    fun caveEffect(input: ShortArray): ShortArray {
        val delayMs = 150     // delay nhỏ để echo gần nhau hơn
        val decay = 0.5f      // decay mỗi lần echo
        val repeats = 4       // số lần echo lặp lại
        val delaySamples = (sampleRate * delayMs) / 1000
        val outputLength = input.size + delaySamples * repeats
        val output = ShortArray(outputLength)

        // Copy input vào output
        for (i in input.indices) {
            output[i] = input[i]
        }

        // Thêm nhiều lần echo
        for (r in 1..repeats) {
            for (i in input.indices) {
                val delayedIndex = i + delaySamples * r
                if (delayedIndex < outputLength) {
                    val echoSample = (input[i] * decay / r).toInt()
                    val mixed = output[delayedIndex] + echoSample
                    output[delayedIndex] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
        }

        // Low-pass filter đơn giản (moving average 3 điểm)
        for (i in 1 until outputLength - 1) {
            val filtered = (output[i - 1].toInt() + output[i].toInt() + output[i + 1].toInt()) / 3
            output[i] = filtered.toShort()
        }

        return output
    }
    fun ghostEffect(input: ShortArray): ShortArray {
        val pitchFactor = 1.05f         // pitch tăng nhẹ để hơi khác thường
        val delayMs = 250               // độ trễ echo dài hơn
        val decay = 0.5f                // echo decay trung bình
        val repeatEcho = 5              // echo lặp 5 lần
        val tremoloFreq = 1.5           // tremolo rất chậm

        val delaySamples = (sampleRate * delayMs) / 1000
        val newLength = (input.size / pitchFactor).toInt()
        val outputLength = newLength + delaySamples * repeatEcho
        val output = ShortArray(outputLength)

        // Pitch shift đơn giản
        val pitched = ShortArray(newLength)
        for (i in pitched.indices) {
            val index = (i * pitchFactor).toInt()
            pitched[i] = if (index < input.size) input[index] else 0
        }

        // Copy âm gốc vào output
        for (i in pitched.indices) {
            output[i] = pitched[i]
        }

        // Thêm nhiều echo lặp lại
        for (echoIndex in 1..repeatEcho) {
            val currentDecay = decay.pow(echoIndex)
            for (i in pitched.indices) {
                val delayedIndex = i + echoIndex * delaySamples
                if (delayedIndex < outputLength) {
                    val echoSample = (pitched[i] * currentDecay).toInt()
                    val mixed = output[delayedIndex] + echoSample
                    output[delayedIndex] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
        }

        // Tremolo rất chậm (biến đổi âm lượng)
        for (i in output.indices) {
            val t = i / sampleRate.toDouble()
            val tremolo = 0.5 * (1 + kotlin.math.sin(2 * Math.PI * tremoloFreq * t))
            val modulated = output[i] * tremolo
            output[i] = modulated.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble()).toInt().toShort()
        }

        return output
    }
    fun radioEffect(input: ShortArray): ShortArray {
        val output = ShortArray(input.size)

        val noiseLevel = 1500.0         // Tăng noise cho rõ ràng hơn
        val distortionGain = 2.5        // Nhân lớn rồi clip để méo tiếng
        val bandpassLow = 300.0
        val bandpassHigh = 3000.0
        val dt = 1.0 / sampleRate
        val rcLow = 1.0 / (2 * Math.PI * bandpassHigh)
        val rcHigh = 1.0 / (2 * Math.PI * bandpassLow)

        val alphaLow = dt / (rcLow + dt)
        val alphaHigh = rcHigh / (rcHigh + dt)

        var lowPass = 0.0
        var highPass = 0.0
        var lastInput = 0.0
        var lastHighPass = 0.0

        for (i in input.indices) {
            val x = input[i].toDouble()

            // High-pass filter
            highPass = alphaHigh * (lastHighPass + x - lastInput)
            lastInput = x
            lastHighPass = highPass

            // Low-pass filter
            lowPass = lowPass + alphaLow * (highPass - lowPass)

            // Thêm nhiễu mạnh hơn
            val noise = (Math.random() - 0.5) * noiseLevel

            // Distortion nhẹ (clip)
            var filtered = lowPass * distortionGain + noise
            filtered = filtered.coerceIn(Short.MIN_VALUE.toDouble(), Short.MAX_VALUE.toDouble())

            output[i] = filtered.toInt().toShort()
        }

        return output
    }

    fun oldManEffect(input: ShortArray): ShortArray {
        val pitchFactor = 0.7f // Giảm pitch (giọng trầm)
        val delayMs = 100
        val decay = 0.3f

        val delaySamples = (sampleRate * delayMs) / 1000
        val newLength = (input.size / pitchFactor).toInt()
        val outputLength = newLength + delaySamples
        val output = ShortArray(outputLength)

        // Giảm pitch (resample)
        val pitched = ShortArray(newLength)
        for (i in pitched.indices) {
            val index = (i * pitchFactor).toInt()
            pitched[i] = if (index < input.size) input[index] else 0
        }

        // Copy âm gốc vào output
        for (i in pitched.indices) {
            output[i] = pitched[i]
        }

        // Thêm echo nhẹ
        for (i in pitched.indices) {
            val delayedIndex = i + delaySamples
            if (delayedIndex < outputLength) {
                val echoSample = (pitched[i] * decay).toInt()
                val mixed = output[delayedIndex] + echoSample

                // Méo tiếng nhẹ (clip)
                val clipped = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                output[delayedIndex] = clipped.toShort()
            }
        }

        return output
    }
}