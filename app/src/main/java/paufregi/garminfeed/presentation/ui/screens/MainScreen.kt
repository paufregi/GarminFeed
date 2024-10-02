package paufregi.garminfeed.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import paufregi.garminfeed.Screen
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.presentation.ui.components.Button
import paufregi.garminfeed.presentation.preview.CredentialsPreview

@Preview
@Composable
@ExperimentalMaterial3Api
fun MainScreen(
    @PreviewParameter(CredentialsPreview::class) credentials: Credentials?,
    clearCache: () -> Unit = {},
    nav: NavController = rememberNavController(),
) {
    Scaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (credentials != null) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = "All done",
                    tint = Color.Green,
                    modifier = Modifier
                        .scale(3f)
                        .padding(30.dp)
                )
                Text(text = "All good")
            } else {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Setup credentials",
                    tint = Color.Red,
                    modifier = Modifier
                        .scale(3f)
                        .padding(30.dp)
                )
                Text(text = "Setup credentials")
            }
            Spacer(modifier = Modifier.height(75.dp))
            Button(text = "Setup", onClick = { nav.navigate(Screen.Credentials.route) })
            Spacer(modifier = Modifier.height(30.dp))
            Button(text = "Clear cache", onClick = clearCache)
        }
    }
}
