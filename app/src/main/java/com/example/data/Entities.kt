package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Juara Matematika",
    val selectedLevelCode: String = "SD",
    val selectedDifficultyCode: String = "NORMAL",
    val xp: Int = 0,
    val playerLevel: Int = 1,
    val coins: Int = 100,
    val selectedAvatar: String = "⚡",
    val selectedTheme: String = "CYBER_NEON",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val modeId: String,
    val levelCode: String,
    val difficultyCode: String,
    val score: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val totalQuestions: Int,
    val accuracy: Float,
    val durationSeconds: Int,
    val isWin: Boolean
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
    @PrimaryKey val id: String, // e.g. "2026-10-16_play_1_game"
    val questDate: String,
    val questKey: String,
    val title: String,
    val rewardXp: Int,
    val rewardCoins: Int,
    val progress: Int = 0,
    val target: Int = 1,
    val isClaimed: Boolean = false
)

@Entity(tableName = "topic_stats")
data class TopicStatEntity(
    @PrimaryKey val topicName: String,
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0
)
