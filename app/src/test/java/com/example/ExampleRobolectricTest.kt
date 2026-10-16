package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.MathBattleRepository
import com.example.engine.BossRoster
import com.example.engine.FormulaRepository
import com.example.engine.MathGenerator
import com.example.model.Difficulty
import com.example.model.EducationLevel
import org.junit.Assert.assertEquals
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
}
