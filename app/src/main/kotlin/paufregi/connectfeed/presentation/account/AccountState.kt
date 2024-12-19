package paufregi.connectfeed.presentation.account

import paufregi.connectfeed.presentation.ui.models.ProcessState

data class AccountState(
    val process: ProcessState = ProcessState.Idle,
)
