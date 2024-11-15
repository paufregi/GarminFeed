package paufregi.connectfeed.presentation.quickedit

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Profile

data class QuickEditState(
    val loading: Boolean = false,
    val activities: List<Activity> = emptyList(),
    val allProfiles: List<Profile> = emptyList(),
    val availableProfiles: List<Profile> = emptyList(),
    val selectedActivity: Activity? = null,
    val selectedProfile: Profile? = null,
    val selectedEffort: Float = 0f,
    val selectedFeel: Float? = null
)
