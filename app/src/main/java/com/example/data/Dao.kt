package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun clearProfile()
}

@Dao
interface GameHistoryDao {
    @Query("SELECT * FROM game_history ORDER BY timestamp DESC LIMIT 30")
    fun getRecentHistory(): Flow<List<GameHistoryEntity>>

    @Query("SELECT COUNT(*) FROM game_history")
    fun getTotalGamesCount(): Flow<Int>

    @Query("SELECT MAX(score) FROM game_history WHERE modeId = :modeId")
    suspend fun getHighScoreForMode(modeId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameHistory(history: GameHistoryEntity): Long

    @Query("DELETE FROM game_history")
    suspend fun clearHistory()
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun seedAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getAchievementById(id: String): AchievementEntity?

    @Query("DELETE FROM achievements")
    suspend fun clearAchievements()
}

@Dao
interface DailyQuestDao {
    @Query("SELECT * FROM daily_quests WHERE questDate = :date")
    fun getQuestsForDate(date: String): Flow<List<DailyQuestEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun seedQuests(quests: List<DailyQuestEntity>)

    @Update
    suspend fun updateQuest(quest: DailyQuestEntity)

    @Query("SELECT * FROM daily_quests WHERE questDate = :date")
    suspend fun getQuestsForDateOnce(date: String): List<DailyQuestEntity>

    @Query("DELETE FROM daily_quests")
    suspend fun clearDailyQuests()
}

@Dao
interface TopicStatDao {
    @Query("SELECT * FROM topic_stats ORDER BY totalAnswered DESC")
    fun getAllTopicStats(): Flow<List<TopicStatEntity>>

    @Query("SELECT * FROM topic_stats WHERE topicName = :topic LIMIT 1")
    suspend fun getTopicStat(topic: String): TopicStatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTopicStat(stat: TopicStatEntity)

    @Query("DELETE FROM topic_stats")
    suspend fun clearTopicStats()
}
