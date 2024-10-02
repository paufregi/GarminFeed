package paufregi.garminfeed.presentation.utils

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Credentials : Screen("credentials")
}
