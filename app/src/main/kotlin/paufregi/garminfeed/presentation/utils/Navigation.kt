package paufregi.garminfeed.presentation.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.presentation.home.HomeScreen
import paufregi.garminfeed.presentation.home.HomeViewModel
import paufregi.garminfeed.presentation.settings.SettingsScreen
import paufregi.garminfeed.presentation.settings.SettingsViewModel

@Composable
@ExperimentalMaterial3Api
fun Navigation(
    paddingValues: PaddingValues,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.Home) {
        composable(route = Route.Home) {
            val viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle().value
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                paddingValues = paddingValues,
                nav = navController
            )
        }
        composable(route = Route.Settings) {
            val viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle().value
            SettingsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                paddingValues = paddingValues,
                nav = navController
            )
        }
    }
}