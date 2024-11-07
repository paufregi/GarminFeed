package paufregi.garminfeed.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TextEffort(effort: Float, modifier: Modifier = Modifier) {
    when(effort) {
        0f -> Text("None selected", modifier = modifier)
        1f -> Text("${effort.toInt()} - Very light", modifier = modifier)
        2f -> Text("${effort.toInt()} - Light", modifier = modifier)
        3f -> Text("${effort.toInt()} - Moderate", modifier = modifier)
        4f -> Text("${effort.toInt()} - Somewhat Hard", modifier = modifier)
        5f, 6f -> Text("${effort.toInt()} - Hard", modifier = modifier)
        7f, 8f -> Text("${effort.toInt()} - Very Hard", modifier = modifier)
        9f -> Text("${effort.toInt()} - Extremely Hard", modifier = modifier)
        10f -> Text("${effort.toInt()} - Maximum", modifier = modifier)
        else -> Text("${effort.toInt()} - What!?", modifier = modifier)
    }
}