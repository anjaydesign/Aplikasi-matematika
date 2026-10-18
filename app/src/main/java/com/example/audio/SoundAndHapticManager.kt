package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.R

class SoundAndHapticManager(private val context: Context) {

    private val appContext = context.applicationContext

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null

    private var soundClickId: Int = 0
    private var soundCorrectId: Int = 0
    private var soundWrongId: Int = 0
    private var soundStartId: Int = 0
    private var soundVictoryId: Int = 0
    private var soundTickId: Int = 0

    var sfxEnabled: Boolean = true
    var bgmEnabled: Boolean = true
    var sfxVolume: Float = 0.8f
    var bgmVolume: Float = 0.5f

    private var isAppInBackground: Boolean = false

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttributes)
            .build().apply {
                soundClickId = load(appContext, R.raw.sfx_click, 1)
                soundCorrectId = load(appContext, R.raw.sfx_correct, 1)
                soundWrongId = load(appContext, R.raw.sfx_wrong, 1)
                soundStartId = load(appContext, R.raw.sfx_start, 1)
                soundVictoryId = load(appContext, R.raw.sfx_victory, 1)
                soundTickId = load(appContext, R.raw.sfx_tick, 1)
            }
    }

    fun playClick() {
        if (!sfxEnabled) return
        soundPool?.play(soundClickId, sfxVolume, sfxVolume, 1, 0, 1.0f)
        vibrateShort(20)
    }

    fun playCorrect() {
        if (!sfxEnabled) return
        soundPool?.play(soundCorrectId, sfxVolume, sfxVolume, 1, 0, 1.0f)
        vibrateShort(40)
    }

    fun playWrong() {
        if (!sfxEnabled) return
        soundPool?.play(soundWrongId, sfxVolume, sfxVolume, 1, 0, 1.0f)
        vibrateShort(80)
    }

    fun playStart() {
        if (!sfxEnabled) return
        soundPool?.play(soundStartId, sfxVolume, sfxVolume, 1, 0, 1.0f)
    }

    fun playVictory() {
        if (!sfxEnabled) return
        soundPool?.play(soundVictoryId, sfxVolume, sfxVolume, 1, 0, 1.0f)
        vibratePattern(longArrayOf(0, 50, 50, 100, 50, 150))
    }

    fun playTick() {
        if (!sfxEnabled) return
        soundPool?.play(soundTickId, sfxVolume * 0.4f, sfxVolume * 0.4f, 1, 0, 1.0f)
    }

    fun startBgm() {
        if (!bgmEnabled || isAppInBackground) return
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(appContext, R.raw.math_battle_bgm)?.apply {
                    isLooping = true
                    setVolume(bgmVolume, bgmVolume)
                }
            }
            mediaPlayer?.let { mp ->
                if (!mp.isPlaying) {
                    mp.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseBgm() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopBgm() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBgmVolumeLevel(volume: Float) {
        bgmVolume = volume
        try {
            mediaPlayer?.setVolume(bgmVolume, bgmVolume)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onAppResume() {
        isAppInBackground = false
        if (bgmEnabled) {
            startBgm()
        }
    }

    fun onAppPause() {
        isAppInBackground = true
        pauseBgm()
    }

    fun release() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            soundPool?.release()
            soundPool = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrateShort(durationMs: Long) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
