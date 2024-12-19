package paufregi.connectfeed.presentation.account

import paufregi.connectfeed.presentation.ui.components.ProcessState

data class AccountState(
    val processState: ProcessState = ProcessState.Processing,
)
