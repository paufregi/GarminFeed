package paufregi.connectfeed.presentation.ui.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

data class DropdownItem(
    val text: String,
    val leadingIcon: @Composable (() -> Unit)? = null,
    val onClick: () -> Unit
)

@ExperimentalMaterial3Api
fun Activity.toDropdownItem(onClick: () -> Unit) = DropdownItem(
    text = name,
    leadingIcon = { ActivityIcon(this.type) },
    onClick = onClick
)

@ExperimentalMaterial3Api
fun ActivityType.toDropdownItem(onClick: () -> Unit) = DropdownItem(
    text = name,
    leadingIcon = { ActivityIcon(this) },
    onClick = onClick
)

@ExperimentalMaterial3Api
fun EventType.toDropdownItem(onClick: () -> Unit) = DropdownItem(
    text = name,
    onClick = onClick
)

@ExperimentalMaterial3Api
fun Course.toDropdownItem(onClick: () -> Unit) = DropdownItem(
    text = name,
    leadingIcon = { ActivityIcon(type) },
    onClick = onClick
)

@ExperimentalMaterial3Api
fun Profile.toDropdownItem(onClick: () -> Unit) = DropdownItem(
    text = this.name,
    leadingIcon = { ActivityIcon(activityType) },
    onClick = onClick
)

@Composable
@ExperimentalMaterial3Api
fun Dropdown(
    label: @Composable (() -> Unit)? = null,
    selected: DropdownItem? = null,
    items: List<DropdownItem> = emptyList(),
    enabled: Boolean = true,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox (
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            label = label,
            value = selected?.text ?: "",
            leadingIcon = selected?.leadingIcon,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            enabled = enabled,
            isError = isError,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ){
            items.forEach{
                DropdownMenuItem(
                    text = { Text(it.text) },
                    leadingIcon = it.leadingIcon,
                    onClick = {
                        it.onClick()
                        expanded = false
                    }
                )
            }
        }
    }
}