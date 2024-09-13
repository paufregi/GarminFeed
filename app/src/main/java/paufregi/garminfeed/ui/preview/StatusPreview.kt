package paufregi.garminfeed.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.flow.MutableStateFlow
import paufregi.garminfeed.models.Status

class StatusPreview : PreviewParameterProvider<MutableStateFlow<Status?>> {
    override val values: Sequence<MutableStateFlow<Status?>> = sequenceOf(
        MutableStateFlow(Status.Uploading),
        MutableStateFlow(Status.Success),
        MutableStateFlow(Status.Failure),
        MutableStateFlow(null)
    )
}