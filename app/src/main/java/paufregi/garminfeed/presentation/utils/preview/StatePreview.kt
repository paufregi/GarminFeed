package paufregi.garminfeed.presentation.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.core.models.State

class StatePreview : PreviewParameterProvider<State> {
    override val values = sequenceOf(
        State(
            credentials = Credentials(username = "paul", password = "password")
        ),
        State(
            credentials = null
        )
    )
}