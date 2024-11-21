package paufregi.connectfeed.presentation.editprofile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.Credential

class EditProfileStatePreview : PreviewParameterProvider<EditProfileState> {
    override val values = sequenceOf(
        EditProfileState(Credential("paul", "pass"), false),
        EditProfileState(Credential("paul", "pass"), true),
        EditProfileState(Credential("", ""), false)
    )
}