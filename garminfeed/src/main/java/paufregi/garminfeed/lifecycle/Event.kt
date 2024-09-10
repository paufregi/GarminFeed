package paufregi.garminfeed.lifecycle

import android.net.Uri
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth2
import paufregi.garminfeed.models.Credentials

sealed interface Event {
    data class SyncWeights(val uri: Uri) : Event

    data class SaveCredentials(val credentials: Credentials) : Event

    data class CacheOauth1(val oauth1: Oauth1) : Event
    data class CacheOauth2(val oauth2: Oauth2) : Event
    data object ClearCache : Event
}
