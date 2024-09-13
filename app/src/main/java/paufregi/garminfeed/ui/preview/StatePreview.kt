package paufregi.garminfeed.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.lifecycle.State
import paufregi.garminfeed.models.Credentials

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