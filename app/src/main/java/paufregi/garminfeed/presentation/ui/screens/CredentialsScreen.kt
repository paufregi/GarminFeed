package paufregi.garminfeed.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.presentation.ui.components.Button
import paufregi.garminfeed.presentation.utils.preview.CredentialsPreview

@Preview
@Composable
@ExperimentalMaterial3Api
fun CredentialsScreen(
    @PreviewParameter(CredentialsPreview::class) credentials: Credentials?,
    onSave: (Credentials) -> Unit = {},
    nav: NavController = rememberNavController(),
) {
    var username by remember { mutableStateOf(credentials?.username ?: "") }
    var password by remember { mutableStateOf(credentials?.password ?: "") }
    var showPassword by remember { mutableStateOf(false) }
    Scaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.width(IntrinsicSize.Min)
            ) {
                TextField(
                    label = { Text("Username") },
                    value = username,
                    isError = username.isBlank(),
                    onValueChange = { username = it }
                )
                TextField(
                    label = { Text("Password") },
                    value = password,
                    isError = password.isBlank(),
                    onValueChange = { password = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Button(
                            onClick = { showPassword = !showPassword },
                            icon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            description = if (showPassword) "Hide password" else "Show password"
                        )
                    }
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(text = "Cancel", onClick = { nav.navigateUp() })
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        text = "Save",
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        onClick = {
                            onSave(
                                Credentials(
                                    username = username,
                                    password = password
                                )
                            )
                            nav.navigateUp()
                        }
                    )
                }
            }
        }
    }
}
