package paufregi.connectfeed.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.material3.IconButton as MaterialIconButton

@Preview
@Composable
@Suppress("PreviewShouldNotBeCalledRecursively")
fun Button(
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    @PreviewParameter(ButtonTextPreview ::class) text: String,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    MaterialButton(
        modifier = modifier,
        colors = colors,
        onClick = onClick,
        enabled = enabled
    ) {
       Text(text)
    }
}

@Preview
@Composable
fun Button(
    modifier: Modifier = Modifier,
    @PreviewParameter(ButtonIconPreview ::class) icon: ImageVector,
    description: String? = "",
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    MaterialIconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(icon, description)
    }
}

private class ButtonTextPreview : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Save", "Back", "Cancel"
    )
}

private class ButtonIconPreview : PreviewParameterProvider<ImageVector> {
    override val values = sequenceOf(
        Icons.Default.Visibility, Icons.Default.VisibilityOff
    )
}