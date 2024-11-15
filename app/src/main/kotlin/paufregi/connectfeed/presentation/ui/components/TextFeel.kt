package paufregi.connectfeed.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Preview
@Composable
fun TextFeel(
    @PreviewParameter(FeePreview::class) feel: Float?, modifier: Modifier = Modifier) {
    val label = when(feel) {
        0f -> "Very weak"
        25f -> "Weak"
        50f -> "Normal"
        75f -> "Strong"
        100f -> "Very strong"
        null -> "None selected"
        else -> "What!?"
    }
    Text(label, modifier = modifier)
}

private class FeePreview : PreviewParameterProvider<Float?> {
    override val values = sequenceOf(
        0f, 25f, 50f, 75f, 100f, null, 200f
    )
}