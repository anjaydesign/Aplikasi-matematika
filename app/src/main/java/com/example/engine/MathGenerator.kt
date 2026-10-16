package com.example.engine

import com.example.model.Difficulty
import com.example.model.EducationLevel
import com.example.model.MathQuestion
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

object MathGenerator {

    fun getTopicsForLevel(level: EducationLevel): List<String> {
        return when (level) {
            EducationLevel.SD -> listOf(
                "Semua Materi SD",
                "Penjumlahan & Pengurangan",
                "Perkalian & Pembagian",
                "Operasi Hitung Campuran",
                "Pecahan Sederhana",
                "Satuan Panjang, Berat & Waktu",
                "Keliling & Luas Bangun Datar",
                "Pola Bilangan",
                "Soal Cerita Sehari-hari"
            )
            EducationLevel.SMP -> listOf(
                "Semua Materi SMP",
                "Bilangan Bulat Negatif & Positif",
                "Pecahan, Rasio & Persentase",
                "Aljabar Linear",
                "Persamaan & Pertidaksamaan Linear",
                "Pangkat & Akar Kuadrat",
                "Teorema Pythagoras",
                "Bangun Datar & Bangun Ruang",
                "Statistika & Peluang",
                "Relasi, Fungsi & Koordinat"
            )
            EducationLevel.SMA -> listOf(
                "Semua Materi SMA",
                "Persamaan & Fungsi Kuadrat",
                "Eksponen & Logaritma",
                "Trigonometri Dasar",
                "Barisan & Deret Aritmatika & Geometri",
                "Matriks & Determinan",
                "Limit Fungsi Aljabar",
                "Turunan Dasar",
                "Integral Aljabar Dasar"
            )
            EducationLevel.UMUM -> listOf(
                "Semua Materi Umum",
                "Hitung Cepat Mental",
                "Diskon & Persentase Belanja",
                "Perbandingan Harga & Satuan",
                "Kalkulasi Uang & Kembalian",
                "Konversi Satuan Sehari-hari",
                "Logika Deret & Numerik",
                "Estimasi & Masalah Nyata"
            )
            EducationLevel.LANJUTAN -> listOf(
                "Semua Materi Lanjutan",
                "Vektor & Dot Product",
                "Determinan & Invers Matriks",
                "Kombinatorika (Permutasi & Kombinasi)",
                "Peluang Majemuk",
                "Kalkulus: Turunan Lanjutan",
                "Kalkulus: Integral Substitusi"
            )
        }
    }

    fun generateQuestion(
        level: EducationLevel,
        difficulty: Difficulty,
        specificTopic: String? = null
    ): MathQuestion {
        val topic = if (specificTopic.isNullOrBlank() || specificTopic.startsWith("Semua")) {
            val topics = getTopicsForLevel(level).filter { !it.startsWith("Semua") }
            topics.random()
        } else {
            specificTopic
        }

        return when (level) {
            EducationLevel.SD -> generateSDQuestion(topic, difficulty)
            EducationLevel.SMP -> generateSMPQuestion(topic, difficulty)
            EducationLevel.SMA -> generateSMAQuestion(topic, difficulty)
            EducationLevel.UMUM -> generateUmumQuestion(topic, difficulty)
            EducationLevel.LANJUTAN -> generateLanjutanQuestion(topic, difficulty)
        }
    }

    // =========================================================================
    // SD GENERATORS
    // =========================================================================

