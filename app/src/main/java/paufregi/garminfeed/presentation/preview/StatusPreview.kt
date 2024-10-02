package paufregi.garminfeed.presentation.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.core.models.ImportStatus

class StatusPreview : PreviewParameterProvider<ImportStatus?> {
    override val values: Sequence<ImportStatus?> = sequenceOf(
        ImportStatus.Uploading,
        ImportStatus.Success,
        ImportStatus.Failure,
        null
    )
}