package com.example.data.generator

import com.example.data.model.Difficulty
import com.example.data.model.MathCategory
import com.example.data.model.MathGrade
import com.example.data.model.MathQuestion
import java.util.UUID
import kotlin.random.Random

object QuestionGenerator {

    fun generateQuestions(
        count: Int,
        grade: MathGrade,
        category: MathCategory,
        difficulty: Difficulty
    ): List<MathQuestion> {
        val list = mutableListOf<MathQuestion>()
        for (i in 0 until count) {
            val cat = if (category == MathCategory.CAMPURAN_ARITMATIKA) {
                listOf(
                    MathCategory.PENJUMLAHAN,
                    MathCategory.PENGURANGAN,
                    MathCategory.PERKALIAN,
                    MathCategory.PEMBAGIAN
                ).random()
            } else {
                category
            }

            val diff = if (difficulty == Difficulty.CAMPURAN) {
                listOf(Difficulty.MUDAH, Difficulty.SEDANG, Difficulty.SULIT).random()
            } else {
                difficulty
            }

            list.add(createSingleQuestion(grade, cat, diff))
        }
        return list
    }

    private fun createSingleQuestion(
        grade: MathGrade,
        category: MathCategory,
        difficulty: Difficulty
    ): MathQuestion {
        return when (category) {
            MathCategory.PENJUMLAHAN -> generateAddition(grade, difficulty)
            MathCategory.PENGURANGAN -> generateSubtraction(grade, difficulty)
            MathCategory.PERKALIAN -> generateMultiplication(grade, difficulty)
            MathCategory.PEMBAGIAN -> generateDivision(grade, difficulty)
            MathCategory.PECAHAN -> generateFractions(grade, difficulty)
            MathCategory.DESIMAL -> generateDecimals(grade, difficulty)
            MathCategory.PERSEN -> generatePercentages(grade, difficulty)
            MathCategory.ALJABAR -> generateAlgebra(grade, difficulty)
            MathCategory.GEOMETRI -> generateGeometry(grade, difficulty)
            MathCategory.CAMPURAN_ARITMATIKA -> generateAddition(grade, difficulty)
        }
    }

