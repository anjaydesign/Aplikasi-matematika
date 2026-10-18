package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.Difficulty
import com.example.data.model.MathCategory
import com.example.data.model.MathGrade
import com.example.ui.components.AnimatedTimerBar
import com.example.ui.components.AnswerOptionButton
import com.example.ui.components.MathQuestionCard
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AccentGold
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.SecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MathBattleViewModel
import com.example.viewmodel.SoloModeType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoloSelectScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    var selectedMode by remember { mutableStateOf(SoloModeType.RACING) }
    var selectedGrade by remember { mutableStateOf(MathGrade.SD) }
    var selectedCategory by remember { mutableStateOf(MathCategory.CAMPURAN_ARITMATIKA) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.SEDANG) }
    var questionCount by remember { mutableIntStateOf(10) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Mode Solo & Tantangan",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PILIH MODE PERMAINAN",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            SoloModeCard(
                type = SoloModeType.RACING,
                title = "🏁 Math Racing",
                subtitle = "Kumpulkan poin sebanyak-banyaknya dengan cepat sebelum timer habis!",
                isSelected = selectedMode == SoloModeType.RACING,
                color = PrimaryBlue,
                testTag = "solo_mode_racing",
                onClick = { selectedMode = SoloModeType.RACING }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SoloModeCard(
                type = SoloModeType.BOSS_BATTLE,
                title = "👾 Team Boss Battle",
                subtitle = "Jawab soal secara tepat untuk menyerang dan kalahkan Monster Matematika!",
                isSelected = selectedMode == SoloModeType.BOSS_BATTLE,
                color = SecondaryPurple,
                testTag = "solo_mode_boss",
                onClick = { selectedMode = SoloModeType.BOSS_BATTLE }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SoloModeCard(
                type = SoloModeType.STREAK,
                title = "🔥 Streak Challenge",
                subtitle = "Bangun rantai jawaban benar berturut-turut untuk bonus multiplier skor!",
                isSelected = selectedMode == SoloModeType.STREAK,
                color = Color(0xFFE65100),
                testTag = "solo_mode_streak",
                onClick = { selectedMode = SoloModeType.STREAK }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SoloModeCard(
                type = SoloModeType.SURVIVAL,
                title = "❤️ Survival Mode",
                subtitle = "Bertahan hidup dengan 3 nyawa. Salah atau kehabisan waktu mengurangi nyawa!",
                isSelected = selectedMode == SoloModeType.SURVIVAL,
                color = ErrorRed,
                testTag = "solo_mode_survival",
                onClick = { selectedMode = SoloModeType.SURVIVAL }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Jenjang & Kategori
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pengaturan Soal",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Jenjang Pendidikan:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MathGrade.values().forEach { grade ->
                            FilterChip(
                                selected = selectedGrade == grade,
                                onClick = { selectedGrade = grade },
                                label = { Text(grade.displayName) },
                                modifier = Modifier.testTag("grade_${grade.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Kategori Matematika:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MathCategory.values().forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.displayName, fontSize = 12.sp) },
                                modifier = Modifier.testTag("cat_${cat.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Tingkat Kesulitan:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Difficulty.values().forEach { diff ->
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { selectedDifficulty = diff },
                                label = { Text(diff.displayName) },
                                modifier = Modifier.testTag("diff_${diff.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Jumlah Soal:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 20).forEach { count ->
                            FilterChip(
                                selected = questionCount == count,
                                onClick = { questionCount = count },
                                label = { Text("$count Soal") },
                                modifier = Modifier.testTag("count_$count")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.startSoloGame(
                        mode = selectedMode,
                        grade = selectedGrade,
                        category = selectedCategory,
                        difficulty = selectedDifficulty,
                        count = questionCount
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_start_solo_game")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mulai Permainan Solo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SoloModeCard(
    type: SoloModeType,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) color else MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = if (isSelected) 3.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
            ) {
                Text(
                    text = when (type) {
                        SoloModeType.RACING -> "🏁"
                        SoloModeType.BOSS_BATTLE -> "👾"
                        SoloModeType.STREAK -> "🔥"
                        SoloModeType.SURVIVAL -> "❤️"
                    },
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

private fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)

@Composable
fun SoloGameScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val mode by viewModel.currentSoloMode.collectAsState()
    val questions by viewModel.soloQuestions.collectAsState()
    val qIndex by viewModel.soloQuestionIndex.collectAsState()
    val score by viewModel.soloScore.collectAsState()
    val streak by viewModel.soloStreak.collectAsState()
    val lives by viewModel.soloLives.collectAsState()
    val bossHp by viewModel.soloBossHp.collectAsState()
    val bossMaxHp by viewModel.soloBossMaxHp.collectAsState()
    val timeRemaining by viewModel.soloTimeRemaining.collectAsState()
    val feedback by viewModel.soloFeedback.collectAsState()
    val isAnswered by viewModel.isSoloAnswered.collectAsState()
    val isSpeaking by viewModel.speechHelper.isSpeakingFlow.collectAsState()

    val currentQ = questions.getOrNull(qIndex)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = mode.title,
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Status Bar (Score, Mode Specials)
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Soal ${qIndex + 1}/${questions.size}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentGold.copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = "⭐ Skor: ", fontSize = 13.sp)
                                Text(
                                    text = "$score",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AccentGold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    // Mode-Specific Displays
                    when (mode) {
                        SoloModeType.BOSS_BATTLE -> {
                            Spacer(modifier = Modifier.height(10.dp))
                            val bossProgress = (bossHp.toFloat() / bossMaxHp.toFloat()).coerceIn(0f, 1f)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "👾 Monster Boss HP",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SecondaryPurple
                                    )
                                    Text(
                                        text = "$bossHp / $bossMaxHp",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = if (bossHp <= 100) ErrorRed else SecondaryPurple
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { bossProgress },
                                    color = if (bossHp <= 100) ErrorRed else SecondaryPurple,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                )
                            }
                        }
                        SoloModeType.STREAK -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Rantai Streak: $streak (Bonus +${streak * 10} poin)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    fontSize = 13.sp
                                )
                            }
                        }
                        SoloModeType.SURVIVAL -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "Nyawa: ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                for (i in 1..3) {
                                    Icon(
                                        imageVector = if (i <= lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Nyawa $i",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                        SoloModeType.RACING -> {
                            // Standard racing counter
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timer
            AnimatedTimerBar(
                remainingSeconds = timeRemaining,
                totalSeconds = 15
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Question Card
            if (currentQ != null) {
                MathQuestionCard(
                    prompt = currentQ.prompt,
                    categoryName = currentQ.category.displayName,
                    roundText = "Soal ${qIndex + 1}",
                    spokenPrompt = currentQ.spokenPrompt,
                    isSpeaking = isSpeaking,
                    onSpeakClick = { viewModel.speechHelper.speakMathPrompt(currentQ.spokenPrompt) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Answer Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentQ.options.forEachIndexed { index, option ->
                        val isCorrectOption = if (isAnswered) option.trim() == currentQ.correctAnswer.trim() else null

                        AnswerOptionButton(
                            optionText = option,
                            index = index,
                            isSelected = false,
                            isCorrectAnswer = isCorrectOption,
                            enabled = !isAnswered,
                            onClick = {
                                viewModel.submitSoloAnswer(option)
                            }
                        )
                    }
                }

                // Feedback Banner
                AnimatedVisibility(
                    visible = feedback != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    feedback?.let { msg ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (msg.startsWith("Benar")) SuccessGreen.copy(alpha = 0.15f)
                            else ErrorRed.copy(alpha = 0.15f),
                            border = BorderStroke(
                                1.5.dp,
                                if (msg.startsWith("Benar")) SuccessGreen else ErrorRed
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (msg.startsWith("Benar")) Icons.Default.CheckCircle
                                    else Icons.Default.Dangerous,
                                    contentDescription = null,
                                    tint = if (msg.startsWith("Benar")) SuccessGreen else ErrorRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    fontWeight = FontWeight.Bold,
                                    color = if (msg.startsWith("Benar")) SuccessGreen else ErrorRed,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SoloResultScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val mode by viewModel.currentSoloMode.collectAsState()
    val score by viewModel.soloScore.collectAsState()
    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState)
                .padding(20.dp),
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(AccentGold.copy(alpha = 0.2f))
                    ) {
                        Text(text = "🏆", fontSize = 42.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "PERMAINAN SELESAI!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Mode: ${mode.title}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Total Skor Kamu", fontSize = 13.sp)
                            Text(
                                text = "$score",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 36.sp,
                                color = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentGold.copy(alpha = 0.15f),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(text = "🪙 +${50 + (score / 20)} Koin", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 13.sp)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SecondaryPurple.copy(alpha = 0.15f),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(text = "⚡ +${100 + (score / 10)} XP", fontWeight = FontWeight.Bold, color = SecondaryPurple, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.startSoloGame(mode) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_solo_play_again")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Main Lagi", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_solo_back_menu")
            ) {
                Text(
                    text = "Kembali ke Menu Utama",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
