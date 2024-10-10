package paufregi.garminfeed.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import paufregi.garminfeed.presentation.ui.theme.Theme
import paufregi.garminfeed.presentation.utils.Navigation
import paufregi.garminfeed.presentation.utils.ObserveAsEvents
import paufregi.garminfeed.presentation.utils.SnackbarController

@AndroidEntryPoint
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            ObserveAsEvents(
                flow = SnackbarController.events,
                key1 = snackbarState
            ) { event ->
                scope.launch {
                    snackbarState.currentSnackbarData?.dismiss()
                    snackbarState.showSnackbar(message = event.message,)
                }
            }

            Theme {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarState) },
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    Navigation(padding)
                }
            }

        }
    }
}