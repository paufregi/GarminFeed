package paufregi.connectfeed.presentation.account

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

data class AccountState(
    val processing: ProcessState = ProcessState.Processing,
)

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Processing : ProcessState
    data object Success : ProcessState
    data class Failure(val reason: String) : ProcessState
}
