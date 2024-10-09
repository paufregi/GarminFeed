package paufregi.garminfeed.presentation.utils.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ButtonIconPreview : PreviewParameterProvider<ImageVector> {
    override val values = sequenceOf(
        Icons.Default.Visibility,
        Icons.Default.Save
    )
}