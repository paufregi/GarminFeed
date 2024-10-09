package paufregi.garminfeed.presentation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import paufregi.garminfeed.presentation.utils.preview.ButtonIconPreview
import paufregi.garminfeed.presentation.utils.preview.ButtonTextPreview
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.material3.IconButton as MaterialIconButton

@Preview
@Composable
@Suppress("PreviewShouldNotBeCalledRecursively")
fun Button(
    @PreviewParameter(ButtonTextPreview::class) text: String,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    MaterialButton(onClick, enabled = enabled) {
       Text(text)
    }
}

@Preview
@Composable
fun Button(
    @PreviewParameter(ButtonIconPreview::class) icon: ImageVector,
    description: String? = "",
    onClick: () -> Unit = {}
) {
    MaterialIconButton(onClick) {
        Icon(icon, description)
    }
}
