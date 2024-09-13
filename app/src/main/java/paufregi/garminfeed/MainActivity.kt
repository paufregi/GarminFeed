package paufregi.garminfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import paufregi.garminfeed.db.Database
import paufregi.garminfeed.lifecycle.ViewModel
import paufregi.garminfeed.ui.MainNavigation
import paufregi.garminfeed.ui.theme.Theme
import androidx.lifecycle.ViewModel as AndroidViewModel

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

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
                override fun <T : AndroidViewModel> create(modelClass: Class<T>): T {
                    return ViewModel(application, db) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                val state by viewModel.state.collectAsState()
                MainNavigation(
                    state = state,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}