    private fun generateSDQuestion(topic: String, diff: Difficulty): MathQuestion {
        val rangeMax = when (diff) {
            Difficulty.PEMULA -> 20
            Difficulty.MUDAH -> 50
            Difficulty.NORMAL -> 100
            Difficulty.SULIT -> 500
            Difficulty.AHLI -> 1000
        }

        return when {
            topic.contains("Penjumlahan") -> {
                val a = Random.nextInt(5, rangeMax)
                val b = Random.nextInt(5, rangeMax)
                val isAdd = Random.nextBoolean() || diff == Difficulty.PEMULA
                if (isAdd) {
                    val ans = a + b
                    buildQuestion(
                        q = "Berapakah hasil dari $a + $b?",
                        ans = ans.toString(),
                        distractors = listOf((ans + 1).toString(), (ans - 1).toString(), (ans + 10).toString()),
                        exp = "Langkah: Jumlahkan kedua bilangan secara berurutan. $a + $b = $ans.",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.SD
                    )
                } else {
                    val maxVal = maxOf(a, b)
                    val minVal = minOf(a, b)
                    val ans = maxVal - minVal
                    buildQuestion(
                        q = "Berapakah hasil dari $maxVal - $minVal?",
                        ans = ans.toString(),
                        distractors = listOf((ans + 1).toString(), (ans - 1).toString(), (ans + 2).toString()),
                        exp = "Langkah: Kurangkan bilangan kedua dari bilangan pertama. $maxVal - $minVal = $ans.",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.SD
                    )
                }
            }
            topic.contains("Perkalian") -> {
                val multLimit = when (diff) {
                    Difficulty.PEMULA -> 10
                    Difficulty.MUDAH -> 12
                    Difficulty.NORMAL -> 15
                    Difficulty.SULIT -> 25
                    Difficulty.AHLI -> 50
                }
                val isMult = Random.nextBoolean()
                if (isMult) {
                    val a = Random.nextInt(2, multLimit)
                    val b = Random.nextInt(2, multLimit)
                    val ans = a * b
                    buildQuestion(
                        q = "Berapakah hasil dari $a × $b?",
                        ans = ans.toString(),
                        distractors = listOf((ans + a).toString(), (ans - b).toString(), (ans + 2).toString()),
                        exp = "Langkah perkalian: $a dikalikan $b menghasilkan $ans.",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.SD
                    )
                } else {
                    val divisor = Random.nextInt(2, multLimit)
                    val quotient = Random.nextInt(2, multLimit)
                    val dividend = divisor * quotient
                    buildQuestion(
                        q = "Berapakah hasil dari $dividend ÷ $divisor?",
                        ans = quotient.toString(),
                        distractors = listOf((quotient + 1).toString(), (quotient - 1).toString(), (quotient + 2).toString()),
                        exp = "Langkah pembagian: Cari angka yang jika dikalikan $divisor bernilai $dividend. Yaitu $quotient.",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.SD
                    )
                }
            }
            topic.contains("Campuran") -> {
                val a = Random.nextInt(5, 20)
                val b = Random.nextInt(2, 8)
                val c = Random.nextInt(2, 6)
                // a + b * c
                val ans = a + (b * c)
                buildQuestion(
                    q = "Hitunglah nilai dari: $a + $b × $c = ...",
                    ans = ans.toString(),
                    distractors = listOf(((a + b) * c).toString(), (ans - b).toString(), (ans + c).toString()),
                    exp = "Aturan operasi: Perkalian dihitung lebih dahulu daripada penjumlahan. ($b × $c) = ${b * c}. Lalu $a + ${b * c} = $ans.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SD
                )
            }
            topic.contains("Pecahan") -> {
                val denom = listOf(2, 4, 5, 8, 10).random()
                val n1 = Random.nextInt(1, denom)
                val n2 = Random.nextInt(1, denom)
                val sumNum = n1 + n2
                val qText = "$n1/$denom + $n2/$denom = ..."
                val ans = "$sumNum/$denom"
                val dist1 = "${sumNum + 1}/$denom"
                val dist2 = "${sumNum - 1}/$denom"
                val dist3 = "$sumNum/${denom * 2}"
                buildQuestion(
                    q = "Hitung penjumlahan pecahan: $qText",
                    ans = ans,
                    distractors = listOf(dist1, dist2, dist3),
                    exp = "Karena penyebutnya sudah sama ($denom), cukup jumlahkan pembilangnya: $n1 + $n2 = $sumNum. Jadi hasilnya $ans.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SD
                )
            }
            topic.contains("Bangun Datar") -> {
                val isPerimeter = Random.nextBoolean()
                val panjang = Random.nextInt(4, 15)
                val lebar = Random.nextInt(2, panjang)
                if (isPerimeter) {
                    val k = 2 * (panjang + lebar)
                    buildQuestion(
                        q = "Sebuah persegi panjang memiliki panjang $panjang cm dan lebar $lebar cm. Kelilingnya adalah...",
                        ans = "$k cm",
                        distractors = listOf("${panjang * lebar} cm", "${k - 2} cm", "${k + 4} cm"),
                        exp = "Rumus Keliling Persegi Panjang: K = 2 × (p + l) = 2 × ($panjang + $lebar) = 2 × ${panjang + lebar} = $k cm.",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.SD
                    )
                } else {
                    val luas = panjang * lebar
                    buildQuestion(
                        q = "Berapakah luas persegi panjang dengan panjang $panjang cm dan lebar $lebar cm?",
                        ans = "$luas cm²",
                        distractors = listOf("${2 * (panjang + lebar)} cm²", "${luas + 5} cm²", "${luas - 3} cm²"),
                        exp = "Rumus Luas Persegi Panjang: L = panjang × lebar = $panjang × $lebar = $luas cm².",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.SD
                    )
                }
            }
            topic.contains("Pola Bilangan") -> {
                val step = Random.nextInt(2, 6)
                val start = Random.nextInt(1, 10)
                val n1 = start
                val n2 = n1 + step
                val n3 = n2 + step
                val n4 = n3 + step
                val ans = n4 + step
                buildQuestion(
                    q = "Lanjutkan pola bilangan berikut: $n1, $n2, $n3, $n4, ...",
                    ans = ans.toString(),
                    distractors = listOf((ans + 1).toString(), (ans + step).toString(), (ans - 1).toString()),
                    exp = "Pola ini memiliki beda tetap (selisih +$step pada setiap suku berturutan). Suku berikutnya = $n4 + $step = $ans.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SD
                )
            }
            topic.contains("Satuan") -> {
                val km = Random.nextInt(2, 9)
                val ansM = km * 1000
                buildQuestion(
                    q = "$km kilometer (km) sama dengan berapa meter (m)?",
                    ans = "$ansM m",
                    distractors = listOf("${km * 100} m", "${km * 10} m", "${ansM + 500} m"),
                    exp = "Konversi satuan panjang: 1 km = 1.000 m. Jadi $km km = $km × 1.000 = $ansM m.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SD
                )
            }
            else -> { // Soal Cerita
                val jmlApel = Random.nextInt(6, 18)
                val dibagikan = listOf(2, 3, 4).random()
                val ans = jmlApel * dibagikan
                buildQuestion(
                    q = "Budi memiliki $dibagikan kantong buah. Setiap kantong berisi $jmlApel buah apel. Berapa jumlah total seluruh apel Budi?",
                    ans = "$ans buah",
                    distractors = listOf("${ans - dibagikan} buah", "${ans + jmlApel} buah", "${jmlApel + dibagikan} buah"),
                    exp = "Gunakan perkalian: Total apel = jumlah kantong × isi per kantong = $dibagikan × $jmlApel = $ans buah.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SD
                )
            }
        }
    }

