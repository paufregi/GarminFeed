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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import paufregi.connectfeed.presentation.quickedit.QuickEditEvent
import paufregi.connectfeed.presentation.ui.components.Button
import paufregi.connectfeed.presentation.ui.components.Dropdown
import paufregi.connectfeed.presentation.ui.components.toDropdownItem

@Composable
@ExperimentalMaterial3Api
internal fun EditProfileScreen(
    paddingValues: PaddingValues = PaddingValues(),
) {
    val viewModel = hiltViewModel<EditProfileViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    EditProfileContent(
        state = state,
        onEvent = viewModel::onEvent,
        paddingValues = paddingValues,
    )
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun EditProfileContent(
    @PreviewParameter(EditProfileStatePreview::class) state: EditProfileState,
    onEvent: (EditProfileEvent) -> Unit = {  },
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
            Row {
                TextField(
                    label = { Text("Name") },
                    value = state.profile.name,
                    onValueChange = { onEvent(EditProfileEvent.UpdateName(it)) },
                    isError = state.profile.name.isBlank(),
                )
                Column {
                    Checkbox(
                        checked = state.profile.updateName,
                        onCheckedChange = { onEvent(EditProfileEvent.UpdateUpdateName(it)) },
                    )
                    Text("Update name")
                }
            }
            Dropdown(
                label = { Text("Activity") },
                selected = state.profile.activityType.toDropdownItem { },
                items = state.availableActivityType.map {
                    it.toDropdownItem {
                        onEvent(EditProfileEvent.UpdateActivityType(it))
                    }
                }
            )
            Dropdown(
                label = { Text("Event type") },
                selected = state.profile.eventType?.toDropdownItem { },
                items = state.availableEventType.map {
                    it.toDropdownItem {
                        onEvent(EditProfileEvent.UpdateEventType(it))
                    }
                }
            )
            Dropdown(
                label = { Text("Course") },
                selected = state.profile.course?.toDropdownItem { },
                items = state.availableCourses.map {
                    it.toDropdownItem {
                        onEvent(EditProfileEvent.UpdateCourse(it))
                    }
                }
            )
            TextField(
                label = { Text("Water") },
                value = state.profile.water.toString(),
                onValueChange = { onEvent(EditProfileEvent.UpdateWater(it.toInt())) },

            )

            TextField(
                label = { Text("Password") },
                value = state.credential.password,
                onValueChange =  { onEvent(EditProfileEvent.UpdatePassword(it)) },
                isError = state.credential.password.isBlank(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Button(
                        onClick = { onEvent(EditProfileEvent.UpdateShowPassword(!state.showPassword)) },
                        icon = if (state.showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        description = if (state.showPassword) "Hide password" else "Show password"
                    )
                }
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    text = "Save",
                    enabled = state.credential.username.isNotBlank() && state.credential.password.isNotBlank(),
                    onClick = { onEvent(EditProfileEvent.SaveCredential) }
                )
            }
        }
    }
}

