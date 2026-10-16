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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.example.audio.TtsState
import com.example.data.UserProfileEntity
import com.example.model.Difficulty
import com.example.model.EducationLevel
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun SettingsScreen(
    profile: UserProfileEntity?,
    ttsState: TtsState,
    onBack: () -> Unit,
    onSelectLevel: (EducationLevel) -> Unit,
    onSelectDifficulty: (Difficulty) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleTts: (Boolean) -> Unit,
    onToggleTtsAutoRead: (Boolean) -> Unit,
    onSetMusicVolume: (Float) -> Unit,
    onSetSoundVolume: (Float) -> Unit,
    onSetTtsSpeechRate: (Float) -> Unit,
    onTestAudio: () -> Unit,
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
            // =================================================================
            // 1. PENGATURAN AUDIO LENGKAP
            // =================================================================
            item {
                Text(
                    text = "Pengaturan Suara & Audio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // 1. Musik Latar ON/OFF
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Musik Latar (BGM)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Instrumen ceria saat bermain kuis", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            Switch(
                                checked = profile?.musicEnabled ?: true,
                                onCheckedChange = onToggleMusic,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonCyan,
                                    checkedTrackColor = Color(0xFF004E5C)
                                ),
                                modifier = Modifier.testTag("switch_music_bgm")
                            )
                        }

                        // Slider Volume Musik (0 - 100%)
                        if (profile?.musicEnabled != false) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Volume Musik", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                    val musicPct = ((profile?.musicVolume ?: 0.7f) * 100).toInt()
                                    Text("$musicPct%", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Slider(
                                    value = profile?.musicVolume ?: 0.7f,
                                    onValueChange = onSetMusicVolume,
                                    valueRange = 0.0f..1.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonCyan,
                                        activeTrackColor = NeonCyan,
                                        inactiveTrackColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.testTag("slider_music_volume")
                                )
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF1E293B)))

                        // 2. Efek Suara ON/OFF
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Efek Suara (SFX)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Suara tombol, benar, salah, dan kemenangan", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            Switch(
                                checked = profile?.soundEnabled ?: true,
                                onCheckedChange = onToggleSound,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonAmber,
                                    checkedTrackColor = Color(0xFF5A3E00)
                                ),
                                modifier = Modifier.testTag("switch_sound_sfx")
                            )
                        }

                        // Slider Volume Efek Suara (0 - 100%)
                        if (profile?.soundEnabled != false) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Volume Efek Suara", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                    val soundPct = ((profile?.soundVolume ?: 0.8f) * 100).toInt()
                                    Text("$soundPct%", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Slider(
                                    value = profile?.soundVolume ?: 0.8f,
                                    onValueChange = onSetSoundVolume,
                                    valueRange = 0.0f..1.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonAmber,
                                        activeTrackColor = NeonAmber,
                                        inactiveTrackColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.testTag("slider_sound_volume")
                                )
                            }
                        }

                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF1E293B)))

                        // 3. Pembaca Soal (TTS Bahasa Indonesia) ON/OFF
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pembaca Soal Suara (TTS)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Membacakan pertanyaan matematika berbahasa Indonesia", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                            Switch(
                                checked = profile?.ttsEnabled ?: true,
                                onCheckedChange = onToggleTts,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonPurple,
                                    checkedTrackColor = Color(0xFF3B1E6D)
                                ),
                                modifier = Modifier.testTag("switch_tts")
                            )
                        }

                        // 4. Bacakan Soal Otomatis ON/OFF
                        if (profile?.ttsEnabled != false) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Bacakan Soal Otomatis", color = Color(0xFFE2E8F0), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Langsung membacakan saat soal baru muncul", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                                Switch(
                                    checked = profile?.ttsAutoRead ?: true,
                                    onCheckedChange = onToggleTtsAutoRead,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeonPurple,
                                        checkedTrackColor = Color(0xFF3B1E6D)
                                    ),
                                    modifier = Modifier.testTag("switch_tts_auto")
                                )
                            }

                            // 5. Kecepatan Pembaca Soal Slider (0.7x - 1.3x)
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Kecepatan Suara", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                    val rate = profile?.ttsSpeechRate ?: 1.0f
                                    Text(String.format(java.util.Locale.US, "%.1fx", rate), color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Slider(
                                    value = profile?.ttsSpeechRate ?: 1.0f,
                                    onValueChange = onSetTtsSpeechRate,
                                    valueRange = 0.7f..1.3f,
                                    steps = 5,
                                    colors = SliderDefaults.colors(
                                        thumbColor = NeonPurple,
                                        activeTrackColor = NeonPurple,
                                        inactiveTrackColor = Color(0xFF1E293B)
                                    ),
                                    modifier = Modifier.testTag("slider_tts_rate")
                                )
                            }

                            // Status Banner Voice TTS
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (ttsState.isLanguageSupported) {
                                            if (ttsState.isFemaleVoice) Color(0xFF132A3E) else Color(0xFF1E2433)
                                        } else Color(0xFF3F1318)
                                    )
                                    .border(
                                        1.dp,
                                        if (ttsState.isLanguageSupported) {
                                            if (ttsState.isFemaleVoice) NeonCyan.copy(alpha = 0.5f) else Color(0xFF38BDF8).copy(alpha = 0.5f)
                                        } else NeonRed.copy(alpha = 0.5f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (ttsState.isFemaleVoice) "👩" else "🗣️", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (ttsState.isFemaleVoice) "Voice Perempuan Indonesia" else "Status Suara TTS",
                                            color = if (ttsState.isLanguageSupported) NeonCyan else NeonRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = ttsState.statusMessage,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Tombol Uji Suara (Test Sound)
                        Button(
                            onClick = onTestAudio,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E293B),
                                contentColor = NeonCyan
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("test_audio_button")
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Uji Suara & Efek Sekarang", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // =================================================================
            // 2. GETARAN HAPTIK
            // =================================================================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Getaran Haptik HP", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Sensasi getar saat menyentuh tombol & menjawab", color = Color(0xFF94A3B8), fontSize = 11.sp)
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

            // =================================================================
            // 3. PILIHAN JENJANG
            // =================================================================
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

            // =================================================================
            // 4. PILIHAN KESULITAN
            // =================================================================
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

            // =================================================================
            // 5. APP INFO & PRIVASI OFFLINE
            // =================================================================
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
                            text = "Math Battle Offline v1.1 • Audio Edition",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "100% Offline • Musik & SFX Lokal • Text-to-Speech Bahasa Indonesia • Tanpa Internet",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // =================================================================
            // 6. RESET DATA ACTION
            // =================================================================
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
