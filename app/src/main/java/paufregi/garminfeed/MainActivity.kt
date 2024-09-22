package paufregi.garminfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import dagger.hilt.android.AndroidEntryPoint
import paufregi.garminfeed.db.Database
import paufregi.garminfeed.lifecycle.ViewModel
import paufregi.garminfeed.ui.MainNavigation
import paufregi.garminfeed.ui.ShortToast
import paufregi.garminfeed.ui.theme.Theme
import javax.inject.Inject

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var db: Database

    @Inject
    lateinit var viewModel: ViewModel

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