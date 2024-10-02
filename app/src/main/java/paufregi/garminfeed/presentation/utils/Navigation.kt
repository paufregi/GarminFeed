package paufregi.garminfeed.presentation.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.core.models.State
import paufregi.garminfeed.presentation.utils.preview.StatePreview
import paufregi.garminfeed.presentation.ui.screens.CredentialsScreen
import paufregi.garminfeed.presentation.ui.screens.MainScreen

@Composable
@ExperimentalMaterial3Api
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            HomeScreen(nav = navController)
        }
        composable(route = Screen.Credentials.route) {
            CredentialsScreen(nav = navController)
        }
    }
}