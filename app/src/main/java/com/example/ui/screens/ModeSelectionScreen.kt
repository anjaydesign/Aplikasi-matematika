package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.BossRoster
import com.example.engine.MathGenerator
import com.example.model.EducationLevel
import com.example.model.GameMode
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun ModeSelectionScreen(
    mode: GameMode,
    educationLevel: EducationLevel,
    onBack: () -> Unit,
    onStartBattle: (
        topic: String,
        quizLength: Int,
        timeDuration: Int,
        bossIndex: Int
    ) -> Unit
) {
    val topics = remember(educationLevel) { MathGenerator.getTopicsForLevel(educationLevel) }
    var selectedTopic by remember { mutableStateOf(topics.first()) }
    var selectedLength by remember { mutableIntStateOf(10) }
    var selectedDuration by remember { mutableIntStateOf(60) }
    var selectedBossIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopGameBar(
                title = mode.title,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mode Description Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, Color(mode.badgeColorHex).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = mode.iconEmoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = mode.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Configuration Section based on Mode
                if (mode == GameMode.BATTLE_QUIZ) {
                    item {
                        Text(
                            text = "Jumlah Soal",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(10, 20, 30).forEach { count ->
                                val isSelected = count == selectedLength
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF132A3E) else SurfaceDark)
                                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonCyan else Color(0xFF263352), RoundedCornerShape(12.dp))
                                        .clickable { selectedLength = count }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$count Soal",
                                        color = if (isSelected) NeonCyan else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (mode == GameMode.TIME_ATTACK) {
                    item {
                        Text(
                            text = "Durasi Waktu Tantangan",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(30, 60, 120).forEach { dur ->
                                val isSelected = dur == selectedDuration
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF2E2211) else SurfaceDark)
                                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonAmber else Color(0xFF263352), RoundedCornerShape(12.dp))
                                        .clickable { selectedDuration = dur }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$dur Detik",
                                        color = if (isSelected) NeonAmber else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                if (mode == GameMode.BOSS_BATTLE) {
                    item {
                        Text(
                            text = "Pilih Bos Matematika",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            BossRoster.BOSSES.forEachIndexed { idx, boss ->
                                val isSelected = idx == selectedBossIndex
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) Color(boss.colorHex).copy(alpha = 0.2f) else SurfaceDark)
                                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) Color(boss.colorHex) else Color(0xFF263352), RoundedCornerShape(14.dp))
                                        .clickable { selectedBossIndex = idx }
                                        .padding(14.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = boss.emoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = boss.name,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "${boss.maxHp} HP",
                                                    color = Color(boss.colorHex),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                            Text(
                                                text = boss.title,
                                                color = Color(0xFF38BDF8),
                                                fontSize = 11.sp
                                            )
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
                        }
                    }
                }

                // Topic Picker (For all modes except Boss Battle which binds to Boss category)
                if (mode != GameMode.BOSS_BATTLE) {
                    item {
                        Text(
                            text = "Pilih Topik Soal (${educationLevel.code})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            topics.forEach { topic ->
                                val isSelected = topic == selectedTopic
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF132A3E) else SurfaceDark)
                                        .border(if (isSelected) 1.5.dp else 1.dp, if (isSelected) NeonCyan else Color(0xFF263352), RoundedCornerShape(12.dp))
                                        .clickable { selectedTopic = topic }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = topic,
                                            color = if (isSelected) NeonCyan else Color.White,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                        if (isSelected) {
                                            Text(
                                                text = "✓",
                                                color = NeonCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    onStartBattle(
                        selectedTopic,
                        selectedLength,
                        selectedDuration,
                        selectedBossIndex
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(mode.badgeColorHex),
                    contentColor = Color(0xFF0D1322)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_battle_button")
            ) {
                Text(
                    text = "Mulai Pertandingan Sekarang ⚔️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
