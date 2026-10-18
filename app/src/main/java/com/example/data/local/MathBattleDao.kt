package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MathBattleDao {

    @Query("SELECT * FROM player_profile WHERE id = :id")
    fun getProfileFlow(id: String = "local_player"): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = :id")
    suspend fun getProfileSync(id: String = "local_player"): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllMatchHistory(): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchHistory(match: MatchHistoryEntity)

    @Query("DELETE FROM match_history")
    suspend fun clearHistory()

    @Query("SELECT * FROM audio_settings WHERE id = 1")
    fun getAudioSettingsFlow(): Flow<AudioSettingsEntity?>

    @Query("SELECT * FROM audio_settings WHERE id = 1")
    suspend fun getAudioSettingsSync(): AudioSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAudioSettings(settings: AudioSettingsEntity)

    @Query("SELECT * FROM daily_quests")
    fun getAllQuests(): Flow<List<DailyQuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<DailyQuestEntity>)

    @Update
    suspend fun updateQuest(quest: DailyQuestEntity)
}
