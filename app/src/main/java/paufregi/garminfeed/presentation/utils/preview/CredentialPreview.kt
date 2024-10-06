package paufregi.garminfeed.presentation.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.core.models.Credential

class CredentialPreview : PreviewParameterProvider<Credential?> {
    override val values = sequenceOf(
        Credential(username = "paul", password = "password"),
        null
    )
}