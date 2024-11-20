package paufregi.connectfeed.presentation.profiles

import paufregi.connectfeed.core.models.Profile

data class ProfilesState(
    val profiles: List<Profile> = emptyList(),
)
