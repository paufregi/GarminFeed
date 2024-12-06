package paufregi.connectfeed.presentation.profiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import paufregi.connectfeed.presentation.Routes
import paufregi.connectfeed.presentation.ui.components.ActivityIcon
import paufregi.connectfeed.presentation.ui.components.Button

@Composable
@ExperimentalMaterial3Api
internal fun ProfilesScreen(
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavHostController,
) {
    val viewModel = hiltViewModel<ProfilesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfilesContent(
        state = state,
        onEvent = viewModel::onEvent,
        paddingValues = paddingValues,
        nav = nav
    )
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun ProfilesContent(
    @PreviewParameter(ProfilesStatePreview::class) state: ProfilesState,
    onEvent: (ProfileEvent) -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
    nav: NavHostController = rememberNavController(),
) {
    Scaffold(
        modifier = Modifier.padding(paddingValues),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { nav.navigate(Routes.EditProfile()) },
                modifier = Modifier.testTag("create_profile")
            ) {
                Icon(Icons.Default.Add, "Create profile")
            } }
    ) { pv ->
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(top = 40.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.profiles.isEmpty()) {
                Text("No profile")
            }
            state.profiles.fastForEachIndexed { index, it ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable(onClick = { nav.navigate(Routes.EditProfile(it.id)) })
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.padding(10.dp),
                    ) {
                        ActivityIcon(it.activityType)
                        Text(it.name)
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            icon = Icons.Default.Delete,
                            onClick = { onEvent(ProfileEvent.Delete(it)) },
                            modifier = Modifier.testTag("delete_profile_${it.id}")
                        )
                    }
                }
            }
        }
    }
}
