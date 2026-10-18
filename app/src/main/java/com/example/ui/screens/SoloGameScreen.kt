package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AnimatedTimerBar
import com.example.ui.components.AnswerOptionButton
import com.example.ui.components.MathQuestionCard
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AccentGold
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MathBattleViewModel
import com.example.viewmodel.SoloModeType

@Composable
fun SoloSelectScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Pilih Mode Solo",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SoloModeCard(
                title = "Math Racing",
                subtitle = "Berpacu melawan waktu untuk mengumpulkan skor setinggi mungkin!",
                icon = Icons.Default.Speed,
                color = PrimaryBlue,
                testTag = "btn_solo_racing",
                onClick = { viewModel.startSoloGame(SoloModeType.RACING) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SoloModeCard(
                title = "Team Boss Battle",
                subtitle = "Lawan Monster Matematika! Setiap jawaban benar mengurangi darah bos.",
                icon = Icons.Default.Security,
                color = ErrorRed,
                testTag = "btn_solo_boss",
                onClick = { viewModel.startSoloGame(SoloModeType.BOSS_BATTLE) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SoloModeCard(
                title = "Streak Challenge",
                subtitle = "Pertahankan rangkaian jawaban benar berturut-turut untuk skor ganda!",
                icon = Icons.Default.Bolt,
                color = AccentGold,
                testTag = "btn_solo_streak",
                onClick = { viewModel.startSoloGame(SoloModeType.STREAK) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SoloModeCard(
                title = "Survival Mode",
                subtitle = "Hanya 3 kesempatan nyawa! Seberapa jauh kamu bisa bertahan?",
                icon = Icons.Default.Favorite,
                color = SecondaryPurple,
                testTag = "btn_solo_survival",
                onClick = { viewModel.startSoloGame(SoloModeType.SURVIVAL) }
            )
        }
    }
}

@Composable
fun SoloModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SoloGameScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.SOLO_SELECT)
    }

    val mode by viewModel.currentSoloMode.collectAsState()
    val questions by viewModel.soloQuestions.collectAsState()
    val questionIndex by viewModel.soloQuestionIndex.collectAsState()
    val score by viewModel.soloScore.collectAsState()
    val streak by viewModel.soloStreak.collectAsState()
    val lives by viewModel.soloLives.collectAsState()
    val bossHp by viewModel.soloBossHp.collectAsState()
    val bossMaxHp by viewModel.soloBossMaxHp.collectAsState()
    val timeRemaining by viewModel.soloTimeRemaining.collectAsState()
    val feedback by viewModel.soloFeedback.collectAsState()
    val isAnswered by viewModel.isSoloAnswered.collectAsState()

    val currentQ = questions.getOrNull(questionIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = mode.title,
            onBackClick = { viewModel.navigateTo(AppScreen.SOLO_SELECT) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode-specific status bar
            when (mode) {
                SoloModeType.BOSS_BATTLE -> {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👾 Bos Matematika (GigaCalc)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "$bossHp / $bossMaxHp HP",
                                    color = ErrorRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (bossHp.toFloat() / bossMaxHp.toFloat()).coerceIn(0f, 1f) },
                                color = ErrorRed,
                                trackColor = ErrorRed.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            )
                        }
                    }
                }
                SoloModeType.SURVIVAL -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            repeat(3) { i ->
                                val isAlive = i < lives
                                Text(
                                    text = if (isAlive) "❤️" else "🖤",
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = "Skor: $score",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = AccentGold
                        )
                    }
                }
                SoloModeType.STREAK -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AccentGold.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = AccentGold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Streak: $streak Beruntun",
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold
                                )
                            }
                            Text(
                                text = "Skor: $score",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
                SoloModeType.RACING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Soal ${questionIndex + 1} dari ${questions.size}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Skor: $score",
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentGold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedTimerBar(
                remainingSeconds = timeRemaining,
                totalSeconds = 15
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (currentQ != null) {
                MathQuestionCard(
                    prompt = currentQ.prompt,
                    categoryName = currentQ.category.displayName,
                    roundText = "Soal ${questionIndex + 1}/${questions.size}",
                    spokenPrompt = currentQ.spokenPrompt,
                    isSpeaking = false,
                    onSpeakClick = {
                        viewModel.speechHelper.speakMathPrompt(currentQ.spokenPrompt)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            currentQ?.options?.forEachIndexed { index, opt ->
                AnswerOptionButton(
                    optionText = opt,
                    index = index,
                    enabled = !isAnswered,
                    onClick = { viewModel.submitSoloAnswer(opt) },
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
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SoloResultScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val score by viewModel.soloScore.collectAsState()
    val mode by viewModel.currentSoloMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Hasil Permainan Solo",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PERMAINAN SELESAI!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = mode.title,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "$score",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 44.sp,
                        color = AccentGold
                    )
                    Text(
                        text = "Total Skor Poin",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.startSoloGame(mode) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Main Lagi", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Menu Utama", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
