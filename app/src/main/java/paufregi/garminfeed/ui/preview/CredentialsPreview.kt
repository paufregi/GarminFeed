package paufregi.garminfeed.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.data.database.models.Credentials

class CredentialsPreview : PreviewParameterProvider<Credentials?> {
    override val values = sequenceOf(
        Credentials(username = "paul", password = "password"),
        null
    )
}