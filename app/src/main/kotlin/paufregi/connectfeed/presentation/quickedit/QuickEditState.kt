package paufregi.connectfeed.presentation.quickedit

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.presentation.utils.ProcessState

data class QuickEditState(
    val loading: ProcessState = ProcessState.Processing,
    val updating: ProcessState = ProcessState.Idle,
    val activities: List<Activity> = emptyList(),
    val allProfiles: List<Profile> = emptyList(),
    val availableProfiles: List<Profile> = emptyList(),
    val selectedActivity: Activity? = null,
    val selectedProfile: Profile? = null,
    val selectedEffort: Float = 0f,
    val selectedFeel: Float? = null
)
