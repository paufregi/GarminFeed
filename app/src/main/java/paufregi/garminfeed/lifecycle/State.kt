package paufregi.garminfeed.lifecycle

import paufregi.garminfeed.models.CachedOauth1
import paufregi.garminfeed.models.CachedOauth2
import paufregi.garminfeed.models.Credentials

data class State(
    val credentials: Credentials? = null,
    val cachedOauth1: CachedOauth1? = null,
    val cachedOauth2: CachedOauth2? = null,

    val uploading: Boolean = false,
)