package paufregi.connectfeed.presentation.editprofile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

class EditProfileStatePreview : PreviewParameterProvider<EditProfileState> {
    override val values = sequenceOf(
        EditProfileState()
    )
}