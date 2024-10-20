package paufregi.garminfeed.presentation.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    NavHost(navController = navController, startDestination = MainRoutes.HOME) {
        composable(route = MainRoutes.HOME) {
            val viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onEvent = viewModel::onEvent,
                paddingValues = paddingValues,
                nav = navController
            )
        }
        composable(route = MainRoutes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            SettingsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                paddingValues = paddingValues,
                nav = navController
            )
        }
    }
}