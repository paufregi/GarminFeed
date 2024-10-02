package paufregi.garminfeed.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import dagger.hilt.android.AndroidEntryPoint
import paufregi.garminfeed.presentation.MainViewModel
import paufregi.garminfeed.presentation.navigation.MainNavigation
import paufregi.garminfeed.presentation.ShortToast
import paufregi.garminfeed.presentation.theme.Theme

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                val state by viewModel.state.collectAsState()

                DisposableEffect(state.clearCacheToast) {
                    when(state.clearCacheToast) {
                        true -> ShortToast(application.applicationContext, "Cache cleared")
                        else -> {}
                    }

                    onDispose {
                        viewModel.resetClearCacheToast()
                    }
                }
                MainNavigation(
                    state = state,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}