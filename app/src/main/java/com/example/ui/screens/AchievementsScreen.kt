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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.AchievementEntity
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AchievementsScreen(
    achievements: List<AchievementEntity>,
    onBack: () -> Unit
) {
    val unlockedCount = achievements.count { it.isUnlocked }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopGameBar(
                title = "Lencana Pencapaian",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .border(1.dp, NeonAmber.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Koleksi Prestasi",
                                color = NeonAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Buka lencana dengan menyelesaikan tantangan!",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "$unlockedCount / ${achievements.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            items(achievements) { ach ->
                val isUnlocked = ach.isUnlocked
                val borderColor = if (isUnlocked) NeonAmber else Color(0xFF263352)
                val bgColor = if (isUnlocked) Color(0xFF262014) else SurfaceElevated

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) Color(0xFF3D2C10) else SurfaceDark)
                                .border(1.5.dp, if (isUnlocked) NeonAmber else Color(0xFF334155), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ach.iconEmoji,
                                fontSize = 26.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ach.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) NeonAmber else Color.White
                                    )
                                )
                                Text(
                                    text = if (isUnlocked) "Terbuka ✓" else "${ach.progress}/${ach.maxProgress}",
                                    color = if (isUnlocked) NeonGreen else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = ach.description,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )

                            if (!isUnlocked) {
                                val progressRatio = (ach.progress.toFloat() / ach.maxProgress.toFloat()).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progressRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = NeonCyan,
                                    trackColor = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
