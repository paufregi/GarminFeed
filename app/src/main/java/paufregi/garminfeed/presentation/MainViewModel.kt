package paufregi.garminfeed.presentation

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.models.ImportStatus
import paufregi.garminfeed.core.models.State
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.usecases.ClearCacheUseCase
import paufregi.garminfeed.core.usecases.GetCredentialsUseCase
import paufregi.garminfeed.core.usecases.SaveCredentialsUseCase
import paufregi.garminfeed.core.utils.Fit
import paufregi.garminfeed.core.utils.Formatter
import paufregi.garminfeed.core.utils.RenphoReader
import paufregi.garminfeed.data.database.models.Credentials
import java.io.File
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCredentialsUseCase: GetCredentialsUseCase,
    private val saveCredentialsUseCase: SaveCredentialsUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(State())

    val state = _state.onStart {
        when (val res = getCredentialsUseCase()) {
            is Result.Success -> _state.update { it.copy(credentials = res.data) }
            is Result.Failure -> _state.update { it.copy(credentials = null) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), State())

    fun saveCredentials(username: String, password: String) = viewModelScope.launch{
        when(saveCredentialsUseCase(username, password)) {
            is Result.Success -> ShortToast(context = application.applicationContext, "Credentials saved")
            is Result.Failure -> ShortToast(context = application.applicationContext, "Unable to save credentials")
        }
    }

    fun clearCache(username: String, password: String) = viewModelScope.launch{
        when(clearCacheUseCase()) {
            is Result.Success -> ShortToast(context = application.applicationContext, "Cache cleared")
            is Result.Failure -> ShortToast(context = application.applicationContext, "Unable to clear cache")
        }
    }
}