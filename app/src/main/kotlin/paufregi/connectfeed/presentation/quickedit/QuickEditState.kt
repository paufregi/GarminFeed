package paufregi.connectfeed.presentation.quickedit

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Profile

data class QuickEditState(
    val processing: ProcessState = ProcessState.Processing,
    val activities: List<Activity> = emptyList(),
    val profiles: List<Profile> = emptyList(),
    val activity: Activity? = null,
    val profile: Profile? = null,
    val effort: Float? = null,
    val feel: Float? = null
)

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Processing : ProcessState
    data object Success : ProcessState
    data object FailureLoading : ProcessState
    data object FailureUpdating : ProcessState
}
