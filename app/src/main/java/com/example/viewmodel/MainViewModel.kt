package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundAndHapticManager
import com.example.audio.TextToSpeechManager
import com.example.audio.TtsState
import com.example.data.AppDatabase
import com.example.data.MathBattleRepository
import com.example.data.UserProfileEntity
import com.example.engine.AIDuelEngine
import com.example.engine.BossRoster
import com.example.engine.MathGenerator
import com.example.model.BossInfo
import com.example.model.Difficulty
import com.example.model.EducationLevel
import com.example.model.GameMode
import com.example.model.GameResult
import com.example.model.MathQuestion
import com.example.model.QuestionReviewItem
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BattleState(
    val isActive: Boolean = false,
    val mode: GameMode = GameMode.BATTLE_QUIZ,
    val level: EducationLevel = EducationLevel.SD,
    val difficulty: Difficulty = Difficulty.NORMAL,
    val topic: String = "Semua Materi",
    val currentQuestionIndex: Int = 0,
    val totalQuestionsCount: Int = 10,
    val currentQuestion: MathQuestion? = null,
    val score: Int = 0,
    val combo: Int = 0,
    val maxCombo: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lives: Int = 3,
    val timeRemainingSec: Int = 60,
    val maxTimeSec: Int = 60,
    val isPaused: Boolean = false,
    val isAnswerSelected: Boolean = false,
    val selectedOption: String? = null,
    val isCorrectSelection: Boolean = false,
    val showExplanationNow: Boolean = false,
    // AI Duel specific
    val aiName: String = "PythaBot 3000",
    val aiScore: Int = 0,
    val aiStatusText: String = "Sedang berpikir...",
    val aiLastQuote: String = "Bersiaplah menghadapi analisaku!",
    // Boss Battle specific
    val boss: BossInfo? = null,
    // Streak Challenge
    val streakTarget: Int = 10,
    // Free Practice options
    val isFreePracticeNoTimer: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val repository = MathBattleRepository(database)
    val audioManager = SoundAndHapticManager(application)
    val ttsManager = TextToSpeechManager(application)

    val currentScreen = MutableStateFlow<Screen>(Screen.Splash)

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val ttsState: StateFlow<TtsState> = ttsManager.ttsState

    val allAchievements = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayQuests = repository.getTodayQuests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentHistory = repository.recentHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topicStats = repository.allTopicStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending configurations before battle
    val pendingMode = MutableStateFlow(GameMode.BATTLE_QUIZ)
    val pendingTopic = MutableStateFlow("Semua Materi")
    val pendingQuizLength = MutableStateFlow(10) // 10, 20, 30
    val pendingTimeAttackDuration = MutableStateFlow(60) // 30, 60, 120
    val pendingBossIndex = MutableStateFlow(0)

    // Active Battle State
    private val _battleState = MutableStateFlow(BattleState())
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    // Last completed result & review items
    val lastResult = MutableStateFlow<GameResult?>(null)
    private val currentReviewItems = mutableListOf<QuestionReviewItem>()

    // Coroutine Jobs
    private var timerJob: Job? = null
    private var aiDuelJob: Job? = null
    private var battleStartTime: Long = 0

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
            delay(1200) // Splash animation duration
            currentScreen.value = Screen.MainMenu
        }
    }

    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun updateEducationLevel(level: EducationLevel) {
        viewModelScope.launch {
            repository.updateEducationLevel(level)
            pendingTopic.value = "Semua Materi"
        }
    }

    fun updateDifficulty(difficulty: Difficulty) {
        viewModelScope.launch {
            repository.updateDifficulty(difficulty)
        }
    }

    fun updatePlayerName(name: String) {
        viewModelScope.launch {
            repository.updatePlayerName(name)
        }
    }

    fun updateAvatar(emoji: String) {
        viewModelScope.launch {
            repository.updateAvatar(emoji)
        }
    }

    fun toggleAudio(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleAudio(enabled)
            if (!enabled) {
                audioManager.stopBgm()
                ttsManager.stop()
            }
        }
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleMusic(enabled)
            val profile = userProfile.value
            val vol = profile?.musicVolume ?: 0.7f
            if (_battleState.value.isActive) {
                if (enabled) {
                    audioManager.startBgm(true, vol)
                } else {
                    audioManager.stopBgm()
                }
            }
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleSound(enabled)
        }
    }

    fun toggleTts(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleTts(enabled)
            if (!enabled) {
                ttsManager.stop()
            }
        }
    }

    fun toggleTtsAutoRead(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleTtsAutoRead(enabled)
        }
    }

    fun setMusicVolume(volume: Float) {
        viewModelScope.launch {
            repository.setMusicVolume(volume)
            audioManager.setBgmVolume(volume)
        }
    }

    fun setSoundVolume(volume: Float) {
        viewModelScope.launch {
            repository.setSoundVolume(volume)
        }
    }

    fun setTtsSpeechRate(rate: Float) {
        viewModelScope.launch {
            repository.setTtsSpeechRate(rate)
            ttsManager.setSpeechRate(rate)
        }
    }

    fun playButtonClick() {
        val profile = userProfile.value
        val soundVol = profile?.soundVolume ?: 0.8f
        audioManager.playClick(
            profile?.soundEnabled ?: true,
            soundVol,
            profile?.vibrationEnabled ?: true
        )
    }

    fun testAudioSample(sampleText: String = "Dua belas ditambah delapan sama dengan berapa?") {
        val profile = userProfile.value
        val soundVol = profile?.soundVolume ?: 0.8f
        audioManager.playCorrect(profile?.soundEnabled ?: true, soundVol, profile?.vibrationEnabled ?: true)
        ttsManager.speak(sampleText, profile?.ttsEnabled ?: true)
    }

    fun speakQuestion(text: String) {
        val profile = userProfile.value
        ttsManager.setSpeechRate(profile?.ttsSpeechRate ?: 1.0f)
        ttsManager.speak(text, profile?.ttsEnabled ?: true)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleVibration(enabled)
        }
    }

    fun claimQuest(questId: String) {
        viewModelScope.launch {
            repository.claimQuestReward(questId)
            val profile = userProfile.value
            audioManager.playVictory(profile?.soundEnabled ?: true, profile?.soundVolume ?: 0.8f, profile?.vibrationEnabled ?: true)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            audioManager.stopBgm()
            ttsManager.stop()
            repository.resetAllProgress()
            currentScreen.value = Screen.Onboarding
        }
    }

    // =========================================================================
    // BATTLE INITIALIZATION & GAME LOOP
    // =========================================================================

    fun startBattle(
        mode: GameMode,
        topic: String? = null,
        quizLength: Int = 10,
        timeDuration: Int = 60
    ) {
        val profile = userProfile.value ?: UserProfileEntity()
        val level = EducationLevel.fromCode(profile.selectedLevelCode)
        val difficulty = Difficulty.fromCode(profile.selectedDifficultyCode)
        val chosenTopic = topic ?: pendingTopic.value

        currentReviewItems.clear()
        battleStartTime = System.currentTimeMillis()

        val firstQuestion = MathGenerator.generateQuestion(level, difficulty, chosenTopic)

        val boss = if (mode == GameMode.BOSS_BATTLE) {
            val roster = BossRoster.BOSSES
            val idx = pendingBossIndex.value.coerceIn(0, roster.lastIndex)
            roster[idx].copy()
        } else null

        val totalQuestions = when (mode) {
            GameMode.BATTLE_QUIZ -> quizLength
            GameMode.TIME_ATTACK -> 999
            GameMode.SURVIVAL -> 999
            GameMode.STREAK_CHALLENGE -> 50
            GameMode.AI_DUEL -> 10
            GameMode.BOSS_BATTLE -> boss?.maxHp ?: 12
            GameMode.FREE_PRACTICE -> 999
        }

        val maxTime = when (mode) {
            GameMode.TIME_ATTACK -> timeDuration
            GameMode.BATTLE_QUIZ -> difficulty.timeLimitSec
            GameMode.AI_DUEL -> difficulty.timeLimitSec
            GameMode.BOSS_BATTLE -> difficulty.timeLimitSec
            GameMode.SURVIVAL -> difficulty.timeLimitSec
            GameMode.STREAK_CHALLENGE -> difficulty.timeLimitSec
            GameMode.FREE_PRACTICE -> 0
        }

        val aiName = AIDuelEngine.AI_NAMES.random()

        _battleState.value = BattleState(
            isActive = true,
            mode = mode,
            level = level,
            difficulty = difficulty,
            topic = chosenTopic,
            currentQuestionIndex = 1,
            totalQuestionsCount = totalQuestions,
            currentQuestion = firstQuestion,
            score = 0,
            combo = 0,
            maxCombo = 0,
            correctCount = 0,
            wrongCount = 0,
            lives = 3,
            timeRemainingSec = maxTime,
            maxTimeSec = maxTime,
            isPaused = false,
            isAnswerSelected = false,
            selectedOption = null,
            isCorrectSelection = false,
            showExplanationNow = false,
            aiName = aiName,
            aiScore = 0,
            aiStatusText = "Sedang berpikir...",
            aiLastQuote = "Mulai kalkulasi!",
            boss = boss,
            streakTarget = 10,
            isFreePracticeNoTimer = true
        )

        currentScreen.value = Screen.Battle
        startBattleTimer()

        // Audio & TTS initializations
        audioManager.startBgm(profile.musicEnabled, profile.musicVolume)
        audioManager.playStart(profile.soundEnabled, profile.soundVolume, profile.vibrationEnabled)
        if (profile.ttsEnabled && profile.ttsAutoRead) {
            speakQuestion(firstQuestion.question)
        }

        if (mode == GameMode.AI_DUEL) {
            triggerAITurn()
        }
    }

    private fun startBattleTimer() {
        timerJob?.cancel()
        val mode = _battleState.value.mode
        if (mode == GameMode.FREE_PRACTICE && _battleState.value.isFreePracticeNoTimer) {
            return
        }

        timerJob = viewModelScope.launch {
            while (_battleState.value.isActive) {
                delay(1000)
                val current = _battleState.value
                if (current.isPaused || current.isAnswerSelected) {
                    continue
                }

                if (current.timeRemainingSec > 0) {
                    val newTime = current.timeRemainingSec - 1
                    _battleState.value = current.copy(timeRemainingSec = newTime)
                    if (newTime == 0) {
                        handleTimeExpired()
                    }
                }
            }
        }
    }

    private fun handleTimeExpired() {
        ttsManager.stop()
        val current = _battleState.value
        val profile = userProfile.value
        val soundVol = profile?.soundVolume ?: 0.8f
        audioManager.playWrong(profile?.soundEnabled ?: true, soundVol, profile?.vibrationEnabled ?: true)

        if (current.mode == GameMode.TIME_ATTACK) {
            // Time Attack ends when total timer hits 0
            finishBattle()
            return
        }

        // In per-question timed modes, expiring counts as a wrong answer
        evaluateAnswer(selected = null)
    }

    private fun triggerAITurn() {
        aiDuelJob?.cancel()
        val current = _battleState.value
        if (!current.isActive || current.mode != GameMode.AI_DUEL) return

        aiDuelJob = viewModelScope.launch {
            _battleState.value = _battleState.value.copy(aiStatusText = "Sedang berpikir...")
            val q = _battleState.value.currentQuestion ?: return@launch
            val sim = AIDuelEngine.simulateAIAnswer(current.difficulty, q.points)
            delay(sim.responseDelaySec * 1000L)

            val updatedState = _battleState.value
            if (updatedState.isActive && updatedState.mode == GameMode.AI_DUEL) {
                val newAiScore = updatedState.aiScore + sim.aiScoreAdded
                val status = if (sim.isCorrect) "AI menjawab Benar! (+${sim.aiScoreAdded})" else "AI menjawab Salah!"
                _battleState.value = updatedState.copy(
                    aiScore = newAiScore,
                    aiStatusText = status,
                    aiLastQuote = sim.quote
                )
            }
        }
    }

    fun submitAnswer(option: String) {
        val current = _battleState.value
        if (current.isAnswerSelected || !current.isActive) return
        evaluateAnswer(selected = option)
    }

    private fun evaluateAnswer(selected: String?) {
        val current = _battleState.value
        val q = current.currentQuestion ?: return
        val profile = userProfile.value
        // Stop ongoing question speech when user answers
        ttsManager.stop()

        val isSound = profile?.soundEnabled ?: true
        val soundVol = profile?.soundVolume ?: 0.8f
        val isVibe = profile?.vibrationEnabled ?: true

        val isCorrect = selected != null && selected == q.correctAnswer
        val newCombo = if (isCorrect) current.combo + 1 else 0
        val newMaxCombo = maxOf(current.maxCombo, newCombo)

        // Combo bonus: 10% per combo up to 2.5x
        val comboMultiplier = 1.0f + (newCombo * 0.10f).coerceAtMost(1.5f)
        val pointsEarned = if (isCorrect) (q.points * comboMultiplier).toInt() else 0
        val newScore = current.score + pointsEarned
        val newCorrectCount = current.correctCount + (if (isCorrect) 1 else 0)
        val newWrongCount = current.wrongCount + (if (isCorrect) 0 else 1)

        // Audio & Haptics
        if (isCorrect) {
            audioManager.playCorrect(isSound, soundVol, isVibe)
        } else {
            audioManager.playWrong(isSound, soundVol, isVibe)
        }

        // Record review item
        currentReviewItems.add(
            QuestionReviewItem(
                question = q,
                selectedAnswer = selected,
                isCorrect = isCorrect,
                timeSpentSeconds = current.maxTimeSec - current.timeRemainingSec
            )
        )

        // Survival lives update
        var updatedLives = current.lives
        if (current.mode == GameMode.SURVIVAL && !isCorrect) {
            updatedLives--
        }

        // Boss Battle HP update
        var updatedBoss = current.boss
        if (current.mode == GameMode.BOSS_BATTLE) {
            if (isCorrect && updatedBoss != null) {
                audioManager.playBossHit(isSound, soundVol, isVibe)
                val newHp = (updatedBoss.currentHp - 1).coerceAtLeast(0)
                updatedBoss = updatedBoss.copy(currentHp = newHp)
            }
        }

        _battleState.value = current.copy(
            isAnswerSelected = true,
            selectedOption = selected,
            isCorrectSelection = isCorrect,
            score = newScore,
            combo = newCombo,
            maxCombo = newMaxCombo,
            correctCount = newCorrectCount,
            wrongCount = newWrongCount,
            lives = updatedLives,
            boss = updatedBoss,
            showExplanationNow = (current.mode == GameMode.FREE_PRACTICE)
        )

        // Check immediate game-over conditions
        if (current.mode == GameMode.SURVIVAL && updatedLives <= 0) {
            viewModelScope.launch {
                delay(1200)
                finishBattle()
            }
            return
        }

        if (current.mode == GameMode.BOSS_BATTLE && (updatedBoss?.currentHp ?: 1) <= 0) {
            viewModelScope.launch {
                delay(1200)
                finishBattle(isWin = true, bossDefeated = true)
            }
            return
        }

        if (current.mode == GameMode.STREAK_CHALLENGE && !isCorrect) {
            viewModelScope.launch {
                delay(1200)
                finishBattle()
            }
            return
        }

        if (current.mode == GameMode.BATTLE_QUIZ && current.currentQuestionIndex >= current.totalQuestionsCount) {
            viewModelScope.launch {
                delay(1200)
                finishBattle()
            }
            return
        }

        if (current.mode == GameMode.AI_DUEL && current.currentQuestionIndex >= current.totalQuestionsCount) {
            viewModelScope.launch {
                delay(1200)
                finishBattle(isWin = _battleState.value.score >= _battleState.value.aiScore)
            }
            return
        }

        // Automatic advance for fast modes, manual for Free Practice
        if (current.mode != GameMode.FREE_PRACTICE) {
            viewModelScope.launch {
                delay(950)
                advanceToNextQuestion()
            }
        }
    }

    fun advanceToNextQuestion() {
        val current = _battleState.value
        if (!current.isActive) return

        ttsManager.stop()

        val nextQuestion = MathGenerator.generateQuestion(current.level, current.difficulty, current.topic)
        val resetTime = when (current.mode) {
            GameMode.TIME_ATTACK -> current.timeRemainingSec // Keep running total time
            GameMode.FREE_PRACTICE -> 0
            else -> current.difficulty.timeLimitSec
        }

        _battleState.value = current.copy(
            currentQuestionIndex = current.currentQuestionIndex + 1,
            currentQuestion = nextQuestion,
            isAnswerSelected = false,
            selectedOption = null,
            isCorrectSelection = false,
            showExplanationNow = false,
            timeRemainingSec = resetTime,
            maxTimeSec = resetTime
        )

        // Auto read next question if enabled
        val profile = userProfile.value
        if (profile?.ttsEnabled == true && profile.ttsAutoRead) {
            speakQuestion(nextQuestion.question)
        }

        if (current.mode == GameMode.AI_DUEL) {
            triggerAITurn()
        }
    }

    fun pauseBattle() {
        audioManager.pauseBgm()
        ttsManager.stop()
        _battleState.value = _battleState.value.copy(isPaused = true)
    }

    fun resumeBattle() {
        val profile = userProfile.value
        audioManager.resumeBgm(profile?.musicEnabled ?: true, profile?.musicVolume ?: 0.7f)
        _battleState.value = _battleState.value.copy(isPaused = false)
    }

    fun finishBattle(isWin: Boolean = true, bossDefeated: Boolean = false) {
        timerJob?.cancel()
        aiDuelJob?.cancel()
        audioManager.stopBgm()
        ttsManager.stop()

        val current = _battleState.value
        val duration = ((System.currentTimeMillis() - battleStartTime) / 1000).toInt()

        val totalQ = current.correctCount + current.wrongCount
        val accuracy = if (totalQ > 0) (current.correctCount.toFloat() / totalQ.toFloat()) * 100f else 0f

        // Rewards calculation
        var xpEarned = (current.score * 0.4f).toInt() + (current.correctCount * 15)
        var coinsEarned = (current.score * 0.15f).toInt() + (current.correctCount * 5)

        if (bossDefeated && current.boss != null) {
            xpEarned += current.boss.rewardXp
            coinsEarned += current.boss.rewardCoins
        }

        val result = GameResult(
            mode = current.mode,
            level = current.level,
            difficulty = current.difficulty,
            score = current.score,
            correctCount = current.correctCount,
            wrongCount = current.wrongCount,
            totalQuestions = totalQ,
            accuracy = accuracy,
            maxStreak = current.maxCombo,
            durationSeconds = duration,
            xpEarned = xpEarned,
            coinsEarned = coinsEarned,
            isWin = isWin,
            bossDefeated = bossDefeated,
            questionsAnswered = currentReviewItems.toList()
        )

        lastResult.value = result
        _battleState.value = current.copy(isActive = false)

        viewModelScope.launch {
            repository.recordGameResult(result)
        }

        val profile = userProfile.value
        val soundVol = profile?.soundVolume ?: 0.8f
        audioManager.playVictory(profile?.soundEnabled ?: true, soundVol, profile?.vibrationEnabled ?: true)
        currentScreen.value = Screen.BattleResult
    }

    fun exitBattleToMainMenu() {
        timerJob?.cancel()
        aiDuelJob?.cancel()
        audioManager.stopBgm()
        ttsManager.stop()
        _battleState.value = _battleState.value.copy(isActive = false)
        currentScreen.value = Screen.MainMenu
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        aiDuelJob?.cancel()
        audioManager.release()
        ttsManager.shutdown()
    }
}
