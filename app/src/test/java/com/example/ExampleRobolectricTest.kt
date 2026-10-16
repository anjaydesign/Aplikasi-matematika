package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.audio.MathSpeechHelper
import com.example.audio.SoundAndHapticManager
import com.example.data.AppDatabase
import com.example.data.MathBattleRepository
import com.example.engine.BossRoster
import com.example.engine.FormulaRepository
import com.example.engine.MathGenerator
import com.example.model.Difficulty
import com.example.model.EducationLevel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Math Battle Offline", appName)
    }

    @Test
    fun `math generator creates valid questions for all levels`() {
        for (lvl in EducationLevel.entries) {
            val q = MathGenerator.generateQuestion(lvl, Difficulty.NORMAL)
            assertNotNull(q)
            assertTrue("Question text should not be blank", q.question.isNotBlank())
            assertEquals("Should have 4 options", 4, q.options.size)
            assertTrue("Options must contain correct answer", q.options.contains(q.correctAnswer))
            assertTrue("Explanation must be provided", q.explanation.isNotBlank())
            assertTrue("Category must be provided", q.category.isNotBlank())
            assertTrue("Points must be positive", q.points > 0)
        }
    }

    @Test
    fun `leveling XP calculation works accurately`() {
        assertEquals(1, MathBattleRepository.calculatePlayerLevel(0))
        assertEquals(1, MathBattleRepository.calculatePlayerLevel(299))
        assertEquals(2, MathBattleRepository.calculatePlayerLevel(300))
        assertEquals(3, MathBattleRepository.calculatePlayerLevel(800)) // 300 + 500 = 800

        val (curProg, needed) = MathBattleRepository.getXpForNextLevel(150)
        assertEquals(150, curProg)
        assertEquals(300, needed)
    }

    @Test
    fun `boss roster and formulas are loaded properly`() {
        assertEquals(5, BossRoster.BOSSES.size)
        assertTrue(FormulaRepository.FORMULAS.size >= 10)
        val sdFormulas = FormulaRepository.getFormulasByLevel(EducationLevel.SD)
        assertTrue(sdFormulas.isNotEmpty())
    }

    @Test
    fun `math speech helper converts mathematical notation to spoken Indonesian correctly`() {
        // Arithmetic
        val spokenArithmetic = MathSpeechHelper.toSpokenIndonesian("12 × 5 + 8 ÷ 2 = ...")
        assertTrue(spokenArithmetic.contains("dikali"))
        assertTrue(spokenArithmetic.contains("ditambah"))
        assertTrue(spokenArithmetic.contains("dibagi"))
        assertTrue(spokenArithmetic.contains("sama dengan berapa?"))

        // Exponents & roots
        val spokenRoots = MathSpeechHelper.toSpokenIndonesian("√64 + x²")
        assertTrue(spokenRoots.contains("akar kuadrat dari 64"))
        assertTrue(spokenRoots.contains("kuadrat"))

        // Units
        val spokenUnits = MathSpeechHelper.toSpokenIndonesian("Luas = 45 cm²")
        assertTrue(spokenUnits.contains("sentimeter persegi"))

        // Trigonometry
        val spokenTrig = MathSpeechHelper.toSpokenIndonesian("sin 30° + cos 60°")
        assertTrue(spokenTrig.contains("sinus 30 derajat"))
        assertTrue(spokenTrig.contains("kosinus 60 derajat"))

        // Fractions
        val spokenFraction = MathSpeechHelper.toSpokenIndonesian("1/2 + 3/4")
        assertTrue(spokenFraction.contains("setengah"))
        assertTrue(spokenFraction.contains("3 per 4"))
    }

    @Test
    fun `audio settings update and persist in room database`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repo = MathBattleRepository(db)
        repo.initializeDefaultsIfNeeded()

        // Toggle music
        repo.toggleMusic(false)
        var profile = repo.userProfile.first()
        assertFalse(profile?.musicEnabled ?: true)

        repo.toggleMusic(true)
        profile = repo.userProfile.first()
        assertTrue(profile?.musicEnabled ?: false)

        // Volume adjustments
        repo.setMusicVolume(0.5f)
        repo.setSoundVolume(0.9f)
        profile = repo.userProfile.first()
        assertEquals(0.5f, profile?.musicVolume ?: 0f, 0.01f)
        assertEquals(0.9f, profile?.soundVolume ?: 0f, 0.01f)

        // TTS settings
        repo.toggleTts(true)
        repo.toggleTtsAutoRead(false)
        repo.setTtsSpeechRate(1.1f)
        profile = repo.userProfile.first()
        assertTrue(profile?.ttsEnabled ?: false)
        assertFalse(profile?.ttsAutoRead ?: true)
        assertEquals(1.1f, profile?.ttsSpeechRate ?: 0f, 0.01f)
    }

    @Test
    fun `sound and haptic manager can initialize and trigger without crashing`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audioManager = SoundAndHapticManager(context)

        // Test SFX triggers
        audioManager.playClick(soundEnabled = true, volume = 0.8f, vibrationEnabled = false)
        audioManager.playCorrect(soundEnabled = true, volume = 0.8f, vibrationEnabled = false)
        audioManager.playWrong(soundEnabled = true, volume = 0.8f, vibrationEnabled = false)
        audioManager.playStart(soundEnabled = true, volume = 0.8f, vibrationEnabled = false)
        audioManager.playVictory(soundEnabled = true, volume = 0.8f, vibrationEnabled = false)

        // Test BGM controls
        audioManager.startBgm(enabled = false, volume = 0.7f)
        audioManager.stopBgm()
        audioManager.release()
    }
}
