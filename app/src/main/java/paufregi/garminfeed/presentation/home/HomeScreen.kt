package paufregi.garminfeed.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.presentation.utils.preview.CredentialsPreview
import paufregi.garminfeed.presentation.utils.Screen
import paufregi.garminfeed.presentation.ui.components.Button

@Composable
@ExperimentalMaterial3Api
fun HomeScreen(
    nav: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeContent(
        state.credentials,
        () -> viewModel.clearCache(),
        nav
    )
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun HomeContent(
    @PreviewParameter(CredentialsPreview::class) credentials: Credentials?,
    onClearCache: Job = {},
    nav: NavController = rememberNavController()
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
            Button(text = "Clear cache", onClick = onClearCache)
        }
    }
}

