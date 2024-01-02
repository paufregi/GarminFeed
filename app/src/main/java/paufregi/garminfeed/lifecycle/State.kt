package paufregi.garminfeed.lifecycle

import paufregi.garminfeed.models.Credentials

data class State(
    val credentials: Credentials? = null,
)