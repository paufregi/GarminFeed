package paufregi.garminfeed.presentation.ui.components

import androidx.compose.runtime.Composable

data class RadioButtonItem<T>(
    val value: T,
    val label: String,
)

@Composable
fun <T>RadioButtonGroup(
    selected: RadioButtonItem<T>,
    options: List<RadioButtonItem<T>>,
    on
) {
    options.forEach { option ->
        RadioButton(
            selected = selected == option,
            onClick = { /*TODO*/ },
            text = option.label,
        )
    }
}