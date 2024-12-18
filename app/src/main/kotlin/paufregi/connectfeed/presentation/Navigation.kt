package paufregi.connectfeed.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Tune
import kotlinx.serialization.Serializable
import paufregi.connectfeed.presentation.ui.components.NavigationItem

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Profiles : Route

    @Serializable
    data class Profile(val id: Long = 0L) : Route
}

object Navigation {
    const val HOME = 0
    const val PROFILES = 1
    const val PROFILE = 1

    val items  = listOf(
        NavigationItem("Home", Icons.Filled.Home, Route.Home),
        NavigationItem("Profiles", Icons.Filled.Tune, Route.Profiles),
    )
}