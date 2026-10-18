package com.example

import com.example.audio.IndonesianSpeechHelper
import com.example.data.generator.QuestionGenerator
import com.example.data.model.ConnectedPlayer
import com.example.data.model.Difficulty
import com.example.data.model.MathCategory
import com.example.data.model.MathGrade
import com.example.data.model.NetworkMessage
import com.example.data.model.NetworkMessageType
import com.example.data.model.PlayerScore
import com.example.data.model.QuestionClientPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    @Test
    fun testQuestionGenerator_GeneratesValidOptionsAndNoDuplicates() {
        val questions = QuestionGenerator.generateQuestions(
            count = 10,
            grade = MathGrade.SD,
            category = MathCategory.PERKALIAN,
            difficulty = Difficulty.MUDAH
        )

        assertEquals(10, questions.size)
        questions.forEach { q ->
            assertEquals(4, q.options.size)
            // Options must be unique
            assertEquals(4, q.options.toSet().size)
            // Options must contain correct answer
            assertTrue(q.options.contains(q.correctAnswer))
            // Must have spoken prompt
            assertTrue(q.spokenPrompt.isNotEmpty())
        }
    }

    @Test
    fun testDivisionQuestions_AreValidIntegers() {
        val questions = QuestionGenerator.generateQuestions(
            count = 10,
            grade = MathGrade.SD,
            category = MathCategory.PEMBAGIAN,
            difficulty = Difficulty.SEDANG
        )

        assertEquals(10, questions.size)
        questions.forEach { q ->
            val ans = q.correctAnswer.toIntOrNull()
            assertNotNull("Division answer must be integer", ans)
            assertTrue("Quotient must be > 0", ans!! > 0)
        }
    }

    @Test
    fun testNetworkMessage_JsonSerialization() {
        val original = NetworkMessage(
            type = NetworkMessageType.START_MATCH,
            matchId = "match_test123",
            senderId = "player_1",
            senderName = "Budi",
            payloadJson = "{\"totalRounds\":10}"
        )

        val jsonStr = original.toJsonString()
        val parsed = NetworkMessage.parse(jsonStr)

        assertNotNull(parsed)
        assertEquals(NetworkMessageType.START_MATCH, parsed?.type)
        assertEquals("match_test123", parsed?.matchId)
        assertEquals("player_1", parsed?.senderId)
        assertEquals("Budi", parsed?.senderName)
    }

    @Test
    fun testQuestionClientPayload_DoesNotIncludeAnswerKey() {
        val qPayload = QuestionClientPayload(
            questionId = "q_1",
            roundIndex = 1,
            totalRounds = 10,
            prompt = "15 × 3 = ?",
            spokenPrompt = "15 dikali 3 sama dengan berapa?",
            options = listOf("45", "30", "50", "40"),
            categoryName = "Perkalian",
            durationSeconds = 15,
            serverStartTimeMs = 1000000L
        )

        val json = qPayload.toJson().toString()
        // Ensure correctAnswer is never in the client question payload
        assertTrue(!json.contains("correctAnswer"))
        val parsed = QuestionClientPayload.fromJson(qPayload.toJson())
        assertEquals("15 × 3 = ?", parsed.prompt)
        assertEquals(4, parsed.options.size)
    }

    @Test
    fun testScoringAndTiebreakerRanking() {
        val p1 = PlayerScore(
            playerId = "p1",
            playerName = "Andi",
            score = 350,
            correctAnswersCount = 3,
            totalTimeSpentMs = 8000L
        )
        val p2 = PlayerScore(
            playerId = "p2",
            playerName = "Budi",
            score = 350,
            correctAnswersCount = 3,
            totalTimeSpentMs = 5000L // Faster response time = better rank
        )
        val p3 = PlayerScore(
            playerId = "p3",
            playerName = "Citra",
            score = 400,
            correctAnswersCount = 4,
            totalTimeSpentMs = 9000L // Highest score = Rank 1
        )

        val ranked = listOf(p1, p2, p3).sortedWith(
            compareByDescending<PlayerScore> { it.score }
                .thenByDescending { it.correctAnswersCount }
                .thenBy { it.totalTimeSpentMs }
        )

        assertEquals("Citra", ranked[0].playerName) // 400 pts
        assertEquals("Budi", ranked[1].playerName)  // 350 pts, 5000ms
        assertEquals("Andi", ranked[2].playerName)  // 350 pts, 8000ms
    }

    @Test
    fun testIndonesianSpeechFormatting() {
        val raw = "25 + 17 = ?"
        val formatted = IndonesianSpeechHelper.formatMathForIndonesianSpeech(raw)
        assertTrue(formatted.contains("tambah"))
        assertTrue(formatted.contains("sama dengan"))

        val frac = "1/2 + 3/4 = ?"
        val fracFormatted = IndonesianSpeechHelper.formatMathForIndonesianSpeech(frac)
        assertTrue(fracFormatted.contains("setengah"))
        assertTrue(fracFormatted.contains("per"))
    }
}
