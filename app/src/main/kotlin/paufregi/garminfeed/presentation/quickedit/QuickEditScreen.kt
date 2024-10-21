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
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.ActivityType

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun QuickEditScreen(
    @PreviewParameter(QuickEditStatePreview ::class) state: QuickEditState,
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavController = rememberNavController()
) {
    val (actExpanded, setActExpanded) = remember { mutableStateOf(false) }
    var actSelected by remember { mutableStateOf<Activity?>(null) }
    val activities = listOf(
        Activity(123L, "Auckland ride", ActivityType.Cycling),
        Activity(456L, "Auckland run", ActivityType.Running),
    )

    val (profileExpanded, setProfileExpanded) = remember { mutableStateOf(false) }
    var profileSelected by remember { mutableStateOf<Profile?>(null) }

    val byTypePredicate: (Profile) -> Boolean = { profile ->
        actSelected == null || profile.activityType == actSelected?.type
    }

    fun typeToIcon(profile: ActivityType) = when (profile) {
        is ActivityType.Running -> Icons.AutoMirrored.Default.DirectionsRun
        is ActivityType.Cycling -> Icons.AutoMirrored.Default.DirectionsBike
        else -> error("Unknown profile: $profile")
    }

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
                expanded = actExpanded,
                onExpandedChange = setActExpanded
            ) {
                TextField(
                    modifier = Modifier. menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    value = actSelected?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults. TrailingIcon(expanded = actExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = actExpanded,
                    onDismissRequest = { setActExpanded(false) },
                ){
                    activities.forEach{
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            leadingIcon = { Icon(typeToIcon(it.type), null) },
                            onClick = {
                                actSelected = it
                                if (it.type != profileSelected?.activityType) {
                                    profileSelected = null
                                }
                                setActExpanded(false)
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = profileExpanded,
                onExpandedChange = setProfileExpanded,

            ) {
                TextField(
                    modifier = Modifier. menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    value = profileSelected?.activityName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults. TrailingIcon(expanded = profileExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = profileExpanded,
                    onDismissRequest = { setProfileExpanded(false) },
                ){
                    Profile.presets.filter(byTypePredicate).forEach{
                        DropdownMenuItem(
                            text = { Text(it.activityName) },
                            leadingIcon = { Icon(typeToIcon(it.activityType), null) },
                            onClick = {
                                profileSelected = it
                                setProfileExpanded(false)
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
                    onClick = {
                        nav.navigateUp()
                    }
                )
            }
        }
    }
}

