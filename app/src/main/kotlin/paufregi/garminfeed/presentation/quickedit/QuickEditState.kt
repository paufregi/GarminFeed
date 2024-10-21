package paufregi.garminfeed.presentation.quickedit

import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Profile

data class QuickEditState(
    val loading: Boolean = false,
    val activities: List<Activity> = emptyList(),
    val profiles: List<Profile> = emptyList()
)