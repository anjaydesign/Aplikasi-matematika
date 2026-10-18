package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

enum class NetworkMessageType {
    JOIN_ROOM,
    ROOM_STATE,
    PLAYER_READY,
    START_MATCH,
    QUESTION,
    SUBMIT_ANSWER,
    ANSWER_RESULT,
    SCORE_UPDATE,
    NEXT_ROUND,
    MATCH_FINISHED,
    PING,
    PONG,
    LEAVE_ROOM,
    ERROR
}

data class ConnectedPlayer(
    val id: String,
    val name: String,
    val isHost: Boolean = false,
    val isReady: Boolean = false,
    val avatarIndex: Int = 0
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("isHost", isHost)
        put("isReady", isReady)
        put("avatarIndex", avatarIndex)
    }

    companion object {
        fun fromJson(obj: JSONObject): ConnectedPlayer = ConnectedPlayer(
            id = obj.optString("id", ""),
            name = obj.optString("name", "Pemain"),
            isHost = obj.optBoolean("isHost", false),
            isReady = obj.optBoolean("isReady", false),
            avatarIndex = obj.optInt("avatarIndex", 0)
        )
    }
}

data class NetworkMessage(
    val type: NetworkMessageType,
    val matchId: String,
    val senderId: String,
    val senderName: String,
    val payloadJson: String = "{}",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toJsonString(): String {
        val obj = JSONObject()
        obj.put("type", type.name)
        obj.put("matchId", matchId)
        obj.put("senderId", senderId)
        obj.put("senderName", senderName)
        obj.put("payloadJson", payloadJson)
        obj.put("timestamp", timestamp)
        return obj.toString()
    }

    companion object {
        fun parse(jsonStr: String): NetworkMessage? {
            return try {
                val obj = JSONObject(jsonStr)
                NetworkMessage(
                    type = NetworkMessageType.valueOf(obj.getString("type")),
                    matchId = obj.optString("matchId", ""),
                    senderId = obj.optString("senderId", ""),
                    senderName = obj.optString("senderName", ""),
                    payloadJson = obj.optString("payloadJson", "{}"),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class QuestionClientPayload(
    val questionId: String,
    val roundIndex: Int,
    val totalRounds: Int,
    val prompt: String,
    val spokenPrompt: String,
    val options: List<String>,
    val categoryName: String,
    val durationSeconds: Int,
    val serverStartTimeMs: Long
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("questionId", questionId)
        put("roundIndex", roundIndex)
        put("totalRounds", totalRounds)
        put("prompt", prompt)
        put("spokenPrompt", spokenPrompt)
        put("options", JSONArray(options))
        put("categoryName", categoryName)
        put("durationSeconds", durationSeconds)
        put("serverStartTimeMs", serverStartTimeMs)
    }

    companion object {
        fun fromJson(obj: JSONObject): QuestionClientPayload {
            val optArr = obj.optJSONArray("options") ?: JSONArray()
            val list = mutableListOf<String>()
            for (i in 0 until optArr.length()) {
                list.add(optArr.getString(i))
            }
            return QuestionClientPayload(
                questionId = obj.optString("questionId"),
                roundIndex = obj.optInt("roundIndex", 1),
                totalRounds = obj.optInt("totalRounds", 10),
                prompt = obj.optString("prompt"),
                spokenPrompt = obj.optString("spokenPrompt"),
                options = list,
                categoryName = obj.optString("categoryName", "Matematika"),
                durationSeconds = obj.optInt("durationSeconds", 15),
                serverStartTimeMs = obj.optLong("serverStartTimeMs", System.currentTimeMillis())
            )
        }
    }
}

data class AnswerSubmissionPayload(
    val questionId: String,
    val roundIndex: Int,
    val selectedAnswer: String,
    val responseTimeMs: Long
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("questionId", questionId)
        put("roundIndex", roundIndex)
        put("selectedAnswer", selectedAnswer)
        put("responseTimeMs", responseTimeMs)
    }

    companion object {
        fun fromJson(obj: JSONObject): AnswerSubmissionPayload = AnswerSubmissionPayload(
            questionId = obj.optString("questionId"),
            roundIndex = obj.optInt("roundIndex", 1),
            selectedAnswer = obj.optString("selectedAnswer"),
            responseTimeMs = obj.optLong("responseTimeMs", 0L)
        )
    }
}

data class AnswerResultPayload(
    val questionId: String,
    val roundIndex: Int,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val earnedPoints: Int,
    val speedBonus: Int,
    val explanation: String
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("questionId", questionId)
        put("roundIndex", roundIndex)
        put("correctAnswer", correctAnswer)
        put("isCorrect", isCorrect)
        put("earnedPoints", earnedPoints)
        put("speedBonus", speedBonus)
        put("explanation", explanation)
    }

    companion object {
        fun fromJson(obj: JSONObject): AnswerResultPayload = AnswerResultPayload(
            questionId = obj.optString("questionId"),
            roundIndex = obj.optInt("roundIndex", 1),
            correctAnswer = obj.optString("correctAnswer"),
            isCorrect = obj.optBoolean("isCorrect"),
            earnedPoints = obj.optInt("earnedPoints"),
            speedBonus = obj.optInt("speedBonus"),
            explanation = obj.optString("explanation")
        )
    }
}

data class PlayerScoreInfo(
    val playerId: String,
    val playerName: String,
    val score: Int,
    val correctCount: Int,
    val avatarIndex: Int = 0
)

data class ScoreUpdatePayload(
    val roundIndex: Int,
    val scores: List<PlayerScoreInfo>
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("roundIndex", roundIndex)
        val arr = JSONArray()
        for (s in scores) {
            val o = JSONObject()
            o.put("playerId", s.playerId)
            o.put("playerName", s.playerName)
            o.put("score", s.score)
            o.put("correctCount", s.correctCount)
            o.put("avatarIndex", s.avatarIndex)
            arr.put(o)
        }
        put("scores", arr)
    }

    companion object {
        fun fromJson(obj: JSONObject): ScoreUpdatePayload {
            val arr = obj.optJSONArray("scores") ?: JSONArray()
            val list = mutableListOf<PlayerScoreInfo>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    PlayerScoreInfo(
                        playerId = o.getString("playerId"),
                        playerName = o.getString("playerName"),
                        score = o.getInt("score"),
                        correctCount = o.optInt("correctCount", 0),
                        avatarIndex = o.optInt("avatarIndex", 0)
                    )
                )
            }
            return ScoreUpdatePayload(
                roundIndex = obj.optInt("roundIndex", 1),
                scores = list
            )
        }
    }
}
