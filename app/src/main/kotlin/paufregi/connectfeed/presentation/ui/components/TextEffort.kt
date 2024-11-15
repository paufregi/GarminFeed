package paufregi.connectfeed.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Preview
@Composable
fun TextEffort(
    @PreviewParameter(EffortPreview::class) effort: Float,
    modifier: Modifier = Modifier) {
    val score = (effort/10).toInt()
    val label = when(score) {
        0 -> "None selected"
        1 -> "Very light"
        2 -> "Light"
        3 -> "Moderate"
        4 -> "Somewhat Hard"
        5, 6 -> "Hard"
        7, 8 -> "Very Hard"
        9 -> "Extremely Hard"
        10 -> "Maximum"
        else -> "What!?"
    }
    Text("$score - $label", modifier = modifier)
}

private class EffortPreview : PreviewParameterProvider<Float> {
    override val values = sequenceOf(
        0f, 10f, 20f, 30f, 40f, 50f, 60f, 70f, 80f, 90f, 100f, 200f
    )
}