package paufregi.connectfeed.presentation.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import paufregi.connectfeed.presentation.home.HomeScreen
import paufregi.connectfeed.presentation.profiles.ProfilesScreen
import paufregi.connectfeed.presentation.settings.SettingsScreen
import paufregi.connectfeed.presentation.ui.theme.Theme
import paufregi.connectfeed.presentation.ui.components.SnackbarObserver
import paufregi.connectfeed.presentation.utils.NavItem

@AndroidEntryPoint
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val snackState = remember { SnackbarHostState() }
            val nav = rememberNavController()

            val navItems = listOf(
                NavItem(Routes.Home, "Home", Icons.Default.Home),
                NavItem(Routes.Profiles, "Profiles", Icons.Default.Tune),
                NavItem(Routes.Settings, "Settings", Icons.Default.Settings),
            )

            Log.i("MainActivity", "currentDestination: ${nav.currentDestination}")
            Log.i("MainActivity", "currentDestination: ${nav.currentDestination?.route}")

            Theme {
                SnackbarObserver(snackState, scope)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackState) },
                    bottomBar = { NavigationBar {
                        navItems.forEach {
                            NavigationBarItem(
                                selected = false, //TODO: Implement selected
                                onClick = { nav.navigate(it.route) },
                                label = { Text(it.label) },
                                alwaysShowLabel = true,
                                icon = { Icon(it.icons, null) }
                            )
                        }
                    } }
                ) { pd ->
                    NavHost(navController = nav, startDestination = Routes.Home) {
                        composable<Routes.Home> { HomeScreen(pd, nav) }
                        composable<Routes.Settings> { SettingsScreen(pd, nav) }
                        composable<Routes.Profiles> { ProfilesScreen(pd, nav) }
                    }
                }
            }
        }
    }
}