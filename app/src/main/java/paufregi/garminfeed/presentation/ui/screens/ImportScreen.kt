package paufregi.garminfeed.presentation.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import paufregi.garminfeed.core.models.ImportStatus
import paufregi.garminfeed.presentation.ui.components.Loading
import paufregi.garminfeed.presentation.ui.components.StatusInfo
import paufregi.garminfeed.presentation.preview.StatusPreview

@Preview
@Composable
@ExperimentalMaterial3Api
fun ImportScreen(
    @PreviewParameter(StatusPreview::class) status: ImportStatus?,
    onComplete: () -> Unit = {},
) {
    Scaffold {
        when (status) {
            is ImportStatus.Uploading -> Loading(it)
            is ImportStatus.Success -> StatusInfo.Success(onClick = onComplete, contentPadding = it)
            is ImportStatus.Failure -> StatusInfo.Failure(onClick = onComplete, contentPadding = it)
            else -> StatusInfo.Unknown(onClick = onComplete, contentPadding = it)
        }
    }
}
