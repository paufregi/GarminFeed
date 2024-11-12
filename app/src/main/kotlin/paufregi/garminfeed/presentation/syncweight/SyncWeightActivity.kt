package paufregi.garminfeed.presentation.syncweight

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import paufregi.garminfeed.presentation.ui.theme.Theme

@AndroidEntryPoint
@ExperimentalMaterial3Api
class SyncWeightActivity : ComponentActivity() {

    private val viewModel: SyncWeightModelView by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)?.let { uri ->
            contentResolver.openInputStream(uri).let { input ->
                viewModel.syncWeight(input)
            }
        }

        setContent {
            Theme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                SyncWeightScreen(
                    state = state,
                    onComplete = { finish() }
                )
            }
        }
    }
}