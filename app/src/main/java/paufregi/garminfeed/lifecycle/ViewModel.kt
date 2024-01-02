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

class ViewModel(private val application: Application, private val db: Database) :
    AndroidViewModel(application) {
    private val _credentials = db.garminDao.getCredentials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    private val _oauth1 = db.garminDao.getCachedOauth1()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    private val _oauth2 = db.garminDao.getCachedOauth2()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    private val _state = MutableStateFlow(State())
    val state = combine(_state, _credentials, _oauth1, _oauth2)
    { state, credentials, oauth1, oauth2 ->
        state.copy(
            credentials = credentials,
            cachedOauth1 = oauth1,
            cachedOauth2 = oauth2,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), State())

    var status = MutableStateFlow<Status?>(null)

    fun onEvent(event: Event) {
        when (event) {
            is Event.SaveWeights -> {
                if (state.value.credentials == null) {
                    ShortToast(application.applicationContext, "No credentials")
                    return
                }
                if (event.weights.isEmpty()) {
                    ShortToast(application.applicationContext, "Nothing to sync")
                    return
                }
                viewModelScope.launch {
                    status.value = Status.Uploading
                    val fitFile = Fit.weight(application.applicationContext, event.weights)

                    val client = GarminClient(
                        username = state.value.credentials!!.username,
                        password = state.value.credentials!!.password,
                        oauth1 = state.value.cachedOauth1?.oauth1,
                        oauth2 = state.value.cachedOauth2?.oauth2,
                        cacheOauth1 = { oauth1 -> onEvent(Event.CacheOauth1(oauth1)) },
                        cacheOauth2 = { oauth2 -> onEvent(Event.CacheOauth2(oauth2)) }
                    )

                    val res = client.uploadFile(fitFile)
                    status.value = if (res) Status.Success else Status.Fails
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
                if (state.value.cachedOauth1 != null) {
                    db.garminDao.deleteOauth1(state.value.cachedOauth1!!)
                }
                if (state.value.cachedOauth2 != null) {
                    db.garminDao.deleteOauth2(state.value.cachedOauth2!!)
                }
                ShortToast(application.applicationContext, "Cache cleared")
            }

        }
    }
}