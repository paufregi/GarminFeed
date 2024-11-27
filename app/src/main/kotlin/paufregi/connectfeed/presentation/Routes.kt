package paufregi.connectfeed.presentation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Home : Routes

    @Serializable
    data object Settings : Routes

    @Serializable
    data object Profiles : Routes

    @Serializable
    data class EditProfile(val id: Long = 0L) : Routes
}
