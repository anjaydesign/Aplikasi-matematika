package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundAndHapticManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playClick(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
            } catch (_: Exception) {}
        }
        if (vibrationEnabled) {
            vibrate(25)
        }
    }

    fun playCorrect(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
            } catch (_: Exception) {}
        }
        if (vibrationEnabled) {
            vibrate(50)
        }
    }

    fun playWrong(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 250)
            } catch (_: Exception) {}
        }
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 80, 50, 100))
        }
    }

    fun playVictory(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 150)
                    delay(180)
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
                } catch (_: Exception) {}
            }
        }
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 100, 70, 150, 70, 200))
        }
    }

    fun playBossHit(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 180)
            } catch (_: Exception) {}
        }
        if (vibrationEnabled) {
            vibrate(80)
        }
    }

    private fun vibrate(durationMs: Long) {
        vibrator?.let { v ->
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(durationMs)
                }
            }
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        vibrator?.let { v ->
            if (v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(pattern, -1)
                }
            }
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
