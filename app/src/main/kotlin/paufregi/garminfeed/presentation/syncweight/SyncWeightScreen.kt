package paufregi.garminfeed.presentation.syncweight

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import paufregi.garminfeed.presentation.ui.components.Loading
import paufregi.garminfeed.presentation.ui.components.StatusInfo
import paufregi.garminfeed.presentation.ui.components.StatusInfoType

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun SyncWeightScreen(
    @PreviewParameter(SyncWeightStatePreview::class) state: SyncWeightState?,
    onComplete: () -> Unit = {},
) {
    Scaffold {
        when (state) {
            is SyncWeightState.Idle -> StatusInfo(
                type = StatusInfoType.Waiting,
                text = "Waiting",
                onClick = onComplete,
                contentPadding = it)
            is SyncWeightState.Uploading -> Loading(it)
            is SyncWeightState.Success -> StatusInfo(
                type = StatusInfoType.Success,
                text = "Sync succeeded",
                onClick = onComplete,
                contentPadding = it)
            is SyncWeightState.Failure -> StatusInfo(
                type = StatusInfoType.Failure,
                text = "Sync failed",
                onClick = onComplete,
                contentPadding = it)
            else -> StatusInfo(
                type = StatusInfoType.Unknown,
                text = "Don't know what to do",
                onClick = onComplete,
                contentPadding = it)
        }
    }
}
