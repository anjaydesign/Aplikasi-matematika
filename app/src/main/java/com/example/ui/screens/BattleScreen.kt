package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import com.example.viewmodel.BattleState

@Composable
fun BattleScreen(
    state: BattleState,
    isTtsSpeaking: Boolean = false,
    isTtsEnabled: Boolean = true,
    isMusicEnabled: Boolean = true,
    onToggleMusic: () -> Unit = {},
    onAnswerOption: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onSpeakQuestion: (String) -> Unit,
    onStopSpeaking: () -> Unit = {},
    onPause: () -> Unit,
    onResume: () -> Unit,
    onExitBattle: () -> Unit
) {
    val q = state.currentQuestion

    Scaffold(
        containerColor = BgDark,
        topBar = {
            // Battle Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(state.mode.badgeColorHex).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${state.mode.iconEmoji} ${state.mode.title}",
                            color = Color(state.mode.badgeColorHex),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (state.mode == GameMode.BATTLE_QUIZ || state.mode == GameMode.AI_DUEL) {
                        Text(
                            text = "Soal ${state.currentQuestionIndex}/${state.totalQuestionsCount}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (state.mode == GameMode.SURVIVAL) {
                        Text(
                            text = "Soal Ke-${state.currentQuestionIndex}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Timer display (if active)
                    if (state.mode == GameMode.TIME_ATTACK) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (state.timeRemainingSec <= 10) Color(0xFF451111) else Color(0xFF1E293B))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⏱️ ${state.timeRemainingSec}s",
                                color = if (state.timeRemainingSec <= 10) NeonRed else NeonAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else if (state.maxTimeSec > 0 && state.mode != GameMode.FREE_PRACTICE) {
                        Text(
                            text = "⏱️ ${state.timeRemainingSec}s",
                            color = if (state.timeRemainingSec <= 5) NeonRed else Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Quick BGM Music Toggle during gameplay
                    IconButton(
                        onClick = onToggleMusic,
                        modifier = Modifier
                            .testTag("battle_music_toggle")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = if (isMusicEnabled) "Matikan Musik" else "Nyalakan Musik",
                            tint = if (isMusicEnabled) NeonCyan else Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = onPause,
                        modifier = Modifier
                            .testTag("battle_pause_button")
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Jeda Pertandingan",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mode Specific Widget (Boss HP, AI Duel stats, Survival lives, Streak target)
            item {
                when (state.mode) {
                    GameMode.BOSS_BATTLE -> {
                        state.boss?.let { boss ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(boss.colorHex).copy(alpha = 0.2f),
                                                SurfaceDark
                                            )
                                        )
                                    )
                                    .border(1.5.dp, Color(boss.colorHex), RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(boss.emoji, fontSize = 28.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = boss.name,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = boss.title,
                                                    color = Color(0xFF38BDF8),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                        Text(
                                            text = "HP: ${boss.currentHp} / ${boss.maxHp}",
                                            color = Color(boss.colorHex),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val hpRatio = (boss.currentHp.toFloat() / boss.maxHp.toFloat()).coerceIn(0f, 1f)
                                    LinearProgressIndicator(
                                        progress = { hpRatio },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = Color(boss.colorHex),
                                        trackColor = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "\"${boss.quote}\"",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }

                    GameMode.AI_DUEL -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceDark)
                                .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Player
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text("Kamu 👤", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("${state.score} Poin", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    }

                                    Text("VS", color = NeonPink, fontWeight = FontWeight.Black, fontSize = 14.sp)

                                    // Bot AI
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${state.aiName} 🤖", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("${state.aiScore} Poin", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E1B4B))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Bot: ${state.aiStatusText} • \"${state.aiLastQuote}\"",
                                        color = Color(0xFFC084FC),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    GameMode.SURVIVAL -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Nyawa: ", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                repeat(3) { index ->
                                    val hasLife = index < state.lives
                                    Text(
                                        text = if (hasLife) "❤️" else "🖤",
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Benar: ${state.correctCount}",
                                color = NeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    GameMode.STREAK_CHALLENGE -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2E1225))
                                .border(1.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🔥 Kombo Aktif: ${state.combo} berturut-turut",
                                    color = NeonPink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Target: 5, 10, 20, 50",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }

            // Score & Combo HUD
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.score} Poin",
                            color = NeonAmber,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    if (state.combo >= 2) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF3B1D00))
                                .border(1.dp, NeonAmber, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔥 COMBO x${state.combo}",
                                color = NeonAmber,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Question Card with Speaker Audio Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(18.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = q?.category ?: "Matematika",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isTtsSpeaking) Color(0xFF381E54)
                                            else if (isTtsEnabled) Color(0xFF132A3E)
                                            else Color(0xFF1E2433)
                                        )
                                        .border(
                                            1.dp,
                                            if (isTtsSpeaking) NeonPurple
                                            else if (isTtsEnabled) NeonCyan.copy(alpha = 0.5f)
                                            else Color(0xFF475569),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            if (isTtsSpeaking) {
                                                onStopSpeaking()
                                            } else {
                                                onSpeakQuestion(q?.question ?: "")
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("speak_question_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(if (isTtsSpeaking) "⏹️" else if (isTtsEnabled) "🔊" else "🔈", fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isTtsSpeaking) "Hentikan Suara" else if (isTtsEnabled) "Bacakan Soal" else "TTS Nonaktif",
                                            color = if (isTtsSpeaking) NeonPurple else if (isTtsEnabled) NeonCyan else Color(0xFF94A3B8),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "+${q?.points ?: 100} Poin",
                                color = NeonAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = q?.question ?: "Memuat soal...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 19.sp,
                                lineHeight = 26.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 4 Option Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val optionLetters = listOf("A", "B", "C", "D")
                    val options = q?.options ?: emptyList()

                    options.forEachIndexed { idx, opt ->
                        val letter = optionLetters.getOrElse(idx) { "" }
                        val isSelected = state.selectedOption == opt
                        val isCorrectAnswer = opt == q?.correctAnswer

                        val buttonBg: Color
                        val buttonBorder: Color
                        val textColor: Color

                        if (state.isAnswerSelected) {
                            if (isCorrectAnswer) {
                                buttonBg = Color(0xFF0F3A2A)
                                buttonBorder = NeonGreen
                                textColor = NeonGreen
                            } else if (isSelected) {
                                buttonBg = Color(0xFF3F1318)
                                buttonBorder = NeonRed
                                textColor = NeonRed
                            } else {
                                buttonBg = SurfaceDark
                                buttonBorder = Color(0xFF1E293B)
                                textColor = Color(0xFF64748B)
                            }
                        } else {
                            buttonBg = SurfaceElevated
                            buttonBorder = Color(0xFF263352)
                            textColor = Color.White
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(buttonBg)
                                .border(1.5.dp, buttonBorder, RoundedCornerShape(14.dp))
                                .clickable(enabled = !state.isAnswerSelected) {
                                    onAnswerOption(opt)
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0D1322)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            color = buttonBorder,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = opt,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                if (state.isAnswerSelected) {
                                    if (isCorrectAnswer) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Benar",
                                            tint = NeonGreen,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else if (isSelected) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Salah",
                                            tint = NeonRed,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Free Practice Step-by-Step Explanation Card
            if (state.mode == GameMode.FREE_PRACTICE && state.isAnswerSelected) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceDark)
                            .border(1.dp, if (state.isCorrectSelection) NeonGreen else NeonAmber, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (state.isCorrectSelection) "Jawaban Benar! 🎉" else "Jawaban Kurang Tepat 💡",
                                    color = if (state.isCorrectSelection) NeonGreen else NeonAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Kunci Jawaban: ${q?.correctAnswer}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Pembahasan Lengkap:",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = q?.explanation ?: "-",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = onNextQuestion,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = Color(0xFF00252D)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Lanjut ke Soal Berikutnya ▶", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Pause Dialog
    if (state.isPaused) {
        AlertDialog(
            onDismissRequest = onResume,
            containerColor = SurfaceDark,
            title = {
                Text(
                    text = "Permainan Dijeda ⏸️",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Skor sementara: ${state.score} • Benar: ${state.correctCount}\nLanjutkan pertempuran matematika?",
                    color = Color(0xFF94A3B8)
                )
            },
            confirmButton = {
                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color(0xFF00252D)
                    )
                ) {
                    Text("Lanjutkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onExitBattle,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonRed
                    )
                ) {
                    Text("Keluar ke Menu")
                }
            }
        )
    }
}
