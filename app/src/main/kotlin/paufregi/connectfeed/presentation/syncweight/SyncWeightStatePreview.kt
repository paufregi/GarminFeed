package paufregi.connectfeed.presentation.syncweight

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SyncWeightStatePreview : PreviewParameterProvider<SyncWeightState> {
    override val values = sequenceOf(
        SyncWeightState.Idle,
        SyncWeightState.Uploading,
        SyncWeightState.Success,
        SyncWeightState.Failure,
    )
}