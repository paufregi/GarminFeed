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
import paufregi.garminfeed.presentation.utils.Screen
import paufregi.garminfeed.presentation.ui.components.Button
import paufregi.garminfeed.presentation.utils.preview.SetupDonePreview

@Composable
@ExperimentalMaterial3Api
fun HomeScreen(
    nav: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val setUpDone by viewModel.setupDone.collectAsState()

    HomeContent(setUpDone, viewModel::clearCache, nav)
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun HomeContent(
    @PreviewParameter(SetupDonePreview::class) setUpDone: Boolean,
    onClearCache: () -> Unit = {},
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
            if (setUpDone) {
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
            Button(text = "Setup", onClick = { nav.navigate(Screen.Settings.route) })
            Spacer(modifier = Modifier.height(30.dp))
            Button(text = "Clear cache", onClick = onClearCache)
        }
    }
}

