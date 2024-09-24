package paufregi.garminfeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import paufregi.garminfeed.lifecycle.Event
import paufregi.garminfeed.lifecycle.MainViewModel
import paufregi.garminfeed.ui.screens.ImportScreen
import paufregi.garminfeed.ui.theme.Theme

@ExperimentalMaterial3Api
class ImportActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)?.let {
            viewModel.onEvent(Event.SyncWeights(it))
        }

        setContent {
            Theme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                ImportScreen(
                    status = state.importStatus,
                    onComplete = { finish() }
                )
            }
        }
    }
}