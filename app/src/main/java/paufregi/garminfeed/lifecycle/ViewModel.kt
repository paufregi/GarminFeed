package paufregi.garminfeed.lifecycle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.garminfeed.db.Database
import paufregi.garminfeed.garmin.GarminClient
import paufregi.garminfeed.models.CachedOauth1
import paufregi.garminfeed.models.CachedOauth2
import paufregi.garminfeed.models.ImportStatus
import paufregi.garminfeed.ui.ShortToast
import paufregi.garminfeed.utils.Fit
import paufregi.garminfeed.utils.Formatter
import paufregi.garminfeed.utils.RenphoReader
import java.io.File
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val application: Application,
    private val db: Database
) : ViewModel() {
    private val _state = MutableStateFlow(State())

    val state = _state.onStart {
        val credentials = db.garminDao.getCredentials()
        _state.update { it.copy(credentials = credentials) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), State())

    fun resetClearCacheToast() = _state.update { it.copy(clearCacheToast = false) }

    init {
        savedStateHandle
    }

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

                    val credentials = db.garminDao.getCredentials()

                    if (credentials == null) {
                        ShortToast(application.applicationContext, "No credentials")
                        _state.update { it.copy(importStatus = ImportStatus.Failure) }
                        return@launch
                    }

                    val cachedOauth1 = db.garminDao.getCachedOauth1()
                    val cachedOauth2 = db.garminDao.getCachedOauth2()

                    val filename = "ws_${Formatter.dateTimeForFilename.format(Instant.now())}.fit"
                    val fitFile = File(application.applicationContext.cacheDir, filename)
                    Fit.weight(fitFile, weights)
                    val client = GarminClient(
                        username = credentials.username,
                        password = credentials.password,
                        oauth1 = cachedOauth1?.oauth1,
                        oauth2 = cachedOauth2?.oauth2,
                        cacheOauth1 = { onEvent(Event.CacheOauth1(it)) },
                        cacheOauth2 = { onEvent(Event.CacheOauth2(it)) }
                    )

                    val res = client.uploadFile(fitFile)
                    _state.update { it.copy(importStatus = if (res) ImportStatus.Success else ImportStatus.Failure) }

                } catch (e: Exception) {
                    _state.update { it.copy(importStatus = ImportStatus.Failure) }
                    return@launch
                }
            }

            is Event.SaveCredentials -> viewModelScope.launch {
                db.garminDao.upsertCredentials(event.credentials)
            }

            is Event.CacheOauth1 -> viewModelScope.launch {
                db.garminDao.upsertCachedOauth1(CachedOauth1(oauth1 = event.oauth1))
            }

            is Event.CacheOauth2 -> viewModelScope.launch {
                db.garminDao.upsertCachedOauth2(CachedOauth2(oauth2 = event.oauth2))
            }

            is Event.ClearCache -> viewModelScope.launch {
                db.garminDao.clearCachedOauth1()
                db.garminDao.clearCachedOauth2()
                _state.update { it.copy(clearCacheToast = true) }
//                ShortToast(application.applicationContext, "Cache cleared")
            }

        }
    }
}