package paufregi.garminfeed.presentation.settings

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.presentation.ui.components.Button

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun SettingsScreen(
    @PreviewParameter(SettingsStatePreview::class) state: SettingsState,
    onEvent: (SettingsEvent) -> Unit = {  },
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavController = rememberNavController()
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
                label = { Text("Username") },
                value = state.credential.username,
                onValueChange = { onEvent(SettingsEvent.UpdateUsername(it)) },
                isError = state.credential.username.isBlank(),
            )
            TextField(
                label = { Text("Password") },
                value = state.credential.password,
                onValueChange =  { onEvent(SettingsEvent.UpdateUsername(it)) },
                isError = state.credential.password.isBlank(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Button(
                        onClick = { onEvent(SettingsEvent.UpdateShowPassword(!state.showPassword)) },
                        icon = if (state.showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        description = if (state.showPassword) "Hide password" else "Show password"
                    )
                }
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(text = "Cancel", onClick = { nav.navigateUp() })
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    text = "Save",
                    enabled = state.credential.username.isNotBlank() && state.credential.password.isNotBlank(),
                    onClick = {
                        onEvent(SettingsEvent.SaveCredential)
                        nav.navigateUp()
                    }
                )
            }
        }
    }
}

