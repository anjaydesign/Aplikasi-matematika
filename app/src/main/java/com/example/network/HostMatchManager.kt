package com.example.network

import com.example.data.generator.QuestionGenerator
import com.example.data.model.AnswerResultPayload
import com.example.data.model.AnswerSubmissionPayload
import com.example.data.model.ConnectedPlayer
import com.example.data.model.MatchSettings
import com.example.data.model.MathQuestion
import com.example.data.model.NetworkMessage
import com.example.data.model.NetworkMessageType
import com.example.data.model.PlayerScore
import com.example.data.model.PlayerScoreInfo
import com.example.data.model.QuestionClientPayload
import com.example.data.model.ScoreUpdatePayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class HostMatchManager(
    private val hostId: String,
    private val hostName: String,
    private val hostAvatarIndex: Int,
    private val server: LocalMultiplayerServer,
    private val onMatchStateChanged: () -> Unit,
    private val onMatchFinished: (rankings: List<PlayerScore>) -> Unit
) {
    enum class State {
        LOBBY,
        ROUND_ACTIVE,
        ROUND_REVIEW,
        FINISHED
    }

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var timerJob: Job? = null

    var currentState: State = State.LOBBY
        private set

    val matchId: String = "match_${UUID.randomUUID().toString().take(8)}"
    val players = mutableListOf<ConnectedPlayer>()
    val scores = mutableMapOf<String, PlayerScore>()

    private var questions = listOf<MathQuestion>()
    private var currentQuestionIndex = 0
    private var roundStartTimeMs = 0L
    private val answersThisRound = mutableMapOf<String, AnswerSubmissionPayload>()
    var currentSettings = MatchSettings()

    init {
        // Add host as player 1
        val hostPlayer = ConnectedPlayer(
            id = hostId,
            name = hostName,
            isHost = true,
            isReady = true,
            avatarIndex = hostAvatarIndex
        )
        players.add(hostPlayer)
        scores[hostId] = PlayerScore(
            playerId = hostId,
            playerName = hostName,
            avatarIndex = hostAvatarIndex
        )
    }

    val currentQuestion: MathQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    val currentRoundNumber: Int
        get() = currentQuestionIndex + 1

    val totalRounds: Int
        get() = questions.size

    fun handleIncomingMessage(message: NetworkMessage, socketId: String) {
        when (message.type) {
            NetworkMessageType.JOIN_ROOM -> handlePlayerJoin(message, socketId)
            NetworkMessageType.PLAYER_READY -> handlePlayerReady(message)
            NetworkMessageType.SUBMIT_ANSWER -> handleAnswerSubmission(message)
            NetworkMessageType.LEAVE_ROOM -> handlePlayerLeave(message.senderId, socketId)
            else -> {}
        }
    }

    private fun handlePlayerJoin(message: NetworkMessage, socketId: String) {
        if (currentState != State.LOBBY) {
            val err = NetworkMessage(
                type = NetworkMessageType.ERROR,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = "{\"error\":\"Pertandingan sudah dimulai!\"}"
            )
            server.sendToClient(socketId, err)
            return
        }

        if (players.size >= 4) {
            val err = NetworkMessage(
                type = NetworkMessageType.ERROR,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = "{\"error\":\"Ruangan penuh (Maksimal 4 pemain)\"}"
            )
            server.sendToClient(socketId, err)
            return
        }

        val existing = players.firstOrNull { it.id == message.senderId }
        if (existing == null) {
            val avatarIdx = try {
                JSONObject(message.payloadJson).optInt("avatarIndex", players.size)
            } catch (e: Exception) {
                players.size
            }
            val newPlayer = ConnectedPlayer(
                id = message.senderId,
                name = message.senderName.take(15),
                isHost = false,
                isReady = false,
                avatarIndex = avatarIdx
            )
            players.add(newPlayer)
            server.associatePlayerId(socketId, newPlayer.id)
            scores[newPlayer.id] = PlayerScore(
                playerId = newPlayer.id,
                playerName = newPlayer.name,
                avatarIndex = avatarIdx
            )
        }
        broadcastRoomState()
        onMatchStateChanged()
    }

    private fun handlePlayerReady(message: NetworkMessage) {
        if (currentState != State.LOBBY) return
        val player = players.firstOrNull { it.id == message.senderId } ?: return
        val isReady = try {
            JSONObject(message.payloadJson).optBoolean("isReady", true)
        } catch (e: Exception) {
            true
        }
        val idx = players.indexOf(player)
        if (idx >= 0) {
            players[idx] = player.copy(isReady = isReady)
        }
        broadcastRoomState()
        onMatchStateChanged()
    }

    fun setHostReady(isReady: Boolean) {
        val host = players.firstOrNull { it.isHost } ?: return
        val idx = players.indexOf(host)
        if (idx >= 0) {
            players[idx] = host.copy(isReady = isReady)
        }
        broadcastRoomState()
        onMatchStateChanged()
    }

    fun broadcastRoomState() {
        val arr = JSONArray()
        players.forEach { p ->
            arr.put(p.toJson())
        }
        val payload = JSONObject().apply {
            put("players", arr)
            put("matchId", matchId)
            put("questionCount", currentSettings.questionCount)
            put("timePerQuestion", currentSettings.timePerQuestionSeconds)
            put("category", currentSettings.category.name)
            put("grade", currentSettings.grade.name)
            put("difficulty", currentSettings.difficulty.name)
        }.toString()

        server.broadcast(
            NetworkMessage(
                type = NetworkMessageType.ROOM_STATE,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = payload
            )
        )
    }

    fun canStartMatch(): Boolean {
        if (currentState != State.LOBBY) return false
        if (players.size < 2) return false
        return players.all { it.isReady }
    }

    fun startMatch(settings: MatchSettings) {
        if (!canStartMatch()) return
        currentSettings = settings
        questions = QuestionGenerator.generateQuestions(
            count = settings.questionCount,
            grade = settings.grade,
            category = settings.category,
            difficulty = settings.difficulty
        )
        currentQuestionIndex = 0

        // Reset scores
        players.forEach { p ->
            scores[p.id] = PlayerScore(
                playerId = p.id,
                playerName = p.name,
                avatarIndex = p.avatarIndex
            )
        }

        server.broadcast(
            NetworkMessage(
                type = NetworkMessageType.START_MATCH,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = JSONObject().apply {
                    put("totalRounds", questions.size)
                    put("durationSeconds", currentSettings.timePerQuestionSeconds)
                }.toString()
            )
        )

        currentState = State.ROUND_ACTIVE
        startRound(0)
    }

    private fun startRound(questionIndex: Int) {
        if (questionIndex >= questions.size) {
            finishMatch()
            return
        }

        currentQuestionIndex = questionIndex
        val q = questions[questionIndex]
        answersThisRound.clear()
        roundStartTimeMs = System.currentTimeMillis()
        currentState = State.ROUND_ACTIVE

        // CRITICAL: Send Question to all players WITHOUT leaking correctAnswer!
        val clientPayload = QuestionClientPayload(
            questionId = q.id,
            roundIndex = questionIndex + 1,
            totalRounds = questions.size,
            prompt = q.prompt,
            spokenPrompt = q.spokenPrompt,
            options = q.options,
            categoryName = q.category.displayName,
            durationSeconds = currentSettings.timePerQuestionSeconds,
            serverStartTimeMs = roundStartTimeMs
        )

        server.broadcast(
            NetworkMessage(
                type = NetworkMessageType.QUESTION,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = clientPayload.toJson().toString()
            )
        )
        onMatchStateChanged()

        // Authoritative server timer
        timerJob?.cancel()
        timerJob = scope.launch {
            val totalWaitMs = (currentSettings.timePerQuestionSeconds * 1000L) + 1200L // Small buffer for network latency
            delay(totalWaitMs)
            if (isActive && currentState == State.ROUND_ACTIVE) {
                endRound()
            }
        }
    }

    private fun handleAnswerSubmission(message: NetworkMessage) {
        if (currentState != State.ROUND_ACTIVE) return
        val currentQ = currentQuestion ?: return

        val payload = AnswerSubmissionPayload.fromJson(JSONObject(message.payloadJson))
        if (payload.questionId != currentQ.id) return // Message from old round, ignore

        // Guarantee: only the FIRST answer per player is counted
        if (answersThisRound.containsKey(message.senderId)) return
        answersThisRound[message.senderId] = payload

        // Evaluate answer
        val isCorrect = payload.selectedAnswer.trim() == currentQ.correctAnswer.trim()
        val playerScore = scores[message.senderId] ?: return

        var earnedPoints = 0
        var speedBonus = 0
        val maxDurationMs = currentSettings.timePerQuestionSeconds * 1000L
        val timeSpent = payload.responseTimeMs.coerceIn(0L, maxDurationMs)

        if (isCorrect) {
            val basePoints = 100
            speedBonus = if (currentSettings.speedBonusEnabled) {
                val ratio = (1.0f - (timeSpent.toFloat() / maxDurationMs.toFloat())).coerceIn(0f, 1f)
                (ratio * 50).toInt()
            } else 0
            earnedPoints = basePoints + speedBonus

            playerScore.score += earnedPoints
            playerScore.correctAnswersCount += 1
            playerScore.currentStreak += 1
            if (playerScore.currentStreak > playerScore.highestStreak) {
                playerScore.highestStreak = playerScore.currentStreak
            }
        } else {
            playerScore.currentStreak = 0
        }
        playerScore.totalTimeSpentMs += timeSpent

        // Send individual answer result to the answering player
        val answerResult = AnswerResultPayload(
            questionId = currentQ.id,
            roundIndex = currentQuestionIndex + 1,
            correctAnswer = currentQ.correctAnswer,
            isCorrect = isCorrect,
            earnedPoints = earnedPoints,
            speedBonus = speedBonus,
            explanation = currentQ.explanation
        )

        val socketId = server.getSocketIdForPlayer(message.senderId)
        if (socketId != null) {
            server.sendToClient(
                socketId,
                NetworkMessage(
                    type = NetworkMessageType.ANSWER_RESULT,
                    matchId = matchId,
                    senderId = hostId,
                    senderName = hostName,
                    payloadJson = answerResult.toJson().toString()
                )
            )
        }

        // Broadcast score update to everyone
        broadcastScoreUpdate()
        onMatchStateChanged()

        // If all connected players have answered, end round early
        if (answersThisRound.size >= players.size) {
            timerJob?.cancel()
            scope.launch {
                delay(800)
                endRound()
            }
        }
    }

    fun submitHostAnswer(selectedAnswer: String, responseTimeMs: Long) {
        val q = currentQuestion ?: return
        handleAnswerSubmission(
            NetworkMessage(
                type = NetworkMessageType.SUBMIT_ANSWER,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = AnswerSubmissionPayload(
                    questionId = q.id,
                    roundIndex = currentQuestionIndex + 1,
                    selectedAnswer = selectedAnswer,
                    responseTimeMs = responseTimeMs
                ).toJson().toString()
            )
        )
    }

    private fun endRound() {
        currentState = State.ROUND_REVIEW
        val currentQ = currentQuestion ?: return

        // Reveal answer and explanation to all players
        val revealResult = AnswerResultPayload(
            questionId = currentQ.id,
            roundIndex = currentQuestionIndex + 1,
            correctAnswer = currentQ.correctAnswer,
            isCorrect = false,
            earnedPoints = 0,
            speedBonus = 0,
            explanation = currentQ.explanation
        )

        server.broadcast(
            NetworkMessage(
                type = NetworkMessageType.ANSWER_RESULT,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = revealResult.toJson().toString()
            )
        )
        broadcastScoreUpdate()
        onMatchStateChanged()

        // Wait 3.5 seconds review then move to next round
        timerJob?.cancel()
        timerJob = scope.launch {
            delay(3500)
            if (isActive) {
                if (currentQuestionIndex + 1 < questions.size) {
                    server.broadcast(
                        NetworkMessage(
                            type = NetworkMessageType.NEXT_ROUND,
                            matchId = matchId,
                            senderId = hostId,
                            senderName = hostName,
                            payloadJson = "{\"nextRoundIndex\": ${currentQuestionIndex + 2}}"
                        )
                    )
                    startRound(currentQuestionIndex + 1)
                } else {
                    finishMatch()
                }
            }
        }
    }

    private fun broadcastScoreUpdate() {
        val scoreList = players.map { p ->
            val s = scores[p.id] ?: PlayerScore(p.id, p.name)
            PlayerScoreInfo(
                playerId = p.id,
                playerName = p.name,
                score = s.score,
                correctCount = s.correctAnswersCount,
                avatarIndex = p.avatarIndex
            )
        }.sortedByDescending { it.score }

        val payload = ScoreUpdatePayload(
            roundIndex = currentQuestionIndex + 1,
            scores = scoreList
        )

        server.broadcast(
            NetworkMessage(
                type = NetworkMessageType.SCORE_UPDATE,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = payload.toJson().toString()
            )
        )
    }

    fun finishMatch() {
        currentState = State.FINISHED
        timerJob?.cancel()

        // Strict tiebreaker logic per Section 6:
        // 1. Highest score
        // 2. Most correct answers
        // 3. Lowest total response time
        val rankedList = scores.values.toList().sortedWith(
            compareByDescending<PlayerScore> { it.score }
                .thenByDescending { it.correctAnswersCount }
                .thenBy { it.totalTimeSpentMs }
        )

        val arr = JSONArray()
        rankedList.forEachIndexed { rank, ps ->
            val obj = JSONObject().apply {
                put("rank", rank + 1)
                put("playerId", ps.playerId)
                put("playerName", ps.playerName)
                put("score", ps.score)
                put("correctAnswersCount", ps.correctAnswersCount)
                put("totalTimeSpentMs", ps.totalTimeSpentMs)
                put("highestStreak", ps.highestStreak)
                put("avatarIndex", ps.avatarIndex)
            }
            arr.put(obj)
        }

        val payload = JSONObject().apply {
            put("matchId", matchId)
            put("winnerName", rankedList.firstOrNull()?.playerName ?: "")
            put("rankings", arr)
        }.toString()

        server.broadcast(
            NetworkMessage(
                type = NetworkMessageType.MATCH_FINISHED,
                matchId = matchId,
                senderId = hostId,
                senderName = hostName,
                payloadJson = payload
            )
        )

        onMatchFinished(rankedList)
        onMatchStateChanged()
    }

    fun handlePlayerLeave(playerId: String, socketId: String? = null) {
        val player = players.firstOrNull { it.id == playerId }
        if (player != null) {
            players.remove(player)
            scores.remove(playerId)
            answersThisRound.remove(playerId)
            broadcastRoomState()
            broadcastScoreUpdate()
            onMatchStateChanged()

            // If match is active and only 1 player remains, finish match safely
            if (currentState == State.ROUND_ACTIVE || currentState == State.ROUND_REVIEW) {
                if (players.size < 2) {
                    finishMatch()
                }
            }
        }
    }

    fun cleanup() {
        timerJob?.cancel()
        players.clear()
        scores.clear()
        answersThisRound.clear()
    }
}
