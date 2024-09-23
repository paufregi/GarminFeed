package paufregi.garminfeed.ui.screens.utils

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Credentials : Screen("credentials")
}
