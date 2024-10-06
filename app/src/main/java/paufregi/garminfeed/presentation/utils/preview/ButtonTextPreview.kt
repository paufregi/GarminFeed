package paufregi.garminfeed.presentation.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ButtonTextPreview : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Save", "Cancel", "Done"
    )
}