package paufregi.garminfeed.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.core.models.State
import paufregi.garminfeed.presentation.preview.StatePreview
import paufregi.garminfeed.presentation.ui.screens.CredentialsScreen
import paufregi.garminfeed.presentation.ui.screens.MainScreen

@Composable
@ExperimentalMaterial3Api
fun MainNavigation(
    @PreviewParameter(StatePreview::class) state: State?,
    onEvent: (Event) -> Unit = {},
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(route = Screen.Main.route) {
            MainScreen(
                credentials = state?.credentials,
                clearCache = { onEvent(Event.ClearCache) },
                nav = navController
            )
        }
        composable(route = Screen.Credentials.route) {
            CredentialsScreen(
                credentials = state?.credentials,
                onSave = { onEvent(Event.SaveCredentials(it)) },
                nav = navController
            )
        }
    }
}