package paufregi.connectfeed.presentation.syncweight

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.presentation.utils.ProcessState

class SyncWeightStatePreview : PreviewParameterProvider<SyncWeightState> {
    override val values = sequenceOf(
        SyncWeightState(ProcessState.Idle),
        SyncWeightState(ProcessState.Processing),
        SyncWeightState(ProcessState.Success),
        SyncWeightState(ProcessState.Failure),
    )
}