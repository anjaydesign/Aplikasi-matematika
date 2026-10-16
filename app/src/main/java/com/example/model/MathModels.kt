package com.example.model

data class MathQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String,
    val category: String,
    val difficulty: Difficulty,
    val level: EducationLevel,
    val points: Int,
    val hint: String = ""
)

data class GameResult(
    val mode: GameMode,
    val level: EducationLevel,
    val difficulty: Difficulty,
    val score: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val totalQuestions: Int,
    val accuracy: Float,
    val maxStreak: Int,
    val durationSeconds: Int,
    val xpEarned: Int,
    val coinsEarned: Int,
    val isWin: Boolean,
    val bossDefeated: Boolean = false,
    val questionsAnswered: List<QuestionReviewItem> = emptyList()
)

data class QuestionReviewItem(
    val question: MathQuestion,
    val selectedAnswer: String?,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int
)

data class BossInfo(
    val id: String,
    val name: String,
    val title: String,
    val category: String,
    val maxHp: Int,
    val currentHp: Int,
    val emoji: String,
    val quote: String,
    val colorHex: Long,
    val rewardXp: Int,
    val rewardCoins: Int
)

data class PlayerAvatar(
    val id: String,
    val name: String,
    val emoji: String,
    val costCoins: Int,
    val isUnlocked: Boolean = false
)

data class ThemePreset(
    val id: String,
    val name: String,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val costCoins: Int,
    val isUnlocked: Boolean = false
)
