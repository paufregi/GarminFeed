package paufregi.garminfeed.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.garminfeed.data.api.models.ApiResponse
import paufregi.garminfeed.data.repository.GarminRepository
import paufregi.garminfeed.data.datastore.TokenManager
import paufregi.garminfeed.core.models.ImportStatus
import paufregi.garminfeed.core.models.State
import paufregi.garminfeed.core.usecases.SaveCredentialsUseCase
import paufregi.garminfeed.core.utils.Fit
import paufregi.garminfeed.core.utils.Formatter
import paufregi.garminfeed.core.utils.RenphoReader
import java.io.File
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCredentialsUseCase: GetCredentialsUseCase
    private val saveCredentialsUseCase: SaveCredentialsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(State())

    val state = _state.onStart {
        val credentials = garminRepo.getCredentials()
        _state.update { it.copy(credentials = credentials) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), State())

    fun resetClearCacheToast() = _state.update { it.copy(clearCacheToast = false) }

    fun onEvent(event: Event) {
        when (event) {
            is Event.SyncWeights -> viewModelScope.launch {
                _state.update { it.copy(importStatus = ImportStatus.Uploading) }

                try {
                    val weights = application.contentResolver.openInputStream(event.uri)?.let {
                        RenphoReader.read(it)
                    }

                    if (weights.isNullOrEmpty()) {
                        ShortToast(application.applicationContext, "Nothing to sync")
                        _state.update { it.copy(importStatus = ImportStatus.Success) }
                        return@launch
                    }

                    val filename = "ws_${Formatter.dateTimeForFilename.format(Instant.now())}.fit"
                    val fitFile = File(application.applicationContext.cacheDir, filename)
                    Fit.weight(fitFile, weights)
                    when (garminRepo.uploadFile(fitFile)) {
                        is ApiResponse.Success ->  _state.update { it.copy(importStatus = ImportStatus.Success) }
                        is ApiResponse.Failure ->  _state.update { it.copy(importStatus = ImportStatus.Failure) }
                    }

                } catch (e: Exception) {
                    _state.update { it.copy(importStatus = ImportStatus.Failure) }
                    return@launch
                }
            }

            is Event.SaveCredentials -> viewModelScope.launch {
                garminRepo.saveCredentials(event.credentials)
            }

            is Event.ClearCache -> viewModelScope.launch {
                tokenManager.deleteOAuthConsumer()
                tokenManager.deleteOAuth1()
                tokenManager.deleteOAuth2()
                _state.update { it.copy(clearCacheToast = true) }
            }

        }
    }
}