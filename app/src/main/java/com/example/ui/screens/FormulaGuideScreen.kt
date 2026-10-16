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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.FormulaRepository
import com.example.model.EducationLevel
import com.example.ui.components.TopGameBar
import com.example.ui.theme.BgDark
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun FormulaGuideScreen(
    currentLevel: EducationLevel,
    onBack: () -> Unit
) {
    var selectedFilterLevel by remember { mutableStateOf<EducationLevel?>(currentLevel) }
    var searchQuery by remember { mutableStateOf("") }

    val allFormulas = remember(selectedFilterLevel) {
        FormulaRepository.getFormulasByLevel(selectedFilterLevel)
    }

    val filteredFormulas = allFormulas.filter {
        if (searchQuery.isBlank()) true
        else it.title.contains(searchQuery, ignoreCase = true) ||
                it.formulaText.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopGameBar(
                title = "Panduan Rumus Matematika",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari rumus matematika...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                item {
                    val isAll = selectedFilterLevel == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAll) NeonCyan else SurfaceDark)
                            .border(1.dp, if (isAll) NeonCyan else Color(0xFF263352), RoundedCornerShape(12.dp))
                            .clickable { selectedFilterLevel = null }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Semua Jenjang",
                            color = if (isAll) Color(0xFF00252D) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                items(EducationLevel.entries) { lvl ->
                    val isSelected = selectedFilterLevel == lvl
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NeonCyan else SurfaceDark)
                            .border(1.dp, if (isSelected) NeonCyan else Color(0xFF263352), RoundedCornerShape(12.dp))
                            .clickable { selectedFilterLevel = lvl }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${lvl.iconEmoji} ${lvl.code}",
                            color = if (isSelected) Color(0xFF00252D) else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Formula Cards
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (filteredFormulas.isEmpty()) {
                    item {
                        Text(
                            text = "Tidak ada rumus yang cocok dengan kata kunci.",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    items(filteredFormulas) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceElevated)
                                .border(1.dp, Color(0xFF263352), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = "${item.level.code} • ${item.category}",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Formula Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF0B132B))
                                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = item.formulaText,
                                        color = NeonAmber,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = item.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Contoh: ${item.example}",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
