package paufregi.connectfeed.presentation.syncweight

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import paufregi.connectfeed.presentation.ui.components.Button
import paufregi.connectfeed.presentation.ui.components.Loading
import paufregi.connectfeed.presentation.ui.components.StatusInfo
import paufregi.connectfeed.presentation.ui.components.StatusInfoType

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun SyncWeightScreen(
    @PreviewParameter(SyncWeightStatePreview::class) state: SyncWeightState,
    onComplete: () -> Unit = {},
) {
    Scaffold {
        when (state) {
            is SyncWeightState.Uploading -> Loading(it)
            is SyncWeightState.Success -> StatusInfo(
                type = StatusInfoType.Success,
                text = "Sync succeeded",
                actionButton = { Button(text = "Ok", onClick = onComplete) },
                paddingValues = it)
            is SyncWeightState.Failure -> StatusInfo(
                type = StatusInfoType.Failure,
                text = "Sync failed",
                actionButton = { Button(text = "Ok", onClick = onComplete) },
                paddingValues = it)
            else -> StatusInfo(
                type = StatusInfoType.Unknown,
                text = "Don't know what to do",
                actionButton = { Button(text = "Ok", onClick = onComplete) },
                paddingValues = it)
        }
    }
}
