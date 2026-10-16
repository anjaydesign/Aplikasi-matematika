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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import com.example.model.EducationLevel
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceElevated

@Composable
fun OnboardingScreen(
    currentSelectedLevel: EducationLevel,
    onLevelSelected: (EducationLevel) -> Unit,
    onContinue: () -> Unit
) {
    var selectedLevel by remember { mutableStateOf(currentSelectedLevel) }

    Scaffold(
        containerColor = BgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Pilih Jenjang Belajar",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = "Soal dan materi akan disesuaikan otomatis dengan tingkat yang kamu pilih. Kamu dapat mengubahnya kapan saja di Pengaturan.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF94A3B8),
                    lineHeight = 20.sp
                ),
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(EducationLevel.entries) { level ->
                    val isSelected = level == selectedLevel
                    val borderColor = if (isSelected) NeonCyan else Color(0xFF263352)
                    val bgColor = if (isSelected) Color(0xFF132A3E) else SurfaceElevated

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable {
                                selectedLevel = level
                                onLevelSelected(level)
                            }
                            .padding(16.dp)
                            .testTag("level_option_${level.code}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = level.iconEmoji,
                                fontSize = 32.sp,
                                modifier = Modifier.padding(end = 16.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = level.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) NeonCyan else Color.White
                                        )
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = "✓ Terpilih",
                                            color = NeonCyan,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Text(
                                    text = level.subtitle,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )

                                Text(
                                    text = level.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color(0xFF00252D)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_continue_button")
            ) {
                Text(
                    text = "Mulai Math Battle 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
