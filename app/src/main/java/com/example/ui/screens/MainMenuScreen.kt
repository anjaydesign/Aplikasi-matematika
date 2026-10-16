package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DailyQuestEntity
import com.example.data.MathBattleRepository
import com.example.data.UserProfileEntity
import com.example.model.Difficulty
import com.example.model.EducationLevel
import com.example.model.GameMode
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun MainMenuScreen(
    profile: UserProfileEntity?,
    dailyQuests: List<DailyQuestEntity>,
    onSelectMode: (GameMode) -> Unit,
    onQuickPlay: () -> Unit,
    onOpenFormulaGuide: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit,
    onClaimQuest: (String) -> Unit
) {
    val currentLevel = EducationLevel.fromCode(profile?.selectedLevelCode ?: "SD")
    val currentDiff = Difficulty.fromCode(profile?.selectedDifficultyCode ?: "NORMAL")
    val playerLvl = profile?.playerLevel ?: 1
    val title = MathBattleRepository.getLevelTitle(playerLvl)
    val (curXpInLevel, neededXp) = MathBattleRepository.getXpForNextLevel(profile?.xp ?: 0)
    val xpProgress = (curXpInLevel.toFloat() / neededXp.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        containerColor = BgDark,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already in home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        indicatorColor = Color(0xFF132A3E)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenFormulaGuide,
                    icon = { Icon(Icons.Default.Book, contentDescription = "Rumus") },
                    label = { Text("Rumus") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_formulas")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenAchievements,
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Lencana") },
                    label = { Text("Lencana") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_achievements")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenStats,
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Statistik") },
                    label = { Text("Statistik") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_stats")
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Pengaturan") },
                    label = { Text("Pengaturan") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Profile Status Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(20.dp))
                        .clickable(onClick = onOpenProfile)
                        .padding(16.dp)
                        .testTag("profile_banner_card")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0D1322))
                                        .border(2.dp, NeonCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = profile?.selectedAvatar ?: "⚡",
                                        fontSize = 26.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = profile?.name ?: "Juara Matematika",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Lv. $playerLvl • $title",
                                        color = NeonAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF2A2412))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${profile?.coins ?: 100}",
                                        color = NeonAmber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // XP Progress Bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Progres Level",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "$curXpInLevel / $neededXp XP",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { xpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = NeonCyan,
                            trackColor = Color(0xFF0F172A)
                        )
                    }
                }
            }

            // 2. Active Level & Difficulty Pill Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF132A3E))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenSettings)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentLevel.iconEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Jenjang",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = currentLevel.code,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF26193E))
                            .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onOpenSettings)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Kesulitan",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = currentDiff.displayName,
                                    color = Color(currentDiff.colorHex),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Hero Card Arena Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bg_math_arena),
                        contentDescription = "Math Battle Arena",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xCC0B0F19),
                                        Color(0xFA0B0F19)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "ARENA MATEMATIKA",
                            color = NeonAmber,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Siap Bertarung Sekarang?",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onQuickPlay,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color(0xFF002026)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("quick_play_button")
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MAIN BATTLE QUIZ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 4. Game Modes Selection Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mode Permainan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "7 Mode Tersedia",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }

            // 5. Game Modes Cards
            items(GameMode.entries) { mode ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(16.dp))
                        .clickable { onSelectMode(mode) }
                        .padding(14.dp)
                        .testTag("mode_card_${mode.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(mode.badgeColorHex).copy(alpha = 0.15f))
                                .border(1.dp, Color(mode.badgeColorHex).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mode.iconEmoji, fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Mainkan ▶",
                                    color = Color(mode.badgeColorHex),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = mode.subtitle,
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = mode.description,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // 6. Daily Quests Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceDark)
                        .border(1.dp, Color(0xFF263352), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Misi Harian",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            Text(
                                text = "Reset tiap hari",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (dailyQuests.isEmpty()) {
                            Text(
                                text = "Semua misi harian siap dikerjakan!",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        } else {
                            dailyQuests.forEach { quest ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = quest.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Progres: ${quest.progress} / ${quest.target} • +${quest.rewardXp} XP, +${quest.rewardCoins} 🪙",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }

                                    if (quest.isClaimed) {
                                        Text(
                                            text = "Selesai ✓",
                                            color = NeonGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else if (quest.progress >= quest.target) {
                                        Button(
                                            onClick = { onClaimQuest(quest.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = NeonAmber,
                                                contentColor = Color(0xFF281800)
                                            ),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Klaim", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text(
                                            text = "${quest.progress}/${quest.target}",
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
