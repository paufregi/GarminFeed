package paufregi.connectfeed.presentation.syncweight

sealed class SyncWeightState {
    data object Idle : SyncWeightState()
    data object Uploading : SyncWeightState()
    data object Success : SyncWeightState()
    data object Failure : SyncWeightState()
}