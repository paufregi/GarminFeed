package paufregi.connectfeed.presentation.profiles

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ProfilesStatePreview : PreviewParameterProvider<ProfilesState> {
    override val values = sequenceOf(
        ProfilesState("test"),
    )
}