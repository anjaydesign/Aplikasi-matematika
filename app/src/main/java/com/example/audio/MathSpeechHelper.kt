package com.example.audio

object MathSpeechHelper {

    /**
     * Converts a mathematical question text into natural, spoken Indonesian for TTS.
     * Replaces mathematical operators, units, and symbols with clear spoken Indonesian words.
     */
    fun toSpokenIndonesian(text: String): String {
        var spoken = text

        // Replace currency formatting e.g. "Rp 50.000" or "Rp. 50.000" or "Rp50000"
        spoken = spoken.replace(Regex("""Rp\.?\s*([0-9.]+)\b""", RegexOption.IGNORE_CASE)) { match ->
            val num = match.groupValues[1]
            "$num rupiah"
        }

        // Trigonometric functions
        spoken = spoken.replace(Regex("""\bsin\b""", RegexOption.IGNORE_CASE), "sinus ")
        spoken = spoken.replace(Regex("""\bcos\b""", RegexOption.IGNORE_CASE), "kosinus ")
        spoken = spoken.replace(Regex("""\btan\b""", RegexOption.IGNORE_CASE), "tangen ")

        // Degrees symbol
        spoken = spoken.replace("°", " derajat ")

        // Replace units
        spoken = spoken.replace("cm²", " sentimeter persegi ")
        spoken = spoken.replace("cm³", " sentimeter kubik ")
        spoken = spoken.replace("m²", " meter persegi ")
        spoken = spoken.replace("m³", " meter kubik ")
        spoken = spoken.replace(Regex("""\bcm\b"""), " sentimeter ")
        spoken = spoken.replace(Regex("""\bkm\b"""), " kilometer ")
        spoken = spoken.replace(Regex("""\bkg\b"""), " kilogram ")
        spoken = spoken.replace(Regex("""\bdetik\b"""), " detik ")
        spoken = spoken.replace(Regex("""\bmenit\b"""), " menit ")
        spoken = spoken.replace(Regex("""\bjam\b"""), " jam ")

        // Calculus & advanced
        spoken = spoken.replace("∫", " integral dari ")
        spoken = spoken.replace(Regex("""\bdx\b"""), " terhadap x ")
        spoken = spoken.replace(Regex("""\blim\b""", RegexOption.IGNORE_CASE), " limit ")
        spoken = spoken.replace("→", " mendekati ")
        spoken = spoken.replace("->", " mendekati ")
        spoken = spoken.replace("π", " pi ")

        // Logarithm with base: e.g. ²log 8 or ³log 27
        spoken = spoken.replace(Regex("""²log\s*(\d+)""")) { match ->
            " logaritma ${match.groupValues[1]} basis 2 "
        }
        spoken = spoken.replace(Regex("""³log\s*(\d+)""")) { match ->
            " logaritma ${match.groupValues[1]} basis 3 "
        }
        spoken = spoken.replace(Regex("""\blog\b""", RegexOption.IGNORE_CASE), " logaritma ")

        // Relational operators
        spoken = spoken.replace("≤", " kurang dari atau sama dengan ")
        spoken = spoken.replace(">=", " lebih dari atau sama dengan ")
        spoken = spoken.replace("<=", " kurang dari atau sama dengan ")
        spoken = spoken.replace("≥", " lebih dari atau sama dengan ")
        spoken = spoken.replace("≠", " tidak sama dengan ")
        spoken = spoken.replace("!=", " tidak sama dengan ")
        spoken = spoken.replace("±", " plus minus ")

        // Replace percent
        spoken = spoken.replace("%", " persen ")

        // Replace roots: e.g. √64 or √(a² + b²)
        spoken = spoken.replace("√", " akar kuadrat dari ")

        // Replace exponents: e.g. x², 5², cm²
        spoken = spoken.replace("²", " kuadrat ")
        spoken = spoken.replace("³", " pangkat tiga ")
        spoken = spoken.replace(Regex("""\^(\d+)""")) { match ->
            " pangkat ${match.groupValues[1]} "
        }

        // Replace fractions like "1/2" when surrounded by digits: 1/2 -> "1 per 2"
        spoken = spoken.replace(Regex("""(\d+)\s*/\s*(\d+)""")) { match ->
            val num = match.groupValues[1]
            val den = match.groupValues[2]
            if (num == "1" && den == "2") "setengah"
            else if (num == "1" && den == "4") "seperempat"
            else "$num per $den"
        }

        // Replace negative numbers in parentheses e.g. (-5) -> minus 5
        spoken = spoken.replace(Regex("""\(\s*-\s*(\d+)\s*\)""")) { match ->
            " minus ${match.groupValues[1]} "
        }

        // Replace operators with clear Indonesian words
        spoken = spoken.replace("×", " dikali ")
        spoken = spoken.replace("*", " dikali ")
        spoken = spoken.replace("÷", " dibagi ")
        spoken = spoken.replace(" + ", " ditambah ")
        spoken = spoken.replace(" - ", " dikurang ")

        // Replace equal and question endings FIRST before isolated "="
        spoken = spoken.replace(" = ...", " sama dengan berapa?")
        spoken = spoken.replace("= ...", " sama dengan berapa?")
        spoken = spoken.replace(" = ?", " sama dengan berapa?")
        spoken = spoken.replace("=?", " sama dengan berapa?")
        spoken = spoken.replace("= ?", " sama dengan berapa?")
        spoken = spoken.replace(" = ", " sama dengan ")
        spoken = spoken.replace("...", " berapa?")

        // Clean up excess whitespace
        spoken = spoken.replace(Regex("""\s+"""), " ").trim()

        return spoken
    }
}