    // =========================================================================
    // SMP GENERATORS
    // =========================================================================

    private fun generateSMPQuestion(topic: String, diff: Difficulty): MathQuestion {
        return when {
            topic.contains("Bilangan Bulat") -> {
                val a = Random.nextInt(5, 25)
                val b = Random.nextInt(6, 30)
                val ans = a - b
                buildQuestion(
                    q = "Hitunglah nilai dari: $a - $b = ...",
                    ans = ans.toString(),
                    distractors = listOf((-ans).toString(), (ans - 1).toString(), (ans + 2).toString()),
                    exp = "Karena bilangan pengurang ($b) lebih besar daripada $a, hasilnya bernilai negatif: $a - $b = $ans.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMP
                )
            }
            topic.contains("Aljabar") || topic.contains("Persamaan") -> {
                // ax + b = c  => ax = c - b => x = (c - b) / a
                val a = Random.nextInt(2, 6)
                val x = Random.nextInt(2, 9)
                val b = Random.nextInt(1, 15)
                val c = a * x + b
                buildQuestion(
                    q = "Tentukan nilai x dari persamaan: ${a}x + $b = $c",
                    ans = "x = $x",
                    distractors = listOf("x = ${x + 1}", "x = ${x - 1}", "x = ${x + 2}"),
                    exp = "Langkah penyelesaian aljabar:\n1) ${a}x = $c - $b\n2) ${a}x = ${c - b}\n3) x = ${c - b} / $a = $x.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMP
                )
            }
            topic.contains("Pangkat") || topic.contains("Akar") -> {
                val base = Random.nextInt(3, 13)
                val kuadrat = base * base
                buildQuestion(
                    q = "Berapakah nilai dari √$kuadrat?",
                    ans = base.toString(),
                    distractors = listOf((base - 1).toString(), (base + 1).toString(), (base + 2).toString()),
                    exp = "Akar kuadrat dari bilangan kuadrat sempurna: Karena $base² = $kuadrat, maka √$kuadrat = $base.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMP
                )
            }
            topic.contains("Pythagoras") -> {
                val triples = listOf(
                    Triple(3, 4, 5),
                    Triple(5, 12, 13),
                    Triple(6, 8, 10),
                    Triple(7, 24, 25),
                    Triple(8, 15, 17),
                    Triple(9, 12, 15)
                )
                val (alas, tinggi, miring) = triples.random()
                buildQuestion(
                    q = "Pada segitiga siku-siku dengan sisi siku-siku $alas cm dan $tinggi cm, panjang sisi miring (hipotenusa) adalah...",
                    ans = "$miring cm",
                    distractors = listOf("${miring - 1} cm", "${miring + 2} cm", "${alas + tinggi} cm"),
                    exp = "Gunakan Teorema Pythagoras: c² = a² + b² = $alas² + $tinggi² = ${alas * alas} + ${tinggi * tinggi} = ${miring * miring}.\nSehingga c = √${miring * miring} = $miring cm.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMP
                )
            }
            topic.contains("Persentase") || topic.contains("Rasio") -> {
                val hargaAwal = Random.nextInt(2, 10) * 10000
                val diskonPersen = listOf(10, 20, 25, 50).random()
                val potongan = (hargaAwal * diskonPersen) / 100
                val hargaAkhir = hargaAwal - potongan
                buildQuestion(
                    q = "Suatu barang seharga Rp ${formatRupiah(hargaAwal)} mendapat diskon $diskonPersen%. Berapa harga yang harus dibayar?",
                    ans = "Rp ${formatRupiah(hargaAkhir)}",
                    distractors = listOf(
                        "Rp ${formatRupiah(hargaAkhir + 5000)}",
                        "Rp ${formatRupiah(potongan)}",
                        "Rp ${formatRupiah(hargaAkhir - 5000)}"
                    ),
                    exp = "Besar diskon = $diskonPersen% × Rp ${formatRupiah(hargaAwal)} = Rp ${formatRupiah(potongan)}.\nHarga akhir = Rp ${formatRupiah(hargaAwal)} - Rp ${formatRupiah(potongan)} = Rp ${formatRupiah(hargaAkhir)}.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMP
                )
            }
            topic.contains("Statistika") || topic.contains("Peluang") -> {
                val data = List(5) { Random.nextInt(4, 10) }
                val sum = data.sum()
                val mean = sum.toDouble() / 5.0
                val roundedMean = (mean * 10).roundToInt() / 10.0
                buildQuestion(
                    q = "Tentukan rata-rata (mean) dari data nilai berikut: ${data.joinToString(", ")}",
                    ans = "$roundedMean",
                    distractors = listOf("${roundedMean + 0.5}", "${roundedMean - 0.5}", "${roundedMean + 1.0}"),
                    exp = "Rumus Mean: Rata-rata = Jumlah semua nilai / Banyak data.\nJumlah = ${data.joinToString(" + ")} = $sum.\nRata-rata = $sum / 5 = $roundedMean.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMP
                )
            }
            else -> { // Bangun Ruang
                val s = Random.nextInt(3, 9)
                val vol = s * s * s
                buildQuestion(
                    q = "Berapakah volume sebuah kubus yang memiliki rusuk $s cm?",
                    ans = "$vol cm³",
                    distractors = listOf("${s * s * 6} cm³", "${vol + 10} cm³", "${vol - 5} cm³"),
                    exp = "Rumus Volume Kubus: V = s³ = $s × $s × $s = $vol cm³.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMP
                )
            }
        }
    }

