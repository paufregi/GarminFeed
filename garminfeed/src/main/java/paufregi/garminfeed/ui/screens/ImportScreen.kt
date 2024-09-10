package paufregi.garminfeed.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import kotlinx.coroutines.flow.MutableStateFlow
import paufregi.garminfeed.models.Status
import paufregi.garminfeed.ui.components.Loading
import paufregi.garminfeed.ui.components.StatusInfo
import paufregi.garminfeed.ui.preview.StatusPreview

@Preview
@Composable
@ExperimentalMaterial3Api
fun ImportScreen(
    @PreviewParameter(StatusPreview::class) status: MutableStateFlow<Status?>,
    onComplete: () -> Unit = {},
) {
    val statusState = status.collectAsState()

    Scaffold {
        when (statusState.value) {
            is Status.Uploading -> Loading(it)
            is Status.Success -> StatusInfo.Success(onClick = onComplete, contentPadding = it)
            is Status.Failure -> StatusInfo.Failure(onClick = onComplete, contentPadding = it)
            else -> StatusInfo.Unknown(onClick = onComplete, contentPadding = it)
        }
    }
}
