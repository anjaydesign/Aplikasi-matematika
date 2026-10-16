package com.example.model

enum class GameMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val iconEmoji: String,
    val badgeColorHex: Long
) {
    BATTLE_QUIZ(
        id = "BATTLE_QUIZ",
        title = "Battle Quiz",
        subtitle = "Tantangan 10 - 30 Soal",
        description = "Jawab soal beruntun, bangun kombo skor tinggi, dan raih kemenangan sempurna.",
        iconEmoji = "⚡",
        badgeColorHex = 0xFF00F0FF
    ),
    TIME_ATTACK(
        id = "TIME_ATTACK",
        title = "Time Attack",
        subtitle = "Balapan Lawan Waktu",
        description = "Pilih 30, 60, atau 120 detik. Jawab sebanyak mungkin soal sebelum waktu habis!",
        iconEmoji = "⏱️",
        badgeColorHex = 0xFFF59E0B
    ),
    SURVIVAL(
        id = "SURVIVAL",
        title = "Survival",
        subtitle = "3 Nyawa Tanpa Batas",
        description = "Bertahanlah selama mungkin! Satu jawaban salah mengurangi 1 nyawa. Berapa skor terjauhmumu?",
        iconEmoji = "❤️",
        badgeColorHex = 0xFFEF4444
    ),
    STREAK_CHALLENGE(
        id = "STREAK_CHALLENGE",
        title = "Streak Challenge",
        subtitle = "Uji Fokus & Kombo",
        description = "Capai rekor kombo 5, 10, 20, hingga 50 jawaban benar berturut-turut tanpa boleh salah.",
        iconEmoji = "🔥",
        badgeColorHex = 0xFFEC4899
    ),
    AI_DUEL(
        id = "AI_DUEL",
        title = "Duel AI Lokal",
        subtitle = "Lawan Komputer Offline",
        description = "Bertanding 1 vs 1 melawan bot komputer matematika dengan simulasi adil tanpa internet.",
        iconEmoji = "🤖",
        badgeColorHex = 0xFF8B5CF6
    ),
    BOSS_BATTLE(
        id = "BOSS_BATTLE",
        title = "Boss Battle",
        subtitle = "Kalahkan Bos Matematika",
        description = "Hadapi Bos berenergi tinggi! Setiap jawaban benar meluncurkan serangan mematikan ke Bos.",
        iconEmoji = "👑",
        badgeColorHex = 0xFFFFB800
    ),
    FREE_PRACTICE(
        id = "FREE_PRACTICE",
        title = "Latihan Bebas",
        subtitle = "Belajar Santai & Pembahasan",
        description = "Tanpa tekanan waktu. Pelajari langkah demi langkah dan rumus penyelesaian di setiap soal.",
        iconEmoji = "📖",
        badgeColorHex = 0xFF10B981
    )
}