    // =========================================================================
    // SMA GENERATORS
    // =========================================================================

    private fun generateSMAQuestion(topic: String, diff: Difficulty): MathQuestion {
        return when {
            topic.contains("Kuadrat") -> {
                // (x - x1)(x - x2) = 0 => x^2 - (x1+x2)x + x1*x2 = 0
                val x1 = Random.nextInt(1, 6)
                val x2 = Random.nextInt(x1 + 1, 8)
                val b = -(x1 + x2)
                val c = x1 * x2
                val signB = if (b >= 0) "+ $b" else "- ${abs(b)}"
                buildQuestion(
                    q = "Akar-akar dari persamaan kuadrat x² $signB x + $c = 0 adalah...",
                    ans = "x = $x1 atau x = $x2",
                    distractors = listOf(
                        "x = ${-x1} atau x = ${-x2}",
                        "x = $x1 atau x = ${-x2}",
                        "x = ${x1 + 1} atau x = ${x2 - 1}"
                    ),
                    exp = "Faktorkan persamaan: (x - $x1)(x - $x2) = 0.\nMaka x - $x1 = 0 => x = $x1, atau x - $x2 = 0 => x = $x2.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMA
                )
            }
            topic.contains("Trigonometri") -> {
                val angles = listOf(
                    Triple("sin(30°)", "1/2", listOf("√3/2", "0", "1")),
                    Triple("cos(60°)", "1/2", listOf("√3/2", "√2/2", "1")),
                    Triple("tan(45°)", "1", listOf("0", "√3", "1/2")),
                    Triple("sin(90°)", "1", listOf("0", "1/2", "√2")),
                    Triple("cos(0°)", "1", listOf("0", "1/2", "-1")),
                    Triple("sin(45°)", "1/2 √2", listOf("1/2 √3", "1", "1/2")),
                    Triple("cos(30°)", "1/2 √3", listOf("1/2", "1", "0"))
                )
                val item = angles.random()
                buildQuestion(
                    q = "Berapakah nilai perbandingan trigonometri dari ${item.first}?",
                    ans = item.second,
                    distractors = item.third,
                    exp = "Nilai sudut istimewa trigonometri pada kuadran I: ${item.first} bernilai ${item.second}.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMA
                )
            }
            topic.contains("Eksponen") || topic.contains("Logaritma") -> {
                val base = listOf(2, 3, 5).random()
                val power = Random.nextInt(2, 5)
                val value = Math.pow(base.toDouble(), power.toDouble()).toInt()
                buildQuestion(
                    q = "Nilai dari ^${base}log($value) adalah...",
                    ans = power.toString(),
                    distractors = listOf((power - 1).toString(), (power + 1).toString(), (power * 2).toString()),
                    exp = "Definisi logaritma: ^a log(b) = c jika dan hanya jika a^c = b. Karena $base^$power = $value, maka ^${base}log($value) = $power.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMA
                )
            }
            topic.contains("Matriks") -> {
                // 2x2 matrix determinant: | a b | / | c d | = ad - bc
                val a = Random.nextInt(1, 6)
                val b = Random.nextInt(1, 6)
                val c = Random.nextInt(1, 5)
                val d = Random.nextInt(1, 6)
                val det = (a * d) - (b * c)
                buildQuestion(
                    q = "Diketahui matriks A = [[$a, $b], [$c, $d]]. Tentukan nilai determinan det(A)!",
                    ans = det.toString(),
                    distractors = listOf((det + 2).toString(), (det - 3).toString(), (a * d + b * c).toString()),
                    exp = "Rumus determinan matriks 2x2: det(A) = ad - bc = ($a × $d) - ($b × $c) = ${a * d} - ${b * c} = $det.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMA
                )
            }
            topic.contains("Turunan") -> {
                // d/dx (a * x^n) = a*n * x^(n-1)
                val a = Random.nextInt(2, 7)
                val n = Random.nextInt(2, 5)
                val coef = a * n
                val powerAns = n - 1
                val ansStr = if (powerAns == 1) "${coef}x" else "${coef}x^$powerAns"
                buildQuestion(
                    q = "Tentukan turunan pertama f'(x) dari fungsi f(x) = ${a}x^$n",
                    ans = ansStr,
                    distractors = listOf(
                        "${a}x^$powerAns",
                        "${coef}x^$n",
                        "${coef + 1}x^$powerAns"
                    ),
                    exp = "Gunakan aturan turunan pangkat: Jika f(x) = ax^n, maka f'(x) = a·n·x^(n-1) = ($a × $n) x^($n - 1) = $ansStr.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMA
                )
            }
            topic.contains("Integral") -> {
                // int (a * x) dx = (a/2) x^2 + C
                val a = listOf(2, 4, 6, 8).random()
                val half = a / 2
                val ansStr = "${half}x² + C"
                buildQuestion(
                    q = "Hasil dari ∫ (${a}x) dx adalah...",
                    ans = ansStr,
                    distractors = listOf("${a}x² + C", "${half}x + C", "${a}x + C"),
                    exp = "Gunakan rumus dasar integral tak tentu: ∫ ax^n dx = (a / (n+1)) x^(n+1) + C. Untuk n = 1: ($a / 2) x² + C = $ansStr.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMA
                )
            }
            else -> { // Barisan Aritmatika
                val a = Random.nextInt(2, 10)
                val b = Random.nextInt(2, 6)
                val n = Random.nextInt(6, 12)
                val un = a + (n - 1) * b
                buildQuestion(
                    q = "Suatu barisan aritmatika memiliki suku pertama a = $a dan beda b = $b. Tentukan suku ke-$n (U_$n)!",
                    ans = un.toString(),
                    distractors = listOf((un - b).toString(), (un + b).toString(), (un + 1).toString()),
                    exp = "Rumus suku ke-n barisan aritmatika: U_n = a + (n - 1)b.\nU_$n = $a + ($n - 1) × $b = $a + ${(n - 1) * b} = $un.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.SMA
                )
            }
        }
    }

