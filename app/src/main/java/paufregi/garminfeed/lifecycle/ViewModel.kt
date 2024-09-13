package paufregi.garminfeed.lifecycle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import paufregi.garminfeed.db.Database
import paufregi.garminfeed.garmin.GarminClient
import paufregi.garminfeed.models.CachedOauth1
import paufregi.garminfeed.models.CachedOauth2
import paufregi.garminfeed.models.Status
import paufregi.garminfeed.ui.ShortToast
import paufregi.garminfeed.utils.Fit
import paufregi.garminfeed.utils.RenphoReader

class ViewModel(private val application: Application, private val db: Database) :
    AndroidViewModel(application) {
    private val _credentials = db.garminDao.getFlowCredentials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _state = MutableStateFlow(State())
    val state = combine(_state, _credentials)
    { state, credentials ->
        state.copy(credentials = credentials)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), State())

    var importStatus = MutableStateFlow<Status?>(null)

    fun onEvent(event: Event) {
        when (event) {
            is Event.SyncWeights -> viewModelScope.launch {
                importStatus.value = Status.Uploading

                try {
                    val weights = application.contentResolver.openInputStream(event.uri)?.let {
                        RenphoReader.read(it)
                    }

                    if (weights.isNullOrEmpty()) {
                        ShortToast(application.applicationContext, "Nothing to sync")
                        importStatus.value = Status.Success
                        return@launch
                    }

                    val credentials = db.garminDao.getCredentials()

                    if (credentials == null) {
                        ShortToast(application.applicationContext, "No credentials")
                        importStatus.value = Status.Failure
                        return@launch
                    }

                    val cachedOauth1 = db.garminDao.getCachedOauth1()
                    val cachedOauth2 = db.garminDao.getCachedOauth2()

                    val fitFile = Fit.weight(application.applicationContext, weights)
                    val client = GarminClient(
                        username = credentials.username,
                        password = credentials.password,
                        oauth1 = cachedOauth1?.oauth1,
                        oauth2 = cachedOauth2?.oauth2,
                        cacheOauth1 = { onEvent(Event.CacheOauth1(it)) },
                        cacheOauth2 = { onEvent(Event.CacheOauth2(it)) }
                    )

                    val res = client.uploadFile(fitFile)
                    importStatus.value = if (res) Status.Success else Status.Failure

                } catch (e: Exception) {
                    importStatus.value = Status.Failure
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
                ShortToast(application.applicationContext, "Cache cleared")
            }

        }
    }
}