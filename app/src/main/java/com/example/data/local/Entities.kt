package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: String = "local_player",
    val name: String = "Juara Matematika",
    val coins: Int = 100,
    val xp: Int = 0,
    val level: Int = 1,
    val selectedAvatar: Int = 0,
    val unlockedAvatars: String = "0,1"
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val playerCount: Int,
    val winnerName: String,
    val rankingsJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val questionCount: Int
)

@Entity(tableName = "audio_settings")
data class AudioSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val sfxEnabled: Boolean = true,
    val bgmEnabled: Boolean = true,
    val ttsEnabled: Boolean = true,
    val ttsAutoRead: Boolean = true,
    val sfxVolume: Float = 0.8f,
    val bgmVolume: Float = 0.5f
)

@Entity(tableName = "daily_quests")
data class DailyQuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentProgress: Int = 0,
    val rewardCoins: Int,
    val isClaimed: Boolean = false
)
