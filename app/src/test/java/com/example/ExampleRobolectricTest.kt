package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.generator.QuestionGenerator
import com.example.data.local.AppDatabase
import com.example.data.local.AudioSettingsEntity
import com.example.data.model.Difficulty
import com.example.data.model.MathCategory
import com.example.data.model.MathGrade
import com.example.data.repository.MathBattleRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext_matchesAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Math Battle", appName)
    }

    @Test
    fun testRoomDatabase_initialDataAndPersistence() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repository = MathBattleRepository(db.mathBattleDao())

        repository.ensureInitialData()

        val settings = db.mathBattleDao().getAudioSettingsSync()
        assertNotNull(settings)
        assertEquals(true, settings?.sfxEnabled)

        // Save new setting
        repository.saveAudioSettings(
            AudioSettingsEntity(
                id = 1,
                sfxEnabled = false,
                bgmEnabled = true,
                ttsEnabled = true,
                ttsAutoRead = false,
                sfxVolume = 0.5f,
                bgmVolume = 0.7f
            )
        )

        val updated = db.mathBattleDao().getAudioSettingsSync()
        assertEquals(false, updated?.sfxEnabled)
        assertEquals(0.7f, updated?.bgmVolume ?: 0f, 0.01f)
    }

    @Test
    fun testQuestionGenerator_allCategoriesSD() {
        val categories = listOf(
            MathCategory.PENJUMLAHAN,
            MathCategory.PENGURANGAN,
            MathCategory.PERKALIAN,
            MathCategory.PEMBAGIAN,
            MathCategory.PECAHAN,
            MathCategory.DESIMAL,
            MathCategory.PERSEN,
            MathCategory.ALJABAR,
            MathCategory.GEOMETRI
        )

        for (cat in categories) {
            val qs = QuestionGenerator.generateQuestions(
                count = 3,
                grade = MathGrade.SD,
                category = cat,
                difficulty = Difficulty.MUDAH
            )
            assertEquals(3, qs.size)
            qs.forEach { q ->
                assertEquals(4, q.options.size)
                assertNotNull(q.correctAnswer)
            }
        }
    }
}
