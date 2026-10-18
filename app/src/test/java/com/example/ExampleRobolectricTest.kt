package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.generator.QuestionGenerator
import com.example.data.model.Difficulty
import com.example.data.model.MathCategory
import com.example.data.model.MathGrade
import com.example.data.model.NetworkMessage
import com.example.data.model.NetworkMessageType
import org.json.JSONObject
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
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Math Battle", appName)
    }

    @Test
    fun `question generator creates valid questions with 4 unique options`() {
        val categories = listOf(
            MathCategory.PENJUMLAHAN,
            MathCategory.PENGURANGAN,
            MathCategory.PERKALIAN,
            MathCategory.PEMBAGIAN,
            MathCategory.PECAHAN,
            MathCategory.DESIMAL,
            MathCategory.PERSEN,
            MathCategory.ALJABAR,
            MathCategory.GEOMETRI,
            MathCategory.CAMPURAN_ARITMATIKA
        )

        categories.forEach { category ->
            val questions = QuestionGenerator.generateQuestions(
                count = 5,
                grade = MathGrade.SD,
                category = category,
                difficulty = Difficulty.SEDANG
            )

            assertEquals(5, questions.size)
            questions.forEach { q ->
                assertNotNull(q.prompt)
                assertFalse("Prompt should not be empty", q.prompt.isEmpty())
                assertEquals("Must have 4 options", 4, q.options.size)
                assertEquals("Options must be unique", 4, q.options.toSet().size)
                assertTrue("Options must contain correct answer", q.options.contains(q.correctAnswer))
            }
        }
    }

    @Test
    fun `division question never divides by zero`() {
        val questions = QuestionGenerator.generateQuestions(
            count = 20,
            grade = MathGrade.SMP,
            category = MathCategory.PEMBAGIAN,
            difficulty = Difficulty.SULIT
        )

        questions.forEach { q ->
            assertFalse("Division prompt should never divide by 0", q.prompt.contains("÷ 0") || q.prompt.contains("/ 0"))
        }
    }

    @Test
    fun `network message json protocol serialization and deserialization`() {
        val message = NetworkMessage(
            type = NetworkMessageType.SUBMIT_ANSWER,
            matchId = "match_abc",
            senderId = "player_123",
            senderName = "Budi Juara",
            payloadJson = JSONObject().apply {
                put("questionId", "q_1")
                put("selectedOption", "42")
                put("timeTakenMs", 2500L)
            }.toString()
        )

        val jsonStr = message.toJsonString()
        val parsed = NetworkMessage.parse(jsonStr)

        assertNotNull(parsed)
        assertEquals(NetworkMessageType.SUBMIT_ANSWER, parsed?.type)
        assertEquals("player_123", parsed?.senderId)
        assertEquals("Budi Juara", parsed?.senderName)
        assertEquals("match_abc", parsed?.matchId)
        assertTrue(parsed?.payloadJson?.contains("42") == true)
    }
}
