package com.example.data.model

import java.util.UUID

enum class MathGrade(val displayName: String, val description: String) {
    SD("SD (Kelas 1–6)", "Aritmatika dasar, pecahan sederhana, dan logika angka."),
    SMP("SMP (Kelas 7–9)", "Pecahan, desimal, aljabar awal, dan geometri dasar."),
    SMA_UMUM("SMA & Umum", "Aljabar lanjutan, persen kompleks, eksponen, dan aritmatika cepat.")
}

enum class MathCategory(val displayName: String, val symbol: String) {
    PENJUMLAHAN("Penjumlahan", "+"),
    PENGURANGAN("Pengurangan", "-"),
    PERKALIAN("Perkalian", "×"),
    PEMBAGIAN("Pembagian", "÷"),
    CAMPURAN_ARITMATIKA("Aritmatika Campuran", "±×÷"),
    PECAHAN("Pecahan", "½"),
    DESIMAL("Desimal", "0.5"),
    PERSEN("Persen", "%"),
    ALJABAR("Aljabar Sederhana", "x"),
    GEOMETRI("Geometri Dasar", "📐")
}

enum class Difficulty(val displayName: String, val multiplier: Float) {
    MUDAH("Mudah", 1.0f),
    SEDANG("Sedang", 1.25f),
    SULIT("Sulit", 1.5f),
    CAMPURAN("Campuran", 1.2f)
}

data class MathQuestion(
    val id: String = UUID.randomUUID().toString(),
    val prompt: String,
    val spokenPrompt: String,
    val options: List<String>,
    val correctAnswer: String,
    val category: MathCategory,
    val difficulty: Difficulty,
    val explanation: String = ""
)

data class MatchSettings(
    val questionCount: Int = 10,
    val timePerQuestionSeconds: Int = 15,
    val grade: MathGrade = MathGrade.SD,
    val category: MathCategory = MathCategory.CAMPURAN_ARITMATIKA,
    val difficulty: Difficulty = Difficulty.SEDANG,
    val speedBonusEnabled: Boolean = true
)

data class PlayerScore(
    val playerId: String,
    val playerName: String,
    var score: Int = 0,
    var correctAnswersCount: Int = 0,
    var totalTimeSpentMs: Long = 0L,
    var currentStreak: Int = 0,
    var highestStreak: Int = 0,
    var avatarIndex: Int = 0
)

data class MatchSummary(
    val matchId: String,
    val modeName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val rankings: List<PlayerScore>,
    val totalQuestions: Int,
    val winnerName: String
)
