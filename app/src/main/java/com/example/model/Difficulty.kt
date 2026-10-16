package com.example.model

enum class Difficulty(
    val code: String,
    val displayName: String,
    val multiplier: Float,
    val colorHex: Long,
    val timeLimitSec: Int
) {
    PEMULA(
        code = "PEMULA",
        displayName = "Pemula",
        multiplier = 1.0f,
        colorHex = 0xFF10B981, // Emerald Green
        timeLimitSec = 25
    ),
    MUDAH(
        code = "MUDAH",
        displayName = "Mudah",
        multiplier = 1.2f,
        colorHex = 0xFF06B6D4, // Cyan
        timeLimitSec = 20
    ),
    NORMAL(
        code = "NORMAL",
        displayName = "Normal",
        multiplier = 1.5f,
        colorHex = 0xFF3B82F6, // Blue
        timeLimitSec = 15
    ),
    SULIT(
        code = "SULIT",
        displayName = "Sulit",
        multiplier = 1.8f,
        colorHex = 0xFFF59E0B, // Amber
        timeLimitSec = 12
    ),
    AHLI(
        code = "AHLI",
        displayName = "Ahli",
        multiplier = 2.2f,
        colorHex = 0xFFEF4444, // Red
        timeLimitSec = 8
    );

    companion object {
        fun fromCode(code: String): Difficulty {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: NORMAL
        }
    }
}