    private fun generateAddition(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val (min, max) = when (difficulty) {
            Difficulty.MUDAH -> if (grade == MathGrade.SD) 2 to 20 else 10 to 50
            Difficulty.SEDANG -> if (grade == MathGrade.SD) 15 to 99 else 25 to 150
            Difficulty.SULIT, Difficulty.CAMPURAN -> if (grade == MathGrade.SD) 50 to 300 else 100 to 999
        }
        val a = Random.nextInt(min, max + 1)
        val b = Random.nextInt(min, max + 1)
        val ans = a + b

        val prompt = "$a + $b = ?"
        val spoken = "$a ditambah $b sama dengan berapa?"
        val options = buildOptions(ans)
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            spokenPrompt = spoken,
            options = options,
            correctAnswer = ans.toString(),
            category = MathCategory.PENJUMLAHAN,
            difficulty = difficulty,
            explanation = "$a + $b = $ans"
        )
    }

    private fun generateSubtraction(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val (min, max) = when (difficulty) {
            Difficulty.MUDAH -> if (grade == MathGrade.SD) 5 to 25 else 15 to 60
            Difficulty.SEDANG -> if (grade == MathGrade.SD) 20 to 99 else 40 to 180
            Difficulty.SULIT, Difficulty.CAMPURAN -> if (grade == MathGrade.SD) 50 to 300 else 100 to 999
        }
        val n1 = Random.nextInt(min, max + 1)
        val n2 = Random.nextInt(min, max + 1)
        val a = maxOf(n1, n2)
        val b = minOf(n1, n2)
        val ans = a - b

        val prompt = "$a - $b = ?"
        val spoken = "$a dikurang $b sama dengan berapa?"
        val options = buildOptions(ans)
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            spokenPrompt = spoken,
            options = options,
            correctAnswer = ans.toString(),
            category = MathCategory.PENGURANGAN,
            difficulty = difficulty,
            explanation = "$a - $b = $ans"
        )
    }

    private fun generateMultiplication(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val (maxA, maxB) = when (difficulty) {
            Difficulty.MUDAH -> if (grade == MathGrade.SD) 9 to 9 else 12 to 10
            Difficulty.SEDANG -> if (grade == MathGrade.SD) 15 to 12 else 20 to 15
            Difficulty.SULIT, Difficulty.CAMPURAN -> if (grade == MathGrade.SD) 25 to 20 else 50 to 25
        }
        val a = Random.nextInt(2, maxA + 1)
        val b = Random.nextInt(2, maxB + 1)
        val ans = a * b

        val prompt = "$a × $b = ?"
        val spoken = "$a dikali $b sama dengan berapa?"
        val options = buildOptions(ans)
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            spokenPrompt = spoken,
            options = options,
            correctAnswer = ans.toString(),
            category = MathCategory.PERKALIAN,
            difficulty = difficulty,
            explanation = "$a × $b = $ans"
        )
    }

    private fun generateDivision(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val (maxDivisor, maxQuotient) = when (difficulty) {
            Difficulty.MUDAH -> if (grade == MathGrade.SD) 9 to 9 else 12 to 10
            Difficulty.SEDANG -> if (grade == MathGrade.SD) 12 to 15 else 15 to 20
            Difficulty.SULIT, Difficulty.CAMPURAN -> if (grade == MathGrade.SD) 20 to 20 else 25 to 30
        }
        val divisor = Random.nextInt(2, maxDivisor + 1)
        val quotient = Random.nextInt(2, maxQuotient + 1)
        val dividend = divisor * quotient

        val prompt = "$dividend ÷ $divisor = ?"
        val spoken = "$dividend dibagi $divisor sama dengan berapa?"
        val options = buildOptions(quotient)
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            spokenPrompt = spoken,
            options = options,
            correctAnswer = quotient.toString(),
            category = MathCategory.PEMBAGIAN,
            difficulty = difficulty,
            explanation = "$dividend ÷ $divisor = $quotient karena $divisor × $quotient = $dividend"
        )
    }

    private fun generateFractions(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val denoms = if (difficulty == Difficulty.MUDAH) listOf(2, 3, 4, 5) else listOf(4, 5, 6, 8, 10)
        val d = denoms.random()
        val num1 = Random.nextInt(1, d)
        val num2 = Random.nextInt(1, d)
        val isAdd = Random.nextBoolean()

        if (isAdd) {
            val sumNum = num1 + num2
            val prompt = "$num1/$d + $num2/$d = ?"
            val spoken = "$num1 per $d ditambah $num2 per $d sama dengan berapa?"
            val correct = "$sumNum/$d"
            val options = mutableSetOf(correct)
            while (options.size < 4) {
                val fakeNum = maxOf(1, sumNum + Random.nextInt(-3, 4))
                val fakeD = if (Random.nextBoolean()) d else d * 2
                options.add("$fakeNum/$fakeD")
            }
            return MathQuestion(
                id = UUID.randomUUID().toString(),
                prompt = prompt,
                spokenPrompt = spoken,
                options = options.shuffled(),
                correctAnswer = correct,
                category = MathCategory.PECAHAN,
                difficulty = difficulty,
                explanation = "Karena penyebutnya sama ($d), jumlahkan pembilang: $num1 + $num2 = $sumNum/$d"
            )
        } else {
            val big = maxOf(num1, num2)
            val small = minOf(num1, num2)
            val diffNum = big - small
            val prompt = "$big/$d - $small/$d = ?"
            val spoken = "$big per $d dikurang $small per $d sama dengan berapa?"
            val correct = "$diffNum/$d"
            val options = mutableSetOf(correct)
            while (options.size < 4) {
                val fakeNum = maxOf(0, diffNum + Random.nextInt(-2, 3))
                options.add("$fakeNum/$d")
            }
            return MathQuestion(
                id = UUID.randomUUID().toString(),
                prompt = prompt,
                spokenPrompt = spoken,
                options = options.shuffled(),
                correctAnswer = correct,
                category = MathCategory.PECAHAN,
                difficulty = difficulty,
                explanation = "Karena penyebutnya sama ($d), kurangkan pembilang: $big - $small = $diffNum/$d"
            )
        }
    }

    private fun generateDecimals(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val aInt = Random.nextInt(1, 20)
        val bInt = Random.nextInt(1, 20)
        val a = aInt / 10.0
        val b = bInt / 10.0
        val isAdd = Random.nextBoolean()

        val ans = if (isAdd) {
            (Math.round((a + b) * 10.0) / 10.0)
        } else {
            val big = maxOf(a, b)
            val small = minOf(a, b)
            (Math.round((big - small) * 10.0) / 10.0)
        }

        val prompt = if (isAdd) "%.1f + %.1f = ?".format(a, b) else "%.1f - %.1f = ?".format(maxOf(a, b), minOf(a, b))
        val spoken = if (isAdd) "$a ditambah $b sama dengan berapa?" else "${maxOf(a, b)} dikurang ${minOf(a, b)} sama dengan berapa?"
        val correct = "%.1f".format(ans)

        val options = mutableSetOf(correct)
        while (options.size < 4) {
            val delta = Random.nextInt(-3, 4) * 0.1
            val fake = maxOf(0.1, ans + delta)
            options.add("%.1f".format(fake))
        }

        return MathQuestion(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            spokenPrompt = spoken,
            options = options.shuffled(),
            correctAnswer = correct,
            category = MathCategory.DESIMAL,
            difficulty = difficulty,
            explanation = "Hasil perhitungan desimal adalah $correct"
        )
    }

    private fun generatePercentages(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val percents = listOf(10, 20, 25, 50, 75)
        val p = percents.random()
        val baseMultiplier = Random.nextInt(1, 10)
        val base = baseMultiplier * (100 / (if (p == 25 || p == 75) 4 else 10))
        val ans = (p * base) / 100

        val prompt = "$p% dari $base = ?"
        val spoken = "$p persen dari $base sama dengan berapa?"
        val correct = ans.toString()
        val options = buildOptions(ans)

        return MathQuestion(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            spokenPrompt = spoken,
            options = options,
            correctAnswer = correct,
            category = MathCategory.PERSEN,
            difficulty = difficulty,
            explanation = "$p% × $base = ($p/100) × $base = $ans"
        )
    }

    private fun generateAlgebra(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val x = Random.nextInt(2, 12)
        val a = Random.nextInt(2, 6)
        val c = Random.nextInt(1, 15)
        val isAdd = Random.nextBoolean()

        val prompt: String
        val spoken: String
        val explanation: String
        if (isAdd) {
            val b = a * x + c
            prompt = "$a·x + $c = $b. Berapa nilai x?"
            spoken = "$a x ditambah $c sama dengan $b. Berapakah nilai x?"
            explanation = "$a·x = $b - $c = ${b - c}, sehingga x = ${b - c} ÷ $a = $x"
        } else {
            val b = a * x - c
            prompt = "$a·x - $c = $b. Berapa nilai x?"
            spoken = "$a x dikurang $c sama dengan $b. Berapakah nilai x?"
            explanation = "$a·x = $b + $c = ${b + c}, sehingga x = ${b + c} ÷ $a = $x"
        }

        val correct = x.toString()
        val options = buildOptions(x)
        return MathQuestion(
            id = UUID.randomUUID().toString(),
            prompt = prompt,
            spokenPrompt = spoken,
            options = options,
            correctAnswer = correct,
            category = MathCategory.ALJABAR,
            difficulty = difficulty,
            explanation = explanation
        )
    }

    private fun generateGeometry(grade: MathGrade, difficulty: Difficulty): MathQuestion {
        val types = listOf("persegi_luas", "persegi_keliling", "persegi_panjang", "segitiga")
        val type = types.random()
        return when (type) {
            "persegi_luas" -> {
                val s = Random.nextInt(3, 15)
                val ans = s * s
                val prompt = "Luas persegi dengan sisi $s cm = ... cm²"
                val spoken = "Luas persegi dengan panjang sisi $s sentimeter adalah berapa sentimeter persegi?"
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    prompt = prompt,
                    spokenPrompt = spoken,
                    options = buildOptions(ans),
                    correctAnswer = ans.toString(),
                    category = MathCategory.GEOMETRI,
                    difficulty = difficulty,
                    explanation = "Luas persegi = sisi × sisi = $s × $s = $ans cm²"
                )
            }
            "persegi_keliling" -> {
                val s = Random.nextInt(3, 20)
                val ans = 4 * s
                val prompt = "Keliling persegi dengan sisi $s cm = ... cm"
                val spoken = "Keliling persegi dengan panjang sisi $s sentimeter adalah berapa sentimeter?"
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    prompt = prompt,
                    spokenPrompt = spoken,
                    options = buildOptions(ans),
                    correctAnswer = ans.toString(),
                    category = MathCategory.GEOMETRI,
                    difficulty = difficulty,
                    explanation = "Keliling persegi = 4 × sisi = 4 × $s = $ans cm"
                )
            }
            "segitiga" -> {
                val alas = Random.nextInt(2, 10) * 2
                val tinggi = Random.nextInt(3, 12)
                val ans = (alas * tinggi) / 2
                val prompt = "Luas segitiga: alas $alas cm, tinggi $tinggi cm = ... cm²"
                val spoken = "Luas segitiga dengan alas $alas sentimeter dan tinggi $tinggi sentimeter adalah?"
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    prompt = prompt,
                    spokenPrompt = spoken,
                    options = buildOptions(ans),
                    correctAnswer = ans.toString(),
                    category = MathCategory.GEOMETRI,
                    difficulty = difficulty,
                    explanation = "Luas segitiga = ½ × alas × tinggi = ½ × $alas × $tinggi = $ans cm²"
                )
            }
            else -> {
                val p = Random.nextInt(4, 15)
                val l = Random.nextInt(2, 10)
                val ans = p * l
                val prompt = "Luas persegi panjang: $p cm × $l cm = ... cm²"
                val spoken = "Luas persegi panjang dengan panjang $p sentimeter dan lebar $l sentimeter adalah?"
                MathQuestion(
                    id = UUID.randomUUID().toString(),
                    prompt = prompt,
                    spokenPrompt = spoken,
                    options = buildOptions(ans),
                    correctAnswer = ans.toString(),
                    category = MathCategory.GEOMETRI,
                    difficulty = difficulty,
                    explanation = "Luas = panjang × lebar = $p × $l = $ans cm²"
                )
            }
        }
    }

    private fun buildOptions(correct: Int): List<String> {
        val set = mutableSetOf(correct)
        val deltas = listOf(-1, 1, -2, 2, -10, 10, -5, 5, 3, -3)
        var tries = 0
        while (set.size < 4 && tries < 50) {
            val d = deltas.random()
            val candidate = correct + d
            if (candidate >= 0 && candidate != correct) {
                set.add(candidate)
            }
            tries++
        }
        var extra = 1
        while (set.size < 4) {
            set.add(maxOf(0, correct + extra))
            extra++
        }
        return set.map { it.toString() }.shuffled()
    }
}