    // =========================================================================
    // UMUM GENERATORS
    // =========================================================================

    private fun generateUmumQuestion(topic: String, diff: Difficulty): MathQuestion {
        return when {
            topic.contains("Diskon") -> {
                val hargaAsli = Random.nextInt(5, 20) * 10000
                val diskon = listOf(15, 20, 25, 30, 40).random()
                val hemat = (hargaAsli * diskon) / 100
                val bayar = hargaAsli - hemat
                buildQuestion(
                    q = "Sebuah kemeja berlabel harga Rp ${formatRupiah(hargaAsli)} didiskon sebesar $diskon%. Berapakah total harga yang harus dibayar kasir?",
                    ans = "Rp ${formatRupiah(bayar)}",
                    distractors = listOf(
                        "Rp ${formatRupiah(bayar + 10000)}",
                        "Rp ${formatRupiah(hemat)}",
                        "Rp ${formatRupiah(bayar - 5000)}"
                    ),
                    exp = "Potongan diskon = $diskon% × Rp ${formatRupiah(hargaAsli)} = Rp ${formatRupiah(hemat)}.\nHarga bayar = Rp ${formatRupiah(hargaAsli)} - Rp ${formatRupiah(hemat)} = Rp ${formatRupiah(bayar)}.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.UMUM
                )
            }
            topic.contains("Uang") || topic.contains("Kembalian") -> {
                val belanja = Random.nextInt(12, 45) * 1000
                val pecahanUang = if (belanja <= 50000) 50000 else 100000
                val kembalian = pecahanUang - belanja
                buildQuestion(
                    q = "Total belanjaan Anda di supermarket adalah Rp ${formatRupiah(belanja)}. Anda membayar dengan uang selembar Rp ${formatRupiah(pecahanUang)}. Berapa uang kembalian yang Anda terima?",
                    ans = "Rp ${formatRupiah(kembalian)}",
                    distractors = listOf(
                        "Rp ${formatRupiah(kembalian - 2000)}",
                        "Rp ${formatRupiah(kembalian + 5000)}",
                        "Rp ${formatRupiah(pecahanUang - kembalian)}"
                    ),
                    exp = "Kembalian = Uang yang dibayarkan - Total belanja\n= Rp ${formatRupiah(pecahanUang)} - Rp ${formatRupiah(belanja)} = Rp ${formatRupiah(kembalian)}.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.UMUM
                )
            }
            topic.contains("Perbandingan") || topic.contains("Harga") -> {
                val kg1 = 2
                val harga1 = 30000
                val kg2 = 5
                val hargaTotal = (harga1 / kg1) * kg2
                buildQuestion(
                    q = "Jika harga $kg1 kg beras adalah Rp ${formatRupiah(harga1)}, berapakah harga yang harus dibayar untuk $kg2 kg beras dengan jenis yang sama?",
                    ans = "Rp ${formatRupiah(hargaTotal)}",
                    distractors = listOf(
                        "Rp ${formatRupiah(hargaTotal + 5000)}",
                        "Rp ${formatRupiah(hargaTotal - 5000)}",
                        "Rp ${formatRupiah(harga1 * 2)}"
                    ),
                    exp = "Cari harga per kilogram: Rp ${formatRupiah(harga1)} / $kg1 = Rp ${formatRupiah(harga1 / kg1)}/kg.\nUntuk $kg2 kg = $kg2 × Rp ${formatRupiah(harga1 / kg1)} = Rp ${formatRupiah(hargaTotal)}.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.UMUM
                )
            }
            topic.contains("Hitung Cepat") -> {
                val a = Random.nextInt(11, 29)
                val ans = a * a
                buildQuestion(
                    q = "Hitung cepat kuadrat bilangan: $a × $a = ...",
                    ans = ans.toString(),
                    distractors = listOf((ans + 10).toString(), (ans - 10).toString(), (ans + 20).toString()),
                    exp = "Trik kuadrat cepat: $a² = ($a - ${a % 10}) × ($a + ${a % 10}) + (${a % 10})² = $ans.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.UMUM
                )
            }
            else -> { // Konversi satuan praktis
                val menit = Random.nextInt(2, 6) * 15
                val detik = menit * 60
                buildQuestion(
                    q = "Sebuah rekaman video berdurasi $menit menit. Berapa durasi video tersebut dalam satuan detik?",
                    ans = "$detik detik",
                    distractors = listOf("${detik - 60} detik", "${detik + 120} detik", "${menit * 100} detik"),
                    exp = "1 menit = 60 detik. Durasi = $menit × 60 = $detik detik.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.UMUM
                )
            }
        }
    }

