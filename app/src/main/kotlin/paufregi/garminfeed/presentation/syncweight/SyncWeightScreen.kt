package paufregi.garminfeed.presentation.syncweight

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import paufregi.garminfeed.presentation.ui.components.Loading
import paufregi.garminfeed.presentation.ui.components.StatusInfo
import paufregi.garminfeed.presentation.utils.preview.SyncWeightStatePreview

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun SyncWeightScreen(
    @PreviewParameter(SyncWeightStatePreview::class) status: SyncWeightState?,
    onComplete: () -> Unit = {},
) {
    Scaffold {
        when (status) {
            is SyncWeightState.Uploading -> Loading(it)
            is SyncWeightState.Success -> StatusInfo.Success(onClick = onComplete, contentPadding = it)
            is SyncWeightState.Failure -> StatusInfo.Failure(onClick = onComplete, contentPadding = it)
            else -> StatusInfo.Unknown(onClick = onComplete, contentPadding = it)
        }
    }
}

