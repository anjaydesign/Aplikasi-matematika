package com.example.engine

import com.example.model.Difficulty
import kotlin.random.Random

data class AISimulationResult(
    val isCorrect: Boolean,
    val responseDelaySec: Int,
    val aiScoreAdded: Int,
    val quote: String
)

object AIDuelEngine {

    val AI_NAMES = listOf(
        "PythaBot 3000",
        "Cyber Gauss",
        "Mecha Newton",
        "Quantum Euler",
        "Turing Prime"
    )

    fun simulateAIAnswer(
        difficulty: Difficulty,
        questionPoints: Int
    ): AISimulationResult {
        // Probability of correct answer based on difficulty
        val accuracyRate = when (difficulty) {
            Difficulty.PEMULA -> 0.50f
            Difficulty.MUDAH -> 0.65f
            Difficulty.NORMAL -> 0.80f
            Difficulty.SULIT -> 0.90f
            Difficulty.AHLI -> 0.96f
        }

        val isCorrect = Random.nextFloat() <= accuracyRate

        val responseDelay = when (difficulty) {
            Difficulty.PEMULA -> Random.nextInt(4, 8)
            Difficulty.MUDAH -> Random.nextInt(3, 7)
            Difficulty.NORMAL -> Random.nextInt(2, 6)
            Difficulty.SULIT -> Random.nextInt(2, 4)
            Difficulty.AHLI -> Random.nextInt(1, 3)
        }

        val points = if (isCorrect) questionPoints else 0

        val quotes = if (isCorrect) {
            listOf(
                "Algoritmaku tidak pernah meleset!",
                "Kalkulasi optimal tercapai.",
                "Kecepatan komputasi 100%.",
                "Jawaban matematika ini logis."
            )
        } else {
            listOf(
                "Terjadi galat komputasi!",
                "Sistemku salah menghitung matriks...",
                "Overheat! Analisaku terganggu.",
                "Kamu lebih cepat dariku!"
            )
        }

        return AISimulationResult(
            isCorrect = isCorrect,
            responseDelaySec = responseDelay,
            aiScoreAdded = points,
            quote = quotes.random()
        )
    }
}
