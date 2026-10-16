package com.example.engine

import com.example.model.BossInfo

object BossRoster {

    val BOSSES = listOf(
        BossInfo(
            id = "boss_aritmetika",
            name = "Kaisar Bilangan",
            title = "Penguasa Aritmetika Kuno",
            category = "Operasi Hitung & Pola",
            maxHp = 10,
            currentHp = 10,
            emoji = "🧙‍♂️",
            quote = "Kamu takkan bisa menembus perisai angka dan perkalian saktiku!",
            colorHex = 0xFF00F0FF,
            rewardXp = 350,
            rewardCoins = 120
        ),
        BossInfo(
            id = "boss_aljabar",
            name = "Titan Aljabar",
            title = "Penjaga Persamaan Linier",
            category = "Aljabar & Variabel",
            maxHp = 12,
            currentHp = 12,
            emoji = "🤖",
            quote = "Variabel x dan y milikku tak berhingga! Bisakah kamu memecahkannya?",
            colorHex = 0xFF8B5CF6,
            rewardXp = 450,
            rewardCoins = 160
        ),
        BossInfo(
            id = "boss_geometri",
            name = "Giga Geometria",
            title = "Raja Dimensi & Pythagoras",
            category = "Bangun Datar & Ruang",
            maxHp = 15,
            currentHp = 15,
            emoji = "📐",
            quote = "Sudut-sudut tajamku akan memotong konsentrasi matematikamu!",
            colorHex = 0xFF10B981,
            rewardXp = 550,
            rewardCoins = 200
        ),
        BossInfo(
            id = "boss_trigonometri",
            name = "Mega Trigonon",
            title = "Dewa Gelombang Sinus",
            category = "Trigonometri & Sudut",
            maxHp = 16,
            currentHp = 16,
            emoji = "⚡",
            quote = "Sinus dan kosinusku berosilasi tanpa ampun! Taklukkan amplitudoku!",
            colorHex = 0xFFFFB800,
            rewardXp = 700,
            rewardCoins = 250
        ),
        BossInfo(
            id = "boss_kalkulus",
            name = "Apex Infinity",
            title = "Entitas Limit & Diferensial",
            category = "Matematika Lanjutan",
            maxHp = 20,
            currentHp = 20,
            emoji = "👑",
            quote = "Limit kekuatanmu mendekati nol saat berhadapan dengan integral abadi!",
            colorHex = 0xFFEF4444,
            rewardXp = 1000,
            rewardCoins = 400
        )
    )

    fun getBossById(id: String): BossInfo {
        return BOSSES.find { it.id == id } ?: BOSSES.first()
    }
}
