package paufregi.connectfeed.presentation.quickedit

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Profile

data class QuickEditState(
    val processing: ProcessState = ProcessState.Processing,
    val activities: List<Activity> = emptyList(),
    val allProfiles: List<Profile> = emptyList(),
    val availableProfiles: List<Profile> = emptyList(),
    val selectedActivity: Activity? = null,
    val selectedProfile: Profile? = null,
    val selectedEffort: Float? = null,
    val selectedFeel: Float? = null
)

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Processing : ProcessState
    data object Success : ProcessState
    data object FailureLoading : ProcessState
    data object FailureUpdating : ProcessState
}
