package paufregi.garminfeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import paufregi.garminfeed.db.Database
import paufregi.garminfeed.lifecycle.Event
import paufregi.garminfeed.lifecycle.ViewModel
import paufregi.garminfeed.models.Weight
import paufregi.garminfeed.ui.components.FailedDialog
import paufregi.garminfeed.ui.screens.ImportScreen
import paufregi.garminfeed.ui.theme.Theme
import paufregi.garminfeed.utils.RenphoReader

@ExperimentalMaterial3Api
class ImportActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "weight.db"
        ).build()
    }

    private val viewModel by viewModels<ViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return ViewModel(application, db) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onComplete = { finish() }
        try {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)?.let { uri ->
                contentResolver.openInputStream(uri)?.let {
                    val weights = RenphoReader.read(it)
                    val onEvent = viewModel::onEvent
                    val onSave = { w: List<Weight> -> onEvent(Event.SaveWeights(w)) }
                    setContent {
                        Theme {
                            viewModel.state.collectAsState()
                            ImportScreen(
                                weights = weights,
                                onSave = onSave,
                                onComplete = onComplete,
                                status = viewModel.status
                            )
                        }
                    }
                    return
                }
            }
        } catch (e: Exception) {
            setContent {
                Theme {
                    FailedDialog(onClick = onComplete)
                }
            }
        }
    }
}