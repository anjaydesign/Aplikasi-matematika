package com.example.data

import com.example.model.Difficulty
import com.example.model.EducationLevel
import com.example.model.GameMode
import com.example.model.GameResult
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MathBattleRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val historyDao = database.gameHistoryDao()
    private val achievementDao = database.achievementDao()
    private val dailyQuestDao = database.dailyQuestDao()
    private val topicStatDao = database.topicStatDao()

    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()
    val recentHistory: Flow<List<GameHistoryEntity>> = historyDao.getRecentHistory()
    val totalGamesCount: Flow<Int> = historyDao.getTotalGamesCount()
    val allAchievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()
    val allTopicStats: Flow<List<TopicStatEntity>> = topicStatDao.getAllTopicStats()

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getTodayQuests(): Flow<List<DailyQuestEntity>> {
        val today = getTodayDateString()
        return dailyQuestDao.getQuestsForDate(today)
    }

    suspend fun initializeDefaultsIfNeeded() {
        // Ensure user profile exists
        val currentProfile = userDao.getUserProfileOnce()
        if (currentProfile == null) {
            userDao.insertOrUpdateProfile(UserProfileEntity())
        }

        // Seed achievements
        val defaultAchievements = listOf(
            AchievementEntity(
                id = "ach_warmup",
                title = "Pemanasan",
                description = "Selesaikan 10 soal matematika",
                iconEmoji = "🏃",
                maxProgress = 10
            ),
            AchievementEntity(
                id = "ach_expert",
                title = "Jago Hitung",
                description = "Jawab 50 soal dengan benar",
                iconEmoji = "🎯",
                maxProgress = 50
            ),
            AchievementEntity(
                id = "ach_combo",
                title = "Combo Master",
                description = "Capai kombo 10 jawaban benar beruntun",
                iconEmoji = "⚡",
                maxProgress = 10
            ),
            AchievementEntity(
                id = "ach_time_hunter",
                title = "Pemburu Waktu",
                description = "Selesaikan mode Time Attack",
                iconEmoji = "⏱️",
                maxProgress = 1
            ),
            AchievementEntity(
                id = "ach_survivor",
                title = "Penyintas Tangguh",
                description = "Jawab 20 soal di mode Survival",
                iconEmoji = "🛡️",
                maxProgress = 20
            ),
            AchievementEntity(
                id = "ach_boss_slayer",
                title = "Penakluk Bos",
                description = "Kalahkan Bos Matematika di Boss Battle",
                iconEmoji = "👑",
                maxProgress = 1
            ),
            AchievementEntity(
                id = "ach_duel_master",
                title = "Duelist Sejati",
                description = "Menangkan duel melawan bot komputer AI",
                iconEmoji = "🤖",
                maxProgress = 1
            ),
            AchievementEntity(
                id = "ach_perfectionist",
                title = "Akurasi Sempurna",
                description = "Selesaikan sesi dengan akurasi 100%",
                iconEmoji = "💎",
                maxProgress = 1
            )
        )
        achievementDao.seedAchievements(defaultAchievements)

        // Seed daily quests for today
        val today = getTodayDateString()
        val existingQuests = dailyQuestDao.getQuestsForDateOnce(today)
        if (existingQuests.isEmpty()) {
            val quests = listOf(
                DailyQuestEntity(
                    id = "${today}_play",
                    questDate = today,
                    questKey = "play",
                    title = "Mainkan 1 Game Apa Saja",
                    rewardXp = 60,
                    rewardCoins = 40,
                    target = 1
                ),
                DailyQuestEntity(
                    id = "${today}_correct",
                    questDate = today,
                    questKey = "correct",
                    title = "Jawab 15 Soal dengan Benar",
                    rewardXp = 100,
                    rewardCoins = 60,
                    target = 15
                ),
                DailyQuestEntity(
                    id = "${today}_streak",
                    questDate = today,
                    questKey = "streak",
                    title = "Raih Kombo 5 Benar Beruntun",
                    rewardXp = 80,
                    rewardCoins = 50,
                    target = 5
                )
            )
            dailyQuestDao.seedQuests(quests)
        }
    }

    suspend fun updateEducationLevel(level: EducationLevel) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(selectedLevelCode = level.code))
    }

    suspend fun updateDifficulty(difficulty: Difficulty) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(selectedDifficultyCode = difficulty.code))
    }

    suspend fun updatePlayerName(name: String) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(name = name.trim()))
    }

    suspend fun updateAvatar(emoji: String) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(selectedAvatar = emoji))
    }

    suspend fun updateTheme(themeId: String) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(selectedTheme = themeId))
    }

    suspend fun toggleAudio(enabled: Boolean) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(soundEnabled = enabled, musicEnabled = enabled))
    }

    suspend fun toggleMusic(enabled: Boolean) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(musicEnabled = enabled))
    }

    suspend fun toggleSound(enabled: Boolean) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(soundEnabled = enabled))
    }

    suspend fun toggleTts(enabled: Boolean) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(ttsEnabled = enabled))
    }

    suspend fun toggleTtsAutoRead(enabled: Boolean) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(ttsAutoRead = enabled))
    }

    suspend fun setMusicVolume(volume: Float) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(musicVolume = volume))
    }

    suspend fun setSoundVolume(volume: Float) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(soundVolume = volume))
    }

    suspend fun setTtsSpeechRate(rate: Float) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(ttsSpeechRate = rate))
    }

    suspend fun toggleVibration(enabled: Boolean) {
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        userDao.insertOrUpdateProfile(profile.copy(vibrationEnabled = enabled))
    }

    suspend fun claimQuestReward(questId: String) {
        val today = getTodayDateString()
        val quests = dailyQuestDao.getQuestsForDateOnce(today)
        val quest = quests.find { it.id == questId } ?: return
        if (quest.progress >= quest.target && !quest.isClaimed) {
            dailyQuestDao.updateQuest(quest.copy(isClaimed = true))
            val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
            val newXp = profile.xp + quest.rewardXp
            val newCoins = profile.coins + quest.rewardCoins
            val newLevel = calculatePlayerLevel(newXp)
            userDao.insertOrUpdateProfile(
                profile.copy(
                    xp = newXp,
                    coins = newCoins,
                    playerLevel = newLevel
                )
            )
        }
    }

    suspend fun recordGameResult(result: GameResult) {
        // 1. Insert history
        historyDao.insertGameHistory(
            GameHistoryEntity(
                modeId = result.mode.id,
                levelCode = result.level.code,
                difficultyCode = result.difficulty.code,
                score = result.score,
                correctCount = result.correctCount,
                wrongCount = result.wrongCount,
                totalQuestions = result.totalQuestions,
                accuracy = result.accuracy,
                durationSeconds = result.durationSeconds,
                isWin = result.isWin
            )
        )

        // 2. Update user profile XP, coins, player level
        val profile = userDao.getUserProfileOnce() ?: UserProfileEntity()
        val updatedXp = profile.xp + result.xpEarned
        val updatedCoins = profile.coins + result.coinsEarned
        val updatedLevel = calculatePlayerLevel(updatedXp)
        userDao.insertOrUpdateProfile(
            profile.copy(
                xp = updatedXp,
                coins = updatedCoins,
                playerLevel = updatedLevel
            )
        )

        // 3. Update topic statistics
        for (item in result.questionsAnswered) {
            val cat = item.question.category
            val existing = topicStatDao.getTopicStat(cat) ?: TopicStatEntity(topicName = cat)
            topicStatDao.insertOrUpdateTopicStat(
                existing.copy(
                    totalAnswered = existing.totalAnswered + 1,
                    totalCorrect = existing.totalCorrect + (if (item.isCorrect) 1 else 0)
                )
            )
        }

        // 4. Update achievements
        updateAchievementProgress("ach_warmup", result.totalQuestions)
        updateAchievementProgress("ach_expert", result.correctCount)
        if (result.maxStreak >= 10) {
            unlockAchievementDirectly("ach_combo")
        }
        if (result.mode == GameMode.TIME_ATTACK && result.totalQuestions > 0) {
            unlockAchievementDirectly("ach_time_hunter")
        }
        if (result.mode == GameMode.SURVIVAL && result.correctCount >= 20) {
            unlockAchievementDirectly("ach_survivor")
        }
        if (result.mode == GameMode.BOSS_BATTLE && result.bossDefeated) {
            unlockAchievementDirectly("ach_boss_slayer")
        }
        if (result.mode == GameMode.AI_DUEL && result.isWin) {
            unlockAchievementDirectly("ach_duel_master")
        }
        if (result.accuracy >= 100f && result.totalQuestions >= 5) {
            unlockAchievementDirectly("ach_perfectionist")
        }

        // 5. Update daily quests
        val today = getTodayDateString()
        val quests = dailyQuestDao.getQuestsForDateOnce(today)
        for (q in quests) {
            when (q.questKey) {
                "play" -> {
                    val p = minOf(q.target, q.progress + 1)
                    dailyQuestDao.updateQuest(q.copy(progress = p))
                }
                "correct" -> {
                    val p = minOf(q.target, q.progress + result.correctCount)
                    dailyQuestDao.updateQuest(q.copy(progress = p))
                }
                "streak" -> {
                    if (result.maxStreak >= q.target) {
                        dailyQuestDao.updateQuest(q.copy(progress = q.target))
                    }
                }
            }
        }
    }

    private suspend fun updateAchievementProgress(id: String, increment: Int) {
        val ach = achievementDao.getAchievementById(id) ?: return
        if (ach.isUnlocked) return
        val newProg = minOf(ach.maxProgress, ach.progress + increment)
        val isNowUnlocked = newProg >= ach.maxProgress
        achievementDao.updateAchievement(
            ach.copy(
                progress = newProg,
                isUnlocked = isNowUnlocked,
                unlockedAt = if (isNowUnlocked) System.currentTimeMillis() else ach.unlockedAt
            )
        )
    }

    private suspend fun unlockAchievementDirectly(id: String) {
        val ach = achievementDao.getAchievementById(id) ?: return
        if (!ach.isUnlocked) {
            achievementDao.updateAchievement(
                ach.copy(
                    progress = ach.maxProgress,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun resetAllProgress() {
        userDao.clearProfile()
        historyDao.clearHistory()
        achievementDao.clearAchievements()
        dailyQuestDao.clearDailyQuests()
        topicStatDao.clearTopicStats()
        initializeDefaultsIfNeeded()
    }

    companion object {
        fun calculatePlayerLevel(xp: Int): Int {
            // Level formula: Level 1 = 0 XP, Level 2 = 300, Level 3 = 800, etc.
            var level = 1
            var threshold = 300
            var remainingXp = xp
            while (remainingXp >= threshold) {
                remainingXp -= threshold
                level++
                threshold += 200
            }
            return level
        }

        fun getLevelTitle(level: Int): String {
            return when {
                level <= 2 -> "Pemula Berhitung"
                level <= 4 -> "Pelajar Rajin"
                level <= 7 -> "Petarung Angka"
                level <= 10 -> "Pendekar Aljabar"
                level <= 15 -> "Master Matematika"
                level <= 20 -> "Grandmaster Logika"
                else -> "Legenda Math Battle"
            }
        }

        fun getXpForNextLevel(currentXp: Int): Pair<Int, Int> {
            // Returns: Pair(currentLevelProgressXp, totalNeededForNextLevel)
            var threshold = 300
            var remainingXp = currentXp
            while (remainingXp >= threshold) {
                remainingXp -= threshold
                threshold += 200
            }
            return Pair(remainingXp, threshold)
        }
    }
}
