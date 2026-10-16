package com.example.model

enum class EducationLevel(
    val code: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val iconEmoji: String
) {
    SD(
        code = "SD",
        title = "Sekolah Dasar (SD)",
        subtitle = "Kelas 1 - 6",
        description = "Aritmetika dasar, perkalian, pembagian, pecahan sederhana, bangun datar, dan soal cerita kontekstual.",
        iconEmoji = "🎒"
    ),
    SMP(
        code = "SMP",
        title = "Sekolah Menengah Pertama (SMP)",
        subtitle = "Kelas 7 - 9",
        description = "Bilangan bulat, aljabar linear, Pythagoras, pangkat & akar, statistika dasar, dan bangun ruang.",
        iconEmoji = "📐"
    ),
    SMA(
        code = "SMA",
        title = "SMA / SMK",
        subtitle = "Kelas 10 - 12",
        description = "Persamaan kuadrat, eksponen, logaritma, trigonometri, barisan & deret, matriks, limit & turunan dasar.",
        iconEmoji = "🔬"
    ),
    UMUM(
        code = "UMUM",
        title = "Matematika Dasar & Umum",
        subtitle = "Praktis & Sehari-hari",
        description = "Hitung cepat, persentase & diskon, perbandingan harga, kalkulasi uang, estimasi, dan logika numerik.",
        iconEmoji = "💡"
    ),
    LANJUTAN(
        code = "LANJUTAN",
        title = "Matematika Lanjutan",
        subtitle = "Tingkat Mahasiswa / Kompetisi",
        description = "Vektor, matriks lanjutan, kombinatorika, probabilitas majemuk, turunan & integral kalkulus.",
        iconEmoji = "🌌"
    );

    companion object {
        fun fromCode(code: String): EducationLevel {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: SD
        }
    }
}
