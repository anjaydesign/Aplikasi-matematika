package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.IndonesianSpeechHelper
import com.example.audio.SoundAndHapticManager
import com.example.data.generator.QuestionGenerator
import com.example.data.local.AppDatabase
import com.example.data.local.AudioSettingsEntity
import com.example.data.local.DailyQuestEntity
import com.example.data.local.MatchHistoryEntity
import com.example.data.local.PlayerProfileEntity
import com.example.data.model.AnswerResultPayload
import com.example.data.model.AnswerSubmissionPayload
import com.example.data.model.ConnectedPlayer
import com.example.data.model.Difficulty
import com.example.data.model.MatchSettings
import com.example.data.model.MathCategory
import com.example.data.model.MathGrade
import com.example.data.model.MathQuestion
import com.example.data.model.NetworkMessage
import com.example.data.model.NetworkMessageType
import com.example.data.model.PlayerScore
import com.example.data.model.PlayerScoreInfo
import com.example.data.model.QuestionClientPayload
import com.example.data.model.ScoreUpdatePayload
import com.example.data.repository.MathBattleRepository
import com.example.network.HostMatchManager
import com.example.network.LocalMultiplayerClient
import com.example.network.LocalMultiplayerServer
import com.example.network.NetworkUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class AppScreen {
    MAIN_MENU,
    PASS_AND_PLAY_SETUP,
    PASS_AND_PLAY_GAME,
    PASS_AND_PLAY_RESULT,
    WIFI_MENU,
    WIFI_HOST_LOBBY,
    WIFI_JOIN_INPUT,
    WIFI_CLIENT_LOBBY,
    WIFI_GAME_SCREEN,
    WIFI_RESULT_SCREEN,
    SOLO_SELECT,
    SOLO_GAME,
    SOLO_RESULT,
    SETTINGS,
    HISTORY,
    QUESTS_SHOP
}

enum class SoloModeType(val title: String, val description: String) {
    RACING("Math Racing", "Berlomba mengumpulkan poin tertinggi dalam batas waktu!"),
    BOSS_BATTLE("Team Boss Battle", "Kalahkan Bos Matematika dengan menjawab soal secara tepat!"),
    STREAK("Streak Challenge", "Uji kemampuan menjawab benar tanpa henti untuk multiplier skor."),
    SURVIVAL("Survival Mode", "Bertahan selama mungkin dengan 3 kesempatan nyawa.")
}

class MathBattleViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = MathBattleRepository(db.mathBattleDao())

    val soundManager = SoundAndHapticManager(application)
    val speechHelper = IndonesianSpeechHelper(application)

    val profile: StateFlow<PlayerProfileEntity?> = repository.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val matchHistory: StateFlow<List<MatchHistoryEntity>> = repository.matchHistoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audioSettings: StateFlow<AudioSettingsEntity?> = repository.audioSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val quests: StateFlow<List<DailyQuestEntity>> = repository.questsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.MAIN_MENU)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    data class PassAndPlayPlayer(
        val id: String = UUID.randomUUID().toString(),
        var name: String,
        var avatarIndex: Int = 0,
        var score: Int = 0,
        var correctCount: Int = 0,
        var totalTimeSpentMs: Long = 0L,
        var currentStreak: Int = 0,
        var highestStreak: Int = 0
    )

    val passAndPlayPlayers = MutableStateFlow(
        listOf(
            PassAndPlayPlayer(name = "Pemain 1", avatarIndex = 0),
            PassAndPlayPlayer(name = "Pemain 2", avatarIndex = 1)
        )
    )
    val passAndPlaySettings = MutableStateFlow(MatchSettings())
    val passAndPlayQuestions = MutableStateFlow<List<MathQuestion>>(emptyList())
    val currentRoundIndex = MutableStateFlow(0)
    val currentTurnPlayerIndex = MutableStateFlow(0)
    val isWaitingForTurnConfirmation = MutableStateFlow(true)
    val turnTimeRemainingSeconds = MutableStateFlow(15)
    val lastTurnFeedback = MutableStateFlow<String?>(null)
    val isTurnAnswered = MutableStateFlow(false)
    val passAndPlayRankings = MutableStateFlow<List<PassAndPlayPlayer>>(emptyList())

    private var turnTimerJob: Job? = null
    private var turnStartTimeMs = 0L

    private var wifiServer: LocalMultiplayerServer? = null
    private var hostMatchManager: HostMatchManager? = null
    private var wifiClient: LocalMultiplayerClient? = null

    val isHosting = MutableStateFlow(false)
    val isClientConnected = MutableStateFlow(false)
    val wifiRoomCode = MutableStateFlow("")
    val wifiHostIp = MutableStateFlow("")
    val wifiConnectedPlayers = MutableStateFlow<List<ConnectedPlayer>>(emptyList())
    val wifiErrorMessage = MutableStateFlow<String?>(null)
    val wifiMatchScores = MutableStateFlow<List<PlayerScoreInfo>>(emptyList())
    val wifiCurrentQuestion = MutableStateFlow<QuestionClientPayload?>(null)
    val wifiClientAnswerResult = MutableStateFlow<AnswerResultPayload?>(null)
    val wifiHasSubmittedAnswer = MutableStateFlow(false)
    val wifiTimeRemainingSeconds = MutableStateFlow(15)
    val wifiFinalRankings = MutableStateFlow<List<PlayerScore>>(emptyList())

    private var wifiTimerJob: Job? = null

    val currentSoloMode = MutableStateFlow(SoloModeType.RACING)
    val soloQuestions = MutableStateFlow<List<MathQuestion>>(emptyList())
    val soloQuestionIndex = MutableStateFlow(0)
    val soloScore = MutableStateFlow(0)
    val soloStreak = MutableStateFlow(0)
    val soloLives = MutableStateFlow(3)
    val soloBossMaxHp = MutableStateFlow(500)
    val soloBossHp = MutableStateFlow(500)
    val soloTimeRemaining = MutableStateFlow(15)
    val soloFeedback = MutableStateFlow<String?>(null)
    val isSoloAnswered = MutableStateFlow(false)
    private var soloTimerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureInitialData()
        }
        viewModelScope.launch {
            audioSettings.collect { settings ->
                if (settings != null) {
                    soundManager.sfxEnabled = settings.sfxEnabled
                    soundManager.bgmEnabled = settings.bgmEnabled
                    soundManager.sfxVolume = settings.sfxVolume
                    soundManager.setBgmVolumeLevel(settings.bgmVolume)
                    speechHelper.ttsEnabled = settings.ttsEnabled
                    if (settings.bgmEnabled) {
                        soundManager.startBgm()
                    } else {
                        soundManager.pauseBgm()
                    }
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        soundManager.playClick()
        speechHelper.stop()
        _currentScreen.value = screen
    }

    fun setPassAndPlayPlayerCount(count: Int) {
        val current = passAndPlayPlayers.value.toMutableList()
        val defaultNames = listOf("Pemain 1", "Pemain 2", "Pemain 3", "Pemain 4")
        while (current.size < count) {
            val idx = current.size
            current.add(PassAndPlayPlayer(name = defaultNames.getOrElse(idx) { "Pemain ${idx + 1}" }, avatarIndex = idx))
        }
        while (current.size > count) {
            current.removeAt(current.size - 1)
        }
        passAndPlayPlayers.value = current
    }

    fun updatePassAndPlayPlayerName(index: Int, name: String) {
        val list = passAndPlayPlayers.value.toMutableList()
        if (index in list.indices) {
            list[index] = list[index].copy(name = name.take(15))
            passAndPlayPlayers.value = list
        }
    }

    fun startPassAndPlayMatch() {
        val settings = passAndPlaySettings.value
        val questions = QuestionGenerator.generateQuestions(
            count = settings.questionCount,
            grade = settings.grade,
            category = settings.category,
            difficulty = settings.difficulty
        )
        passAndPlayQuestions.value = questions

        val resetPlayers = passAndPlayPlayers.value.map {
            it.copy(
                score = 0,
                correctCount = 0,
                totalTimeSpentMs = 0L,
                currentStreak = 0,
                highestStreak = 0
            )
        }
        passAndPlayPlayers.value = resetPlayers

        currentRoundIndex.value = 0
        currentTurnPlayerIndex.value = 0
        isWaitingForTurnConfirmation.value = true
        isTurnAnswered.value = false
        lastTurnFeedback.value = null

        soundManager.playStart()
        _currentScreen.value = AppScreen.PASS_AND_PLAY_GAME
    }

    fun confirmReadyForTurn() {
        soundManager.playClick()
        isWaitingForTurnConfirmation.value = false
        isTurnAnswered.value = false
        lastTurnFeedback.value = null

        val currentQ = passAndPlayQuestions.value.getOrNull(currentRoundIndex.value)
        if (currentQ != null && audioSettings.value?.ttsAutoRead == true) {
            speechHelper.speakMathPrompt(currentQ.spokenPrompt)
        }

        startTurnTimer()
    }

    private fun startTurnTimer() {
        turnTimerJob?.cancel()
        val duration = passAndPlaySettings.value.timePerQuestionSeconds
        turnTimeRemainingSeconds.value = duration
        turnStartTimeMs = System.currentTimeMillis()

        turnTimerJob = viewModelScope.launch {
            for (sec in duration downTo 1) {
                turnTimeRemainingSeconds.value = sec
                if (sec <= 3) {
                    soundManager.playTick()
                }
                delay(1000)
            }
            turnTimeRemainingSeconds.value = 0
            if (!isTurnAnswered.value) {
                submitPassAndPlayAnswer(selectedAnswer = "")
            }
        }
    }

    fun submitPassAndPlayAnswer(selectedAnswer: String) {
        if (isTurnAnswered.value) return
        isTurnAnswered.value = true
        turnTimerJob?.cancel()
        speechHelper.stop()

        val timeSpent = System.currentTimeMillis() - turnStartTimeMs
        val currentQ = passAndPlayQuestions.value.getOrNull(currentRoundIndex.value) ?: return
        val isCorrect = selectedAnswer.trim() == currentQ.correctAnswer.trim()

        val players = passAndPlayPlayers.value.toMutableList()
        val playerIdx = currentTurnPlayerIndex.value
        val player = players[playerIdx]

        var pointsEarned = 0
        if (isCorrect) {
            soundManager.playCorrect()
            val basePoints = 100
            val maxDur = passAndPlaySettings.value.timePerQuestionSeconds * 1000L
            val speedBonus = if (passAndPlaySettings.value.speedBonusEnabled) {
                val ratio = (1.0f - (timeSpent.toFloat() / maxDur.toFloat())).coerceIn(0f, 1f)
                (ratio * 50).toInt()
            } else 0
            pointsEarned = basePoints + speedBonus

            val newScore = player.score + pointsEarned
            val newCorrect = player.correctCount + 1
            val newStreak = player.currentStreak + 1
            val highStreak = maxOf(player.highestStreak, newStreak)

            players[playerIdx] = player.copy(
                score = newScore,
                correctCount = newCorrect,
                totalTimeSpentMs = player.totalTimeSpentMs + timeSpent,
                currentStreak = newStreak,
                highestStreak = highStreak
            )
            lastTurnFeedback.value = "Benar! +$pointsEarned poin"
        } else {
            soundManager.playWrong()
            players[playerIdx] = player.copy(
                totalTimeSpentMs = player.totalTimeSpentMs + timeSpent,
                currentStreak = 0
            )
            lastTurnFeedback.value = if (selectedAnswer.isEmpty()) {
                "Waktu Habis! Jawaban: ${currentQ.correctAnswer}"
            } else {
                "Salah! Jawaban: ${currentQ.correctAnswer}"
            }
        }
        passAndPlayPlayers.value = players

        viewModelScope.launch {
            delay(1800)
            advancePassAndPlayTurn()
        }
    }

    private fun advancePassAndPlayTurn() {
        val totalPlayers = passAndPlayPlayers.value.size
        val nextPlayerIdx = currentTurnPlayerIndex.value + 1

        if (nextPlayerIdx < totalPlayers) {
            currentTurnPlayerIndex.value = nextPlayerIdx
            isWaitingForTurnConfirmation.value = true
            isTurnAnswered.value = false
            lastTurnFeedback.value = null
        } else {
            val nextRound = currentRoundIndex.value + 1
            if (nextRound < passAndPlayQuestions.value.size) {
                currentRoundIndex.value = nextRound
                currentTurnPlayerIndex.value = 0
                isWaitingForTurnConfirmation.value = true
                isTurnAnswered.value = false
                lastTurnFeedback.value = null
            } else {
                finishPassAndPlayMatch()
            }
        }
    }

    private fun finishPassAndPlayMatch() {
        soundManager.playVictory()
        val ranked = passAndPlayPlayers.value.sortedWith(
            compareByDescending<PassAndPlayPlayer> { it.score }
                .thenByDescending { it.correctCount }
                .thenBy { it.totalTimeSpentMs }
        )
        passAndPlayRankings.value = ranked

        val winner = ranked.firstOrNull()?.name ?: "Pemain"
        val rankingsArr = JSONArray()
        ranked.forEachIndexed { i, p ->
            rankingsArr.put(
                JSONObject().apply {
                    put("rank", i + 1)
                    put("name", p.name)
                    put("score", p.score)
                    put("correct", p.correctCount)
                }
            )
        }

        viewModelScope.launch {
            repository.recordMatch(
                mode = "SATU_HP",
                playerCount = ranked.size,
                winnerName = winner,
                rankingsJson = rankingsArr.toString(),
                questionCount = passAndPlayQuestions.value.size,
                xpGained = 150,
                coinsGained = 75
            )
        }

        _currentScreen.value = AppScreen.PASS_AND_PLAY_RESULT
    }

    fun initHostRoom() {
        val context = getApplication<Application>()
        val myIp = NetworkUtils.getLocalIpAddress(context)
        wifiHostIp.value = myIp
        wifiRoomCode.value = NetworkUtils.ipToRoomCode(myIp)
        wifiErrorMessage.value = null
        isHosting.value = true

        val myProfile = profile.value
        val hostName = myProfile?.name ?: "Host"
        val hostAvatar = myProfile?.selectedAvatar ?: 0

        wifiServer?.stop()
        wifiServer = LocalMultiplayerServer(
            port = NetworkUtils.DEFAULT_PORT,
            onMessageReceived = { msg, socketId ->
                hostMatchManager?.handleIncomingMessage(msg, socketId)
            },
            onClientConnected = { _ -> },
            onClientDisconnected = { socketId ->
                hostMatchManager?.handlePlayerLeave(socketId)
            },
            onError = { err ->
                wifiErrorMessage.value = err
            }
        ).apply {
            start()
        }

        hostMatchManager = HostMatchManager(
            hostId = "host_${UUID.randomUUID().toString().take(6)}",
            hostName = hostName,
            hostAvatarIndex = hostAvatar,
            server = wifiServer!!,
            onMatchStateChanged = {
                syncHostStateToUi()
            },
            onMatchFinished = { rankings ->
                wifiFinalRankings.value = rankings
                soundManager.playVictory()
                _currentScreen.value = AppScreen.WIFI_RESULT_SCREEN
                viewModelScope.launch {
                    val arr = JSONArray()
                    rankings.forEachIndexed { i, r ->
                        arr.put(
                            JSONObject().apply {
                                put("rank", i + 1)
                                put("name", r.playerName)
                                put("score", r.score)
                            }
                        )
                    }
                    repository.recordMatch(
                        mode = "WIFI_LOKAL",
                        playerCount = rankings.size,
                        winnerName = rankings.firstOrNull()?.playerName ?: "Pemenang",
                        rankingsJson = arr.toString(),
                        questionCount = hostMatchManager?.totalRounds ?: 10,
                        xpGained = 200,
                        coinsGained = 100
                    )
                }
            }
        )

        syncHostStateToUi()
        _currentScreen.value = AppScreen.WIFI_HOST_LOBBY
    }

    private fun syncHostStateToUi() {
        val mgr = hostMatchManager ?: return
        wifiConnectedPlayers.value = mgr.players.toList()
        wifiMatchScores.value = mgr.players.map { p ->
            val s = mgr.scores[p.id]
            PlayerScoreInfo(
                playerId = p.id,
                playerName = p.name,
                score = s?.score ?: 0,
                correctCount = s?.correctAnswersCount ?: 0,
                avatarIndex = p.avatarIndex
            )
        }.sortedByDescending { it.score }

        val q = mgr.currentQuestion
        if (q != null && mgr.currentState == HostMatchManager.State.ROUND_ACTIVE) {
            wifiCurrentQuestion.value = QuestionClientPayload(
                questionId = q.id,
                roundIndex = mgr.currentRoundNumber,
                totalRounds = mgr.totalRounds,
                prompt = q.prompt,
                spokenPrompt = q.spokenPrompt,
                options = q.options,
                categoryName = q.category.displayName,
                durationSeconds = mgr.currentSettings.timePerQuestionSeconds,
                serverStartTimeMs = System.currentTimeMillis()
            )
        }
    }

    fun startHostMatch(settings: MatchSettings) {
        val mgr = hostMatchManager ?: return
        if (mgr.canStartMatch()) {
            soundManager.playStart()
            mgr.startMatch(settings)
            syncHostStateToUi()
            _currentScreen.value = AppScreen.WIFI_GAME_SCREEN
            startWifiQuestionTimer(settings.timePerQuestionSeconds)
        } else {
            wifiErrorMessage.value = "Semua pemain (minimal 2) harus dalam status SIAP!"
        }
    }

    fun connectToHost(targetCodeOrIp: String) {
        val context = getApplication<Application>()
        val localIp = NetworkUtils.getLocalIpAddress(context)
        val resolvedIp = NetworkUtils.roomCodeToIp(localIp, targetCodeOrIp)

        wifiErrorMessage.value = null
        isHosting.value = false
        _currentScreen.value = AppScreen.WIFI_CLIENT_LOBBY

        val myProfile = profile.value
        val myName = myProfile?.name ?: "Tamu"
        val myAvatar = myProfile?.selectedAvatar ?: 1
        val myId = "client_${UUID.randomUUID().toString().take(6)}"

        wifiClient?.disconnect()
        wifiClient = LocalMultiplayerClient(
            onMessageReceived = { msg ->
                handleClientMessage(msg)
            },
            onConnected = {
                isClientConnected.value = true
                wifiClient?.sendMessage(
                    NetworkMessage(
                        type = NetworkMessageType.JOIN_ROOM,
                        matchId = "",
                        senderId = myId,
                        senderName = myName,
                        payloadJson = JSONObject().apply {
                            put("avatarIndex", myAvatar)
                        }.toString()
                    )
                )
            },
            onDisconnected = { reason ->
                isClientConnected.value = false
                wifiErrorMessage.value = reason
            },
            onError = { err ->
                wifiErrorMessage.value = err
            }
        ).apply {
            connect(resolvedIp)
        }
    }

    private fun handleClientMessage(msg: NetworkMessage) {
        when (msg.type) {
            NetworkMessageType.ROOM_STATE -> {
                try {
                    val obj = JSONObject(msg.payloadJson)
                    val arr = obj.optJSONArray("players") ?: JSONArray()
                    val list = mutableListOf<ConnectedPlayer>()
                    for (i in 0 until arr.length()) {
                        list.add(ConnectedPlayer.fromJson(arr.getJSONObject(i)))
                    }
                    wifiConnectedPlayers.value = list
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            NetworkMessageType.START_MATCH -> {
                soundManager.playStart()
                _currentScreen.value = AppScreen.WIFI_GAME_SCREEN
            }
            NetworkMessageType.QUESTION -> {
                val qPayload = QuestionClientPayload.fromJson(JSONObject(msg.payloadJson))
                wifiCurrentQuestion.value = qPayload
                wifiClientAnswerResult.value = null
                wifiHasSubmittedAnswer.value = false
                if (audioSettings.value?.ttsAutoRead == true) {
                    speechHelper.speakMathPrompt(qPayload.spokenPrompt)
                }
                startWifiQuestionTimer(qPayload.durationSeconds)
            }
            NetworkMessageType.ANSWER_RESULT -> {
                val res = AnswerResultPayload.fromJson(JSONObject(msg.payloadJson))
                wifiClientAnswerResult.value = res
                if (res.isCorrect) {
                    soundManager.playCorrect()
                } else {
                    soundManager.playWrong()
                }
            }
            NetworkMessageType.SCORE_UPDATE -> {
                val sc = ScoreUpdatePayload.fromJson(JSONObject(msg.payloadJson))
                wifiMatchScores.value = sc.scores
            }
            NetworkMessageType.MATCH_FINISHED -> {
                try {
                    val obj = JSONObject(msg.payloadJson)
                    val arr = obj.optJSONArray("rankings") ?: JSONArray()
                    val list = mutableListOf<PlayerScore>()
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        list.add(
                            PlayerScore(
                                playerId = o.getString("playerId"),
                                playerName = o.getString("playerName"),
                                score = o.getInt("score"),
                                correctAnswersCount = o.optInt("correctAnswersCount", 0),
                                totalTimeSpentMs = o.optLong("totalTimeSpentMs", 0L),
                                highestStreak = o.optInt("highestStreak", 0),
                                avatarIndex = o.optInt("avatarIndex", 0)
                            )
                        )
                    }
                    wifiFinalRankings.value = list
                    soundManager.playVictory()
                    _currentScreen.value = AppScreen.WIFI_RESULT_SCREEN
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            NetworkMessageType.ERROR -> {
                try {
                    val err = JSONObject(msg.payloadJson).optString("error")
                    wifiErrorMessage.value = err
                } catch (e: Exception) {
                    wifiErrorMessage.value = "Terjadi kesalahan pada jaringan."
                }
            }
            else -> {}
        }
    }

    fun toggleClientReady(isReady: Boolean) {
        val myId = wifiConnectedPlayers.value.firstOrNull { !it.isHost }?.id ?: "client"
        wifiClient?.sendMessage(
            NetworkMessage(
                type = NetworkMessageType.PLAYER_READY,
                matchId = "",
                senderId = myId,
                senderName = "",
                payloadJson = JSONObject().apply { put("isReady", isReady) }.toString()
            )
        )
    }

    fun submitWifiAnswer(answer: String) {
        if (wifiHasSubmittedAnswer.value) return
        wifiHasSubmittedAnswer.value = true
        speechHelper.stop()

        val q = wifiCurrentQuestion.value ?: return
        val timeSpent = (q.durationSeconds - wifiTimeRemainingSeconds.value) * 1000L

        if (isHosting.value) {
            hostMatchManager?.submitHostAnswer(answer, timeSpent)
        } else {
            val myId = wifiConnectedPlayers.value.firstOrNull { !it.isHost }?.id ?: "client"
            wifiClient?.sendMessage(
                NetworkMessage(
                    type = NetworkMessageType.SUBMIT_ANSWER,
                    matchId = "",
                    senderId = myId,
                    senderName = "",
                    payloadJson = AnswerSubmissionPayload(
                        questionId = q.questionId,
                        roundIndex = q.roundIndex,
                        selectedAnswer = answer,
                        responseTimeMs = timeSpent
                    ).toJson().toString()
                )
            )
        }
    }

    private fun startWifiQuestionTimer(durationSeconds: Int) {
        wifiTimerJob?.cancel()
        wifiTimeRemainingSeconds.value = durationSeconds
        wifiTimerJob = viewModelScope.launch {
            for (sec in durationSeconds downTo 1) {
                wifiTimeRemainingSeconds.value = sec
                if (sec <= 3) {
                    soundManager.playTick()
                }
                delay(1000)
            }
            wifiTimeRemainingSeconds.value = 0
            if (!wifiHasSubmittedAnswer.value) {
                submitWifiAnswer("")
            }
        }
    }

    fun exitWifiMatch() {
        wifiTimerJob?.cancel()
        wifiServer?.stop()
        wifiClient?.disconnect()
        hostMatchManager?.cleanup()
        wifiServer = null
        wifiClient = null
        hostMatchManager = null
        _currentScreen.value = AppScreen.MAIN_MENU
    }

    fun startSoloGame(
        mode: SoloModeType,
        grade: MathGrade = MathGrade.SD,
        category: MathCategory = MathCategory.CAMPURAN_ARITMATIKA,
        difficulty: Difficulty = Difficulty.SEDANG,
        count: Int = 10
    ) {
        currentSoloMode.value = mode
        soloScore.value = 0
        soloStreak.value = 0
        soloLives.value = 3
        soloBossMaxHp.value = 500
        soloBossHp.value = 500
        soloQuestionIndex.value = 0
        isSoloAnswered.value = false
        soloFeedback.value = null

        val questions = QuestionGenerator.generateQuestions(
            count = count,
            grade = grade,
            category = category,
            difficulty = difficulty
        )
        soloQuestions.value = questions

        soundManager.playStart()
        _currentScreen.value = AppScreen.SOLO_GAME
        startSoloTimer()
    }

    private fun startSoloTimer() {
        soloTimerJob?.cancel()
        val duration = 15
        soloTimeRemaining.value = duration
        val q = soloQuestions.value.getOrNull(soloQuestionIndex.value)
        if (q != null && audioSettings.value?.ttsAutoRead == true) {
            speechHelper.speakMathPrompt(q.spokenPrompt)
        }

        soloTimerJob = viewModelScope.launch {
            for (sec in duration downTo 1) {
                soloTimeRemaining.value = sec
                if (sec <= 3) soundManager.playTick()
                delay(1000)
            }
            soloTimeRemaining.value = 0
            if (!isSoloAnswered.value) {
                submitSoloAnswer("")
            }
        }
    }

    fun submitSoloAnswer(selectedAnswer: String) {
        if (isSoloAnswered.value) return
        isSoloAnswered.value = true
        soloTimerJob?.cancel()
        speechHelper.stop()

        val q = soloQuestions.value.getOrNull(soloQuestionIndex.value) ?: return
        val isCorrect = selectedAnswer.trim() == q.correctAnswer.trim()

        if (isCorrect) {
            soundManager.playCorrect()
            val base = 100
            val speed = soloTimeRemaining.value * 3
            val streakBonus = soloStreak.value * 10
            val gained = base + speed + streakBonus

            soloScore.value += gained
            soloStreak.value += 1
            soloFeedback.value = "Benar! +$gained poin"

            if (currentSoloMode.value == SoloModeType.BOSS_BATTLE) {
                val dmg = 40 + (speed / 2)
                soloBossHp.value = maxOf(0, soloBossHp.value - dmg)
            }
        } else {
            soundManager.playWrong()
            soloStreak.value = 0
            soloLives.value -= 1
            soloFeedback.value = if (selectedAnswer.isEmpty()) {
                "Waktu Habis! Jawaban: ${q.correctAnswer}"
            } else {
                "Salah! Jawaban: ${q.correctAnswer}"
            }
        }

        viewModelScope.launch {
            delay(1500)
            advanceSoloQuestion()
        }
    }

    private fun advanceSoloQuestion() {
        isSoloAnswered.value = false
        soloFeedback.value = null

        if (currentSoloMode.value == SoloModeType.SURVIVAL && soloLives.value <= 0) {
            finishSoloGame()
            return
        }
        if (currentSoloMode.value == SoloModeType.BOSS_BATTLE && soloBossHp.value <= 0) {
            finishSoloGame()
            return
        }

        val next = soloQuestionIndex.value + 1
        if (next < soloQuestions.value.size) {
            soloQuestionIndex.value = next
            startSoloTimer()
        } else {
            finishSoloGame()
        }
    }

    private fun finishSoloGame() {
        soundManager.playVictory()
        viewModelScope.launch {
            repository.recordMatch(
                mode = currentSoloMode.value.name,
                playerCount = 1,
                winnerName = profile.value?.name ?: "Pemain",
                rankingsJson = "[{\"score\":${soloScore.value}}]",
                questionCount = soloQuestionIndex.value + 1,
                xpGained = 100 + (soloScore.value / 10),
                coinsGained = 50 + (soloScore.value / 20)
            )
        }
        _currentScreen.value = AppScreen.SOLO_RESULT
    }

    fun updateAudioSettings(
        sfxEnabled: Boolean,
        bgmEnabled: Boolean,
        ttsEnabled: Boolean,
        ttsAutoRead: Boolean,
        sfxVolume: Float,
        bgmVolume: Float
    ) {
        viewModelScope.launch {
            repository.saveAudioSettings(
                AudioSettingsEntity(
                    id = 1,
                    sfxEnabled = sfxEnabled,
                    bgmEnabled = bgmEnabled,
                    ttsEnabled = ttsEnabled,
                    ttsAutoRead = ttsAutoRead,
                    sfxVolume = sfxVolume,
                    bgmVolume = bgmVolume
                )
            )
        }
    }

    fun selectAvatar(avatarIndex: Int) {
        soundManager.playClick()
        viewModelScope.launch {
            repository.updateAvatar(avatarIndex)
        }
    }

    fun unlockAvatar(avatarIndex: Int, cost: Int) {
        viewModelScope.launch {
            val success = repository.unlockAvatar(avatarIndex, cost)
            if (success) {
                soundManager.playVictory()
            } else {
                soundManager.playWrong()
            }
        }
    }

    fun updateProfileName(name: String) {
        viewModelScope.launch {
            repository.updatePlayerName(name)
        }
    }

    fun claimQuest(quest: DailyQuestEntity) {
        viewModelScope.launch {
            repository.claimQuest(quest)
            soundManager.playVictory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        turnTimerJob?.cancel()
        wifiTimerJob?.cancel()
        soloTimerJob?.cancel()
        soundManager.release()
        speechHelper.release()
        wifiServer?.stop()
        wifiClient?.disconnect()
        hostMatchManager?.cleanup()
    }
}