    // =========================================================================
    // LANJUTAN GENERATORS
    // =========================================================================

    private fun generateLanjutanQuestion(topic: String, diff: Difficulty): MathQuestion {
        return when {
            topic.contains("Vektor") -> {
                // Dot product u = (u1, u2), v = (v1, v2) => u.v = u1*v1 + u2*v2
                val u1 = Random.nextInt(1, 6)
                val u2 = Random.nextInt(1, 6)
                val v1 = Random.nextInt(1, 6)
                val v2 = Random.nextInt(1, 6)
                val dot = (u1 * v1) + (u2 * v2)
                buildQuestion(
                    q = "Diketahui dua vektor u = ($u1, $u2) dan v = ($v1, $v2). Nilai perkalian titik (dot product) u · v adalah...",
                    ans = dot.toString(),
                    distractors = listOf((dot + 2).toString(), (dot - 3).toString(), (dot + 5).toString()),
                    exp = "Rumus dot product: u · v = (u1 × v1) + (u2 × v2) = ($u1 × $v1) + ($u2 × $v2) = ${u1 * v1} + ${u2 * v2} = $dot.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.LANJUTAN
                )
            }
            topic.contains("Kombinatorika") -> {
                val isPermutation = Random.nextBoolean()
                val n = Random.nextInt(4, 7)
                val r = 2
                if (isPermutation) {
                    // P(n, 2) = n * (n-1)
                    val ans = n * (n - 1)
                    buildQuestion(
                        q = "Berapakah nilai permutasi P($n, $r)?",
                        ans = ans.toString(),
                        distractors = listOf((ans / 2).toString(), (ans + 2).toString(), (ans - 2).toString()),
                        exp = "Rumus Permutasi: P(n, r) = n! / (n - r)!. Untuk P($n, 2) = $n × ${n - 1} = $ans.",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.LANJUTAN
                    )
                } else {
                    // C(n, 2) = n * (n-1) / 2
                    val ans = (n * (n - 1)) / 2
                    buildQuestion(
                        q = "Berapakah nilai kombinasi C($n, $r)?",
                        ans = ans.toString(),
                        distractors = listOf((ans * 2).toString(), (ans + 1).toString(), (ans - 1).toString()),
                        exp = "Rumus Kombinasi: C(n, r) = n! / (r! × (n - r)!). Untuk C($n, 2) = ($n × ${n - 1}) / 2 = $ans.",
                        topic = topic,
                        diff = diff,
                        lvl = EducationLevel.LANJUTAN
                    )
                }
            }
            topic.contains("Determinan") -> {
                // 3x3 diagonal or triangular matrix determinant
                val d1 = Random.nextInt(2, 5)
                val d2 = Random.nextInt(2, 4)
                val d3 = Random.nextInt(1, 4)
                val det = d1 * d2 * d3
                buildQuestion(
                    q = "Determinan dari matriks segitiga atas M = [[$d1, 3, 5], [0, $d2, 7], [0, 0, $d3]] adalah...",
                    ans = det.toString(),
                    distractors = listOf((det + 2).toString(), (d1 + d2 + d3).toString(), (det - 4).toString()),
                    exp = "Teorema Aljabar Linear: Determinan dari matriks segitiga sama dengan perkalian elemen-elemen pada diagonal utamanya: det(M) = $d1 × $d2 × $d3 = $det.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.LANJUTAN
                )
            }
            else -> { // Kalkulus aturan rantai
                val n = Random.nextInt(2, 4)
                // f(x) = (2x + 1)^n => f'(x) = 2*n (2x + 1)^(n-1)
                val coef = 2 * n
                val powerAns = n - 1
                val ansStr = if (powerAns == 1) "${coef}(2x + 1)" else "${coef}(2x + 1)^$powerAns"
                buildQuestion(
                    q = "Tentukan turunan f'(x) dari fungsi f(x) = (2x + 1)^$n menggunakan aturan rantai!",
                    ans = ansStr,
                    distractors = listOf(
                        "$n(2x + 1)^$powerAns",
                        "${coef}(2x + 1)^$n",
                        "${coef}x(2x + 1)^$powerAns"
                    ),
                    exp = "Aturan Rantai: f'(x) = n · u(x)^(n-1) · u'(x). Di sini u(x) = 2x + 1 sehingga u'(x) = 2. f'(x) = $n · (2x + 1)^$powerAns · 2 = $ansStr.",
                    topic = topic,
                    diff = diff,
                    lvl = EducationLevel.LANJUTAN
                )
            }
        }
    }

