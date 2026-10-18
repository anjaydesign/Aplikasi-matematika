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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ScreenHeader
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MathBattleViewModel

@Composable
fun SettingsScreen(viewModel: MathBattleViewModel) {
    BackHandler {
        viewModel.navigateTo(AppScreen.MAIN_MENU)
    }

    val audioSettings by viewModel.audioSettings.collectAsState()
    val scrollState = rememberScrollState()

    var sfxEnabled by remember(audioSettings) { mutableStateOf(audioSettings?.sfxEnabled ?: true) }
    var bgmEnabled by remember(audioSettings) { mutableStateOf(audioSettings?.bgmEnabled ?: true) }
    var ttsEnabled by remember(audioSettings) { mutableStateOf(audioSettings?.ttsEnabled ?: true) }
    var ttsAutoRead by remember(audioSettings) { mutableStateOf(audioSettings?.ttsAutoRead ?: true) }
    var sfxVolume by remember(audioSettings) { mutableFloatStateOf(audioSettings?.sfxVolume ?: 0.8f) }
    var bgmVolume by remember(audioSettings) { mutableFloatStateOf(audioSettings?.bgmVolume ?: 0.5f) }

    fun syncAudio() {
        viewModel.updateAudioSettings(
            sfxEnabled = sfxEnabled,
            bgmEnabled = bgmEnabled,
            ttsEnabled = ttsEnabled,
            ttsAutoRead = ttsAutoRead,
            sfxVolume = sfxVolume,
            bgmVolume = bgmVolume
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = "Pengaturan & Panduan",
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Audio Settings Card
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pengaturan Musik & Efek Suara",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BGM Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Musik Latar (BGM)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(text = "Melodi santai saat bermain", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = bgmEnabled,
                            onCheckedChange = {
                                bgmEnabled = it
                                syncAudio()
                            },
                            modifier = Modifier.testTag("switch_bgm")
                        )
                    }

                    if (bgmEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Volume Musik: ${(bgmVolume * 100).toInt()}%", fontSize = 12.sp)
                        Slider(
                            value = bgmVolume,
                            onValueChange = {
                                bgmVolume = it
                                syncAudio()
                            },
                            modifier = Modifier.testTag("slider_bgm_volume")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // SFX Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Efek Suara (SFX)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(text = "Suara klik, benar, salah, dan kemenangan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = sfxEnabled,
                            onCheckedChange = {
                                sfxEnabled = it
                                syncAudio()
                            },
                            modifier = Modifier.testTag("switch_sfx")
                        )
                    }

                    if (sfxEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Volume Efek Suara: ${(sfxVolume * 100).toInt()}%", fontSize = 12.sp)
                        Slider(
                            value = sfxVolume,
                            onValueChange = {
                                sfxVolume = it
                                syncAudio()
                            },
                            modifier = Modifier.testTag("slider_sfx_volume")
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.soundManager.playCorrect() },
                                modifier = Modifier.weight(1f).testTag("btn_test_sfx_correct")
                            ) {
                                Text("Uji Benar", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.soundManager.playWrong() },
                                modifier = Modifier.weight(1f).testTag("btn_test_sfx_wrong")
                            ) {
                                Text("Uji Salah", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.soundManager.playVictory() },
                                modifier = Modifier.weight(1f).testTag("btn_test_sfx_victory")
                            ) {
                                Text("Uji Menang", fontSize = 12.sp)
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // TTS Switch (Female Indonesian Voice)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = SecondaryPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Pembaca Soal Perempuan (Bahasa Indonesia)",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Membacakan rumus dan soal matematika otomatis dalam bahasa Indonesia",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = ttsEnabled,
                            onCheckedChange = {
                                ttsEnabled = it
                                syncAudio()
                            },
                            modifier = Modifier.testTag("switch_tts")
                        )
                    }

                    if (ttsEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Bacakan Soal Otomatis Saat Muncul", fontSize = 13.sp)
                            Switch(
                                checked = ttsAutoRead,
                                onCheckedChange = {
                                    ttsAutoRead = it
                                    syncAudio()
                                },
                                modifier = Modifier.testTag("switch_tts_autoread")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.speechHelper.speakMathPrompt("Halo! Saya asisten suara perempuan bahasa Indonesia. Siap bertanding matematika!")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                            modifier = Modifier.fillMaxWidth().testTag("btn_test_female_tts")
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Uji Suara Perempuan (Bahasa Indonesia)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rules & Scoring Fairness Card
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Rule,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sistem Penilaian & Keadilan Skor",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Aturan skor transparan dan konsisten untuk semua mode:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ScoreRuleItem(
                        icon = "✅",
                        title = "Jawaban Benar",
                        desc = "Mendapatkan 100 Poin Dasar."
                    )
                    ScoreRuleItem(
                        icon = "⚡",
                        title = "Bonus Kecepatan",
                        desc = "Sisa Detik x 3 Poin (dibatasi agar adil dan tidak melebihi poin dasar)."
                    )
                    ScoreRuleItem(
                        icon = "🔥",
                        title = "Bonus Rantai Streak",
                        desc = "+10 Poin per kelipatan jawaban benar berturut-turut."
                    )
                    ScoreRuleItem(
                        icon = "❌",
                        title = "Jawaban Salah / Waktu Habis",
                        desc = "0 Poin. Tidak ada pengurangan poin minus agar permainan tetap menyenangkan."
                    )
                    ScoreRuleItem(
                        icon = "🤝",
                        title = "Pemecah Hasil Seri",
                        desc = "Jika total skor sama, ditentukan dari jumlah jawaban benar terbanyak, kemudian waktu tercepat."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Panduan Bermain Multiplayer
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Petunjuk Mode Multiplayer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "1. Multiplayer Satu HP (Pass-and-Play):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Pilih 2–4 nama pemain. Setiap pemain menjawab soal saat gilirannya. Layar transisi 'Giliran Pemain Berikutnya' melindungi kerahasiaan jawaban sebelum diserahkan ke teman berikutnya.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "2. Mabar Wi-Fi Lokal (Antar-HP):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SuccessGreen
                    )
                    Text(
                        text = "Semua pemain harus berada di jaringan Wi-Fi yang sama ATAU satu pemain menyalakan Hotspot HP (tanpa kuota internet). Host memilih 'Buat Ruangan' dan pemain lain memilih 'Gabung Ruangan' memasukkan kode ruangan / IP Host.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ScoreRuleItem(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
