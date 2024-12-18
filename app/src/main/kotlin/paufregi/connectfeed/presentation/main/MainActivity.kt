package paufregi.connectfeed.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import paufregi.connectfeed.presentation.Route
import paufregi.connectfeed.presentation.profile.EditProfileScreen
import paufregi.connectfeed.presentation.profiles.ProfilesScreen
import paufregi.connectfeed.presentation.quickedit.QuickEditScreen
import paufregi.connectfeed.presentation.setup.SetupScreen
import paufregi.connectfeed.presentation.ui.theme.Theme
import kotlin.getValue

@AndroidEntryPoint
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val setupDone by viewModel.state.collectAsStateWithLifecycle()
            installSplashScreen().setKeepOnScreenCondition { setupDone == null }

            val nav = rememberNavController()

            Theme {
                when (setupDone) {
                    true -> NavHost(navController = nav, startDestination = Route.Home) {
                        composable<Route.Home> { QuickEditScreen(nav = nav) }
                        composable<Route.Profiles> { ProfilesScreen(nav = nav) }
                        composable<Route.Profile> { EditProfileScreen(nav = nav) }
                    }

                    else -> SetupScreen()
                }
            }
        }
    }
}