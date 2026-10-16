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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.data.MathBattleRepository
import com.example.data.UserProfileEntity
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun ProfileScreen(
    profile: UserProfileEntity?,
    onBack: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateAvatar: (String) -> Unit
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var newNameText by remember { mutableStateOf(profile?.name ?: "Juara Matematika") }

    val avatars = listOf("⚡", "🧙‍♂️", "🤖", "👑", "🦉", "🚀", "💎", "🔥", "🎯", "🌟", "🛡️", "⚔️")
    val playerLvl = profile?.playerLevel ?: 1
    val title = MathBattleRepository.getLevelTitle(playerLvl)
    val (curXpInLevel, neededXp) = MathBattleRepository.getXpForNextLevel(profile?.xp ?: 0)
    val xpProgress = (curXpInLevel.toFloat() / neededXp.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopGameBar(
                title = "Profil Pemain",
                onBack = onBack,
                coins = profile?.coins
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
            // Profile Header Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0D1322))
                                .border(3.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.selectedAvatar ?: "⚡",
                                fontSize = 42.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = profile?.name ?: "Juara Matematika",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            IconButton(
                                onClick = { showEditNameDialog = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Nama",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "Level $playerLvl • $title",
                            color = NeonAmber,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // XP Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pengalaman", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("${profile?.xp ?: 0} XP Total", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NeonCyan,
                            trackColor = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$curXpInLevel / $neededXp XP menuju Level ${playerLvl + 1}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Avatar Selector Grid
            item {
                Text(
                    text = "Pilih Avatar Pemain",
                    style = MaterialTheme.typography.titleSmall.copy(
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
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        avatars.chunked(4).forEach { rowEmojis ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowEmojis.forEach { emoji ->
                                    val isSelected = emoji == (profile?.selectedAvatar ?: "⚡")
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color(0xFF132A3E) else SurfaceElevated)
                                            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonCyan else Color(0xFF263352), CircleShape)
                                            .clickable { onUpdateAvatar(emoji) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = emoji, fontSize = 26.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text("Ubah Nama Pemain", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                OutlinedTextField(
                    value = newNameText,
                    onValueChange = { newNameText = it },
                    label = { Text("Nama Pemain") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNameText.isNotBlank()) {
                            onUpdateName(newNameText)
                        }
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color(0xFF00252D)
                    )
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEditNameDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF94A3B8)
                    )
                ) {
                    Text("Batal")
                }
            }
        )
    }
}
