package paufregi.garminfeed.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.presentation.ui.components.Button
import paufregi.garminfeed.presentation.utils.preview.SettingsStatePreview

@Composable
@ExperimentalMaterial3Api
fun SettingsScreen(
    nav: NavController = rememberNavController(),
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsContent(state = viewModel.state, onSave = viewModel::saveCredential, nav = nav)
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun SettingsContent(
    @PreviewParameter(SettingsStatePreview::class) state: MutableState<SettingsState>,
    onSave: () -> Unit = {  },
    nav: NavController = rememberNavController()
) {

    Scaffold { paddingValues ->
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
                    label = { Text("Username") },
                    value = state.value.username,
                    onValueChange = { state.value = state.value.copy(username = it) },
                    isError = state.value.username.isBlank(),
                )
                TextField(
                    label = { Text("Password") },
                    value = state.value.password,
                    onValueChange = { state.value = state.value.copy(password = it) },
                    isError = state.value.password.isBlank(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (state.value.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Button(
                            onClick = { state.value = state.value.copy(showPassword = !state.value.showPassword) },
                            icon = if (state.value.showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            description = if (state.value.showPassword) "Hide password" else "Show password"
                        )
                    }
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(text = "Cancel", onClick = { nav.navigateUp() })
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        text = "Save",
                        enabled = state.value.username.isNotBlank() && state.value.password.isNotBlank(),
                        onClick = {
                            onSave()
                            nav.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

