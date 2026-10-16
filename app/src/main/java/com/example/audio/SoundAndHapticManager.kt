package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundAndHapticManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null

    // SFX Sound IDs
    private var soundClickId = 0
    private var soundCorrectId = 0
    private var soundWrongId = 0
    private var soundStartId = 0
    private var soundVictoryId = 0
    private var isSoundPoolLoaded = false

    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        initSoundPool()
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {}
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isSoundPoolLoaded = true
            }
        }

        try {
            soundClickId = soundPool?.load(context, R.raw.sfx_click, 1) ?: 0
            soundCorrectId = soundPool?.load(context, R.raw.sfx_correct, 1) ?: 0
            soundWrongId = soundPool?.load(context, R.raw.sfx_wrong, 1) ?: 0
            soundStartId = soundPool?.load(context, R.raw.sfx_start, 1) ?: 0
            soundVictoryId = soundPool?.load(context, R.raw.sfx_victory, 1) ?: 0
        } catch (_: Exception) {}
    }

    // =========================================================================
    // BACKGROUND MUSIC (BGM)
    // =========================================================================

    fun startBgm(enabled: Boolean, volume: Float) {
        if (!enabled) {
            stopBgm()
            return
        }

        val clampedVol = volume.coerceIn(0.0f, 1.0f)

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.bgm_game)?.apply {
                    isLooping = true
                    setVolume(clampedVol, clampedVol)
                    start()
                }
            } else {
                mediaPlayer?.let { player ->
                    player.setVolume(clampedVol, clampedVol)
                    if (!player.isPlaying) {
                        player.start()
                    }
                }
            }
        } catch (_: Exception) {}
    }

    fun setBgmVolume(volume: Float) {
        val clampedVol = volume.coerceIn(0.0f, 1.0f)
        try {
            mediaPlayer?.setVolume(clampedVol, clampedVol)
        } catch (_: Exception) {}
    }

    fun pauseBgm() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (_: Exception) {}
    }

    fun resumeBgm(enabled: Boolean, volume: Float) {
        if (!enabled) return
        val clampedVol = volume.coerceIn(0.0f, 1.0f)
        try {
            mediaPlayer?.let { player ->
                player.setVolume(clampedVol, clampedVol)
                if (!player.isPlaying) {
                    player.start()
                }
            } ?: startBgm(enabled, volume)
        } catch (_: Exception) {}
    }

    fun stopBgm() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}
    }

    // =========================================================================
    // SOUND EFFECTS (SFX)
    // =========================================================================

    fun playClick(soundEnabled: Boolean, volume: Float, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSfx(soundClickId, volume, fallbackTone = ToneGenerator.TONE_PROP_BEEP, fallbackDuration = 30)
        }
        if (vibrationEnabled) {
            vibrate(25)
        }
    }

    fun playCorrect(soundEnabled: Boolean, volume: Float, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSfx(soundCorrectId, volume, fallbackTone = ToneGenerator.TONE_PROP_ACK, fallbackDuration = 120)
        }
        if (vibrationEnabled) {
            vibrate(50)
        }
    }

    fun playWrong(soundEnabled: Boolean, volume: Float, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSfx(soundWrongId, volume, fallbackTone = ToneGenerator.TONE_PROP_NACK, fallbackDuration = 250)
        }
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 80, 50, 100))
        }
    }

    fun playStart(soundEnabled: Boolean, volume: Float, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSfx(soundStartId, volume, fallbackTone = ToneGenerator.TONE_PROP_PROMPT, fallbackDuration = 180)
        }
        if (vibrationEnabled) {
            vibrate(40)
        }
    }

    fun playVictory(soundEnabled: Boolean, volume: Float, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSfx(soundVictoryId, volume, fallbackTone = ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, fallbackDuration = 300)
        }
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 100, 70, 150, 70, 200))
        }
    }

    fun playBossHit(soundEnabled: Boolean, volume: Float, vibrationEnabled: Boolean) {
        if (soundEnabled) {
            playSfx(soundCorrectId, volume, fallbackTone = ToneGenerator.TONE_DTMF_D, fallbackDuration = 150)
        }
        if (vibrationEnabled) {
            vibrate(80)
        }
    }

    private fun playSfx(soundId: Int, volume: Float, fallbackTone: Int, fallbackDuration: Int) {
        val vol = volume.coerceIn(0.0f, 1.0f)
        if (soundId != 0 && isSoundPoolLoaded) {
            try {
                soundPool?.play(soundId, vol, vol, 1, 0, 1.0f)
                return
            } catch (_: Exception) {}
        }
        // Fallback tone if soundPool isn't ready
        try {
            toneGenerator?.startTone(fallbackTone, fallbackDuration)
        } catch (_: Exception) {}
    }

    // =========================================================================
    // HAPTIC FEEDBACK
    // =========================================================================

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
        stopBgm()
        try {
            soundPool?.release()
            soundPool = null
        } catch (_: Exception) {}
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
