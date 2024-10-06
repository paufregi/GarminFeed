package paufregi.garminfeed.presentation.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.presentation.syncweight.SyncWeightState

class StatusPreview : PreviewParameterProvider<SyncWeightState?> {
    override val values: Sequence<SyncWeightState?> = sequenceOf(
        SyncWeightState.Uploading,
        SyncWeightState.Success,
        SyncWeightState.Failure,
        null
    )
}