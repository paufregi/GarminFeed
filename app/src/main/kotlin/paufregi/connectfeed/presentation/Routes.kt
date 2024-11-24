package paufregi.connectfeed.presentation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    object Home : Routes

    @Serializable
    object Settings : Routes

    @Serializable
    object QuickEdit : Routes

    @Serializable
    object Profiles : Routes

    @Serializable
    object EditProfile : Routes
}
