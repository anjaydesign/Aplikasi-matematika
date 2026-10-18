package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.PassAndPlayGameScreen
import com.example.ui.screens.PassAndPlayResultScreen
import com.example.ui.screens.PassAndPlaySetupScreen
import com.example.ui.screens.QuestsShopScreen
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MathBattleApp()
            }
        }
    }
}

@Composable
fun MathBattleApp(viewModel: MathBattleViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val audioSettings by viewModel.audioSettings.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle observer to handle pausing/resuming BGM cleanly when the app goes background
    DisposableEffect(lifecycleOwner, audioSettings) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.soundManager.onAppPause()
                    viewModel.speechHelper.stop()
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (audioSettings?.bgmEnabled == true) {
                        viewModel.soundManager.onAppResume()
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.soundManager.release()
                    viewModel.speechHelper.release()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.MAIN_MENU -> MainMenuScreen(
                    viewModel = viewModel,
                    profile = profile
                )
                AppScreen.PASS_AND_PLAY_SETUP -> PassAndPlaySetupScreen(viewModel = viewModel)
                AppScreen.PASS_AND_PLAY_GAME -> PassAndPlayGameScreen(viewModel = viewModel)
                AppScreen.PASS_AND_PLAY_RESULT -> PassAndPlayResultScreen(viewModel = viewModel)
                AppScreen.WIFI_MENU -> WifiMenuScreen(viewModel = viewModel)
                AppScreen.WIFI_HOST_LOBBY -> WifiHostLobbyScreen(viewModel = viewModel)
                AppScreen.WIFI_JOIN_INPUT -> WifiJoinInputScreen(viewModel = viewModel)
                AppScreen.WIFI_CLIENT_LOBBY -> WifiClientLobbyScreen(viewModel = viewModel)
                AppScreen.WIFI_GAME_SCREEN -> WifiGameScreen(viewModel = viewModel)
                AppScreen.WIFI_RESULT_SCREEN -> WifiResultScreen(viewModel = viewModel)
                AppScreen.SOLO_SELECT -> SoloSelectScreen(viewModel = viewModel)
                AppScreen.SOLO_GAME -> SoloGameScreen(viewModel = viewModel)
                AppScreen.SOLO_RESULT -> SoloResultScreen(viewModel = viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                AppScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
                AppScreen.QUESTS_SHOP -> QuestsShopScreen(viewModel = viewModel)
            }
        }
    }
}