    // =========================================================================
    // HELPER UTILITIES
    // =========================================================================

    private fun buildQuestion(
        q: String,
        ans: String,
        distractors: List<String>,
        exp: String,
        topic: String,
        diff: Difficulty,
        lvl: EducationLevel
    ): MathQuestion {
        val uniqueOptions = mutableListOf(ans)
        for (d in distractors) {
            if (d != ans && !uniqueOptions.contains(d)) {
                uniqueOptions.add(d)
            }
        }
        // Ensure 4 unique options
        var counter = 1
        while (uniqueOptions.size < 4) {
            val fallback = try {
                val num = ans.filter { it.isDigit() || it == '-' }.toInt()
                (num + counter * 2).toString()
            } catch (e: Exception) {
                "$ans ($counter)"
            }
            if (!uniqueOptions.contains(fallback)) {
                uniqueOptions.add(fallback)
            }
            counter++
        }
        val shuffledOptions = uniqueOptions.take(4).shuffled()
        val basePoints = (100 * diff.multiplier).toInt()

        return MathQuestion(
            id = UUID.randomUUID().toString(),
            question = q,
            options = shuffledOptions,
            correctAnswer = ans,
            explanation = exp,
            category = topic,
            difficulty = diff,
            level = lvl,
            points = basePoints
        )
    }

    private fun formatRupiah(num: Int): String {
        return num.toString().reversed().chunked(3).joinToString(".").reversed()
    }
}
