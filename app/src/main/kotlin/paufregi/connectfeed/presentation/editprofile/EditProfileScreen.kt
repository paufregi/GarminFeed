package paufregi.connectfeed.presentation.editprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.presentation.ui.components.Button
import paufregi.connectfeed.presentation.ui.components.Dropdown
import paufregi.connectfeed.presentation.ui.components.Loading
import paufregi.connectfeed.presentation.ui.components.StatusInfo
import paufregi.connectfeed.presentation.ui.components.StatusInfoType
import paufregi.connectfeed.presentation.ui.components.toDropdownItem

@Composable
@ExperimentalMaterial3Api
internal fun EditProfileScreen(
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavHostController = rememberNavController()
) {
    val viewModel = hiltViewModel<EditProfileViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditProfileContent(
        state = state,
        onEvent = viewModel::onEvent,
        paddingValues = paddingValues,
        nav = nav
    )
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun EditProfileContent(
    @PreviewParameter(EditProfileStatePreview ::class) state: EditProfileState,
    onEvent: (EditProfileEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavHostController = rememberNavController()
) {
    when (state.processing) {
        is ProcessState.Processing -> Loading(paddingValues)
        is ProcessState.FailureLoading -> StatusInfo(
            type = StatusInfoType.Failure,
            text = state.processing.reason,
            actionButton = { Button(text = "Ok", onClick = { nav.navigateUp() } )},
            contentPadding = paddingValues)
        is ProcessState.FailureUpdating -> StatusInfo(
            type = StatusInfoType.Failure,
            text = "Couldn't save profile",
            actionButton = { Button(text = "Ok", onClick = { nav.navigateUp() } )},
            contentPadding = paddingValues)
        is ProcessState.Success -> StatusInfo(
            type = StatusInfoType.Success,
            text = "Profile saved",
            actionButton = { Button(text = "Ok", onClick = { nav.navigateUp() } )},
            contentPadding = paddingValues)
        else -> EditProfileForm(state, onEvent, paddingValues)
    }
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun EditProfileForm(
    @PreviewParameter(EditProfileStatePreview::class) state: EditProfileState,
    onEvent: (EditProfileEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
) {
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
            TextField(
                label = { Text("Name") },
                value = state.profile.name,
                onValueChange = { onEvent(EditProfileEvent.SetName(it)) },
                isError = state.profile.name.isBlank(),
            )

            Dropdown(
                label = { Text("Activity Type") },
                selected = state.profile.activityType.toDropdownItem { },
                items = state.availableActivityType.map {
                    it.toDropdownItem { onEvent(EditProfileEvent.SetActivityType(it)) }
                }
            )
            Dropdown(
                label = { Text("Event type") },
                selected = state.profile.eventType?.toDropdownItem { },
                items = state.availableEventType.map {
                    it.toDropdownItem {
                        onEvent(EditProfileEvent.SetEventType(it))
                    }
                },
                isError = state.profile.activityType != ActivityType.Any && state.profile.eventType == null

            )
            Dropdown(
                label = { Text("Course") },
                selected = state.profile.course?.toDropdownItem { },
                items = state.availableCourses.map {
                    it.toDropdownItem {
                        onEvent(EditProfileEvent.SetCourse(it))
                    }
                }
            )
            TextField(
                label = { Text("Water") },
                value = state.profile.water?.toString() ?: "",
                onValueChange = { if (it.isDigitsOnly()) onEvent(EditProfileEvent.SetWater(it.toInt())) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.profile.rename,
                    onCheckedChange = { onEvent(EditProfileEvent.SetRename(it)) },
                )
                Text("Rename activity")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.profile.customWater,
                    onCheckedChange = { onEvent(EditProfileEvent.SetCustomWater(it)) },
                )
                Text("Customizable water")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = state.profile.feelAndEffort,
                    onCheckedChange = { onEvent(EditProfileEvent.SetFeelAndEffort(it)) },
                )
                Text("Feel & Effort")
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "Save",
                    enabled = state.profile.name.isNotBlank() &&
                            (state.profile.activityType == ActivityType.Any || state.profile.eventType != null),
                    onClick = { onEvent(EditProfileEvent.Save) }
                )
            }
        }
    }
}

