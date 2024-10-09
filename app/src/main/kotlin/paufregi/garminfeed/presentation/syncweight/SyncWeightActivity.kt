package paufregi.garminfeed.presentation.syncweight

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import paufregi.garminfeed.presentation.home.SyncWeightModelView
import paufregi.garminfeed.presentation.ui.theme.Theme

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
                val status by viewModel.status
                SyncWeightScreen(
                    status = status,
                    onComplete = { finish() }
                )
            }
        }
    }
}