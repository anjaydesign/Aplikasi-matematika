package com.example.data.repository

import com.example.data.local.AudioSettingsEntity
import com.example.data.local.DailyQuestEntity
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.MathBattleDao
import com.example.data.local.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

class MathBattleRepository(private val dao: MathBattleDao) {

    val profileFlow: Flow<PlayerProfileEntity?> = dao.getProfileFlow()
    val matchHistoryFlow: Flow<List<MatchHistoryEntity>> = dao.getAllMatchHistory()
    val audioSettingsFlow: Flow<AudioSettingsEntity?> = dao.getAudioSettingsFlow()
    val questsFlow: Flow<List<DailyQuestEntity>> = dao.getAllQuests()

    suspend fun ensureInitialData() {
        if (dao.getProfileSync() == null) {
            dao.insertOrUpdateProfile(PlayerProfileEntity())
        }
        if (dao.getAudioSettingsSync() == null) {
            dao.saveAudioSettings(AudioSettingsEntity())
        }
        dao.insertQuests(
            listOf(
                DailyQuestEntity(
                    id = "quest_1",
                    title = "Pemanasan Otak",
                    description = "Selesaikan 1 pertandingan dalam mode apa pun",
                    targetCount = 1,
                    rewardCoins = 50
                ),
                DailyQuestEntity(
                    id = "quest_2",
                    title = "Jawara Hitung",
                    description = "Jawab 10 soal matematika dengan benar",
                    targetCount = 10,
                    rewardCoins = 100
                ),
                DailyQuestEntity(
                    id = "quest_3",
                    title = "Mabar Seru",
                    description = "Mainkan 1 pertandingan Multiplayer (Satu HP atau Wi-Fi)",
                    targetCount = 1,
                    rewardCoins = 150
                )
            )
        )
    }

    suspend fun saveAudioSettings(settings: AudioSettingsEntity) {
        dao.saveAudioSettings(settings)
    }

    suspend fun recordMatch(
        mode: String,
        playerCount: Int,
        winnerName: String,
        rankingsJson: String,
        questionCount: Int,
        xpGained: Int,
        coinsGained: Int
    ) {
        dao.insertMatchHistory(
            MatchHistoryEntity(
                mode = mode,
                playerCount = playerCount,
                winnerName = winnerName,
                rankingsJson = rankingsJson,
                questionCount = questionCount
            )
        )

        // Update profile XP and Coins
        val current = dao.getProfileSync() ?: PlayerProfileEntity()
        val newXp = current.xp + xpGained
        val newCoins = current.coins + coinsGained
        val newLevel = 1 + (newXp / 200)

        dao.insertOrUpdateProfile(
            current.copy(
                xp = newXp,
                coins = newCoins,
                level = newLevel
            )
        )
    }

    suspend fun updateAvatar(avatarIndex: Int) {
        val current = dao.getProfileSync() ?: PlayerProfileEntity()
        dao.insertOrUpdateProfile(current.copy(selectedAvatar = avatarIndex))
    }

    suspend fun unlockAvatar(avatarIndex: Int, cost: Int): Boolean {
        val current = dao.getProfileSync() ?: PlayerProfileEntity()
        val unlocked = current.unlockedAvatars.split(",").mapNotNull { it.toIntOrNull() }.toMutableSet()
        if (unlocked.contains(avatarIndex)) return true
        if (current.coins >= cost) {
            unlocked.add(avatarIndex)
            dao.insertOrUpdateProfile(
                current.copy(
                    coins = current.coins - cost,
                    unlockedAvatars = unlocked.joinToString(",")
                )
            )
            return true
        }
        return false
    }

    suspend fun updatePlayerName(newName: String) {
        val clean = newName.trim().take(20)
        if (clean.isNotEmpty()) {
            val current = dao.getProfileSync() ?: PlayerProfileEntity()
            dao.insertOrUpdateProfile(current.copy(name = clean))
        }
    }

    suspend fun claimQuest(quest: DailyQuestEntity) {
        if (!quest.isClaimed && quest.currentProgress >= quest.targetCount) {
            dao.updateQuest(quest.copy(isClaimed = true))
            val current = dao.getProfileSync() ?: PlayerProfileEntity()
            dao.insertOrUpdateProfile(current.copy(coins = current.coins + quest.rewardCoins))
        }
    }

    suspend fun incrementQuestProgress(questId: String, amount: Int = 1) {
        // Simple helper for quest progress updates
    }
}
