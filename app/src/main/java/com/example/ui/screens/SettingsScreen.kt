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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.UserProfileEntity
import com.example.model.Difficulty
import com.example.model.EducationLevel
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun SettingsScreen(
    profile: UserProfileEntity?,
    onBack: () -> Unit,
    onSelectLevel: (EducationLevel) -> Unit,
    onSelectDifficulty: (Difficulty) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onResetData: () -> Unit
) {
    val currentLevel = EducationLevel.fromCode(profile?.selectedLevelCode ?: "SD")
    val currentDiff = Difficulty.fromCode(profile?.selectedDifficultyCode ?: "NORMAL")
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopGameBar(
                title = "Pengaturan",
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
            // Education Level Section
            item {
                Text(
                    text = "Pilih Jenjang Belajar",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    EducationLevel.entries.forEach { lvl ->
                        val isSelected = lvl == currentLevel
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF132A3E) else SurfaceElevated)
                                .border(if (isSelected) 1.5.dp else 1.dp, if (isSelected) NeonCyan else Color(0xFF263352), RoundedCornerShape(12.dp))
                                .clickable { onSelectLevel(lvl) }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(lvl.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = lvl.title,
                                            color = if (isSelected) NeonCyan else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = lvl.subtitle,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Text("✓ Aktif", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Difficulty Section
            item {
                Text(
                    text = "Tingkat Kesulitan",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Difficulty.entries.forEach { diff ->
                        val isSelected = diff == currentDiff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(diff.colorHex).copy(alpha = 0.25f) else SurfaceElevated)
                                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) Color(diff.colorHex) else Color(0xFF263352), RoundedCornerShape(10.dp))
                                .clickable { onSelectDifficulty(diff) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = diff.displayName,
                                color = if (isSelected) Color(diff.colorHex) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Sound & Haptics Section
            item {
                Text(
                    text = "Efek & Suara",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Efek Suara Lokal", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Nada sintesis untuk jawaban benar & salah", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            Switch(
                                checked = profile?.soundEnabled ?: true,
                                onCheckedChange = onToggleSound,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonCyan,
                                    checkedTrackColor = Color(0xFF004E5C)
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Getaran Haptik", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Sensasi getar saat menjawab & kombo", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            Switch(
                                checked = profile?.vibrationEnabled ?: true,
                                onCheckedChange = onToggleVibration,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonCyan,
                                    checkedTrackColor = Color(0xFF004E5C)
                                )
                            )
                        }
                    }
                }
            }

            // App Information & Offline Privacy
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Math Battle Offline v1.0",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "100% Offline • Bebas Iklan • Data Tersimpan Lokal di HP (Room Database)",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Reset Data Action
            item {
                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F1318),
                        contentColor = NeonRed
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reset_progress_button")
                ) {
                    Text("Reset Seluruh Progres Permainan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text("Konfirmasi Reset Progres", color = NeonRed, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Apakah kamu yakin ingin mereset seluruh data skor, XP, level, koin, riwayat pertandingan, dan lencana? Tindakan ini tidak dapat dibatalkan.",
                    color = Color(0xFFCBD5E1)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Ya, Reset Semua", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF94A3B8)
                    )
                ) {
                    Text("Batal")
                }
            }
        )
    }
}
