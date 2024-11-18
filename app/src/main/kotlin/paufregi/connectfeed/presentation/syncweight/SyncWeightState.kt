package paufregi.connectfeed.presentation.syncweight

import paufregi.connectfeed.presentation.utils.ProcessState

data class SyncWeightState(
    val loading: ProcessState = ProcessState.Idle
)