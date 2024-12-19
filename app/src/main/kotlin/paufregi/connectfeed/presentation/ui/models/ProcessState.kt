package paufregi.connectfeed.presentation.ui.models

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Processing : ProcessState
    data class Success(val message: String) : ProcessState
    data class Failure(val reason: String) : ProcessState
}