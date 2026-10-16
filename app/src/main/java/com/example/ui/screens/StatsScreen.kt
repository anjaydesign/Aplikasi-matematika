package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameHistoryEntity
import com.example.data.TopicStatEntity
import com.example.model.GameMode
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    historyList: List<GameHistoryEntity>,
    topicStats: List<TopicStatEntity>,
    onBack: () -> Unit
) {
    val totalGames = historyList.size
    val totalScore = historyList.sumOf { it.score }
    val totalCorrect = historyList.sumOf { it.correctCount }
    val totalQuestions = historyList.sumOf { it.totalQuestions }
    val overallAccuracy = if (totalQuestions > 0) (totalCorrect.toFloat() / totalQuestions.toFloat()) * 100f else 0f

    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopGameBar(
                title = "Statistik & Riwayat",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Stats Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceDark)
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Ringkasan Performa",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Pertandingan", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("$totalGames Game", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column {
                                Text("Akurasi Keseluruhan", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text(
                                    String.format(Locale.US, "%.1f%%", overallAccuracy),
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Column {
                                Text("Total Poin", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("$totalScore", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // Topic Performance Section
            if (topicStats.isNotEmpty()) {
                item {
                    Text(
                        text = "Statistik Per Topik Materi",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                items(topicStats) { stat ->
                    val acc = if (stat.totalAnswered > 0) (stat.totalCorrect.toFloat() / stat.totalAnswered.toFloat()) * 100f else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, Color(0xFF263352), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stat.topicName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${stat.totalCorrect} benar dari ${stat.totalAnswered} soal",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = String.format(Locale.US, "%.0f%%", acc),
                                color = if (acc >= 75f) NeonGreen else if (acc >= 50f) NeonAmber else NeonRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            // Match History Section
            item {
                Text(
                    text = "Riwayat Pertandingan Terakhir",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            if (historyList.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada riwayat permainan. Mulailah bertanding!",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            } else {
                items(historyList) { game ->
                    val modeEnum = try {
                        GameMode.valueOf(game.modeId)
                    } catch (e: Exception) {
                        GameMode.BATTLE_QUIZ
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, Color(0xFF263352), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(modeEnum.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = modeEnum.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${game.levelCode} • ${game.difficultyCode} • ${sdf.format(Date(game.timestamp))}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${game.score} Pts",
                                    color = NeonAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${game.correctCount}/${game.totalQuestions} Benar (${game.accuracy.toInt()}%)",
                                    color = if (game.accuracy >= 70f) NeonGreen else Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
