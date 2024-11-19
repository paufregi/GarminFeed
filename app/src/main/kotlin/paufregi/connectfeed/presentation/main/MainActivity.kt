package paufregi.connectfeed.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import paufregi.connectfeed.presentation.Routes
import paufregi.connectfeed.presentation.profiles.ProfilesScreen
import paufregi.connectfeed.presentation.quickedit.QuickEditScreen
import paufregi.connectfeed.presentation.settings.SettingsScreen
import paufregi.connectfeed.presentation.setup.SetupScreen
import paufregi.connectfeed.presentation.ui.components.Loading
import paufregi.connectfeed.presentation.ui.components.NavBar
import paufregi.connectfeed.presentation.ui.components.NavItem
import paufregi.connectfeed.presentation.ui.theme.Theme
import paufregi.connectfeed.presentation.ui.components.SnackbarObserver
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
                installSplashScreen().setKeepOnScreenCondition({ setupDone == null })

                val scope = rememberCoroutineScope()
                val snackState = remember { SnackbarHostState() }
                val nav = rememberNavController()

                Theme {
                    SnackbarObserver(snackState, scope)
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackState) },
                        bottomBar = { NavBar(
                            nav = nav,
                            navItems = listOf(
                                NavItem(Routes.Home, "Home", Icons.Default.Home),
                                NavItem(Routes.Profiles, "Profiles", Icons.Default.Tune),
                            NavItem(Routes.Settings, "Settings", Icons.Default.Settings),
                        ),
                    ) }
                ) { pv ->
                    NavHost(navController = nav, startDestination = Routes.Home) {
                        composable<Routes.Home> {
                            when (setupDone)   {
                                true -> QuickEditScreen(pv)
                                else -> SetupScreen(pv)
                            }
                        }
                        composable<Routes.Profiles> { ProfilesScreen(pv) }
                        composable<Routes.Settings> { SettingsScreen(pv) }
                    }
                }
            }
        }
    }
}