package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.PassAndPlayGameScreen
import com.example.ui.screens.PassAndPlayResultScreen
import com.example.ui.screens.PassAndPlaySetupScreen
import com.example.ui.screens.QuestsAndRewardsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SoloGameScreen
import com.example.ui.screens.SoloResultScreen
import com.example.ui.screens.SoloSelectScreen
import com.example.ui.screens.WifiClientLobbyScreen
import com.example.ui.screens.WifiGameScreen
import com.example.ui.screens.WifiHostLobbyScreen
import com.example.ui.screens.WifiJoinInputScreen
import com.example.ui.screens.WifiMenuScreen
import com.example.ui.screens.WifiResultScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MathBattleViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MathBattleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MathBattleApp(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.soundManager.onAppResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.soundManager.onAppPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.speechHelper.stop()
    }
}

@Composable
fun MathBattleApp(viewModel: MathBattleViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val profile by viewModel.profile.collectAsState()

    when (currentScreen) {
        AppScreen.MAIN_MENU -> MainMenuScreen(viewModel, profile)
        AppScreen.PASS_AND_PLAY_SETUP -> PassAndPlaySetupScreen(viewModel)
        AppScreen.PASS_AND_PLAY_GAME -> PassAndPlayGameScreen(viewModel)
        AppScreen.PASS_AND_PLAY_RESULT -> PassAndPlayResultScreen(viewModel)
        AppScreen.WIFI_MENU -> WifiMenuScreen(viewModel)
        AppScreen.WIFI_HOST_LOBBY -> WifiHostLobbyScreen(viewModel)
        AppScreen.WIFI_JOIN_INPUT -> WifiJoinInputScreen(viewModel)
        AppScreen.WIFI_CLIENT_LOBBY -> WifiClientLobbyScreen(viewModel)
        AppScreen.WIFI_GAME_SCREEN -> WifiGameScreen(viewModel)
        AppScreen.WIFI_RESULT_SCREEN -> WifiResultScreen(viewModel)
        AppScreen.SOLO_SELECT -> SoloSelectScreen(viewModel)
        AppScreen.SOLO_GAME -> SoloGameScreen(viewModel)
        AppScreen.SOLO_RESULT -> SoloResultScreen(viewModel)
        AppScreen.SETTINGS -> SettingsScreen(viewModel)
        AppScreen.HISTORY -> HistoryScreen(viewModel)
        AppScreen.QUESTS_SHOP -> QuestsAndRewardsScreen(viewModel)
    }
}
