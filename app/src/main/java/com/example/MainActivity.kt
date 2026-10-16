package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.EducationLevel
import com.example.model.GameMode
import com.example.ui.navigation.Screen
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.BattleResultScreen
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.ExplanationScreen
import com.example.ui.screens.FormulaGuideScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.ModeSelectionScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark
                ) {
                    MathBattleApp()
                }
            }
        }
    }
}

@Composable
fun MathBattleApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val battleState by viewModel.battleState.collectAsStateWithLifecycle()
    val achievements by viewModel.allAchievements.collectAsStateWithLifecycle()
    val dailyQuests by viewModel.todayQuests.collectAsStateWithLifecycle()
    val recentHistory by viewModel.recentHistory.collectAsStateWithLifecycle()
    val topicStats by viewModel.topicStats.collectAsStateWithLifecycle()
    val lastResult by viewModel.lastResult.collectAsStateWithLifecycle()
    val pendingMode by viewModel.pendingMode.collectAsStateWithLifecycle()

    val currentEducationLevel = EducationLevel.fromCode(profile?.selectedLevelCode ?: "SD")

    when (currentScreen) {
        Screen.Splash -> {
            SplashScreen()
        }

        Screen.Onboarding -> {
            OnboardingScreen(
                currentSelectedLevel = currentEducationLevel,
                onLevelSelected = { level ->
                    viewModel.updateEducationLevel(level)
                },
                onContinue = {
                    viewModel.navigateTo(Screen.MainMenu)
                }
            )
        }

        Screen.MainMenu -> {
            MainMenuScreen(
                profile = profile,
                dailyQuests = dailyQuests,
                onSelectMode = { mode ->
                    viewModel.pendingMode.value = mode
                    viewModel.navigateTo(Screen.ModeSelection)
                },
                onQuickPlay = {
                    viewModel.startBattle(mode = GameMode.BATTLE_QUIZ, quizLength = 10)
                },
                onOpenFormulaGuide = {
                    viewModel.navigateTo(Screen.FormulaGuide)
                },
                onOpenAchievements = {
                    viewModel.navigateTo(Screen.Achievements)
                },
                onOpenStats = {
                    viewModel.navigateTo(Screen.Stats)
                },
                onOpenProfile = {
                    viewModel.navigateTo(Screen.Profile)
                },
                onOpenSettings = {
                    viewModel.navigateTo(Screen.Settings)
                },
                onClaimQuest = { questId ->
                    viewModel.claimQuest(questId)
                }
            )
        }

        Screen.ModeSelection -> {
            BackHandler {
                viewModel.navigateTo(Screen.MainMenu)
            }
            ModeSelectionScreen(
                mode = pendingMode,
                educationLevel = currentEducationLevel,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                },
                onStartBattle = { topic, length, duration, bossIndex ->
                    viewModel.pendingTopic.value = topic
                    viewModel.pendingQuizLength.value = length
                    viewModel.pendingTimeAttackDuration.value = duration
                    viewModel.pendingBossIndex.value = bossIndex

                    viewModel.startBattle(
                        mode = pendingMode,
                        topic = topic,
                        quizLength = length,
                        timeDuration = duration
                    )
                }
            )
        }

        Screen.Battle -> {
            BackHandler {
                viewModel.pauseBattle()
            }
            BattleScreen(
                state = battleState,
                onAnswerOption = { opt ->
                    viewModel.submitAnswer(opt)
                },
                onNextQuestion = {
                    viewModel.advanceToNextQuestion()
                },
                onPause = {
                    viewModel.pauseBattle()
                },
                onResume = {
                    viewModel.resumeBattle()
                },
                onExitBattle = {
                    viewModel.exitBattleToMainMenu()
                }
            )
        }

        Screen.BattleResult -> {
            BackHandler {
                viewModel.navigateTo(Screen.MainMenu)
            }
            BattleResultScreen(
                result = lastResult,
                onViewExplanations = {
                    viewModel.navigateTo(Screen.Explanation)
                },
                onPlayAgain = {
                    lastResult?.let { prev ->
                        viewModel.startBattle(
                            mode = prev.mode,
                            quizLength = prev.totalQuestions.coerceAtLeast(10),
                            timeDuration = prev.durationSeconds.coerceAtLeast(30)
                        )
                    } ?: viewModel.navigateTo(Screen.MainMenu)
                },
                onBackToMenu = {
                    viewModel.navigateTo(Screen.MainMenu)
                }
            )
        }

        Screen.Explanation -> {
            BackHandler {
                viewModel.navigateTo(Screen.BattleResult)
            }
            ExplanationScreen(
                items = lastResult?.questionsAnswered ?: emptyList(),
                onBack = {
                    viewModel.navigateTo(Screen.BattleResult)
                }
            )
        }

        Screen.Profile -> {
            BackHandler {
                viewModel.navigateTo(Screen.MainMenu)
            }
            ProfileScreen(
                profile = profile,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                },
                onUpdateName = { newName ->
                    viewModel.updatePlayerName(newName)
                },
                onUpdateAvatar = { emoji ->
                    viewModel.updateAvatar(emoji)
                }
            )
        }

        Screen.Achievements -> {
            BackHandler {
                viewModel.navigateTo(Screen.MainMenu)
            }
            AchievementsScreen(
                achievements = achievements,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                }
            )
        }

        Screen.Stats -> {
            BackHandler {
                viewModel.navigateTo(Screen.MainMenu)
            }
            StatsScreen(
                historyList = recentHistory,
                topicStats = topicStats,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                }
            )
        }

        Screen.FormulaGuide -> {
            BackHandler {
                viewModel.navigateTo(Screen.MainMenu)
            }
            FormulaGuideScreen(
                currentLevel = currentEducationLevel,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                }
            )
        }

        Screen.Settings -> {
            BackHandler {
                viewModel.navigateTo(Screen.MainMenu)
            }
            SettingsScreen(
                profile = profile,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                },
                onSelectLevel = { level ->
                    viewModel.updateEducationLevel(level)
                },
                onSelectDifficulty = { diff ->
                    viewModel.updateDifficulty(diff)
                },
                onToggleSound = { enabled ->
                    viewModel.toggleAudio(enabled)
                },
                onToggleVibration = { enabled ->
                    viewModel.toggleVibration(enabled)
                },
                onResetData = {
                    viewModel.resetAllData()
                }
            )
        }

        Screen.TopicSelection -> {
            BackHandler {
                viewModel.navigateTo(Screen.ModeSelection)
            }
            ModeSelectionScreen(
                mode = pendingMode,
                educationLevel = currentEducationLevel,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                },
                onStartBattle = { topic, length, duration, bossIndex ->
                    viewModel.pendingTopic.value = topic
                    viewModel.pendingQuizLength.value = length
                    viewModel.pendingTimeAttackDuration.value = duration
                    viewModel.pendingBossIndex.value = bossIndex

                    viewModel.startBattle(
                        mode = pendingMode,
                        topic = topic,
                        quizLength = length,
                        timeDuration = duration
                    )
                }
            )
        }

        Screen.DifficultySelection -> {
            BackHandler {
                viewModel.navigateTo(Screen.Settings)
            }
            SettingsScreen(
                profile = profile,
                onBack = {
                    viewModel.navigateTo(Screen.MainMenu)
                },
                onSelectLevel = { level ->
                    viewModel.updateEducationLevel(level)
                },
                onSelectDifficulty = { diff ->
                    viewModel.updateDifficulty(diff)
                },
                onToggleSound = { enabled ->
                    viewModel.toggleAudio(enabled)
                },
                onToggleVibration = { enabled ->
                    viewModel.toggleVibration(enabled)
                },
                onResetData = {
                    viewModel.resetAllData()
                }
            )
        }
    }
}
