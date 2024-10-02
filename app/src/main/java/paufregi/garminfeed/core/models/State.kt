package paufregi.garminfeed.core.models

import paufregi.garminfeed.data.database.models.Credentials

data class State(
    val credentials: Credentials? = null,
    val importStatus: ImportStatus? = null,
    val clearCacheToast: Boolean = false
)