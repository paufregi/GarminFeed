package paufregi.connectfeed.core.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val avatarUrl: String,
)
