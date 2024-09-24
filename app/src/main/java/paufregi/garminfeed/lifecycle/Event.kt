package paufregi.garminfeed.lifecycle

import android.net.Uri
import paufregi.garminfeed.data.database.models.Credentials

sealed interface Event {
    data class SyncWeights(val uri: Uri) : Event

    data class SaveCredentials(val credentials: Credentials) : Event

    data object ClearCache : Event
}
