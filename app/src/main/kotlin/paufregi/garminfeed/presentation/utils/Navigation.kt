package paufregi.garminfeed.presentation.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.presentation.home.HomeScreen
import paufregi.garminfeed.presentation.settings.SettingsScreen

@Composable
@ExperimentalMaterial3Api
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.Home) {
        composable(route = Route.Home) {
            HomeScreen(nav = navController)
        }
        composable(route = Route.Settings) {
            SettingsScreen(nav = navController)
        }
    }
}