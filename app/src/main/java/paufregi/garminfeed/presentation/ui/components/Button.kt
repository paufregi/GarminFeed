package paufregi.garminfeed.presentation.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Button as MaterialButton
import androidx.compose.material3.IconButton as MaterialIconButton

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    MaterialButton(onClick = onClick, enabled = enabled) {
        Text(text = text)
    }
}

@Composable
fun Button(
    icon: ImageVector,
    description: String?,
    onClick: () -> Unit
) {
    MaterialIconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = description)
    }
}
