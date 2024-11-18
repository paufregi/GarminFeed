package paufregi.connectfeed.presentation.utils

sealed interface ProcessState {
    object Idle : ProcessState
    object Processing : ProcessState
    object Success : ProcessState
    object Failure : ProcessState
}
