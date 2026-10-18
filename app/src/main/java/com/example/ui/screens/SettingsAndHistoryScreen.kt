package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AudioSettingsEntity
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryPurple
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MathBattleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val audioSettings by viewModel.audioSettings.collectAsState()
    val settings = audioSettings ?: AudioSettingsEntity()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Pengaturan Audio & Suara",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "EFEK SUARA & MUSIK LATAR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // SFX Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Efek Suara (SFX)", fontWeight = FontWeight.Bold)
                                Text("Suara klik tombol, benar, dan salah", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.sfxEnabled,
                            onCheckedChange = {
                                viewModel.updateAudioSettings(
                                    sfxEnabled = it,
                                    bgmEnabled = settings.bgmEnabled,
                                    ttsEnabled = settings.ttsEnabled,
                                    ttsAutoRead = settings.ttsAutoRead,
                                    sfxVolume = settings.sfxVolume,
                                    bgmVolume = settings.bgmVolume
                                )
                            },
                            modifier = Modifier.testTag("switch_sfx")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // BGM Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = SecondaryPurple)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Musik Latar (BGM)", fontWeight = FontWeight.Bold)
                                Text("Musik instrumental ceria saat bermain", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.bgmEnabled,
                            onCheckedChange = {
                                viewModel.updateAudioSettings(
                                    sfxEnabled = settings.sfxEnabled,
                                    bgmEnabled = it,
                                    ttsEnabled = settings.ttsEnabled,
                                    ttsAutoRead = settings.ttsAutoRead,
                                    sfxVolume = settings.sfxVolume,
                                    bgmVolume = settings.bgmVolume
                                )
                            },
                            modifier = Modifier.testTag("switch_bgm")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Volume Musik Latar: ${(settings.bgmVolume * 100).toInt()}%", fontSize = 13.sp)
                    Slider(
                        value = settings.bgmVolume,
                        onValueChange = {
                            viewModel.updateAudioSettings(
                                sfxEnabled = settings.sfxEnabled,
                                bgmEnabled = settings.bgmEnabled,
                                ttsEnabled = settings.ttsEnabled,
                                ttsAutoRead = settings.ttsAutoRead,
                                sfxVolume = settings.sfxVolume,
                                bgmVolume = it
                            )
                        },
                        modifier = Modifier.testTag("slider_bgm_volume")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Text-to-speech card
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "PEMBACA SOAL BAHASA INDONESIA (TTS)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = AccentGold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = AccentGold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Aktifkan Suara Pembaca", fontWeight = FontWeight.Bold)
                                Text("Membacakan soal matematika dengan lantang", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.ttsEnabled,
                            onCheckedChange = {
                                viewModel.updateAudioSettings(
                                    sfxEnabled = settings.sfxEnabled,
                                    bgmEnabled = settings.bgmEnabled,
                                    ttsEnabled = it,
                                    ttsAutoRead = settings.ttsAutoRead,
                                    sfxVolume = settings.sfxVolume,
                                    bgmVolume = settings.bgmVolume
                                )
                            },
                            modifier = Modifier.testTag("switch_tts")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Bacakan Otomatis Tiap Soal", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Soal langsung dibacakan saat giliran dibuka", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = settings.ttsAutoRead,
                            onCheckedChange = {
                                viewModel.updateAudioSettings(
                                    sfxEnabled = settings.sfxEnabled,
                                    bgmEnabled = settings.bgmEnabled,
                                    ttsEnabled = settings.ttsEnabled,
                                    ttsAutoRead = it,
                                    sfxVolume = settings.sfxVolume,
                                    bgmVolume = settings.bgmVolume
                                )
                            },
                            modifier = Modifier.testTag("switch_tts_auto")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.speechHelper.speakMathPrompt("25 + 17 = 42. Dua puluh lima ditambah tujuh belas sama dengan empat puluh dua.")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_test_tts_voice")
                    ) {
                        Text("🔊 Uji Suara Bahasa Indonesia", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val historyList by viewModel.matchHistory.collectAsState()
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Riwayat Pertandingan",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        if (historyList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "📜", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Belum Ada Riwayat Pertandingan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Mainkan mode Satu HP, Mabar Wi-Fi, atau Solo untuk melihat rekaman skor di sini.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyList) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (item.mode) {
                                        "SATU_HP" -> PrimaryBlue.copy(alpha = 0.15f)
                                        "WIFI_LOKAL" -> com.example.ui.theme.SuccessGreen.copy(alpha = 0.15f)
                                        else -> SecondaryPurple.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = when (item.mode) {
                                            "SATU_HP" -> "Satu HP (${item.playerCount}P)"
                                            "WIFI_LOKAL" -> "Wi-Fi Lokal (${item.playerCount}P)"
                                            else -> item.mode
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = when (item.mode) {
                                            "SATU_HP" -> PrimaryBlue
                                            "WIFI_LOKAL" -> com.example.ui.theme.SuccessGreen
                                            else -> SecondaryPurple
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = sdf.format(Date(item.timestamp)),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "🏆 Pemenang: ${item.winnerName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Total ${item.questionCount} soal diselesaikan",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
