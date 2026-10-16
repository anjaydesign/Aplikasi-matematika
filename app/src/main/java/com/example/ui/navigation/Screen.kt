package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object MainMenu : Screen("main_menu")
    object ModeSelection : Screen("mode_selection")
    object TopicSelection : Screen("topic_selection")
    object DifficultySelection : Screen("difficulty_selection")
    object Battle : Screen("battle")
    object BattleResult : Screen("battle_result")
    object Explanation : Screen("explanation")
    object Profile : Screen("profile")
    object Achievements : Screen("achievements")
    object Stats : Screen("stats")
    object FormulaGuide : Screen("formula_guide")
    object Settings : Screen("settings")
}
