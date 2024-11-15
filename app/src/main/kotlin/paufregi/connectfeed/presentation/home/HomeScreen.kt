package paufregi.connectfeed.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import paufregi.connectfeed.presentation.ui.components.Button
import paufregi.connectfeed.presentation.main.MainRoutes

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun HomeScreen(
    @PreviewParameter(HomeStatePreview::class) state: HomeState,
    onEvent: (HomeEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier.padding(paddingValues),
        floatingActionButton = { if (state.setupDone) {
            FloatingActionButton(
                onClick = { nav.navigate(MainRoutes.QUICKEDIT) },
                modifier = Modifier.testTag("quickedit")
            ) {
                Icon(Icons.AutoMirrored.Default.DirectionsRun, "Edit activities")
            } } }
    ) { pv ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {
            if (state.setupDone) {
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
                    contentDescription = "Setup credential",
                    tint = Color.Red,
                    modifier = Modifier
                        .scale(3f)
                        .padding(30.dp)
                )
                Text(text = "Setup credential")
            }
            Spacer(modifier = Modifier.height(75.dp))
            Button(text = "Setup", onClick = { nav.navigate(MainRoutes.SETTINGS) })
            Spacer(modifier = Modifier.height(30.dp))
            Button(text = "Clear cache", onClick = { onEvent(HomeEvent.CleanCache) } )
        }
    }
}

