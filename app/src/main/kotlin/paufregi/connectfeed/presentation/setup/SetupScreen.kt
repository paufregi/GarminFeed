package paufregi.connectfeed.presentation.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import paufregi.connectfeed.presentation.ui.components.Button
import paufregi.connectfeed.presentation.ui.components.Info
import paufregi.connectfeed.presentation.ui.components.Loading
import paufregi.connectfeed.presentation.ui.components.ProcessDisplay
import paufregi.connectfeed.presentation.ui.components.StatusInfo
import paufregi.connectfeed.presentation.ui.components.StatusInfoType

@Composable
@ExperimentalMaterial3Api
internal fun SetupScreen() {
    val viewModel = hiltViewModel<SetupViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Connect Feed") }) }
    ) { pv ->
        SetupContent(
            state = state,
            onEvent = viewModel::onEvent,
            paddingValues = pv,
        )
    }
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun SetupContent(
    @PreviewParameter(SetupFormStatePreview::class) state: SetupState,
    onEvent: (SetupEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    ProcessDisplay(
        state = state.processState,
        successInfo = { s -> Info(
            text = "Welcome ${s.message}" ,
            actionButton = { Button(text = "Ok", onClick = { onEvent(SetupEvent.Reset) } )}
        ) },
        failureInfo = { s -> Info(
            text = s.reason,
            actionButton = { Button(text = "Ok", onClick = { onEvent(SetupEvent.Reset) } )}
        ) }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            TextField(
                label = { Text("Username") },
                value = state.credential.username,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { onEvent(SetupEvent.SetUsername(it)) },
                isError = state.credential.username.isBlank(),
            )
            TextField(
                label = { Text("Password") },
                value = state.credential.password,
                onValueChange =  { onEvent(SetupEvent.SetPassword(it)) },
                isError = state.credential.password.isBlank(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (state.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Button(
                        onClick = { onEvent(SetupEvent.ShowPassword(!state.showPassword)) },
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
                    text = "Sign in",
                    enabled = state.credential.username.isNotBlank() && state.credential.password.isNotBlank(),
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onEvent(SetupEvent.SignIn)
                    }
                )
            }
        }
    }
}

