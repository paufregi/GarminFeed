package paufregi.garminfeed.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import paufregi.garminfeed.presentation.home.HomeScreen
import paufregi.garminfeed.presentation.settings.SettingsScreen
import paufregi.garminfeed.presentation.utils.Navigation
import paufregi.garminfeed.presentation.utils.Screen

@AndroidEntryPoint
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigation()
        }
    }
}