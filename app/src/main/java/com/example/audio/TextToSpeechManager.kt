package com.example.audio

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class TtsState(
    val isInitialized: Boolean = false,
    val isLanguageSupported: Boolean = false,
    val isFemaleVoice: Boolean = false,
    val voiceName: String = "",
    val statusMessage: String = "Menginisialisasi pembaca soal...",
    val isSpeaking: Boolean = false
)

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    private val _ttsState = MutableStateFlow(TtsState())
    val ttsState: StateFlow<TtsState> = _ttsState.asStateFlow()

    private var currentSpeechRate: Float = 1.0f

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            _ttsState.value = TtsState(
                isInitialized = false,
                isLanguageSupported = false,
                statusMessage = "Gagal menginisialisasi Text-to-Speech pada perangkat."
            )
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return
            val idLocale = Locale("id", "ID")
            val langResult = ttsEngine.setLanguage(idLocale)

            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Try broader "in" or default locale
                val fallbackResult = ttsEngine.setLanguage(Locale("in", "ID"))
                if (fallbackResult == TextToSpeech.LANG_MISSING_DATA || fallbackResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    _ttsState.value = TtsState(
                        isInitialized = true,
                        isLanguageSupported = false,
                        statusMessage = "Data suara Bahasa Indonesia belum terpasang di perangkat."
                    )
                    return
                }
            }

            // Set UtteranceProgressListener to monitor speech playback states
            ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _ttsState.value = _ttsState.value.copy(isSpeaking = true)
                }

                override fun onDone(utteranceId: String?) {
                    _ttsState.value = _ttsState.value.copy(isSpeaking = false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _ttsState.value = _ttsState.value.copy(isSpeaking = false)
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _ttsState.value = _ttsState.value.copy(isSpeaking = false)
                }
            })

            // Search for Female Indonesian Voice on API 21+
            var isFemale = false
            var selectedVoiceName = "Standar Bahasa Indonesia"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    val voices = ttsEngine.voices
                    if (!voices.isNullOrEmpty()) {
                        // Filter Indonesian voices
                        val idVoices = voices.filter { v ->
                            val lang = v.locale?.language?.lowercase()
                            lang == "id" || lang == "in"
                        }

                        // Try to find female voice
                        val femaleVoice = idVoices.firstOrNull { v ->
                            val name = v.name.lowercase()
                            name.contains("female") ||
                            name.contains("perempuan") ||
                            name.contains("dfz") ||
                            name.contains("fem") ||
                            name.contains("#female") ||
                            name.contains("wanita")
                        }

                        if (femaleVoice != null) {
                            ttsEngine.voice = femaleVoice
                            isFemale = true
                            selectedVoiceName = "Suara Perempuan (${femaleVoice.name})"
                        } else if (idVoices.isNotEmpty()) {
                            val defaultIdVoice = idVoices.first()
                            ttsEngine.voice = defaultIdVoice
                            isFemale = false
                            selectedVoiceName = "Suara Indonesia (${defaultIdVoice.name})"
                        }
                    }
                } catch (_: Exception) {}
            }

            ttsEngine.setSpeechRate(currentSpeechRate)

            _ttsState.value = TtsState(
                isInitialized = true,
                isLanguageSupported = true,
                isFemaleVoice = isFemale,
                voiceName = selectedVoiceName,
                statusMessage = if (isFemale) {
                    "Suara perempuan Bahasa Indonesia aktif & siap digunakan."
                } else {
                    "Suara Bahasa Indonesia siap digunakan (fallback otomatis)."
                }
            )
        } else {
            _ttsState.value = TtsState(
                isInitialized = false,
                isLanguageSupported = false,
                statusMessage = "Mesin TTS perangkat tidak siap."
            )
        }
    }

    fun setSpeechRate(rate: Float) {
        currentSpeechRate = rate.coerceIn(0.7f, 1.3f)
        try {
            tts?.setSpeechRate(currentSpeechRate)
        } catch (_: Exception) {}
    }

    fun speak(text: String, isTtsEnabled: Boolean) {
        if (!isTtsEnabled) return
        val ttsEngine = tts ?: return
        if (!_ttsState.value.isLanguageSupported) return

        try {
            ttsEngine.stop()
            val spokenText = MathSpeechHelper.toSpokenIndonesian(text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val params = Bundle()
                ttsEngine.speak(spokenText, TextToSpeech.QUEUE_FLUSH, params, "math_question_id")
            } else {
                @Suppress("DEPRECATION")
                ttsEngine.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null)
            }
        } catch (_: Exception) {}
    }

    fun stop() {
        try {
            tts?.stop()
            _ttsState.value = _ttsState.value.copy(isSpeaking = false)
        } catch (_: Exception) {}
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            _ttsState.value = TtsState(
                isInitialized = false,
                isLanguageSupported = false,
                statusMessage = "TTS dinonaktifkan."
            )
        } catch (_: Exception) {}
    }
}
