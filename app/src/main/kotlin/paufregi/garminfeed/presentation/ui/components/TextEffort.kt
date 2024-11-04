package paufregi.garminfeed.presentation.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TextEffort(effort: Float) {
    when(effort) {
        0f -> Text("None selected")
        1f -> Text("${effort.toInt()} - Very light")
        2f -> Text("${effort.toInt()} - Light")
        3f -> Text("${effort.toInt()} - Moderate")
        4f -> Text("${effort.toInt()} - Somewhat Hard")
        5f, 6f -> Text("${effort.toInt()} - Hard")
        7f, 8f -> Text("${effort.toInt()} - Very Hard")
        9f -> Text("${effort.toInt()} - Extremely Hard")
        10f -> Text("${effort.toInt()} - Maximum")
        else -> Text("${effort.toInt()} - Whet!?")
    }
}