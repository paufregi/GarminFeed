package paufregi.connectfeed.presentation.profiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import paufregi.connectfeed.presentation.ui.components.ActivityIcon

@Composable
@ExperimentalMaterial3Api
internal fun ProfilesScreen(
    paddingValues: PaddingValues = PaddingValues(),
) {
    val viewModel = hiltViewModel<ProfilesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfilesContent(
        state = state,
        paddingValues = paddingValues,
    )
}

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun ProfilesContent(
    @PreviewParameter(ProfilesStatePreview::class) state: ProfilesState,
    paddingValues: PaddingValues = PaddingValues(),
) {
    Scaffold(
        modifier = Modifier.padding(paddingValues),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                modifier = Modifier.testTag("addProfile")
            ) {
                Icon(Icons.Default.Add, "Create profile")
            } }
    ) { pv ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .verticalScroll(rememberScrollState())
        ) {
            state.profiles.fastForEachIndexed { index, it ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.padding(10.dp),
                    ) {
                        ActivityIcon(it.activityType)
                        Text(it.name)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }
    }
    
}
