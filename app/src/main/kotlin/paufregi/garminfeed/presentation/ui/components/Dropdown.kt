package paufregi.garminfeed.presentation.ui.components

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
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Profile

data class DropdownItem(
    val text: String,
    val leadingIcon: @Composable (() -> Unit)? = null,
    val onClick: () -> Unit
)

@ExperimentalMaterial3Api
fun Activity.toDropdownItem(onClick: () -> Unit) = DropdownItem(
    text = this.name,
    leadingIcon = { ActivityIcon(this.type) },
    onClick = onClick
)

@ExperimentalMaterial3Api
fun Profile.toDropdownItem(onClick: () -> Unit) = DropdownItem(
    text = this.activityName,
    leadingIcon = { ActivityIcon(this.activityType) },
    onClick = onClick
)

@Composable
@ExperimentalMaterial3Api
fun Dropdown(
    label: @Composable (() -> Unit)? = null,
    selected: DropdownItem? = null,
    items: List<DropdownItem> = emptyList()
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox (
        expanded = expanded,
        onExpandedChange = { expanded = it }
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