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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameResult
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun BattleResultScreen(
    result: GameResult?,
    onViewExplanations: () -> Unit,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit
) {
    if (result == null) {
        onBackToMenu()
        return
    }

    val isWin = result.isWin
    val titleText = if (result.bossDefeated) {
        "BOS DITAKLUKKAN! 👑"
    } else if (isWin) {
        "HASIL GEMILANG! 🏆"
    } else {
        "PERMAINAN SELESAI! ⚡"
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopGameBar(
                title = "Hasil Pertandingan",
                onBack = onBackToMenu
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isWin) Color(0xFF132A3E) else Color(0xFF2E1926),
                                    SurfaceDark
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            if (isWin) NeonCyan else NeonPink,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D1322))
                                .border(2.dp, NeonAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        )

                        Text(
                            text = "${result.mode.title} • ${result.level.code} • ${result.difficulty.displayName}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${result.score}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = NeonAmber
                            )
                        )
                        Text(
                            text = "TOTAL SKOR AKHIR",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Rewards Card
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF132A3E))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✨", fontSize = 20.sp)
                            Text(
                                text = "+${result.xpEarned} XP",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text("Pengalaman", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF2A2312))
                            .border(1.dp, NeonAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🪙", fontSize = 20.sp)
                            Text(
                                text = "+${result.coinsEarned} Koin",
                                color = NeonAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text("Hadiah Koin", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                    }
                }
            }

            // Statistics Breakdown Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Rincian Evaluasi",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jawaban Benar", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            Text("${result.correctCount} Soal", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jawaban Salah", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            Text("${result.wrongCount} Soal", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tingkat Akurasi", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            Text(
                                String.format(java.util.Locale.US, "%.1f%%", result.accuracy),
                                color = if (result.accuracy >= 80f) NeonCyan else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kombo Maksimum", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            Text("${result.maxStreak}x", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Durasi Pertandingan", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            Text("${result.durationSeconds} Detik", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onViewExplanations,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = NeonCyan
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("view_explanations_button")
                    ) {
                        Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lihat Pembahasan Lengkap Soal", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onPlayAgain,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color(0xFF00252D)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("play_again_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Main Lagi", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onBackToMenu,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF94A3B8)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263352)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("back_to_menu_button")
                    ) {
                        Text("Kembali ke Menu Utama", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
