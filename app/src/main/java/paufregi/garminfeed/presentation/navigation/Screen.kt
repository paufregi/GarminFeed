package paufregi.garminfeed.presentation.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Credentials : Screen("credentials")
}
