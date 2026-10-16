package com.example.engine

import com.example.model.EducationLevel

data class FormulaItem(
    val title: String,
    val category: String,
    val formulaText: String,
    val description: String,
    val example: String,
    val level: EducationLevel
)

object FormulaRepository {

    val FORMULAS = listOf(
        // SD
        FormulaItem(
            title = "Persegi Panjang",
            category = "Geometri SD",
            formulaText = "Luas = p × l\nKeliling = 2 × (p + l)",
            description = "p = panjang, l = lebar",
            example = "Jika p = 6 cm, l = 4 cm, maka Luas = 24 cm², Keliling = 20 cm.",
            level = EducationLevel.SD
        ),
        FormulaItem(
            title = "Persegi",
            category = "Geometri SD",
            formulaText = "Luas = s × s = s²\nKeliling = 4 × s",
            description = "s = panjang sisi",
            example = "Sisi = 5 cm => Luas = 25 cm², Keliling = 20 cm.",
            level = EducationLevel.SD
        ),
        FormulaItem(
            title = "Segitiga",
            category = "Geometri SD",
            formulaText = "Luas = 1/2 × a × t\nKeliling = s1 + s2 + s3",
            description = "a = alas, t = tinggi tegak lurus",
            example = "Alas = 8 cm, tinggi = 5 cm => Luas = 1/2 × 8 × 5 = 20 cm².",
            level = EducationLevel.SD
        ),
        FormulaItem(
            title = "Operasi Hitung Campuran",
            category = "Aritmetika SD",
            formulaText = "1. Tanda kurung () didahulukan\n2. Perkalian (×) dan Pembagian (÷)\n3. Penjumlahan (+) dan Pengurangan (-)",
            description = "Urutan hierarki pengerjaan matematika (Kabataku)",
            example = "5 + 3 × 4 = 5 + 12 = 17 (bukan 32).",
            level = EducationLevel.SD
        ),

        // SMP
        FormulaItem(
            title = "Teorema Pythagoras",
            category = "Aljabar & Geometri SMP",
            formulaText = "c² = a² + b²\nc = √(a² + b²)",
            description = "Berlaku khusus untuk segitiga siku-siku dengan c sebagai hipotenusa.",
            example = "a = 3, b = 4 => c = √(9 + 16) = √25 = 5.",
            level = EducationLevel.SMP
        ),
        FormulaItem(
            title = "Volume Kubus & Balok",
            category = "Bangun Ruang SMP",
            formulaText = "V_kubus = s³\nV_balok = p × l × t",
            description = "Volume ruang dalam satuan kubik.",
            example = "Balok p=4, l=3, t=2 => Volume = 4 × 3 × 2 = 24 cm³.",
            level = EducationLevel.SMP
        ),
        FormulaItem(
            title = "Rata-rata (Mean)",
            category = "Statistika SMP",
            formulaText = "x̄ = (Σx) / n",
            description = "Jumlah total seluruh nilai dibagi dengan banyaknya data.",
            example = "Nilai: 6, 7, 8, 9, 10 => Total = 40, n = 5 => Rata-rata = 8.",
            level = EducationLevel.SMP
        ),
        FormulaItem(
            title = "Persamaan Linear Satu Variabel",
            category = "Aljabar SMP",
            formulaText = "ax + b = c  =>  ax = c - b  =>  x = (c - b) / a",
            description = "Pindahkan konstanta ke ruas kanan lalu bagi koefisien variabel.",
            example = "2x + 4 = 10 => 2x = 6 => x = 3.",
            level = EducationLevel.SMP
        ),

        // SMA
        FormulaItem(
            title = "Persamaan Kuadrat (Rumus ABC)",
            category = "Aljabar SMA",
            formulaText = "x = (-b ± √(b² - 4ac)) / (2a)",
            description = "Mencari akar-akar persamaan kuadrat ax² + bx + c = 0.",
            example = "x² - 5x + 6 = 0 => D = 25 - 24 = 1 => x = (5 ± 1)/2 => x=3 atau x=2.",
            level = EducationLevel.SMA
        ),
        FormulaItem(
            title = "Trigonometri Sudut Istimewa",
            category = "Trigonometri SMA",
            formulaText = "sin(30°) = 1/2,  cos(60°) = 1/2\nsin(45°) = 1/2√2, cos(45°) = 1/2√2\nsin(60°) = 1/2√3, cos(30°) = 1/2√3\ntan(45°) = 1",
            description = "Tabel nilai perbandingan trigonometri sudut-sudut dasar.",
            example = "sin²(θ) + cos²(θ) = 1 untuk setiap sudut θ.",
            level = EducationLevel.SMA
        ),
        FormulaItem(
            title = "Turunan Fungsi Aljabar",
            category = "Kalkulus SMA",
            formulaText = "f(x) = a·x^n  =>  f'(x) = n·a·x^(n-1)",
            description = "Aturan pangkat diferensial turunan pertama.",
            example = "f(x) = 3x² => f'(x) = 2 · 3 · x^1 = 6x.",
            level = EducationLevel.SMA
        ),
        FormulaItem(
            title = "Determinan Matriks 2×2",
            category = "Matriks SMA",
            formulaText = "det([[a, b], [c, d]]) = a·d - b·c",
            description = "Perkalian diagonal utama dikurangi perkalian diagonal samping.",
            example = "A = [[2, 1], [4, 3]] => det(A) = (2×3) - (1×4) = 6 - 4 = 2.",
            level = EducationLevel.SMA
        ),

        // UMUM
        FormulaItem(
            title = "Perhitungan Diskon Belanja",
            category = "Keuangan Praktis",
            formulaText = "Potongan = Harga Asli × (Diskon% / 100)\nHarga Bayar = Harga Asli - Potongan",
            description = "Rumus cepat menghitung harga setelah potongan promo.",
            example = "Harga Rp 100.000 diskon 20% => Potongan = Rp 20.000, Bayar = Rp 80.000.",
            level = EducationLevel.UMUM
        ),
        FormulaItem(
            title = "Perbandingan Harga per Satuan",
            category = "Belanja Pintar",
            formulaText = "Harga per unit = Total Harga / Jumlah Unit",
            description = "Untuk menentukan produk mana yang paling ekonomis dan murah per kg/liter.",
            example = "1 liter = Rp 14.000 vs 5 liter = Rp 65.000 (Rp 13.000/liter - lebih hemat).",
            level = EducationLevel.UMUM
        ),

        // LANJUTAN
        FormulaItem(
            title = "Perkalian Titik Vektor (Dot Product)",
            category = "Vektor Lanjutan",
            formulaText = "u · v = u1·v1 + u2·v2 + u3·v3 = |u||v| cos(θ)",
            description = "Menghasilkan skalar dan menentukan ortogonalitas dua vektor.",
            example = "u = (2, 3), v = (4, 1) => u · v = 2(4) + 3(1) = 8 + 3 = 11.",
            level = EducationLevel.LANJUTAN
        ),
        FormulaItem(
            title = "Kombinasi & Permutasi",
            category = "Kombinatorika Lanjutan",
            formulaText = "P(n, r) = n! / (n - r)!\nC(n, r) = n! / (r! · (n - r)!)",
            description = "P untuk susunan memperhatikan urutan, C untuk pemilihan tanpa urutan.",
            example = "C(5, 2) = 5! / (2! · 3!) = (5 × 4) / 2 = 10 cara.",
            level = EducationLevel.LANJUTAN
        )
    )

    fun getFormulasByLevel(level: EducationLevel?): List<FormulaItem> {
        if (level == null) return FORMULAS
        return FORMULAS.filter { it.level == level }
    }
}
