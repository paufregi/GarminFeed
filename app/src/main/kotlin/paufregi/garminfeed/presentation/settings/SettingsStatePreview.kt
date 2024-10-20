package paufregi.garminfeed.presentation.settings

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.core.models.Credential

class SettingsStatePreview : PreviewParameterProvider<SettingsState> {
    override val values = sequenceOf(
        SettingsState(Credential("paul", "pass"), false),
        SettingsState(Credential("paul", "pass"), true),
        SettingsState(Credential("", ""), false)
    )
}