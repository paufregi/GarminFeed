package paufregi.garminfeed.presentation.quickedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.presentation.ui.components.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import paufregi.garminfeed.core.models.ActivityType
import paufregi.garminfeed.presentation.ui.components.Loading

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun QuickEditScreen(
    @PreviewParameter(QuickEditStatePreview ::class) state: QuickEditState,
    onEvent: (QuickEditEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavController = rememberNavController()
) {
    var activityExpanded by remember { mutableStateOf(false) }
    var profileExpanded by remember { mutableStateOf(false) }

    @Composable
    fun typeToIcon(activityType: ActivityType?) = when (activityType) {
        is ActivityType.Running -> Icon(Icons.AutoMirrored.Default.DirectionsRun, null)
        is ActivityType.Cycling -> Icon(Icons.AutoMirrored.Default.DirectionsBike, null)
        else -> null
    }

    if (state.loading) {
        Loading(paddingValues)
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                ExposedDropdownMenuBox(
                    expanded = activityExpanded,
                    onExpandedChange = { activityExpanded = it }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).testTag("activityDropDown"),
                        label = { Text("Activity") },
                        value = state.selectedActivity?.name ?: "",
                        leadingIcon = { typeToIcon(state.selectedActivity?.type) },
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = activityExpanded,
                        onDismissRequest = { activityExpanded = false },
                    ){
                        state.activities.forEach{
                            DropdownMenuItem(
                                text = { Text(it.name) },
                                leadingIcon = { typeToIcon(it.type) },
                                onClick = {
                                    onEvent(QuickEditEvent.SelectActivity(it))
                                    activityExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = profileExpanded,
                    onExpandedChange = { profileExpanded = it },
                    modifier = Modifier.testTag("profileDropDown")
                    ) {
                    TextField(
                        modifier = Modifier. menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = state.selectedProfile?.activityName ?: "",
                        label = { Text("Profile") },
                        leadingIcon = { typeToIcon(state.selectedProfile?.activityType) },
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults. TrailingIcon(expanded = profileExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = profileExpanded,
                        onDismissRequest = { profileExpanded = false },
                    ){
                        state.availableProfiles.forEach{
                            DropdownMenuItem(
                                text = { Text(it.activityName) },
                                leadingIcon = { typeToIcon(it.activityType) },
                                onClick = {
                                    onEvent(QuickEditEvent.SelectProfile(it))
                                    profileExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(text = "Cancel", onClick = { nav.navigateUp() })
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        text = "Save",
                        enabled = state.selectedActivity != null && state.selectedProfile != null,
                        onClick = { onEvent(QuickEditEvent.Save({ nav.navigateUp() })) }
                    )
                }
            }
        }
    }
}

