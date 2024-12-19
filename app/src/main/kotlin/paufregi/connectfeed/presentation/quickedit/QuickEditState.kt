package paufregi.connectfeed.presentation.quickedit

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.presentation.ui.models.ProcessState

data class QuickEditState(
    val process: ProcessState = ProcessState.Processing,
    val activities: List<Activity> = emptyList(),
    val profiles: List<Profile> = emptyList(),
    val activity: Activity? = null,
    val profile: Profile? = null,
    val water: Int? = null,
    val effort: Float? = null,
    val feel: Float? = null
)

