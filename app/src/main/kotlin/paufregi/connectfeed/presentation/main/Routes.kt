package paufregi.connectfeed.presentation.main

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
}
