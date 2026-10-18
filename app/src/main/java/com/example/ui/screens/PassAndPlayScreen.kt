package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.data.model.MathCategory
import com.example.ui.components.AnimatedTimerBar
import com.example.ui.components.AnswerOptionButton
import com.example.ui.components.AvatarHelper
import com.example.ui.components.MathQuestionCard
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AccentGold
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MathBattleViewModel

@Composable
fun PassAndPlaySetupScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val players by viewModel.passAndPlayPlayers.collectAsState()
    val settings by viewModel.passAndPlaySettings.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Pengaturan Satu HP",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Jumlah Pemain (2–4)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 3, 4).forEach { count ->
                            val isSelected = players.size == count
                            Button(
                                onClick = { viewModel.setPassAndPlayPlayerCount(count) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_player_count_$count")
                            ) {
                                Text(
                                    text = "$count Pemain",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Nama & Avatar Pemain:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    players.forEachIndexed { index, player ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.12f))
                            ) {
                                Text(
                                    text = AvatarHelper.getEmoji(player.avatarIndex),
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            OutlinedTextField(
                                value = player.name,
                                onValueChange = { viewModel.updatePassAndPlayPlayerName(index, it) },
                                singleLine = true,
                                label = { Text("Pemain ${index + 1}") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_player_name_$index")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kategori Matematika",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            MathCategory.CAMPURAN_ARITMATIKA to "Campuran",
                            MathCategory.PENJUMLAHAN to "+",
                            MathCategory.PERKALIAN to "×",
                            MathCategory.PEMBAGIAN to "÷"
                        ).forEach { (cat, label) ->
                            val isSel = settings.category == cat
                            FilterChip(
                                selected = isSel,
                                onClick = {
                                    viewModel.passAndPlaySettings.value = settings.copy(category = cat)
                                },
                                label = { Text(label, fontWeight = FontWeight.SemiBold) },
                                modifier = Modifier.testTag("chip_category_${cat.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tingkat Kesulitan & Jenjang",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(Difficulty.MUDAH, Difficulty.SEDANG, Difficulty.SULIT).forEach { diff ->
                            FilterChip(
                                selected = settings.difficulty == diff,
                                onClick = {
                                    viewModel.passAndPlaySettings.value = settings.copy(difficulty = diff)
                                },
                                label = { Text(diff.displayName) },
                                modifier = Modifier.testTag("chip_diff_${diff.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Jumlah Soal & Waktu per Giliran",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Jumlah Soal:", fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(5, 10, 15, 20).forEach { qCount ->
                                FilterChip(
                                    selected = settings.questionCount == qCount,
                                    onClick = {
                                        viewModel.passAndPlaySettings.value = settings.copy(questionCount = qCount)
                                    },
                                    label = { Text("$qCount") }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Timer per Giliran:", fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(10, 15, 20, 30).forEach { sec ->
                                FilterChip(
                                    selected = settings.timePerQuestionSeconds == sec,
                                    onClick = {
                                        viewModel.passAndPlaySettings.value = settings.copy(timePerQuestionSeconds = sec)
                                    },
                                    label = { Text("${sec}s") }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.startPassAndPlayMatch() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_start_pass_and_play")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mulai Pertandingan!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PassAndPlayGameScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val questions by viewModel.passAndPlayQuestions.collectAsState()
    val roundIndex by viewModel.currentRoundIndex.collectAsState()
    val turnPlayerIndex by viewModel.currentTurnPlayerIndex.collectAsState()
    val players by viewModel.passAndPlayPlayers.collectAsState()
    val isWaitingConfirmation by viewModel.isWaitingForTurnConfirmation.collectAsState()
    val timeRemaining by viewModel.turnTimeRemainingSeconds.collectAsState()
    val settings by viewModel.passAndPlaySettings.collectAsState()
    val feedback by viewModel.lastTurnFeedback.collectAsState()
    val isAnswered by viewModel.isTurnAnswered.collectAsState()

    val activePlayer = players.getOrNull(turnPlayerIndex) ?: players.firstOrNull()
    val currentQuestion = questions.getOrNull(roundIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Ronde ${roundIndex + 1} dari ${questions.size}",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        if (isWaitingConfirmation) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GILIRAN PEMAIN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryBlue,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = AvatarHelper.getEmoji(activePlayer?.avatarIndex ?: 0),
                                fontSize = 48.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = activePlayer?.name ?: "Pemain",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Skor saat ini: ${activePlayer?.score ?: 0} poin",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = AccentGold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "🔒 Berikan HP kepada ${activePlayer?.name ?: "pemain"}. Soal belum ditampilkan untuk menjaga kerahasiaan jawaban.",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.confirmReadyForTurn() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_ready_for_turn")
                        ) {
                            Text(
                                text = "SAYA SIAP! BUKA SOAL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PrimaryBlue.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = AvatarHelper.getEmoji(activePlayer?.avatarIndex ?: 0), fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Giliran: ${activePlayer?.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PrimaryBlue
                            )
                        }
                        Text(
                            text = "${activePlayer?.score} Poin",
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentGold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedTimerBar(
                    remainingSeconds = timeRemaining,
                    totalSeconds = settings.timePerQuestionSeconds
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (currentQuestion != null) {
                    MathQuestionCard(
                        prompt = currentQuestion.prompt,
                        categoryName = currentQuestion.category.displayName,
                        roundText = "Soal ${roundIndex + 1}/${questions.size}",
                        spokenPrompt = currentQuestion.spokenPrompt,
                        isSpeaking = false,
                        onSpeakClick = {
                            viewModel.speechHelper.speakMathPrompt(currentQuestion.spokenPrompt)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                currentQuestion?.options?.forEachIndexed { index, opt ->
                    AnswerOptionButton(
                        optionText = opt,
                        index = index,
                        enabled = !isAnswered,
                        onClick = { viewModel.submitPassAndPlayAnswer(opt) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                if (feedback != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (feedback!!.startsWith("Benar")) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = feedback!!,
                            fontWeight = FontWeight.Bold,
                            color = if (feedback!!.startsWith("Benar")) SuccessGreen else ErrorRed,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PassAndPlayResultScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val rankings by viewModel.passAndPlayRankings.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Hasil Pertandingan",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val winner = rankings.firstOrNull()

            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 JUARA MATH BATTLE 🏆",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = AccentGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AccentGold.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = AvatarHelper.getEmoji(winner?.avatarIndex ?: 0),
                            fontSize = 44.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = winner?.name ?: "Pemenang",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${winner?.score ?: 0} Poin",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = AccentGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "KLASEMEN AKHIR",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            )

            val medals = listOf("🥇", "🥈", "🥉", "4️⃣")
            rankings.forEachIndexed { index, p ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = medals.getOrElse(index) { "${index + 1}" }, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = AvatarHelper.getEmoji(p.avatarIndex), fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = p.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${p.correctCount} benar • streak max: ${p.highestStreak}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "${p.score} Poin",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.startPassAndPlayMatch() },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("btn_rematch_pass_and_play")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Main Lagi", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("btn_menu_pass_and_play")
                ) {
                    Text(
                        "Menu Utama",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
