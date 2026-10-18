package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class IndonesianSpeechHelper(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeakingFlow = MutableStateFlow(false)
    val isSpeakingFlow: StateFlow<Boolean> = _isSpeakingFlow.asStateFlow()

    private val _isAvailableFlow = MutableStateFlow(false)
    val isAvailableFlow: StateFlow<Boolean> = _isAvailableFlow.asStateFlow()

    var ttsEnabled: Boolean = true

    init {
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val localeIndo = Locale("id", "ID")
            val result = tts?.setLanguage(localeIndo)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true
                _isAvailableFlow.value = true
                selectIndonesianFemaleVoice(localeIndo)
            } else {
                // Fallback to default
                tts?.setLanguage(Locale.getDefault())
                isInitialized = true
                _isAvailableFlow.value = true
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeakingFlow.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeakingFlow.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeakingFlow.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeakingFlow.value = false
                }
            })
        } else {
            isInitialized = false
            _isAvailableFlow.value = false
        }
    }

    private fun selectIndonesianFemaleVoice(locale: Locale) {
        try {
            val voices = tts?.voices ?: return
            // Look for female voice with "id" language
            val femaleVoice = voices.firstOrNull { voice: Voice ->
                voice.locale.language == locale.language &&
                        (voice.name.contains("female", ignoreCase = true) ||
                                voice.name.contains("wanita", ignoreCase = true) ||
                                voice.name.contains("f0", ignoreCase = true) ||
                                voice.name.contains("id-id-x-dfg#female", ignoreCase = true))
            } ?: voices.firstOrNull { it.locale.language == locale.language }

            femaleVoice?.let { tts?.voice = it }
            tts?.setPitch(1.05f) // Slightly higher friendly pitch
            tts?.setSpeechRate(0.95f) // Clear child-friendly pacing
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun speakMathPrompt(prompt: String) {
        if (!ttsEnabled || !isInitialized) return
        val formattedSpeech = formatMathForIndonesianSpeech(prompt)
        stop()

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "math_speech_${System.currentTimeMillis()}")
        tts?.speak(formattedSpeech, TextToSpeech.QUEUE_FLUSH, params, "math_speech")
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeakingFlow.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun formatMathForIndonesianSpeech(raw: String): String {
            var text = raw
                .replace("+", " tambah ")
                .replace("-", " kurang ")
                .replace("×", " kali ")
                .replace("*", " kali ")
                .replace("÷", " bagi ")
                .replace(":", " bagi ")
                .replace("=", " sama dengan ")
                .replace("%", " persen ")
                .replace("²", " kuadrat ")
                .replace("³", " kubik ")
                .replace("^2", " kuadrat ")
                .replace("^", " pangkat ")
                .replace("?", " berapa ")
                .replace("...", " titik titik ")
                .replace("·", " kali ")

            // Fractions like 1/2, 3/4
            text = text.replace(Regex("(\\d+)/(\\d+)")) { match ->
                val num = match.groupValues[1]
                val den = match.groupValues[2]
                when {
                    num == "1" && den == "2" -> "setengah"
                    num == "1" && den == "4" -> "seperempat"
                    else -> "$num per $den"
                }
            }

            // Decimal numbers like 2.5 or 0.5
            text = text.replace(Regex("(\\d+)\\.(\\d+)")) { match ->
                "${match.groupValues[1]} koma ${match.groupValues[2]}"
            }

            return text.replace(Regex("\\s+"), " ").trim()
        }
    }
}
